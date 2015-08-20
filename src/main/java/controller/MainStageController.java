package controller;

import common.MainStage;
import common.service.base.Services;
import common.service.custom.UserService;

/**
 * Created by Евгений on 28.06.2015.
 */
public class MainStageController {

    MainStage ui;

    public MainStageController(MainStage app) {
        this.ui = app;
        UserService userService = Services.get(UserService.class);
        userService.authenticate("admin", "admin");
    }
}
