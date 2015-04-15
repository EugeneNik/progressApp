package common;

import csv.CSVHelper;
import data.Task;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Created by DARIA on 12.04.2015.
 */
public class MainStage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // 1.
        TreeTableView<Task> tree = new TreeTableView<>();
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<Task, String> descriptionColumn = new TreeTableColumn<>("Description");
        TreeTableColumn<Task, Double> progressColumn = new TreeTableColumn<>("Progress");

        progressColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("progress"));

        tree.getColumns().addAll(descriptionColumn, progressColumn);

        descriptionColumn.setCellValueFactory(param -> param.getValue().getValue().taskProperty());
        progressColumn.setCellFactory(param -> new ProgressBarTreeTableCell<>());

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
                    .otherwise((ContextMenu)null));
            return row;
        });

        Task root = new Task("Root", 0.0, false, null);

        CSVHelper.parseBackup(FileNamespace.BACKUP, root);
        CSVHelper.parseCSV(FileNamespace.RESOURCES, root);

        final TreeItem<Task> rootItem = new TreeItem<>(root);
        tree.setRoot(rootItem);
        tree.setShowRoot(false);

        addTreeItemsRecursive(root, rootItem);

        VBox parent = new VBox();
        parent.getChildren().add(tree);

        primaryStage.setScene(new Scene(parent, 800, 600));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            CSVHelper.saveBackup(FileNamespace.BACKUP, root);
        });
    }

    private void addTreeItemsRecursive(Task task, TreeItem<Task> item){
        for (Task subtask : task.getSubtasks()) {
            TreeItem<Task> subTaskItem = new TreeItem<>(subtask);
            item.getChildren().add(subTaskItem);

            addTreeItemsRecursive(subtask, subTaskItem);
        }
    }
}
