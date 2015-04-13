package data;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by DARIA on 12.04.2015.
 */
public class Task {

    private StringProperty task;
    private StringProperty description = new SimpleStringProperty();
    private ObservableList<Task> subtasks = FXCollections.observableArrayList();
    private Task parent = null;
    private DoubleProperty progress;
    private BooleanProperty completed;

    public Task(String task, Double progress, Boolean completed, Task parent) {
        this.task = new SimpleStringProperty(task);
        this.progress = new SimpleDoubleProperty(progress);
        this.completed = new SimpleBooleanProperty(completed);
        this.parent = parent;
    }

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public String getDescription() {
        return description.get();
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

    public BooleanProperty completedProperty() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
        this.setProgress(1.0);
        updateParent();
    }

    public boolean isLeaf() {
        return subtasks.isEmpty();
    }

    public void updateParent() {
        Task iterator = this;
        while (iterator.parent != null) {
            double percentage = 1.0 / iterator.parent.getSubtasks().size() * (iterator.getSubtasks().isEmpty() ? 1.0 : 1.0 / iterator.getSubtasks().size());
            iterator.parent.setProgress(iterator.getParent().getProgress() + percentage);
            iterator = iterator.parent;
        }
    }

    public boolean equals(Task task) {
        return this.parent == task.parent && this.task.get().equals(task.getTask());
    }
}
