package common.service.custom;

import asana.AsanaHelper;
import com.asana.Client;
import com.asana.models.Project;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.AbstractService;
import common.service.base.TransPlatformService;
import data.Task;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Евгений on 28.06.2015.
 */
public class AsanaService extends AbstractService {

    private boolean stopper = false;
    private Client client;

    protected void customInitialization() {
        listeners = new ArrayList<>();
        //can be changed to oauth 2 protocol
        client = Client.basicAuth(PropertyManager.getValue(PropertyNamespace.APP_KEY));
    }

    private void updateMessage(String status) {
        Platform.runLater(() -> this.status.setValue(status));
    }

    public void updateProgress(double progress, double full) {
        Platform.runLater(() -> this.progress.setValue(progress / full));
    }

    public void sync(Task root) {
        onStart();
        log.info("Auto Sync start");
        updateMessage("Wait for data loading...");

        try {
            List<Project> array = client.projects.findAll().execute();
            int size = array.size();
            int i = 0;
            updateProgress(0, size);
            for (Project project : array) {
                if (isStopped()) {
                    break;
                }
                String themeName = new String(project.name.getBytes(), "UTF-8");
                Long id = Long.parseLong(project.id);
                themeName += ":";
                Task currentTask = new Task(id, themeName, 0L, 0.0, 0.0, false, 0L, root);
                int index = root.getSubtasks().indexOf(currentTask);
                if (index < 0) {
                    root.getSubtasks().add(currentTask);
                } else {
                    currentTask = root.getSubtasks().get(index);
                    currentTask.setTask(themeName);
                }
                AsanaHelper.parseAndFill(currentTask, i, array.size());
                if (isStopped()) {
                    break;
                }
                i++;
            }
            if (!isStopped()) {
                TransPlatformService.getInstance().setRoot(root);
            }
            onFinish();
        } catch (Exception e) {
            updateMessage("Asana Sync failed");
            e.printStackTrace();
        }
        updateProgress(1, 1);
        log.info("Auto Sync finish");
    }

    public Client getClient() {
        return client;
    }

    public boolean isStopped() {
        return stopper;
    }

    public void stop() {
        stopper = true;
    }

    public void resume() {
        stopper = false;
    }
}