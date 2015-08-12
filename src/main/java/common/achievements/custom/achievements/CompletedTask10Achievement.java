package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask10Achievement extends CompletedTaskAchievement {

    public CompletedTask10Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 10 tasks");
        this.setImagePath(FileNamespace.COMPLETED_TASKS_10);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 10;
    }
}
