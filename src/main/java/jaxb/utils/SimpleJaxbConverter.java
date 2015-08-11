package jaxb.utils;

import data.Task;
import jaxb.TaskJAXB;

/**
 * Created by nikiforov on 26.05.2015.
 */
public class SimpleJaxbConverter {
    public static <T> T convert(Class<T> clazz) {
        if (clazz.equals(TaskJAXB.class)) {
            return (T) JaxbConverter.convertToJaxb(new Task("My progress".hashCode(), "My progress", 1L, 0.0, 0.0, false, null));
        }
        return null;
    }
}
