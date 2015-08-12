package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedStoryPoints1000Achievement extends CompletedStoryPointsAchievement{
    public CompletedStoryPoints1000Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 1000 Story Points");
        this.setImagePath(FileNamespace.COMPLETED_STORY_POINTS_1000);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 1000.0;
    }
}
