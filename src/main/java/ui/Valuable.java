package ui;

/**
 * Created by nikiforov on 24.08.2015.
 */
public interface Valuable<T> {

    void setValue(T value);

    T getValue();

    Class<T> getClazz();
}
