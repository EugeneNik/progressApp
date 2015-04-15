package common;

import csv.CSVHelper;
import data.Task;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DecimalFormat;

/**
 * Created by DARIA on 12.04.2015.
 */
public class MainStage extends Application {

    public static long id = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        TextArea commentArea = new TextArea();
        TreeTableView<Task> tree = new TreeTableView<>();
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<Task, String> descriptionColumn = new TreeTableColumn<>("Description");
        TreeTableColumn<Task, Double> progressColumn = new TreeTableColumn<>("Progress");

        progressColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("progress"));

        tree.getColumns().addAll(descriptionColumn, progressColumn);

        Task root = new Task("Root".hashCode(),"Root", 0.0, false, null);

        CSVHelper.parseBackup(FileNamespace.BACKUP, root);
        CSVHelper.parseCSV(FileNamespace.RESOURCES, root);
        //update progress

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
                    tree.getSelectionModel().getSelectedItem().getValue().setCompleted(1.0);
                }
            });

            MenuItem resetItem = new MenuItem("Reset progress");
            resetItem.setOnAction(event -> {
                if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                    tree.getSelectionModel().getSelectedItem().getValue().setCompleted(0.0);
                }
            });
            rowMenu.getItems().addAll(completeItem, resetItem);
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

        VBox parent = new VBox();
        parent.getChildren().addAll(tree, commentArea);

        String cssPath = this.getClass().getResource(FileNamespace.CSS).toExternalForm();
        Scene scene = new Scene(parent, 800, 600);
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            CSVHelper.saveBackup(FileNamespace.BACKUP, root);
        });
    }

    private void addTreeItemsRecursive(Task task, TreeItem<Task> item) {
        for (Task subtask : task.getSubtasks()) {
            TreeItem<Task> subTaskItem = new TreeItem<>(subtask);
            item.getChildren().add(subTaskItem);

            addTreeItemsRecursive(subtask, subTaskItem);
        }
    }
}
