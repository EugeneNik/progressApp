package controller;

import common.AchievementTab;
import common.achievements.Achievement;
import common.service.base.ServiceListener;
import common.service.base.Services;
import common.service.base.TransPlatformService;
import common.service.custom.AchievementService;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import utils.ImageUtils;

import java.util.List;

/**
 * Created by Евгений on 12.08.2015.
 */
public class AchievementTabController {

    private AchievementTab ui;

    public AchievementTabController(AchievementTab ui) {
        this.ui = ui;
        AchievementService achievementService = new AchievementService();
        achievementService.addServiceListener(new ServiceListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    public void fillGrid(GridPane pane) {
        List<Achievement> achievements = Services.get(AchievementService.class).getRegisteredAchievements();
        int i = 0;
        int j = 0;
        for (Achievement achievement : achievements) {
            BorderPane cell = new BorderPane();
            Image image = ImageUtils.loadJavaFXImage(achievement.getImagePath());
            ImageView view = new ImageView(image);
            view.setDisable(!achievement.isCompleted());
            cell.setTop(view);
            cell.setBottom(new Text(achievement.getTooltip()));
//            Tooltip tooltip = new Tooltip(achievement.getTooltip());
//            Tooltip.install(view, tooltip);
            pane.add(cell, i, j);
            i++;
            if (i == 5) {
                i = 0;
                j++;
            }
        }
    }
}
