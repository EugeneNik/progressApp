package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Евгений on 19.08.2015.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserStatisticData {
    private long registerDate;
    private long lastLoginDate;
    private long onlineTime;

    @XmlElementWrapper
    private List<Long> dateHistory;

    public UserStatisticData() {
    }

    public UserStatisticData(long registerDate, long lastLoginDate, long onlineTime) {
        this.registerDate = registerDate;
        this.lastLoginDate = lastLoginDate;
        this.onlineTime = onlineTime;
        this.dateHistory = new ArrayList<>();
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public long getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public List<Long> getDateHistory() {
        return dateHistory;
    }

    public void setDateHistory(List<Long> dateHistory) {
        this.dateHistory = dateHistory;
    }
}
