package common.achievements.custom.achievements;

import common.FileNamespace;
import common.achievements.custom.base.MinutesOnlineAchievement;
import common.service.base.Services;
import common.service.custom.UserService;
import data.UserProfile;

import java.util.concurrent.TimeUnit;

/**
 * Created by Евгений on 20.08.2015.
 */
public class MinutesOnline1440Achievement extends MinutesOnlineAchievement {

    public MinutesOnline1440Achievement() {
        super();
        this.setTooltip("Spend 1 day online");
        this.setImagePath(FileNamespace.MINUTES_ONLINE_1440);
    }

    @Override
    protected boolean calcResult() {
        UserProfile profile = Services.get(UserService.class).getProfile();
        return profile.getUserStatisticData().getOnlineTime() >= TimeUnit.DAYS.toMillis(1);
    }
}
