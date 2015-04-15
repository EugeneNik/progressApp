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
    private DoubleProperty progress;
    private BooleanProperty completed;

    public Task(long id, String task, Double progress, Boolean completed, Task parent) {
        this.id = id;
        this.task = new SimpleStringProperty(task);
        this.progress = new SimpleDoubleProperty(progress);
        this.completed = new SimpleBooleanProperty(completed);
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

    public void updateParent(double percentageToAdd) {
        Task iterator = this;
        while (iterator.parent != null) {
            double percentage = 1.0 / iterator.parent.getSubtasks().size() * (iterator.getSubtasks().isEmpty() ? percentageToAdd : percentageToAdd / iterator.getSubtasks().size());
            iterator.parent.setProgress(iterator.getParent().getProgress() + percentage);
            iterator = iterator.parent;
        }
    }

    @Override
    public boolean equals(Object obj) {
        Task task = (Task) obj;
        return task == null ? false : this.id == task.getId();
    }
}
