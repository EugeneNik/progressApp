package common.achievements.custom.achievements;

import common.FileNamespace;
import common.achievements.custom.base.DaysFromStartAchievement;
import common.service.base.Services;
import common.service.custom.UserService;
import data.UserProfile;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by nikiforov on 20.08.2015.
 */
public class DaysFromStart365Achievement extends DaysFromStartAchievement {

    public DaysFromStart365Achievement() {
        super();
        this.setImagePath(FileNamespace.DAYS_FROM_START_365);
        this.setTooltip("Spend year on app");
    }

    @Override
    public boolean calcResult() {
        UserProfile profile = Services.get(UserService.class).getProfile();
        return Calendar.getInstance().getTimeInMillis() - profile.getUserStatisticData().getRegisterDate() > TimeUnit.DAYS.toMillis(365);
    }
}
