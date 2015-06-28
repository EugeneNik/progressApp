package common.service;

import java.util.List;

/**
 * Created by Евгений on 28.06.2015.
 */
public abstract class AbstractService implements Service {
    protected List<ServiceListener> listeners;

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
}
