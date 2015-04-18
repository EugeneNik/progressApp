package ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.text.DecimalFormat;

/**
 * Created by Acer on 23.02.2015.
 */
public class StatusBar extends BorderPane {

    private StatusBar self;
    private Text text;
    private Text progressText;
    private ProgressBar progressBar;

    public StatusBar() {
        self = this;
        text = new Text();
        progressBar = new ProgressBar();
        progressText = new Text();

        StackPane pane = new StackPane();
        pane.getChildren().addAll(progressBar, progressText);

        progressBar.setProgress(-1);
        this.setLeft(text);
        this.setRight(pane);
        progressBar.setVisible(false);
        progressBar.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                progressBar.setVisible(newValue != null && newValue.longValue() >= 0);
                DecimalFormat format = new DecimalFormat("#0.00");
                System.out.println(progressBar.getProgress() * 100.0);
                if (!Double.isNaN(progressBar.progressProperty().doubleValue())) {
                    progressText.setText(format.format(progressBar.getProgress() * 100.0) + "%");
                }
                System.out.println("Value changed " + (newValue != null && newValue.longValue() >= 0));
            }
        });
    }

    public StringProperty textProperty() {
        return text.textProperty();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public DoubleProperty progressProperty() {
        return progressBar.progressProperty();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
