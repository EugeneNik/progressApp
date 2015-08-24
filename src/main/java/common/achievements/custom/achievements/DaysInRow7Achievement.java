package common.achievements.custom.achievements;

import common.FileNamespace;
import common.achievements.custom.base.DaysInRowAchievement;
import common.service.base.Services;
import common.service.custom.UserService;
import data.UserProfile;

import java.util.concurrent.TimeUnit;

/**
 * Created by nikiforov on 20.08.2015.
 */
public class DaysInRow7Achievement extends DaysInRowAchievement {

    public DaysInRow7Achievement() {
        super();
        this.setTooltip("7 Days in a row");
        this.setImagePath(FileNamespace.DAYS_IN_ROW_7);
    }

    @Override
    public boolean calcResult() {
        UserProfile profile = Services.get(UserService.class).getProfile();
        Services.get(UserService.class).syncProfile();

        if (profile.getUserStatisticData().getDateHistory().isEmpty()) {
            return false;
        }
        int count = 0;
        for (int i = profile.getUserStatisticData().getDateHistory().size() - 2; i >= 0; i--) {
            if (profile.getUserStatisticData().getDateHistory().get(i + 1) - profile.getUserStatisticData().getDateHistory().get(i) == TimeUnit.DAYS.toMillis(1)) {
                count++;
            }
        }
        return count >= 7;
    }
}
