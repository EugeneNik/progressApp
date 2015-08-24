package common.achievements.custom.achievements;

import common.FileNamespace;
import common.achievements.custom.base.CompletedTaskAchievement;
import data.Task;

import java.util.ArrayList;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTaskAllAchievement extends CompletedTaskAchievement {

    public CompletedTaskAllAchievement(Task task) {
        super(task);
        this.setTooltip("Complete all tasks");
        this.setImagePath(FileNamespace.COMPLETED_TASKS_ALL);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted == task.getManager().getLeafList(task, new ArrayList<>()).size();
    }
}
