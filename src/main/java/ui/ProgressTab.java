package ui;

import common.FileNamespace;
import common.service.base.TransPlatformService;
import controller.ProgressTabController;
import data.Task;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jaxb.TaskJAXB;
import jaxb.utils.JaxbConverter;
import jaxb.utils.JaxbUnmarshaller;
import utils.FormatUtils;
import utils.ImageUtils;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class ProgressTab extends Tab {
    private TreeTableView<Task> tree;
    private final StatusBar statusBar;
    private Button openLink;
    private final TreeItem<Task> rootItem;
    private Button syncButton;
    private TextField searchField;
    private TextArea commentArea;
    ProgressTabController controller;

    public ProgressTab(Stage primaryStage) {
        controller = new ProgressTabController(this);

        commentArea = new TextArea();

        searchField = new TextField();
        searchField.setMaxWidth(80);

        commentArea.minWidthProperty().bind(primaryStage.widthProperty().subtract(120));
        commentArea.minHeightProperty().bind(primaryStage.heightProperty().multiply(.3));
        openLink = new Button("Open link");
        openLink.setGraphic(new ImageView(ImageUtils.loadJavaFXImage(FileNamespace.GLOBAL_SEARCH)));
        syncButton = new Button("Sync");
        syncButton.setGraphic(new ImageView(ImageUtils.loadJavaFXImage(FileNamespace.REFRESH)));

        statusBar = new StatusBar();
        tree = new TreeTableView<>();
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        tree.prefHeightProperty().bind(primaryStage.heightProperty().multiply(.7));

        TreeTableColumn<Task, String> descriptionColumn = new TreeTableColumn<>("Description");
        TreeTableColumn<Task, Double> progressColumn = new TreeTableColumn<>("Progress");

        progressColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("progress"));

        tree.getColumns().addAll(descriptionColumn, progressColumn);

        tree.setOnKeyPressed(controller.getOnTreeKeyPressedListener());

        Task root = JaxbConverter.convertToSimple(JaxbUnmarshaller.unmarshall(FileNamespace.STRUCTURE, TaskJAXB.class));
        Task back = JaxbConverter.convertToSimple(JaxbUnmarshaller.unmarshall(FileNamespace.BACKUP, TaskJAXB.class));

        root.getManager().mergeTask(back);

        rootItem = new TreeItem<>(root);

        TransPlatformService.getInstance().setRoot(root);

        tree.setRoot(rootItem);
        tree.setShowRoot(false);

        primaryStage.titleProperty().bind(root.taskProperty().concat(Bindings.format(" (" + FormatUtils.getProperDoubleFormat(true) + ")", root.progressProperty().multiply(100.0))));

        controller.addTreeItemsRecursive(root, rootItem);

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


        descriptionColumn.setCellValueFactory(controller.getDescriptionColumnCellValueFactory());
        descriptionColumn.setCellFactory(controller.getDescriptionColumnCellFactory());

        progressColumn.setCellFactory(controller.getProgressColumnCellFactory());

        searchField.setOnKeyPressed(controller.getOnSearchFieldPressListener());

        tree.getSelectionModel().selectedItemProperty().addListener(controller.getOnTreeChangeListener());

        openLink.setOnAction(controller.getOnOpenLinkPressListener());

        syncButton.setOnAction(controller.getOnSyncButtonPressListener());

        this.setContent(parent);
        this.setText("Progress");
        this.setClosable(false);
    }

    public TextArea getCommentArea() {
        return commentArea;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getSyncButton() {
        return syncButton;
    }

    public TreeItem<Task> getRootItem() {
        return rootItem;
    }

    public Button getOpenLink() {
        return openLink;
    }

    public TreeTableView<Task> getTree() {
        return tree;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }
}
