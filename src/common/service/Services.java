package common.service;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class Services {

    public <T extends Service> T get(Class<T> service) {
        return TransPlatformService.getInstance().getService(service);
    }
}
