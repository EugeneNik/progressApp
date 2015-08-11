package common.service;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Created by Евгений on 28.06.2015.
 */
public abstract class AbstractService implements Service {
    protected List<ServiceListener> listeners;
    protected DoubleProperty progress = new SimpleDoubleProperty();
    protected StringProperty status = new SimpleStringProperty();

    @Override
    public void addServiceListener(ServiceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeServiceListener(ServiceListener listener) {
        listeners.add(listener);
    }

    protected void onStart() {
        for (ServiceListener listener : listeners) {
            listener.onStart();
        }
    }

    protected void onFinish() {
        for (ServiceListener listener : listeners) {
            listener.onFinish();
        }
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

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
