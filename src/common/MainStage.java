package common;

import asana.AsanaHelper;
import common.property.PropertyManager;
import data.Task;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.*;
import javafx.scene.Scene;
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
import java.util.List;

/**
 * Created by DARIA on 12.04.2015.
 */
public class MainStage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        PropertyManager.getApplicationSettings();

        ProgressTab progressTab = new ProgressTab(primaryStage);
        SuggestionTab suggestionTab = new SuggestionTab(progressTab.getRoot());

        TabPane parent = new TabPane();

        parent.getTabs().addAll(progressTab);

        String cssPath = this.getClass().getResource(FileNamespace.CSS).toExternalForm();
        Scene scene = new Scene(parent, 800, 600);
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.show();
    }


}
