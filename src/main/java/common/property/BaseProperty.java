package common.property;


/**
 * Created by Acer on 10.11.2014.
 */
public class BaseProperty<T> {
    private String name;
    private T value;
    private T defaultValue;
    private T startValue;

    BaseProperty(String name, T value, T defaultValue) {
        this.name = name;
        this.value = value;
        this.startValue = value;
        this.defaultValue = defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setStartValue(T startValue) {
        this.startValue = startValue;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getStartValue() {
        return startValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }
}