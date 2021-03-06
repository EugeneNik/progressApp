package ui;

import controller.AchievementTabController;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by ������� on 12.08.2015.
 */
public class AchievementTab extends Tab {

    AchievementTabController controller;
    GridPane achievementGrid;
    ScrollPane achievementScrollPane;

    public AchievementTab(Stage primaryStage) {
        this.setText("Achievements");
        this.setClosable(false);

        controller = new AchievementTabController(this);

        achievementScrollPane = new ScrollPane();
        achievementScrollPane.setStyle("-fx-background-color:transparent;");

        achievementGrid = new GridPane();
        achievementGrid.prefWidthProperty().bind(primaryStage.widthProperty());
        achievementGrid.prefHeightProperty().bind(primaryStage.heightProperty());

        achievementGrid.widthProperty().addListener(controller.getOnResizeListener());
        achievementGrid.heightProperty().addListener(controller.getOnResizeListener());
        achievementGrid.setVgap(50.0);
        achievementGrid.setHgap(20.0);

        controller.fillGrid(achievementGrid);

        achievementScrollPane.setContent(achievementGrid);

        BorderPane mainLayout = new BorderPane();
        this.setContent(mainLayout);
        mainLayout.setBottom(achievementScrollPane);
    }

    public GridPane getAchievementGrid() {
        return achievementGrid;
    }
}
