package common;

import asana.AsanaHelper;
import common.property.PropertyManager;
import data.Task;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import jaxb.TaskJAXB;
import jaxb.utils.JaxbConverter;
import jaxb.utils.JaxbMarshaller;
import jaxb.utils.JaxbUnmarshaller;
import org.json.JSONArray;
import org.json.JSONObject;
import ui.StatusBar;
import utils.ImageUtils;

import java.text.DecimalFormat;

/**
 * Created by DARIA on 12.04.2015.
 */
public class MainStage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        PropertyManager.getApplicationSettings();
        TextArea commentArea = new TextArea();

        commentArea.setMinWidth(700);
        Button syncButton = new Button("Sync");
        syncButton.setGraphic(new ImageView(ImageUtils.loadJavaFXImage(FileNamespace.REFRESH)));

        final StatusBar statusBar = new StatusBar();
        TreeTableView<Task> tree = new TreeTableView<>();
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<Task, String> descriptionColumn = new TreeTableColumn<>("Description");
        TreeTableColumn<Task, Double> progressColumn = new TreeTableColumn<>("Progress");

        progressColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("progress"));

        tree.getColumns().addAll(descriptionColumn, progressColumn);

        Task root = JaxbConverter.convertToSimple(JaxbUnmarshaller.unmarshall(FileNamespace.BACKUP));

        //CSVHelper.parseBackup(FileNamespace.BACKUP, root);
        //root = JaxbConverter.convertToSimple(JaxbUnmarshaller.unmarshall(TaskJAXB.class, FileNamespace.BACKUP);

        final TreeItem<Task> rootItem = new TreeItem<>(root);
        tree.setRoot(rootItem);
        tree.setShowRoot(false);

        addTreeItemsRecursive(root, rootItem);


        descriptionColumn.setCellValueFactory(param -> param.getValue().getValue().taskProperty());
        progressColumn.setCellFactory(new Callback<TreeTableColumn<Task, Double>, TreeTableCell<Task, Double>>() {

            @Override
            public TreeTableCell<Task, Double> call(TreeTableColumn<Task, Double> param) {
                final ProgressBar progressBar = new ProgressBar(-1);

                final TreeTableCell cell = new TreeTableCell<Task, Double>() {
                    @Override
                    protected void updateItem(Double t, boolean bln) {
                        super.updateItem(t, bln);
                        if (bln) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            StackPane box = new StackPane();
                            progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue != null && newValue.doubleValue() == 1.0) {
                                    progressBar.getStyleClass().addAll("green-bar");
                                }
                            });
                            progressBar.setProgress(t);
                            DecimalFormat format = new DecimalFormat("#0.00");
                            Label label = new Label(format.format(t * 100) + "%");
                            box.getChildren().addAll(progressBar, label);
                            progressBar.prefWidthProperty().bind(this.widthProperty());
                            setGraphic(box);
                        }
                    }
                };


                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        tree.setRowFactory(treeTableView -> {
            final TreeTableRow<Task> row = new TreeTableRow<>();
            final ContextMenu rowMenu = new ContextMenu();
            MenuItem completeItem = new MenuItem("Complete task");
            completeItem.setOnAction(event -> {
                if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf() && !tree.getSelectionModel().getSelectedItem().getValue().getCompleted()) {
                    tree.getSelectionModel().getSelectedItem().getValue().setCompleted(0.0);
                    tree.getSelectionModel().getSelectedItem().getValue().setCompleted(1.0);
                }
            });

            Menu partialCompleteItem = new Menu("Partial Complete");
            for (int i = 10; i <= 90; i += 10) {
                MenuItem item = new MenuItem(Integer.toString(i));
                partialCompleteItem.getItems().add(item);
                final int parameter = i;
                item.setOnAction(event -> {
                    if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                        tree.getSelectionModel().getSelectedItem().getValue().setCompleted(0.0);
                        tree.getSelectionModel().getSelectedItem().getValue().setCompleted(parameter / 100.0);
                    }
                });
            }

            MenuItem resetItem = new MenuItem("Reset progress");
            resetItem.setOnAction(event -> {
                if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                    tree.getSelectionModel().getSelectedItem().getValue().setCompleted(0.0);
                }
            });
            rowMenu.getItems().addAll(completeItem, partialCompleteItem, resetItem);
            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu) null));
            return row;
        });

        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Task>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Task>> observable, TreeItem<Task> oldValue, TreeItem<Task> newValue) {
                if (oldValue != null && oldValue.getValue() != null) {
                    oldValue.getValue().descriptionProperty().unbind();
                }
                if (newValue != null && newValue.getValue() != null) {
                    commentArea.setText(newValue.getValue().getDescription());
                    newValue.getValue().descriptionProperty().bind(commentArea.textProperty());
                }
            }
        });

        syncButton.setOnAction(event -> {
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task() {
                @Override
                protected Void call() throws Exception {
                    updateMessage("Wait for data loading...");

                    try {
                        JSONArray array = AsanaHelper.connector.getProjects().getJSONArray("data");
                        updateProgress(0, array.length());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject project = array.getJSONObject(i);
                            String themeName = project.getString("name");
                            Long id = project.getLong("id");
                            themeName += ":";
                            Task currentTask = new Task(id, themeName, 0.0, false, root);
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
                        Platform.runLater(() -> {
                            final TreeItem<Task> rootItem = new TreeItem<>(root);
                            tree.setRoot(rootItem);
                            tree.setShowRoot(false);

                            addTreeItemsRecursive(root, rootItem);
                            updateMessage("Loaded");
                        });
                    } catch (Exception e) {
                        updateMessage("Asana Sync failed");
                        System.err.println(e);
                    }

                    updateProgress(0, 0);
                    done();
                    return null;
                }
            };
            statusBar.textProperty().bind(task.messageProperty());
            statusBar.progressProperty().bind(task.progressProperty());

            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    statusBar.textProperty().unbind();
                    statusBar.progressProperty().unbind();
                }
            });

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });

        primaryStage.setOnCloseRequest(event -> {
            //CSVHelper.saveBackup(FileNamespace.BACKUP, root);
            JaxbMarshaller.marshall(JaxbConverter.convertToJaxb(root), TaskJAXB.class, FileNamespace.BACKUP);
            PropertyManager.save();
        });

        VBox parent = new VBox();
        HBox bottom = new HBox();
        BorderPane rightBottom = new BorderPane();
        rightBottom.setPadding(new Insets(5, 0, 0, 15));
        rightBottom.setTop(syncButton);
        bottom.getChildren().addAll(commentArea, rightBottom);
        parent.getChildren().addAll(tree, bottom, statusBar);

        String cssPath = this.getClass().getResource(FileNamespace.CSS).toExternalForm();
        Scene scene = new Scene(parent, 800, 600);
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.show();


    }

    private void addTreeItemsRecursive(Task task, TreeItem<Task> item) {
        for (Task subtask : task.getSubtasks()) {
            TreeItem<Task> subTaskItem = new TreeItem<>(subtask);
            item.getChildren().add(subTaskItem);

            addTreeItemsRecursive(subtask, subTaskItem);
        }
    }
}
