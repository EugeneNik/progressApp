package controller;

import common.SettingsTab;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.ServiceListener;
import common.service.base.Services;
import common.service.custom.ControllerService;
import common.service.custom.SettingsService;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ui.Valuable;

/**
 * Created by nikiforov on 24.08.2015.
 */
public class SettingsTabController {
    SettingsTab ui;

    public SettingsTabController(SettingsTab ui) {
        this.ui = ui;
        SettingsService settingsService = Services.get(SettingsService.class);
        settingsService.addServiceListener(new ServiceListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    public EventHandler<KeyEvent> getSaveSettingsAction() {
        return event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                for (Valuable valuableElement : ui.getMap().keySet()) {
                    Object value = valuableElement.getValue();
                    if (valuableElement.getClazz() == Integer.class) {
                        value = Integer.parseInt(value.toString());
                    }
                    PropertyManager.setValue(ui.getMap().get(valuableElement).getName(), value);
                }
            }
        };
    }

    public void registerListeners() {
        Property digits = PropertyManager.getObservableProperty(PropertyNamespace.DIGITS_AFTER_POINTS).getProperty();
        digits.addListener((observable, oldValue, newValue) -> {
            Services.get(ControllerService.class).getController(ProgressTabController.class).changeDigits();
        });
    }
}
