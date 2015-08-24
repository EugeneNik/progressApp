package common.achievements.custom.achievements;

import common.FileNamespace;
import common.achievements.custom.base.CompletedTaskAchievement;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask1Achievement extends CompletedTaskAchievement {

    public CompletedTask1Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 1 tasks");
        this.setImagePath(FileNamespace.COMPLETED_TASKS_1);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 1;
    }
}
