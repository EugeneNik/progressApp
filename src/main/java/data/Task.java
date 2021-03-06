package data;

import controller.TaskManager;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Calendar;

/**
 * Created by DARIA on 12.04.2015.
 */
public class Task {

    private long id;
    private StringProperty task;
    private StringProperty description = new SimpleStringProperty();
    private ObservableList<Task> subtasks = FXCollections.observableArrayList();
    private Task parent = null;
    private DoubleProperty storyPoints;
    private LongProperty timeEstimated;
    private DoubleProperty progress;
    private BooleanProperty completed;
    private LongProperty completeDate;
    private TaskManager manager;

    public Task(long id, String task, Long timeEstimated, Double storyPoints, Double progress, Boolean completed, Long completeDate, Task parent) {
        this.id = id;
        this.task = new SimpleStringProperty(task);
        this.progress = new SimpleDoubleProperty(progress);
        this.completed = new SimpleBooleanProperty(completed);
        this.storyPoints = new SimpleDoubleProperty(storyPoints);
        this.timeEstimated = new SimpleLongProperty(timeEstimated);
        this.completeDate = new SimpleLongProperty(completeDate);
        this.parent = parent;
        this.manager = new TaskManager(this);
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

    public long getTimeEstimated() {
        return timeEstimated.get();
    }

    public LongProperty timeEstimatedProperty() {
        return timeEstimated;
    }

    public void setTimeEstimated(long timeEstimated) {
        this.timeEstimated.set(timeEstimated);
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

    public long getCompleteDate() {
        return completeDate.get();
    }

    public LongProperty completeDateProperty() {
        return completeDate;
    }

    public void setCompleteDate(long completeDate) {
        this.completeDate.set(completeDate);
    }

    public void setCompleted(double progress) {
        if (progress != 0.0) {
            manager.reset();
        }
        this.completed.set(progress == 1.0);
        if (progress == 1.0) {
            this.completeDate.set(Calendar.getInstance().getTimeInMillis());
        } else {
            this.completeDate.set(0);
        }
        manager.updateParent(progress == 0 ? -this.getProgress() : progress);
        this.setProgress(progress);
    }

    public boolean isLeaf() {
        return subtasks.isEmpty();
    }

    public TaskManager getManager() {
        return manager;
    }

    @Override
    public boolean equals(Object obj) {
        Task task = (Task) obj;
        return task != null && this.id == task.getId();
    }

    @Override
    public String toString() {
        return "[" + getTask() + "," + getStoryPoints() + "," + getProgress() + "]\n";
    }
}
