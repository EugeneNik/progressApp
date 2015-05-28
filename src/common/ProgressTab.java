package common;

import asana.AsanaHelper;
import common.property.PropertyManager;
import common.service.TransPlatformService;
import data.Task;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class ProgressTab extends Tab {
    private TreeTableView<Task> tree;
    private TreeItem<Task> foundElement;

    public ProgressTab(Stage primaryStage) {
        TextArea commentArea = new TextArea();

        TextField searchField = new TextField();
        searchField.setMaxWidth(80);

        commentArea.setMinWidth(700);
        Button openLink = new Button("Open link");
        openLink.setGraphic(new ImageView(ImageUtils.loadJavaFXImage(FileNamespace.GLOBAL_SEARCH)));
        Button syncButton = new Button("Sync");
        syncButton.setGraphic(new ImageView(ImageUtils.loadJavaFXImage(FileNamespace.REFRESH)));

        final StatusBar statusBar = new StatusBar();
        tree = new TreeTableView<>();
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<Task, String> descriptionColumn = new TreeTableColumn<>("Description");
        TreeTableColumn<Task, Double> progressColumn = new TreeTableColumn<>("Progress");

        progressColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("progress"));

        tree.getColumns().addAll(descriptionColumn, progressColumn);

        tree.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (tree.getSelectionModel().getSelectedItem() != null) {
                    openLink.fire();
                }
            }
        });

        Task root = JaxbConverter.convertToSimple(JaxbUnmarshaller.unmarshall(FileNamespace.STRUCTURE, TaskJAXB.class));
        Task back = JaxbConverter.convertToSimple(JaxbUnmarshaller.unmarshall(FileNamespace.BACKUP, TaskJAXB.class));

        root.getManager().mergeTask(back);

        final TreeItem<Task> rootItem = new TreeItem<>(root);

        TransPlatformService.getInstance().setRoot(root);

        tree.setRoot(rootItem);
        tree.setShowRoot(false);

        primaryStage.titleProperty().bind(root.taskProperty().concat(Bindings.format(" (%.2f%%)", root.progressProperty().multiply(100.0))));

        addTreeItemsRecursive(root, rootItem);

        descriptionColumn.setCellValueFactory(param -> param.getValue().getValue().taskProperty());
        descriptionColumn.setCellFactory(param -> {
            final TreeTableCell<Task, String> cell = new TreeTableCell<Task, String>() {
                @Override
                protected void updateItem(String t, boolean bln) {
                    super.updateItem(t, bln);
                    if (bln) {
                        setContextMenu(null);
                        setGraphic(null);
                    } else {
                        setText(t);
                        if (getTreeTableRow() != null && getTreeTableRow().getTreeItem() != null && getTreeTableRow().getTreeItem().isLeaf()) {
                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem completeItem = new MenuItem("Complete task");
                            completeItem.setOnAction(event -> {
                                if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf() && !tree.getSelectionModel().getSelectedItem().getValue().getCompleted()) {
                                    tree.getSelectionModel().getSelectedItem().getValue().setCompleted(1.0);
                                }
                            });

                            Menu changeStoryPoints = new Menu("Change estimate");
                            for (int i = 1; i <= 32; i *= 2) {
                                MenuItem item = new MenuItem(Integer.toString(i));
                                changeStoryPoints.getItems().add(item);
                                final int parameter = i;
                                item.setOnAction(event -> {
                                    if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                                        tree.getSelectionModel().getSelectedItem().getValue().getManager().updateStoryPoints(parameter);
                                        tree.getSelectionModel().getSelectedItem().getValue().setTimeEstimated(Calendar.getInstance().getTimeInMillis());
                                        updateItem(t, bln);
                                    }
                                });
                            }

                            Menu partialCompleteItem = new Menu("Partial Complete");
                            for (int i = 10; i <= 90; i += 10) {
                                MenuItem item = new MenuItem(Integer.toString(i));
                                partialCompleteItem.getItems().add(item);
                                final int parameter = i;
                                item.setOnAction(event -> {
                                    if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                                        tree.getSelectionModel().getSelectedItem().getValue().setCompleted(parameter / 100.0);
                                    }
                                });
                            }

                            MenuItem resetItem = new MenuItem("Reset progress");
                            resetItem.setOnAction(event -> {
                                if (tree.getSelectionModel().getSelectedItem().getValue().isLeaf()) {
                                    tree.getSelectionModel().getSelectedItem().getValue().getManager().reset();
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
        });

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
                                } else if (progressBar.getStyleClass().contains("green-bar")) {
                                    progressBar.getStyleClass().remove("green-bar");
                                }
                            });
                            progressBar.setProgress(t);
                            DecimalFormat format = new DecimalFormat("#0.00");
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
            }
        });

        searchField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (!searchField.getText().equals("")) {
                    if (!recursiveSearch(rootItem, searchField.getText())) {
                        statusBar.setText("Item not found: " + searchField.getText());
                    } else {
                        tree.getSelectionModel().select(foundElement);
                        tree.scrollTo(tree.getRow(foundElement));
                        statusBar.setText("Item " + searchField.getText() + " found");
                    }
                }
            }
        });

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && oldValue.getValue() != null) {
                oldValue.getValue().descriptionProperty().unbind();
            }
            if (newValue != null && newValue.getValue() != null) {
                commentArea.setText(newValue.getValue().getDescription());
                newValue.getValue().descriptionProperty().bind(commentArea.textProperty());
            }
        });

        openLink.setOnAction(event -> {
            String[] stringsBySpace = commentArea.getText().split(" ");
            for (String s : stringsBySpace) {
                String[] stringsByEndLine = s.split("\n");
                for (String str : stringsByEndLine) {
                    try {
                        if (str.startsWith("http") || str.startsWith("www")) {
                            URI u = new URI(str);
                            java.awt.Desktop.getDesktop().browse(u);
                        }
                    } catch (URISyntaxException | IOException e) {
                        statusBar.setText("Error in provided URL, check it please");
                    }
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
                        Platform.runLater(() -> {
                            final TreeItem<Task> rootItem = new TreeItem<>(root);
                            tree.setRoot(rootItem);
                            tree.setShowRoot(false);

                            addTreeItemsRecursive(root, rootItem);
                            updateMessage("Loaded");
                        });
                    } catch (Exception e) {
                        updateMessage("Asana Sync failed");
                        e.printStackTrace();
                    }

                    updateProgress(1, 1);
                    done();
                    return null;
                }
            };
            statusBar.textProperty().bind(task.messageProperty());
            statusBar.progressProperty().bind(task.progressProperty());

            task.setOnSucceeded(event1 -> {
                statusBar.textProperty().unbind();
                statusBar.progressProperty().unbind();
            });

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });

        VBox parent = new VBox();
        HBox bottom = new HBox();
        FlowPane rightBottom = new FlowPane(Orientation.VERTICAL);
        rightBottom.setRowValignment(VPos.CENTER);
        rightBottom.setColumnHalignment(HPos.CENTER);
        rightBottom.setPadding(new Insets(5, 0, 0, 5));
        rightBottom.setVgap(8);
        rightBottom.getChildren().addAll(searchField, openLink, syncButton);
        bottom.getChildren().addAll(commentArea, rightBottom);
        parent.getChildren().addAll(tree, bottom, statusBar);

        this.setContent(parent);
        this.setText("Progress");
        this.setClosable(false);
    }

    private boolean recursiveSearch(TreeItem<Task> task, String name) {
        if (task.getValue().getTask().indexOf(name) >= 0) {
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

    private void addTreeItemsRecursive(Task task, TreeItem<Task> item) {
        for (Task subtask : task.getSubtasks()) {
            TreeItem<Task> subTaskItem = new TreeItem<>(subtask);
            item.getChildren().add(subTaskItem);

            addTreeItemsRecursive(subtask, subTaskItem);
        }
    }
}
