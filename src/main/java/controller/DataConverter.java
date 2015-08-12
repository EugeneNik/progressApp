package controller;

import common.service.base.TransPlatformService;
import data.SuggestedTaskData;
import data.Task;

/**
 * Created by Евгений on 28.06.2015.
 */
public class DataConverter {

    public static SuggestedTaskData convertTaskToSuggestedTask(Task task) {
        return new SuggestedTaskData(task.getId(), task.taskProperty(), task.storyPointsProperty(), task.getParent().taskProperty(), task.getParent().progressProperty());
    }

    public static Task convertSuggestedTaskToTask(SuggestedTaskData suggestedTaskData) {
        return  TransPlatformService.getInstance().getRoot().getManager().find(suggestedTaskData.getId());
    }
}
