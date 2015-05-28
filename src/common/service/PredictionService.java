package common.service;

import common.FileNamespace;
import common.SystemConstants;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import jaxb.HistoriesJAXB;
import jaxb.HistoryJAXB;
import jaxb.utils.JaxbMarshaller;
import jaxb.utils.JaxbUnmarshaller;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Евгений on 25.05.2015.
 */
public class PredictionService implements Service {

    private int lastUsedPeriods;
    private int difficultyOfLearning;
    private int periodOfLearning;
    ServiceListener listener;

    public PredictionService() {
        if (!ServiceCache.isInited(getClass())) {
            ServiceCache.init(getClass(), this);
            customInitialization();
        } else {
            throw new UnsupportedOperationException("Prediction Service is initialized use Services.get");
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
        TransPlatformService.getInstance().setHistory(histories);
        double estimate = 0.0;
        if (histories != null) {
            for (int i = 0; i < Math.min(lastUsedPeriods, histories.getHistories().size()); i++) {
                HistoryJAXB history = histories.getHistories().get(i);
                double daysInPeriod = (double) (history.getPeriodEnd() - history.getPeriodStart()) / SystemConstants.MILLIS_IN_DAY;
                double totalLearnedHours = daysInPeriod * difficultyOfLearning;
                double storyPointsPerHour = (history.getEndStoryPoints() - history.getStartStoryPoints()) / totalLearnedHours;
                estimate += storyPointsPerHour;
            }

            estimate /= Math.min(lastUsedPeriods, histories.getHistories().size());
        } else {
            estimate = 1.0;
        }
        estimate *= periodOfLearning * difficultyOfLearning;
        return estimate;
    }

    public void savePredictions() {
        HistoriesJAXB histories = TransPlatformService.getInstance().getHistory();
        if (histories == null) {
            histories = new HistoriesJAXB();
        }
        HistoryJAXB history = new HistoryJAXB();
        history.setPeriodStart(Calendar.getInstance().getTimeInMillis());
        history.setPeriodEnd(history.getPeriodStart());
        history.setStartStoryPoints(TransPlatformService.getInstance().getRoot().getManager().getCompletedStoryPoints());
        history.setEndStoryPoints(history.getStartStoryPoints());
        history.setDifficulty(difficultyOfLearning);
        if (histories.getHistories().size() == lastUsedPeriods) {
            histories.getHistories().remove(0);
        }
        histories.getHistories().add(history);
        JaxbMarshaller.marshall(histories, HistoriesJAXB.class, FileNamespace.HISTORY);
    }

    public void updateHistory() {
        HistoriesJAXB histories = JaxbUnmarshaller.unmarshall(FileNamespace.HISTORY, HistoriesJAXB.class);
        if (histories != null) {
            LinkedList<HistoryJAXB> historyList = new LinkedList<>(histories.getHistories());
            if (!historyList.isEmpty()) {
                historyList.getLast().setEndStoryPoints(TransPlatformService.getInstance().getRoot().getManager().getCompletedStoryPoints());
                historyList.getLast().setPeriodEnd(Calendar.getInstance().getTimeInMillis());

                JaxbMarshaller.marshall(histories, HistoriesJAXB.class, FileNamespace.HISTORY);
            }
        }
    }
}
