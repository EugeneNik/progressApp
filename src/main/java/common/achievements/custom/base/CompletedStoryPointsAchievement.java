package common.achievements.custom.base;

import common.achievements.AchievementStatus;
import common.achievements.base.TaskAchievement;
import common.achievements.custom.BooleanAchievementStatus;
import data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikiforov on 12.08.2015.
 */
public abstract class CompletedStoryPointsAchievement extends TaskAchievement {
    protected CompletedStoryPointsAchievement(Task task) {
        super(task);
    }

    protected double countOfCompleted = 0.0;

    @Override
    public AchievementStatus retest() {
        countOfCompleted = 0.0;
        List<Task> list = task.getManager().getLeafList(task, new ArrayList<>());
        for (Task currentTask : list) {
            if (currentTask.getCompleted()) {
                countOfCompleted += currentTask.getStoryPoints();
            }
        }
        return new BooleanAchievementStatus(isCompleted());
    }
}
