package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedTask1000Achievement extends CompletedTaskAchievement {

    public CompletedTask1000Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 1000 tasks");
        this.setImagePath(FileNamespace.COMPLETED_TASKS_1000);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 1000;
    }
}
