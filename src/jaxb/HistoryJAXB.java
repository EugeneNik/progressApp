package jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by nikiforov on 26.05.2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HistoryJAXB {
    private long periodStart;
    private long periodEnd;
    private long difficulty;
    private double startStoryPoints;
    private double endStoryPoints;

    public HistoryJAXB() {}

    public long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    public long getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(long periodStart) {
        this.periodStart = periodStart;
    }

    public long getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(long periodEnd) {
        this.periodEnd = periodEnd;
    }

    public double getStartStoryPoints() {
        return startStoryPoints;
    }

    public void setStartStoryPoints(double startStoryPoints) {
        this.startStoryPoints = startStoryPoints;
    }

    public double getEndStoryPoints() {
        return endStoryPoints;
    }

    public void setEndStoryPoints(double endStoryPoints) {
        this.endStoryPoints = endStoryPoints;
    }
}
