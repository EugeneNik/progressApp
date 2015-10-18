package controller;

import common.FileNamespace;
import common.achievements.base.TaskAchievement;
import common.achievements.custom.base.CompletedStoryPointsAchievement;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.ServiceListener;
import common.service.base.Services;
import common.service.base.TransPlatformService;
import common.service.custom.AchievementService;
import common.service.custom.AsanaService;
import data.Task;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ui.ProgressTab;
import utils.DateUtils;
import utils.FormatUtils;
import utils.ImageUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Евгений on 28.06.2015.
 */
public class ProgressTabController extends AbstractTabController {

    private ProgressTab ui;
    private Timer timerToNextStart = null;
    private TreeItem<Task> foundElement;
    private boolean syncRunning = false;

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
        Platform.runLater(() -> {
            Calendar autoSyncProperty = DateUtils.parseTimeString(PropertyManager.getValue(PropertyNamespace.AUTO_SYNC_PROPERTY));
            if (Calendar.getInstance().after(autoSyncProperty)) {
                autoSyncProperty.add(Calendar.DAY_OF_YEAR, 1);
            }
            timerToNextStart = new Timer();
            timerToNextStart.schedule(new TimerTask() {
                @Override
                public void run() {
                    sync();
                }
            }, autoSyncProperty.getTime());
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

    private boolean recursiveSearch(TreeItem<Task> task, String name) {
        if (task.getValue().getTask().toLowerCase().contains(name.toLowerCase())) {
            task.setExpanded(true);
            foundElement = task;
            return true;
        } else {
            for (TreeItem<Task> child : task.getChildren()) {
                if (recursiveSearch(child, name)) {
                    task.setExpanded(true);
                    return true;
                }
            }
        }
        return false;
    }

    // =================================LISTENERS=====================================

    public EventHandler<KeyEvent> getOnTreeKeyPressedListener() {
        return event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (ui.getTree().getSelectionModel().getSelectedItem() != null) {
                    ui.getOpenLink().fire();
                }
            }
        };
    }

    public EventHandler<ActionEvent> getOnOpenLinkPressListener() {
        return event -> {
            String[] stringsBySpace = ui.getCommentArea().getText().split(" ");
            for (String s : stringsBySpace) {
                String[] stringsByEndLine = s.split("\n");
                for (String str : stringsByEndLine) {
                    try {
                        if (str.startsWith("http") || str.startsWith("www")) {
                            URI u = new URI(str);
                            java.awt.Desktop.getDesktop().browse(u);
                        }
                    } catch (URISyntaxException | IOException e) {
                        ui.getStatusBar().setText("Error in provided URL, check it please");
                    }
                }
            }
        };
    }

    public ChangeListener<TreeItem<Task>> getOnTreeChangeListener() {
        return (observable, oldValue, newValue) -> {
            if (oldValue != null && oldValue.getValue() != null) {
                oldValue.getValue().descriptionProperty().unbind();
            }
            if (newValue != null && newValue.getValue() != null) {
                ui.getCommentArea().setText(newValue.getValue().getDescription());
                newValue.getValue().descriptionProperty().bind(ui.getCommentArea().textProperty());
            }
        };
    }

