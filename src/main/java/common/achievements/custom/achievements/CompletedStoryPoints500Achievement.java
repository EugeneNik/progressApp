package common.achievements.custom.achievements;

import common.FileNamespace;
import data.Task;

/**
 * Created by nikiforov on 12.08.2015.
 */
public class CompletedStoryPoints500Achievement extends CompletedStoryPointsAchievement {
    public CompletedStoryPoints500Achievement(Task task) {
        super(task);
        this.setTooltip("Complete 500 Story Points");
        this.setImagePath(FileNamespace.COMPLETED_STORY_POINTS_500);
    }

    @Override
    protected boolean calcResult() {
        return countOfCompleted >= 500.0;
    }
}
