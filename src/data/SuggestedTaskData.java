package data;

import javafx.beans.property.*;

/**
 * Created by Евгений on 28.06.2015.
 */
public class SuggestedTaskData {
    private BooleanProperty selected;
    private StringProperty name;
    private DoubleProperty storyPoints;
    private StringProperty parentTaskName;
    private DoubleProperty parentTaskComplete;
    private long id;


    public SuggestedTaskData(long id, StringProperty name, DoubleProperty storyPoints, StringProperty parentTaskName, DoubleProperty parentTaskComplete) {
        this.id = id;

        this.name = new SimpleStringProperty();
        this.name.bind(name);

        this.selected = new SimpleBooleanProperty(false);

        this.storyPoints = new SimpleDoubleProperty();
        this.storyPoints.bind(storyPoints);

        this.parentTaskName = new SimpleStringProperty();
        this.parentTaskName.bind(parentTaskName);

        this.parentTaskComplete = new SimpleDoubleProperty();
        this.parentTaskComplete.bind(parentTaskComplete);
    }

    public boolean getSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

    public String getParentTaskName() {
        return parentTaskName.get();
    }

    public StringProperty parentTaskNameProperty() {
        return parentTaskName;
    }

    public void setParentTaskName(String parentTaskName) {
        this.parentTaskName.set(parentTaskName);
    }

    public double getParentTaskComplete() {
        return parentTaskComplete.get();
    }

    public DoubleProperty parentTaskCompleteProperty() {
        return parentTaskComplete;
    }

    public void setParentTaskComplete(double parentTaskComplete) {
        this.parentTaskComplete.set(parentTaskComplete);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
