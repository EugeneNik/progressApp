package common.service;

import java.util.HashSet;

/**
 * Created by Евгений on 25.05.2015.
 */
public class ServiceCache {
    private static HashSet<Class> cache = new HashSet<>();

    public static void init(Class clazz) {
        cache.add(clazz);
    }

    public static boolean isInited(Class clazz) {
        return cache.contains(clazz);
    }
}
