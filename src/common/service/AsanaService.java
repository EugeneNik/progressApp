package common.service;

import asana.AsanaHelper;
import data.Task;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ������� on 28.06.2015.
 */
public class AsanaService extends AbstractService {

    public AsanaService() {
        if (!ServiceCache.isInited(getClass())) {
            ServiceCache.init(getClass(), this);
            customInitialization();
        } else {
            throw new UnsupportedOperationException("Asana Service is initialized use Services.get");
        }
    }

    private void customInitialization() {
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
                JSONObject project = array.getJSONObject(i);
                String themeName = project.getString("name");
                Long id = project.getLong("id");
                themeName += ":";
                Task currentTask = new Task(id, themeName, 0L, 0.0, 0.0, false, root);
                int index = root.getSubtasks().indexOf(currentTask);
                if (index < 0) {
                    root.getSubtasks().add(currentTask);
                } else {
                    currentTask = root.getSubtasks().get(index);
                    currentTask.setTask(themeName);
                }
                AsanaHelper.parseAndFill(currentTask);
                updateProgress(i + 1, array.length());
            }
            TransPlatformService.getInstance().setRoot(root);
            onFinish();
        } catch (Exception e) {
            updateMessage("Asana Sync failed");
            e.printStackTrace();
        }
        updateProgress(1, 1);
    }
}