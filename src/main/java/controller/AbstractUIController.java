package controller;

import common.service.base.Services;
import common.service.custom.ControllerService;

/**
 * Created by Евгений on 07.10.2015.
 */
public abstract class AbstractUIController {

    protected AbstractUIController() {
        Services.get(ControllerService.class).registerController(this);
    }
}
