package common.property;

/**
 * Created by Acer on 10.11.2014.
 */

import common.FileNamespace;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Eugene_nik
 */
public class PropertyManager {

    /**
     * Key to Asana access
     */
    private static BaseProperty<String> APP_KEY;

    /**
     * timer on sync update
     */
    private static BaseProperty<Integer> TIMER_UPDATE_FREQUENCY;



    //suggestion properties

    /**
     * time in millis when suggestion analyzer started last
     */
    private static BaseProperty<Long> LAST_ANALYZATION_MADE;

    /**
     * frequency in days when analyzer should be launched
     */
    private static BaseProperty<Integer> ANALYZER_FREQUENCY;



    /**
     * map to collect setting name and setting
     */
    public static HashMap<String, BaseProperty> settingsList = new HashMap<>();

    public static void getApplicationSettings() {
        try {
            Properties prop = new Properties();
            try (InputStream is = new FileInputStream(FileNamespace.SETTINGS)) {
                prop.load(is);
                APP_KEY = new BaseProperty<>(PropertyNamespace.APP_KEY, prop.getProperty(PropertyNamespace.APP_KEY, ""), "");
                TIMER_UPDATE_FREQUENCY = new BaseProperty<>(PropertyNamespace.TIMER_UPDATE_FREQUENCY, Integer.parseInt(prop.getProperty(PropertyNamespace.TIMER_UPDATE_FREQUENCY, "5")), 5);
                LAST_ANALYZATION_MADE = new BaseProperty<>(PropertyNamespace.LAST_ANALYZATION_MADE, Long.parseLong(prop.getProperty(PropertyNamespace.LAST_ANALYZATION_MADE, "0")), 0L);
                ANALYZER_FREQUENCY = new BaseProperty<>(PropertyNamespace.ANALYZER_FREQUENCY, Integer.parseInt(prop.getProperty(PropertyNamespace.ANALYZER_FREQUENCY, "7")), 7);
                registerApplicationSettings();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e);
        }
    }

    public static <T> T getValue(String name) {
        if (PropertyManager.settingsList.containsKey(name)) {
            return (T) PropertyManager.settingsList.get(name).getValue();
        }
        return null;
    }

    public static void setValue(String name, Object value) {
        PropertyManager.settingsList.get(name).setValue(value);
        PropertyManager.save();
    }

    private static void registerApplicationSettings() {
        for (Field field : PropertyManager.class.getDeclaredFields()) {
            if (field.getType() == BaseProperty.class) {
                try {
                    String propertyName = PropertyNamespace.class.getField(field.getName()).get(PropertyNamespace.class.getField(field.getName())).toString();
                    settingsList.put(propertyName, (BaseProperty)field.get(null));
                } catch (NoSuchFieldException exception) {
                    System.err.println("Add " + field.getName()+" to PropertyNamespace");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void save() {
        try {
            Properties prop = new Properties();
            try (FileOutputStream fos = new FileOutputStream(FileNamespace.SETTINGS)) {
                settingsList.keySet().stream().filter((propName) -> (settingsList.get(propName) != null))
                        .forEach((propName) -> prop.put(propName, settingsList.get(propName).getValue().toString()));
                prop.store(fos, "Settings");
                fos.flush();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e);
        }
    }

    public static void resetToDefault() {
        settingsList.keySet().stream().filter((name) -> (name != null)).forEach((name) -> {
            settingsList.get(name).setValue(settingsList.get(name).getDefaultValue());
        });
    }

    public static boolean isDefault(String name) {
        if (name != null && settingsList.containsKey(name)) {
            return settingsList.get(name).getValue().equals(settingsList.get(name).getStartValue());
        }
        return true;
    }

    private PropertyManager() {
    }
}