    public EventHandler<KeyEvent> getOnSearchFieldPressListener() {
        return event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (!ui.getSearchField().getText().equals("")) {
                    if (!recursiveSearch(ui.getRootItem(), ui.getSearchField().getText())) {
                        ui.getStatusBar().setText("Item not found: " + ui.getSearchField().getText());
                    } else {
                        ui.getTree().getSelectionModel().select(foundElement);
                        ui.getTree().scrollTo(ui.getTree().getRow(foundElement));
                        ui.getStatusBar().setText("Item " + ui.getSearchField().getText() + " found");
                    }
                }
            }
        };
    }

    public Callback<TreeTableColumn<Task, Double>, TreeTableCell<Task, Double>> getProgressColumnCellFactory() {
        return param -> {
            final ProgressBar progressBar = new ProgressBar(-1);

            final TreeTableCell cell = new TreeTableCell<Task, Double>() {
                @Override
                protected void updateItem(Double t, boolean bln) {
                    super.updateItem(t, bln);
                    if (t == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        StackPane box = new StackPane();
                        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue != null && newValue.doubleValue() == 1.0) {
                                progressBar.getStyleClass().addAll("green-bar");
                            } else if (progressBar.getStyleClass().contains("green-bar")) {
                                progressBar.getStyleClass().remove("green-bar");
                            }
                        });
                        progressBar.setProgress(t);
                        DecimalFormat format = new DecimalFormat(FormatUtils.getProperDoubleFormatForProgressBars());
                        Label label = new Label(format.format(t * 100) + "%");
                        label.getStyleClass().addAll("progress-text");
                        box.getChildren().addAll(progressBar, label);
                        progressBar.prefWidthProperty().bind(this.widthProperty());
                        setGraphic(box);
                    }
                }
            };

            cell.setAlignment(Pos.CENTER);
            return cell;
        };
    }

    public Callback<TreeTableColumn.CellDataFeatures<Task, String>, ObservableValue<String>> getDescriptionColumnCellValueFactory() {
        return param -> param.getValue().getValue().taskProperty();
    }

    public Callback<TreeTableColumn<Task, String>, TreeTableCell<Task, String>> getDescriptionColumnCellFactory() {
        return param -> {
            final TreeTableCell<Task, String> cell = new TreeTableCell<Task, String>() {
                @Override
                protected void updateItem(String t, boolean bln) {
                    super.updateItem(t, bln);
                    if (t == null) {
                        setContextMenu(null);
                        setGraphic(null);
                    } else {
                        setText(t);
                        if (getTreeTableRow() != null && getTreeTableRow().getTreeItem() != null && getTreeTableRow().getTreeItem().isLeaf()) {
                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem completeItem = new MenuItem("Complete task");
                            completeItem.setOnAction(event -> {
                                if (ui.getTree().getSelectionModel().getSelectedItem().getValue().isLeaf() && !ui.getTree().getSelectionModel().getSelectedItem().getValue().getCompleted()) {
                                    ui.getTree().getSelectionModel().getSelectedItem().getValue().setCompleted(1.0);
                                    Services.get(AchievementService.class).retest(TaskAchievement.class);
                                }
                            });

                            Menu changeStoryPoints = new Menu("Change estimate");
                            for (int i = 1; i <= 32; i *= 2) {
                                MenuItem item = new MenuItem(Integer.toString(i));
                                changeStoryPoints.getItems().add(item);
                                final int parameter = i;
                                item.setOnAction(event -> {
                                    if (ui.getTree().getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                                        ui.getTree().getSelectionModel().getSelectedItem().getValue().getManager().updateStoryPoints(parameter);
                                        ui.getTree().getSelectionModel().getSelectedItem().getValue().setTimeEstimated(Calendar.getInstance().getTimeInMillis());
                                        updateItem(t, bln);
                                        Services.get(AchievementService.class).retest(CompletedStoryPointsAchievement.class);
                                    }
                                });
                            }

                            Menu partialCompleteItem = new Menu("Partial Complete");
                            for (int i = 10; i <= 90; i += 10) {
                                MenuItem item = new MenuItem(Integer.toString(i));
                                partialCompleteItem.getItems().add(item);
                                final int parameter = i;
                                item.setOnAction(event -> {
                                    if (ui.getTree().getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                                        ui.getTree().getSelectionModel().getSelectedItem().getValue().setCompleted(parameter / 100.0);
                                    }
                                });
                            }

                            MenuItem resetItem = new MenuItem("Reset progress");
                            resetItem.setOnAction(event -> {
                                if (ui.getTree().getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                                    ui.getTree().getSelectionModel().getSelectedItem().getValue().getManager().reset();
                                    Services.get(AchievementService.class).retest(TaskAchievement.class);
                                }
                            });
                            rowMenu.getItems().addAll(completeItem, partialCompleteItem, resetItem, changeStoryPoints);
                            if (getContextMenu() == null) {
                                setContextMenu(rowMenu);
                            }
                        }
                        if (getTreeTableRow() != null && getTreeTableRow().getTreeItem() != null) {
                            StackPane storyPoints = new StackPane();
                            Circle circle = new Circle(12, getTreeTableRow().getTreeItem().getValue().getTimeEstimated() == 0L ? Color.CORAL : Color.LIGHTBLUE);
                            Text text = new Text("");
                            text.textProperty().bind(Bindings.format("%.0f", getTreeTableRow().getTreeItem().getValue().storyPointsProperty()));
                            storyPoints.getChildren().addAll(circle, text);
                            setGraphic(storyPoints);
                        }
                    }
                }
            };
            return cell;
        };
    }

    public EventHandler<ActionEvent> getOnSyncButtonPressListener() {
        return event -> {
            if (!syncRunning) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("System message");
                alert.setHeaderText(null);
                alert.setContentText("Do you really want to do it? It will delete all your data");
                Optional<ButtonType> result = alert.showAndWait();
                result.ifPresent((type) -> {
                    if (type.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        syncRunning = true;
                        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                sync();
                                done();
                                syncRunning = false;
                                return null;
                            }
                        };

                        Thread t = new Thread(task);
                        t.setDaemon(true);
                        t.start();

                        ui.getSyncButton().setText("Stop");
                        ui.getSyncButton().setGraphic(new ImageView(ImageUtils.loadJavaFXImage(FileNamespace.STOP)));
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("System message");
                alert.setHeaderText(null);
                alert.setContentText("Do you really want to do it? It will stop synchronizing your repo data");
                Optional<ButtonType> result = alert.showAndWait();
                result.ifPresent((type) -> {
                    if (type.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        Services.get(AsanaService.class).stop();
                        syncRunning = false;
                        ui.getSyncButton().setText("Sync");
                        ui.getSyncButton().setGraphic(new ImageView(ImageUtils.loadJavaFXImage(FileNamespace.REFRESH)));
                    }
                });
            }
        };
    }

    public void changeDigits() {
        ui.getMainStage().titleProperty().unbind();
        ui.getMainStage().titleProperty().bind(TransPlatformService.getInstance().getRoot().taskProperty().concat(Bindings.format(" (" + FormatUtils.getProperDoubleFormat(true) + ")", TransPlatformService.getInstance().getRoot().progressProperty().multiply(100.0))));
    }
}
