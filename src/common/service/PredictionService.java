package common.service;

import common.FileNamespace;
import common.SystemConstants;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import jaxb.HistoriesJAXB;
import jaxb.HistoryJAXB;
import jaxb.utils.JaxbUnmarshaller;

/**
 * Created by Евгений on 25.05.2015.
 */
public class PredictionService implements Service {

    private int lastUsedPeriods;
    private int difficultyOfLearning;
    private int periodOfLearning;

    public PredictionService() {
        if (!ServiceCache.isInited(getClass())) {
            ServiceCache.init(getClass());
            customInitialization();
        } else {
            throw new UnsupportedOperationException("Suggestion Service is initialized use Services.get");
        }
    }

    private void customInitialization() {
        this.lastUsedPeriods = PropertyManager.getValue(PropertyNamespace.SAVE_LAST_PERIODS_COUNT);
        this.difficultyOfLearning = PropertyManager.getValue(PropertyNamespace.DIFFICULTY_ON_LEARNING);
        this.periodOfLearning = PropertyManager.getValue(PropertyNamespace.ANALYZER_FREQUENCY);
    }

    /**
     * @return storyPoints
     */
    public double predict() {
        HistoriesJAXB histories = JaxbUnmarshaller.unmarshall(FileNamespace.HISTORY, HistoriesJAXB.class);
        double estimate = 0.0;
        if (histories != null) {
            for (int i = 0; i < Math.min(lastUsedPeriods, histories.getHistories().size()); i++) {
                HistoryJAXB history = histories.getHistories().get(i);
                double daysInPeriod = (double) (history.getPeriodEnd() - history.getPeriodStart()) / SystemConstants.MILLIS_IN_DAY;
                double totalLearnedHours = daysInPeriod * difficultyOfLearning;
                double storyPointsPerHour = history.getCompletedStoryPoints() / totalLearnedHours;
                estimate += storyPointsPerHour;
            }

            estimate /= Math.min(lastUsedPeriods, histories.getHistories().size());
        } else {
            estimate = 1.0;
        }
        estimate *= periodOfLearning * difficultyOfLearning;
        return estimate;
    }
}
