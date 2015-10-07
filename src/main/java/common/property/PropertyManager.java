package common.property;

/**
 * Created by Acer on 10.11.2014.
 */

import common.FileNamespace;
import common.custom.property.ExpertLevel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Eugene_nik
 */
public class PropertyManager {

    /**
     * Key to Asana access
     * Internal property
     */
    private static BaseProperty<String> APP_KEY;

    /**
     * timer on sync update
     * Internal property
     */
    private static BaseProperty<Integer> TIMER_UPDATE_FREQUENCY;


    //suggestion properties

    /**
     * time in millis when suggestion analyzer started last
     * Internal property
     */
    private static BaseProperty<Long> LAST_ANALYZATION_MADE;

    /**
     * frequency in days when analyzer should be launched
     * External property
     */
    private static BaseProperty<Integer> ANALYZER_FREQUENCY;

    /**
     * number of last periods that should be saved for analyzer
     * External property
     */
    private static BaseProperty<Integer> SAVE_LAST_PERIODS_COUNT;

    /**
     * hours that should be spent in learning
     * e.g. period = 7 days, this param = 2 hours, so 14 hours of learning.
     * use it to calc speed it should influence to dynamic story points border
     * External property
     */
    private static BaseProperty<Integer> DIFFICULTY_ON_LEARNING;

    /**
     * parameter for user, how much overwork he can do
     * External property
     *
     * @see ExpertLevel
     */
    private static BaseProperty<ExpertLevel> EXPERT_LEVEL;

    /**
     * unit of time when update
     * External property
     */
    private static BaseProperty<Long> ANALYZER_FREQUENCY_UOM;


    /**
     * parameter for user, set number of digits that will be printed after point
     * External property
     */
    private static ObservableBaseProperty<Property<Number>, Number> DIGITS_AFTER_POINTS;

    /**
     * property to make auto syncs
     * External property
     */
    private static BaseProperty<String> AUTO_SYNC_PROPERTY;

    /**
     * property to make achievement analyze
     * Internal property
     */
    private static BaseProperty<Integer> SYSTEM_ACHIEVEMENT_ANALYZE_FREQUENCY;

    /**
     * property to set lag between user service init and start of timer running
     * Internal property
     */
    private static BaseProperty<Integer> SCHEDULER_LAG_TIMEOUT;

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
                SAVE_LAST_PERIODS_COUNT = new BaseProperty<>(PropertyNamespace.SAVE_LAST_PERIODS_COUNT, Integer.parseInt(prop.getProperty(PropertyNamespace.SAVE_LAST_PERIODS_COUNT, "3")), 3);
                DIFFICULTY_ON_LEARNING = new BaseProperty<>(PropertyNamespace.DIFFICULTY_ON_LEARNING, Integer.parseInt(prop.getProperty(PropertyNamespace.DIFFICULTY_ON_LEARNING, "2")), 2);
                EXPERT_LEVEL = new BaseProperty<>(PropertyNamespace.EXPERT_LEVEL, ExpertLevel.getByName(prop.getProperty(PropertyNamespace.EXPERT_LEVEL, "Base")), ExpertLevel.BASE);
                ANALYZER_FREQUENCY_UOM = new BaseProperty<>(PropertyNamespace.ANALYZER_FREQUENCY_UOM, Long.parseLong(prop.getProperty(PropertyNamespace.ANALYZER_FREQUENCY_UOM, Long.toString(TimeUnit.DAYS.toMillis(1)))), TimeUnit.DAYS.toMillis(1));
                DIGITS_AFTER_POINTS = new ObservableBaseProperty<>(PropertyNamespace.DIGITS_AFTER_POINTS, new SimpleIntegerProperty(Integer.parseInt(prop.getProperty(PropertyNamespace.DIGITS_AFTER_POINTS, "2"))), 2);
                AUTO_SYNC_PROPERTY = new BaseProperty<>(PropertyNamespace.AUTO_SYNC_PROPERTY, prop.getProperty(PropertyNamespace.AUTO_SYNC_PROPERTY, "03:00:00"), "03:00:00");
                SYSTEM_ACHIEVEMENT_ANALYZE_FREQUENCY = new BaseProperty<>(PropertyNamespace.SYSTEM_ACHIEVEMENT_ANALYZE_FREQUENCY, Integer.parseInt(prop.getProperty(PropertyNamespace.SYSTEM_ACHIEVEMENT_ANALYZE_FREQUENCY, "10000")), 10000);
                SCHEDULER_LAG_TIMEOUT = new BaseProperty<>(PropertyNamespace.SCHEDULER_LAG_TIMEOUT, Integer.parseInt(prop.getProperty(PropertyNamespace.SCHEDULER_LAG_TIMEOUT, "5000")), 5000);
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

    public static <T> BaseProperty<T> getProperty(String name) {
        if (PropertyManager.settingsList.containsKey(name)) {
            return (BaseProperty<T>) PropertyManager.settingsList.get(name);
        }
        return null;
    }

    public static <T extends Property<E>, E> ObservableBaseProperty<T, E> getObservableProperty(String name) {
        if (PropertyManager.settingsList.containsKey(name)) {
            return (ObservableBaseProperty<T, E>) PropertyManager.settingsList.get(name);
        }
        return null;
    }


    public static void setValue(String name, Object value) {
        PropertyManager.settingsList.get(name).setValue(value);
        PropertyManager.save();
    }

    private static void registerApplicationSettings() {
        for (Field field : PropertyManager.class.getDeclaredFields()) {
            if (field.getType() == BaseProperty.class || field.getType() == ObservableBaseProperty.class) {
                try {
                    String propertyName = PropertyNamespace.class.getField(field.getName()).get(PropertyNamespace.class.getField(field.getName())).toString();
                    settingsList.put(propertyName, (BaseProperty) field.get(null));
                } catch (NoSuchFieldException exception) {
                    System.err.println("Add " + field.getName() + " to PropertyNamespace");
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
            return settingsList.get(name).getValue().equals(settingsList.get(name).getDefaultValue());
        }
        return true;
    }

    private PropertyManager() {
    }
}
