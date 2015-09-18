package common.service.custom;

import common.FileNamespace;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.AbstractService;
import common.service.base.TransPlatformService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import jaxb.HistoriesJAXB;
import jaxb.HistoryJAXB;
import jaxb.utils.JaxbMarshaller;
import jaxb.utils.JaxbUnmarshaller;

import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by Евгений on 25.05.2015.
 */
public class PredictionService extends AbstractService {

    private int lastUsedPeriods;
    private int difficultyOfLearning;
    private int periodOfLearning;
    private DoubleProperty prediction;

    @Override
    protected void customInitialization() {
        this.lastUsedPeriods = PropertyManager.getValue(PropertyNamespace.SAVE_LAST_PERIODS_COUNT);
        // if last used periods = 3 it means that we should use 3 last periods and skip fourth (current one)
        lastUsedPeriods++;
        this.difficultyOfLearning = PropertyManager.getValue(PropertyNamespace.DIFFICULTY_ON_LEARNING);
        this.periodOfLearning = PropertyManager.getValue(PropertyNamespace.ANALYZER_FREQUENCY);
    }

    /**
     * @return storyPoints
     */
    public double predict() {

        HistoriesJAXB histories = TransPlatformService.getInstance().getHistory();
        if (histories == null) {
            histories = JaxbUnmarshaller.unmarshall(FileNamespace.HISTORY, HistoriesJAXB.class);
            TransPlatformService.getInstance().setHistory(histories);
        }
        long analyzerUOM = PropertyManager.getValue(PropertyNamespace.ANALYZER_FREQUENCY_UOM);
        double estimate = 0.0;
        if (histories != null) {
            for (int i = 0; i < Math.min(lastUsedPeriods - 1, histories.getHistories().size() - 1); i++) {
                HistoryJAXB history = histories.getHistories().get(i);
                double unitsInPeriod = (double) (history.getPeriodEnd() - history.getPeriodStart()) / analyzerUOM;
                double totalLearnedUnits = unitsInPeriod * difficultyOfLearning;
                double storyPointsPerUnit = (history.getEndStoryPoints() - history.getStartStoryPoints()) / totalLearnedUnits;
                estimate += storyPointsPerUnit;
            }
            if (Math.min(lastUsedPeriods - 1, histories.getHistories().size() - 1) != 0) {
                estimate /= Math.min(lastUsedPeriods - 1, histories.getHistories().size() - 1);
            }
        } else {
            estimate = 1.0;
        }
        estimate *= periodOfLearning * difficultyOfLearning;
        prediction.set(estimate);
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
        HistoriesJAXB histories = TransPlatformService.getInstance().getHistory();
        if (histories != null) {
            LinkedList<HistoryJAXB> historyList = new LinkedList<>(histories.getHistories());
            if (!historyList.isEmpty()) {
                historyList.getLast().setEndStoryPoints(TransPlatformService.getInstance().getRoot().getManager().getCompletedStoryPoints());
                historyList.getLast().setPeriodEnd(Calendar.getInstance().getTimeInMillis());

                JaxbMarshaller.marshall(histories, HistoriesJAXB.class, FileNamespace.HISTORY);
            }
        }
    }

    public DoubleProperty getPrediction() {
        if (prediction == null) {
            prediction = new SimpleDoubleProperty();
            predict();
        }
        return prediction;
    }
}
