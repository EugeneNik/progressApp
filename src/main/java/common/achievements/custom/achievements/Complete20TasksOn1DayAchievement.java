package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 21.08.2015.
 */
public class Complete20TasksOn1DayAchievement extends CompleteNTasksOnMDaysAchievement {
    public Complete20TasksOn1DayAchievement(Task task) {
        super(task);
        this.setTooltip("Complete 20 tasks in 1 day");
        this.setImagePath(FileNamespace.COMPLETE_20_IN_1_DAY);
    }

    @Override
    protected boolean calcResult() {
        boolean result = false;
        for (long day : dayToTasksMap.keySet()) {
            if (dayToTasksMap.get(day).size() >= 20) {
                result = true;
                break;
            }
        }
        return result;
    }
}
