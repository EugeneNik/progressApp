package common.achievements.custom.achievements;

import common.FileNamespace;
import common.achievements.custom.base.CompleteNTasksOnMDaysAchievement;
import data.Task;

/**
 * Created by nikiforov on 21.08.2015.
 */
public class Complete5TasksOn1DayAchievement extends CompleteNTasksOnMDaysAchievement {
    public Complete5TasksOn1DayAchievement(Task task) {
        super(task);
        this.setTooltip("Complete 5 tasks in 1 day");
        this.setImagePath(FileNamespace.COMPLETE_5_IN_1_DAY);
    }

    @Override
    protected boolean calcResult() {
        boolean result = false;
        for (long day : dayToTasksMap.keySet()) {
            if (dayToTasksMap.get(day).size() >= 5) {
                result = true;
                break;
            }
        }
        return result;
    }
}
