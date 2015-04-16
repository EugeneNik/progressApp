package common.property;

/**
 * Created by Acer on 10.11.2014.
 */

import common.FileNamespace;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Eugene_nik
 */
public class PropertyManager {

    private static BaseProperty<String> APP_KEY;

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
        settingsList.put(PropertyNamespace.APP_KEY, APP_KEY);
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
