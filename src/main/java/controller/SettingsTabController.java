package controller;

import common.SettingsTab;
import common.service.base.ServiceListener;
import common.service.base.Services;
import common.service.custom.SettingsService;

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
}
