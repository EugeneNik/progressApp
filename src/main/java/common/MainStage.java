package common;

import common.property.PropertyManager;
import controller.MainStageController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * Created by DARIA on 12.04.2015.
 */
public class MainStage extends Application {

    MainStageController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        PropertyManager.getApplicationSettings();

        controller = new MainStageController(this);

        ProgressTab progressTab = new ProgressTab(primaryStage);

        SuggestionTab suggestionTab = new SuggestionTab();

        AchievementTab achievementTab = new AchievementTab();

        TabPane parent = new TabPane();

        parent.getTabs().addAll(progressTab, suggestionTab, achievementTab);

        String cssPath = this.getClass().getResource(FileNamespace.CSS).toExternalForm();
        Scene scene = new Scene(parent, 800, 600);
        scene.getStylesheets().add(cssPath);

        primaryStage.setOnCloseRequest(controller.getOnCloseListener());

        //System.out.println(Services.get(PredictionService.class).predict());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.show();
    }


}
