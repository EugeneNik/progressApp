package common;

import common.property.BaseProperty;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import controller.SettingsTabController;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.LabeledEdit;
import ui.Valuable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikiforov on 24.08.2015.
 */
public class SettingsTab extends Tab {
    SettingsTabController controller;
    Map<Valuable, BaseProperty> map = new HashMap<>();

    public SettingsTab() {
        controller = new SettingsTabController(this);

        this.setText("Settings");
        this.setClosable(false);

        VBox mainPage = new VBox();

        LabeledEdit<Integer> digitsAfterPoint = new LabeledEdit<>("Digits after point:", PropertyManager.getValue(PropertyNamespace.DIGITS_AFTER_POINTS));

        map.put(digitsAfterPoint, PropertyManager.getProperty(PropertyNamespace.DIGITS_AFTER_POINTS));

        for (Valuable valuableElement : map.keySet()) {
            valuableElement.setValue(map.get(valuableElement).getStartValue());
            mainPage.getChildren().add((HBox) valuableElement);
        }


        this.setContent(mainPage);
    }
}
