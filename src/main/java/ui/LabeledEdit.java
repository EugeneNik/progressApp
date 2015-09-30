package ui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * Created by nikiforov on 24.08.2015.
 */
public class LabeledEdit<T> extends HBox implements Valuable<T> {
    private Text label;
    private TextInputControl field;

    public LabeledEdit(String name, T value) {
        this.label = new Text(name);
        if (value instanceof Integer) {
            this.field = new NumberField((Integer) value);
        } else if (value instanceof String) {
            this.field = new TextField((String) value);
        }
        BorderPane leftAlignment = new BorderPane();
        leftAlignment.setCenter(this.label);
        this.setSpacing(8);
        this.getChildren().addAll(leftAlignment, field);
    }

    @Override
    public void setValue(T value) {
        this.field.setText(value.toString());
    }

    @Override
    public T getValue() {
        return (T) field.getText();
    }
}
