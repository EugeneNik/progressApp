package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask20Achievement extends CompletedTaskAchievement {

    public CompletedTask20Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 20 tasks");
        this.setImagePath(FileNamespace.COMPLETED_TASKS_20);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 20;
    }
}
