package common.property;

import javafx.beans.property.Property;

/**
 * Created by Евгений on 07.10.2015.
 */
public class ObservableBaseProperty<T extends Property<E>, E> extends BaseProperty {

    private Property<E> property;

    ObservableBaseProperty(String name, Property<E> property, E defaultValue) {
        super(name, property.getValue(), defaultValue);
        this.property = property;
    }

    public T getProperty() {
        return (T) property;
    }

    @Override
    public E getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(Object value) {
        property.setValue((E) value);
    }
}
