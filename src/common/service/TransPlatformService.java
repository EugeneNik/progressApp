package common.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class TransPlatformService {
    private Map<Class<? extends Service>, Service> serviceMap;
    private static final TransPlatformService self = new TransPlatformService();

    public TransPlatformService() {
        this.serviceMap = new HashMap<>();
    }

    public static TransPlatformService getInstance() {
        return self;
    }

    public <T extends Service> T  getService(Class<T> service) {
        return (T) serviceMap.get(service);
    }
}
