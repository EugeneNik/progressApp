package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask100Achievement extends CompletedTaskAchievement {

    public CompletedTask100Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 100 tasks");
        this.setImagePath(FileNamespace.COMPLETED_TASKS_100);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 100;
    }
}
