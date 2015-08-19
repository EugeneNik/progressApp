package controller;

import common.MainStage;
import common.service.custom.UserService;

/**
 * Created by Евгений on 28.06.2015.
 */
public class MainStageController {

    MainStage ui;

    public MainStageController(MainStage app) {
        this.ui = app;
        UserService userService = new UserService();
        userService.authenticate("admin", "admin");
    }
}
