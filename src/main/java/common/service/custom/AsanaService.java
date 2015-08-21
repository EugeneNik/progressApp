package common.service.custom;

import asana.AsanaHelper;
import common.service.base.AbstractService;
import common.service.base.TransPlatformService;
import data.Task;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Евгений on 28.06.2015.
 */
public class AsanaService extends AbstractService {

    private boolean stopper = false;

    protected void customInitialization() {
        listeners = new ArrayList<>();
    }

    private void updateMessage(String status) {
        Platform.runLater(() -> this.status.setValue(status));
    }

    private void updateProgress(double progress, double full) {
        Platform.runLater(() -> this.progress.setValue(progress / full));
    }

    public void sync(Task root) {
        onStart();
        updateMessage("Wait for data loading...");

        try {
            JSONArray array = AsanaHelper.connector.getProjects().getJSONArray("data");
            updateProgress(0, array.length());
            for (int i = 0; i < array.length(); i++) {
                if (isStopped()) {
                    break;
                }
                JSONObject project = array.getJSONObject(i);
                String themeName = project.getString("name");
                Long id = project.getLong("id");
                themeName += ":";
                Task currentTask = new Task(id, themeName, 0L, 0.0, 0.0, false, 0L, root);
                int index = root.getSubtasks().indexOf(currentTask);
                if (index < 0) {
                    root.getSubtasks().add(currentTask);
                } else {
                    currentTask = root.getSubtasks().get(index);
                    currentTask.setTask(themeName);
                }
                AsanaHelper.parseAndFill(currentTask);
                updateProgress(i + 1, array.length());
                if (isStopped()) {
                    break;
                }
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