package common.achievements;

import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class TaskAchievement extends BaseAchievement {
    protected Task task;

    protected TaskAchievement(Task task) {
        this.task = task;
        retest();
    }
}
