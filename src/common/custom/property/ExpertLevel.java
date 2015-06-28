package common.custom.property;

/**
 * Created by Евгений on 28.06.2015.
 */
public enum ExpertLevel {
    LOW(.0, "Low"),
    BASE(.1, "Base"),
    MEDIUM(.15, "Medium"),
    HIGH(.2, "High");

    private double expertIntensity;
    private String name;

    ExpertLevel(double intensity, String name) {
        this.expertIntensity = intensity;
        this.name = name;
    }

    public double getExpertIntensity() {
        return expertIntensity;
    }

    public String getName() {
        return name;
    }

    public static ExpertLevel getByIntensity(double expertIntensity) {
        for (ExpertLevel level : values()) {
            if (level.getExpertIntensity() == expertIntensity) {
                return level;
            }
        }
        return null;
    }

    public static ExpertLevel getByName(String name) {
        for (ExpertLevel level : values()) {
            if (level.getName().equals(name)) {
                return level;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
