package common.service.custom;

import common.FileNamespace;
import common.achievements.base.SystemAchievement;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.AbstractService;
import common.service.base.Services;
import data.UserProfile;
import data.UserStatisticData;
import jaxb.utils.JaxbUnmarshaller;
import org.apache.commons.lang.time.DateUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Евгений on 20.08.2015.
 */
public class UserService extends AbstractService {

    UserProfile profile = null;

    @Override
    protected void customInitialization() {
    }

    private void initTimer() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long prevTime = profile.getUserStatisticData().getLastLoginDate();
                long lastAnalyze = profile.getUserStatisticData().getLastLoginDate();
                while (true) {
                    profile.getUserStatisticData().setOnlineTime(profile.getUserStatisticData().getOnlineTime() + Calendar.getInstance().getTimeInMillis() - prevTime);
                    prevTime = Calendar.getInstance().getTimeInMillis();
                    if (prevTime - lastAnalyze > (Integer) PropertyManager.getValue(PropertyNamespace.SYSTEM_ACHIEVEMENT_ANALYZE_FREQUENCY)) {
                        Services.get(AchievementService.class).retest(SystemAchievement.class);
                        lastAnalyze = Calendar.getInstance().getTimeInMillis();
                    }
                }
            }
        };
        timer.schedule(task, new Date(Calendar.getInstance().getTimeInMillis() + (Integer) PropertyManager.getValue(PropertyNamespace.SCHEDULER_LAG_TIMEOUT)));
    }

    public boolean authenticate(String login, String password) {
        if (login.equals("admin") && password.equals("admin")) {
            File file = new File(FileNamespace.USER_PROFILE);
            if (file.exists()) {
                profile = JaxbUnmarshaller.unmarshall(FileNamespace.USER_PROFILE, UserProfile.class);
                profile.getUserStatisticData().setLastLoginDate(Calendar.getInstance().getTimeInMillis());
            } else {
                profile = new UserProfile(login, password, new UserStatisticData(Calendar.getInstance().getTimeInMillis(), Calendar.getInstance().getTimeInMillis(), 0));
            }

            syncProfile();
            initTimer();
            return true;
        }
        return false;
    }

    public void syncProfile() {
        long time = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH).getTimeInMillis();
        if (!profile.getUserStatisticData().getDateHistory().contains(time)) {
            profile.getUserStatisticData().getDateHistory().add(time);
        }
    }

    public UserProfile getProfile() {
        return profile;
    }
}
