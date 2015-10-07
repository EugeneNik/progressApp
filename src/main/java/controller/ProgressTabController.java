package controller;

import common.ProgressTab;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.ServiceListener;
import common.service.base.Services;
import common.service.base.TransPlatformService;
import common.service.custom.AsanaService;
import data.Task;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TreeItem;
import utils.DateUtils;
import utils.FormatUtils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Евгений on 28.06.2015.
 */
public class ProgressTabController extends AbstractTabController {

    private ProgressTab ui;
    private Timer timerToNextStart = null;

    public ProgressTabController(ProgressTab ui) {
        this.ui = ui;
        AsanaService asanaService = Services.get(AsanaService.class);
        asanaService.addServiceListener(new ServiceListener() {
            @Override
            public void onStart() {
                Platform.runLater(() -> {
                    ui.getStatusBar().textProperty().bind(asanaService.statusProperty());
                    ui.getStatusBar().progressProperty().bind(asanaService.progressProperty());
                });
            }

            @Override
            public void onFinish() {
                Platform.runLater(() -> {
                    final TreeItem<Task> rootItem = new TreeItem<>(TransPlatformService.getInstance().getRoot());
                    ui.getTree().setRoot(rootItem);
                    ui.getTree().setShowRoot(false);

                    addTreeItemsRecursive(TransPlatformService.getInstance().getRoot(), rootItem);
                    asanaService.setStatus(!Services.get(AsanaService.class).isStopped() ? "Loaded" : "Aborted");
                    Services.get(AsanaService.class).resume();

                    ui.getStatusBar().textProperty().unbind();
                    ui.getStatusBar().progressProperty().unbind();
                });
            }
        });
        scheduleAutoSync();
    }

    public void scheduleAutoSync() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Calendar autoSyncProperty = DateUtils.parseTimeString(PropertyManager.getValue(PropertyNamespace.AUTO_SYNC_PROPERTY));
                if (Calendar.getInstance().after(autoSyncProperty)) {
                    autoSyncProperty.add(Calendar.DAY_OF_YEAR, 1);
                }
                timerToNextStart = new Timer();
                timerToNextStart.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Auto Sync start");
                        sync();
                        System.out.println("Auto Sync finish");
                    }
                }, autoSyncProperty.getTime());
            }
        });
    }

    public void sync() {
        Services.get(AsanaService.class).sync(TransPlatformService.getInstance().getRoot());
    }

    public void addTreeItemsRecursive(Task task, TreeItem<Task> item) {
        for (Task subtask : task.getSubtasks()) {
            TreeItem<Task> subTaskItem = new TreeItem<>(subtask);
            item.getChildren().add(subTaskItem);

            addTreeItemsRecursive(subtask, subTaskItem);
        }
    }

    public void changeDigits() {
        ui.getMainStage().titleProperty().unbind();
        ui.getMainStage().titleProperty().bind(TransPlatformService.getInstance().getRoot().taskProperty().concat(Bindings.format(" (" + FormatUtils.getProperDoubleFormat(true) + ")", TransPlatformService.getInstance().getRoot().progressProperty().multiply(100.0))));
    }
}
