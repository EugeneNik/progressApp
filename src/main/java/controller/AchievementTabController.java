package controller;

import common.FileNamespace;
import common.achievements.Achievement;
import common.service.base.ServiceListener;
import common.service.base.Services;
import common.service.custom.AchievementService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import ui.AchievementTab;
import utils.ImageUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Евгений on 12.08.2015.
 */
public class AchievementTabController extends AbstractTabController {

    private AchievementTab ui;

    public AchievementTabController(AchievementTab ui) {
        this.ui = ui;
        AchievementService achievementService = Services.get(AchievementService.class);
        achievementService.addServiceListener(new ServiceListener() {
            Set<Achievement> completedAchievements;

            @Override
            public void onStart() {
                completedAchievements = new HashSet<>();
                completedAchievements.addAll(achievementService.getRegisteredAchievements().stream().filter(achievement -> achievement.isWasCompleted()).collect(Collectors.toList()));
            }

            @Override
            public void onFinish() {
                Set<Achievement> finishedAchivements = achievementService.getRegisteredAchievements().stream().filter(achievement -> achievement.isCompleted()).collect(Collectors.toSet());
                boolean shouldBeReloaded = false;
                for (Achievement achievement : finishedAchivements) {
                    if (completedAchievements.contains(achievement)) {
                        completedAchievements.remove(achievement);
                    } else {
                        shouldBeReloaded = true;
                    }
                }
                if (!completedAchievements.isEmpty()) {
                    shouldBeReloaded = true;
                }
                if (shouldBeReloaded) {
                    Platform.runLater(() -> {
                        ui.getAchievementGrid().getChildren().clear();
                        fillGrid(ui.getAchievementGrid());
                    });
                }
            }
        });
    }

    public ChangeListener<Number> getOnResizeListener() {
        return (observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillGrid(ui.getAchievementGrid());
            }
        };
    }

    public void fillGrid(GridPane pane) {
        List<Achievement> achievements = Services.get(AchievementService.class).getRegisteredAchievements();
        int i = 0;
        int j = 0;
        pane.getChildren().clear();
        int itemsPerRow = (int) ((pane.getPrefWidth() == 0.0 ? 800 : pane.getPrefWidth()) / 150);
        for (Achievement achievement : achievements) {
            BorderPane cell = new BorderPane();
            Image image;
            if (achievement.isCompleted()) {
                image = ImageUtils.loadJavaFXImage(achievement.getImagePath());
            } else {
                image = ImageUtils.loadJavaFXImage(FileNamespace.DEFAULT_ACHIEVEMENT_IMAGE);
            }
            ImageView view = new ImageView();
            WritableImage writableImage = ImageUtils.copyImage(image, achievement.isCompleted() ? ImageUtils.ImageRenderType.NORMAL : ImageUtils.ImageRenderType.GRAYSCALE);
            view.setImage(writableImage);
            cell.setTop(view);
            BorderPane bottomTextArea = new BorderPane();
            bottomTextArea.setCenter(new Text(achievement.getTooltip()));
            cell.setBottom(bottomTextArea);
            pane.add(cell, i, j);
            i++;
            if (i == itemsPerRow) {
                i = 0;
                j++;
            }
        }
        //2, cause order from zero + it is counting for the left high corner
        ui.getAchievementGrid().setMinHeight((j + 2) * 128 + (j + 2) * 50);
    }
}
