package controller;

import common.FileNamespace;
import common.property.PropertyManager;
import common.service.base.Services;
import common.service.base.TransPlatformService;
import common.service.custom.UserService;
import data.UserProfile;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import jaxb.TaskJAXB;
import jaxb.utils.JaxbConverter;
import jaxb.utils.JaxbMarshaller;
import ui.MainStage;


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

    public EventHandler<WindowEvent> getOnCloseListener () {
        return event -> {
            JaxbMarshaller.marshall(JaxbConverter.convertToJaxb(TransPlatformService.getInstance().getRoot()), TaskJAXB.class, FileNamespace.BACKUP);
            TransPlatformService.getInstance().getRoot().getManager().anullate();
            JaxbMarshaller.marshall(JaxbConverter.convertToJaxb(TransPlatformService.getInstance().getRoot()), TaskJAXB.class, FileNamespace.STRUCTURE);
            JaxbMarshaller.marshall(Services.get(UserService.class).getProfile(), UserProfile.class, FileNamespace.USER_PROFILE);
            PropertyManager.save();
            System.exit(0);
        };
    }
}
