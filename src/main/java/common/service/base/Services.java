package common.service.base;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class Services {

    public static <T extends Service> T get(Class<T> service) {
        return TransPlatformService.getInstance().getService(service);
    }
}
