package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Евгений on 19.08.2015.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserProfile {
    private String login;
    private String password;
    private UserStatisticData userStatisticData;

    public UserProfile() {
    }

    public UserProfile(String login, String password, UserStatisticData userStatisticData) {
        this.login = login;
        this.password = password;
        this.userStatisticData = userStatisticData;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatisticData getUserStatisticData() {
        return userStatisticData;
    }

    public void setUserStatisticData(UserStatisticData userStatisticData) {
        this.userStatisticData = userStatisticData;
    }
}
