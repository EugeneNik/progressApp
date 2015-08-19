package common.achievements.custom.achievements;

import common.FileNamespace;
import common.service.base.Services;
import common.service.custom.UserService;
import data.UserProfile;

import java.util.concurrent.TimeUnit;

/**
 * Created by Евгений on 20.08.2015.
 */
public class MinutesOnline10Achievement extends MinutesOnlineAchievement {

    public MinutesOnline10Achievement() {
        super();
        this.setTooltip("Spend 10 minutes online");
        this.setImagePath(FileNamespace.MINUTES_ONLINE_10);
    }

    @Override
    protected boolean calcResult() {
        UserProfile profile = Services.get(UserService.class).getProfile();
        return profile.getUserStatisticData().getOnlineTime() >= TimeUnit.MINUTES.toMillis(10);
    }
}
