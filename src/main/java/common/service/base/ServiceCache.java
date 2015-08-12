package common.service.base;

import java.util.HashSet;

/**
 * Created by Евгений on 25.05.2015.
 */
public class ServiceCache {
    private static HashSet<Class> cache = new HashSet<>();

    public static void init(Class clazz, Service service) {
        cache.add(clazz);
        TransPlatformService.getInstance().initService(service);
    }

    public static boolean isInited(Class clazz) {
        return cache.contains(clazz);
    }
}
