package common;

import common.property.PropertyManager;
import common.service.PredictionService;
import common.service.Services;
import common.service.TransPlatformService;
import controller.MainStageController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import jaxb.TaskJAXB;
import jaxb.utils.JaxbConverter;
import jaxb.utils.JaxbMarshaller;

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

        TabPane parent = new TabPane();

        parent.getTabs().addAll(progressTab, suggestionTab);

        String cssPath = this.getClass().getResource(FileNamespace.CSS).toExternalForm();
        Scene scene = new Scene(parent, 800, 600);
        scene.getStylesheets().add(cssPath);

        primaryStage.setOnCloseRequest(event -> {
            JaxbMarshaller.marshall(JaxbConverter.convertToJaxb(TransPlatformService.getInstance().getRoot()), TaskJAXB.class, FileNamespace.BACKUP);
            TransPlatformService.getInstance().getRoot().getManager().anullate();
            JaxbMarshaller.marshall(JaxbConverter.convertToJaxb(TransPlatformService.getInstance().getRoot()), TaskJAXB.class, FileNamespace.STRUCTURE);
            PropertyManager.save();
            System.exit(0);
        });

        //System.out.println(Services.get(PredictionService.class).predict());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.show();
    }


}
