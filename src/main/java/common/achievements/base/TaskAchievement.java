package common.achievements.base;

import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public abstract class TaskAchievement extends BaseAchievement {
    protected Task task;

    protected TaskAchievement(Task task) {
        this.task = task;
        this.isInitialRun = true;
        retest();
        this.isInitialRun = false;
    }
}
