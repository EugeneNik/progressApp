package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask50Achievement extends CompletedTaskAchievement {

    public CompletedTask50Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 50 tasks");
        this.setImagePath(FileNamespace.COMPLETED_TASKS_50);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 50;
    }
}
