package common.achievements.custom.base;

import common.achievements.AchievementStatus;
import common.achievements.base.TaskAchievement;
import common.achievements.custom.BooleanAchievementStatus;
import data.Task;
import org.apache.commons.lang.time.DateUtils;

import java.util.*;

/**
 * Created by nikiforov on 21.08.2015.
 */
public abstract class CompleteNTasksOnMDaysAchievement extends TaskAchievement {
    protected Map<Long, Set<Task>> dayToTasksMap;

    public CompleteNTasksOnMDaysAchievement(Task task) {
        super(task);
    }

    @Override
    public AchievementStatus retest() {
        dayToTasksMap = new HashMap<>();
        List<Task> list = task.getManager().getLeafList(task, new ArrayList<>());
        for (Task currentTask : list) {
            if (currentTask.getCompleted()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentTask.getCompleteDate());
                long day = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH).getTimeInMillis();
                if (!dayToTasksMap.containsKey(day)) {
                    dayToTasksMap.put(day, new HashSet<>());
                }
                dayToTasksMap.get(day).add(currentTask);
            }
        }
        return new BooleanAchievementStatus(isCompleted());
    }
}
