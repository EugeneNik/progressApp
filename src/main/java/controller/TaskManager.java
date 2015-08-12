package controller;

import common.achievements.base.TaskAchievement;
import data.Task;

import java.util.List;

/**
 * Created by nikiforov on 28.05.2015.
 */
public class TaskManager {

    private Task parent;

    public TaskManager(Task parent) {
        this.parent = parent;
    }

    public void mergeTask(Task task) {
        if (task == null) {
            return;
        }
        if (parent.isLeaf()) {
            parent.setCompleted(task.getProgress());
            updateStoryPoints(task.getStoryPoints() == 0.0 ? 8.0 : task.getStoryPoints());
            parent.setTimeEstimated(task.getTimeEstimated());
            return;
        }
        for (int i = 0; i < parent.getSubtasks().size(); i++) {
            Task taskWithId = null;
            for (int j = 0; j < task.getSubtasks().size(); j++) {
                if (task.getSubtasks().get(j).equals(parent.getSubtasks().get(i))) {
                    taskWithId = task.getSubtasks().get(j);
                }
            }
            parent.getSubtasks().get(i).setStoryPoints(0.0);
            parent.getSubtasks().get(i).setTimeEstimated(1L);
            parent.getSubtasks().get(i).getManager().mergeTask(taskWithId);
        }
    }

    public Task find(long id) {
        if (parent.getId() == id) {
            return parent;
        }
        for (Task task : parent.getSubtasks()) {
            Task returned = task.getManager().find(id);
            if (returned != null) {
                return returned;
            }
        }
        return null;
    }

    public void reset() {
        parent.setCompleted(0.0);
    }

    public void updateParent(double percentageToAdd) {
        Task iterator = parent;
        while (iterator.getParent() != null) {
            double percentage = 1.0 / iterator.getParent().getSubtasks().size() * (iterator.getSubtasks().isEmpty() ? percentageToAdd : percentageToAdd / iterator.getSubtasks().size());
            iterator.getParent().setProgress(iterator.getParent().getProgress() + percentage);
            iterator = iterator.getParent();
        }
    }

    public void addStoryPoints(double newValue) {
        Task iterator = parent;
        while (iterator.getParent() != null) {
            iterator.getParent().setStoryPoints(iterator.getParent().getStoryPoints() + newValue);
            iterator = iterator.getParent();
        }
        parent.setStoryPoints(newValue);
    }

    public void updateStoryPoints(double newValue) {
        Task iterator = parent;
        while (iterator.getParent() != null) {
            iterator.getParent().setStoryPoints(iterator.getParent().getStoryPoints() - parent.storyPointsProperty().getValue());
            iterator = iterator.getParent();
        }
        addStoryPoints(newValue);
    }

    public void anullate() {
        if (parent.isLeaf()) {
            reset();
            return;
        }
        for (int i = 0; i < parent.getSubtasks().size(); i++) {
            parent.setStoryPoints(0.0);
            parent.setTimeEstimated(0L);
            parent.getSubtasks().get(i).getManager().anullate();
        }
    }

    public double getCompletedStoryPoints() {
        double completedStoryPoints = 0.0;
        for (Task task : parent.getSubtasks()) {
            if (task.isLeaf()) {
                completedStoryPoints += task.getStoryPoints() * task.getProgress();
            }
            completedStoryPoints += task.getManager().getCompletedStoryPoints();
        }
        return completedStoryPoints;
    }

    public List<Task> getLeafList(Task currentTask, List<Task> list) {
        if (currentTask.isLeaf()) {
            list.add(currentTask);
            return list;
        }
        for (Task subTask : currentTask.getSubtasks()) {
            list = getLeafList(subTask, list);
        }
        return list;
    }
}
