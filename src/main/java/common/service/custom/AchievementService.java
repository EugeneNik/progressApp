package common.service.custom;

import common.achievements.Achievement;
import common.achievements.AchievementStatus;
import common.achievements.custom.achievements.*;
import common.service.base.AbstractService;
import common.service.base.TransPlatformService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class AchievementService extends AbstractService {

    private List<Achievement> achievementAnalyzers;

    protected void customInitialization() {
        listeners = new ArrayList<>();
        achievementAnalyzers = new ArrayList<>();
        achievementAnalyzers.add(new CompletedTask1Achievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedTask10Achievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedTask20Achievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedTask50Achievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedTask100Achievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedTask1000Achievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedTaskAllAchievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedStoryPoints500Achievement(TransPlatformService.getInstance().getRoot()));
        achievementAnalyzers.add(new CompletedStoryPoints1000Achievement(TransPlatformService.getInstance().getRoot()));
    }

    public List<Achievement> getRegisteredAchievements() {
        return achievementAnalyzers;
    }

    public void retest() {
        onStart();
        retest(false, achievementAnalyzers);
        onFinish();
    }

    public <T> void retest(Class<T> clazz) {
        onStart();
        List<Achievement> list = new ArrayList<>();
        for (Achievement achievement : achievementAnalyzers) {
            if (clazz.isInstance(achievement)) {
                list.add(achievement);
            }
        }
        retest(false, list);
        onFinish();
    }

    public void retest(List<Achievement> group) {
        retest(true, group);
    }

    public AchievementStatus retest(Achievement achievement) {
        return achievement.retest();
    }

    private void retest(boolean follow, List<Achievement> group) {
        if (follow) onStart();
        for (Achievement achievement : group) {
            retest(false, achievement);
        }
        if (follow) onFinish();
    }


    private AchievementStatus retest(boolean follow, Achievement achievement) {
        if (follow) onStart();
        AchievementStatus status = achievement.retest();
        if (follow) onFinish();
        return status;
    }
}
