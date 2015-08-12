package common;

import controller.AchievementTabController;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * Created by Евгений on 12.08.2015.
 */
public class AchievementTab extends Tab {

    AchievementTabController controller;

    public AchievementTab() {
        this.setText("Achievements");
        this.setClosable(false);

        controller = new AchievementTabController(this);

        GridPane achievementGrid = new GridPane();
        achievementGrid.setVgap(3.0);
        achievementGrid.setHgap(8.0);

        controller.fillGrid(achievementGrid);

        BorderPane mainLayout = new BorderPane();
        this.setContent(mainLayout);
        mainLayout.setBottom(achievementGrid);
    }
}
