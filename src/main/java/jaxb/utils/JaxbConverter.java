package jaxb.utils;

import data.Task;
import jaxb.TaskJAXB;

/**
 * Created by DARIA on 18.04.2015.
 */
public class JaxbConverter {

    public static TaskJAXB convertToJaxb(Task task) {
        TaskJAXB jaxb = new TaskJAXB();
        jaxb.setComment(task.getDescription());
        jaxb.setProgress(task.getProgress());
        jaxb.setStoryPoints(task.getStoryPoints());
        jaxb.setTimeEstimated(task.getTimeEstimated());
        jaxb.setCompleteDate(task.getCompleteDate());
        jaxb.setId(task.getId());
        jaxb.setName(task.getTask());
        for (Task task1 : task.getSubtasks()) {
            TaskJAXB child = convertToJaxb(task1);
            jaxb.getTasks().add(child);
        }
        return jaxb;
    }

    public static Task convertToSimple(TaskJAXB task) {
        Task result = new Task(task.getId(), task.getName(), task.getTimeEstimated(), task.getStoryPoints(), task.getProgress(), task.getProgress() == 1.0, task.getCompleteDate(), null);
        result.setDescription(task.getComment());
        for (TaskJAXB task1 : task.getTasks()) {
            Task child = convertToSimple(task1);
            child.setParent(result);
            result.getSubtasks().add(child);
        }
        return result;
    }
}
