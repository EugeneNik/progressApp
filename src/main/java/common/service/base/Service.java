package common.service.base;

/**
 * Created by nikiforov on 25.05.2015.
 */
public interface Service {
    void addServiceListener(ServiceListener listener);
    void removeServiceListener(ServiceListener listener);
}
