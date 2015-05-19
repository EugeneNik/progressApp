package data;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by DARIA on 12.04.2015.
 */
public class Task {

    private long id;
    private StringProperty task;
    private StringProperty description = new SimpleStringProperty();
    private ObservableList<Task> subtasks = FXCollections.observableArrayList();
    private Task parent = null;
    //    private
    private DoubleProperty storyPoints;
    private DoubleProperty progress;
    private BooleanProperty completed;

    public Task(long id, String task, Double storyPoints, Double progress, Boolean completed, Task parent) {
        this.id = id;
        this.task = new SimpleStringProperty(task);
        this.progress = new SimpleDoubleProperty(progress);
        this.completed = new SimpleBooleanProperty(completed);
        this.storyPoints = new SimpleDoubleProperty(storyPoints);
        this.parent = parent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public String getDescription() {
        return description.get() == null || description.get().equals("null") ? "" : description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObservableList<Task> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ObservableList<Task> subtasks) {
        this.subtasks = subtasks;
    }

    public String getTask() {
        return task.get();
    }

    public StringProperty taskProperty() {
        return task;
    }

    public void setTask(String task) {
        this.task.set(task);
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public boolean getCompleted() {
        return completed.get();
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    public double getStoryPoints() {
        return storyPoints.get();
    }

    public DoubleProperty storyPointsProperty() {
        return storyPoints;
    }

    public void setStoryPoints(double storyPoints) {
        this.storyPoints.set(storyPoints);
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public void setCompleted(double progress) {
        this.completed.set(progress == 1.0);
        updateParent(progress == 0 ? -this.getProgress() : progress);
        this.setProgress(progress);
    }

    public boolean isLeaf() {
        return subtasks.isEmpty();
    }

    public void mergeTask(Task task) {
        if (this.isLeaf()) {
            this.setCompleted(task.getProgress());
            this.updateStoryPoints(task.getStoryPoints() == 0.0 ? 8.0 : task.getStoryPoints());
            return;
        }
        for (int i = 0; i < this.getSubtasks().size(); i++) {
            Task taskWithId = null;
            for (int j = 0; j < task.getSubtasks().size(); j++) {
                if (task.getSubtasks().get(j).equals(this.getSubtasks().get(i))) {
                    taskWithId = task.getSubtasks().get(j);
                }
            }
            this.getSubtasks().get(i).setStoryPoints(0.0);
            this.getSubtasks().get(i).mergeTask(taskWithId);
        }
    }

    public void updateParent(double percentageToAdd) {
        Task iterator = this;
        while (iterator.parent != null) {
            double percentage = 1.0 / iterator.parent.getSubtasks().size() * (iterator.getSubtasks().isEmpty() ? percentageToAdd : percentageToAdd / iterator.getSubtasks().size());
            iterator.parent.setProgress(iterator.getParent().getProgress() + percentage);
            iterator = iterator.parent;
        }
    }

    public void updateStoryPoints(double newValue) {
        Task iterator = this;
        while (iterator.parent != null) {
            iterator.parent.setStoryPoints(iterator.parent.getStoryPoints() - storyPoints.getValue());
            iterator = iterator.parent;
        }
        iterator = this;
        while (iterator.parent != null) {
            iterator.parent.setStoryPoints(iterator.parent.getStoryPoints() + newValue);
            iterator = iterator.parent;
        }
        this.setStoryPoints(newValue);
    }

    public void anullate() {
        if (this.isLeaf()) {
            this.setCompleted(0.0);
            return;
        }
        for (int i = 0; i < this.getSubtasks().size(); i++) {
            this.setStoryPoints(0.0);
            this.getSubtasks().get(i).anullate();
        }
    }

    @Override
    public boolean equals(Object obj) {
        Task task = (Task) obj;
        return task == null ? false : this.id == task.getId();
    }
}
