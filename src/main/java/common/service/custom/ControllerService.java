package common.service.custom;

import common.service.base.AbstractService;
import controller.AbstractUIController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Евгений on 07.10.2015.
 */
public class ControllerService extends AbstractService {
    private Map<Class<? extends AbstractUIController>, AbstractUIController> controllerMap;

    @Override
    protected void customInitialization() {
        controllerMap = new HashMap<>();
    }

    public <T extends AbstractUIController> void registerController(T controller) {
        this.controllerMap.put(controller.getClass(), controller);
    }

    public <T extends AbstractUIController> T getController(Class<T> controller) {
        return (T) controllerMap.get(controller);
    }
}
