package controller;

import common.FileNamespace;
import common.SuggestionTab;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.ServiceListener;
import common.service.base.Services;
import common.service.base.TransPlatformService;
import common.service.custom.SuggestionService;
import data.SuggestedTaskData;
import data.Task;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import jaxb.HistoriesJAXB;
import jaxb.HistoryJAXB;
import jaxb.SuggestedTaskJAXB;
import jaxb.SuggestionsJAXB;
import jaxb.utils.JaxbUnmarshaller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Евгений on 28.06.2015.
 */
public class SuggestionController extends AbstractTabController {
    private SuggestionTab ui;
    private Timer timerToNextStart = null;

    public SuggestionController(SuggestionTab ui) {
        this.ui = ui;
        SuggestionService suggestionService = Services.get(SuggestionService.class);
        suggestionService.addServiceListener(new ServiceListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                long time = PropertyManager.getValue(PropertyNamespace.LAST_ANALYZATION_MADE);
                ui.getLastSuggestionMadeDate().setValue(new Date(time).toString());
            }
        });

        loadSuggestions();
        scheduleNext();
    }

    public void loadSuggestions() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SuggestionsJAXB suggestionsJAXB = Services.get(SuggestionService.class).loadSuggestions();
                ObservableList<SuggestedTaskData> list = FXCollections.observableArrayList();
                for (SuggestedTaskJAXB suggestedTaskJAXB : suggestionsJAXB.getSuggestions()) {
                    list.addAll(DataConverter.convertTaskToSuggestedTask(TransPlatformService.getInstance().getRoot().getManager().find(suggestedTaskJAXB.getId())));
                }
                ui.getTable().getItems().clear();
                ui.getTable().setItems(list);
            }
        });
    }

    public void scheduleNext() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                long lastStart = PropertyManager.getValue(PropertyNamespace.LAST_ANALYZATION_MADE);
                int frequency = PropertyManager.getValue(PropertyNamespace.ANALYZER_FREQUENCY);
                long frequencyUom = PropertyManager.getValue(PropertyNamespace.ANALYZER_FREQUENCY_UOM);
                timerToNextStart = new Timer();
                timerToNextStart.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        List<Task> result = Services.get(SuggestionService.class).suggest();
                        ObservableList<SuggestedTaskData> list = FXCollections.observableArrayList();
                        for (Task task : result) {
                            list.add(DataConverter.convertTaskToSuggestedTask(task));
                        }
                        ui.getTable().setItems(list);
                        scheduleNext();
                    }
                }, new Date(lastStart + frequency * frequencyUom));
                updateChart();
            }
        });
    }

    public void updateChart() {
        HistoriesJAXB histories = TransPlatformService.getInstance().getHistory();
        if (histories == null) {
            histories = JaxbUnmarshaller.unmarshall(FileNamespace.HISTORY, HistoriesJAXB.class);
            if (histories == null) {
                histories = new HistoriesJAXB();
            }
        }
        BarChart<String, Number> chart = ui.getBarChart();

        ui.getBarChart().getData().clear();
        int i = 0;
        for (HistoryJAXB history : histories.getHistories()) {
            if (i == histories.getHistories().size() - 1) {
                break;
            }
            XYChart.Series series = new XYChart.Series();
            series.setName(Integer.toString(++i));
            DateFormat df = new SimpleDateFormat("dd/MM");

            series.getData().add(new XYChart.Data(df.format(new Date(history.getPeriodStart())) + " - " + df.format(new Date(history.getPeriodEnd())), history.getEndStoryPoints() - history.getStartStoryPoints()));
            chart.getData().addAll(series);
        }
    }

}
