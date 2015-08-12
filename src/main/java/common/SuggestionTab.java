package common;

import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.custom.PredictionService;
import common.service.base.Services;
import common.service.custom.SuggestionService;
import controller.DataConverter;
import controller.SuggestionController;
import data.SuggestedTaskData;
import data.Task;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import utils.FormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by nikiforov on 25.05.2015.
 */
public class SuggestionTab extends Tab {

    private SuggestionController controller;
    private TableView<SuggestedTaskData> selectedTable;
    private final BarChart<String, Number> completeChart;
    private StringProperty lastSuggestionMadeDate;


    public SuggestionTab() {
        selectedTable = new TableView();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        completeChart = new BarChart<String, Number>(xAxis, yAxis);

        controller = new SuggestionController(this);

//        List<Task> tasks = suggestionService.suggest();

        this.setText("Suggestions");
        this.setClosable(false);

        HBox mainLayout = new HBox();

        selectedTable.setEditable(true);

        selectedTable.setMinWidth(380);

        VBox leftColumn = new VBox();
        VBox rightColumn = new VBox();

        leftColumn.setSpacing(8.0);

        Text storyPointsOnLastPeriods = new Text();
        storyPointsOnLastPeriods.textProperty().bind(Bindings.concat("Story Points on last periods: ", Bindings.format(FormatUtils.getProperDoubleFormat(false), Services.get(PredictionService.class).getPrediction())));
        Text lastSuggestionMade = new Text();
        Long lastSuggestion = PropertyManager.getValue(PropertyNamespace.LAST_ANALYZATION_MADE);
        lastSuggestionMadeDate = new SimpleStringProperty(new Date(lastSuggestion).toString());
        lastSuggestionMade.textProperty().bind(Bindings.concat("Last suggestion made: ", lastSuggestionMadeDate));
        Text learningDifficulty = new Text("Learning difficulty: " + PropertyManager.getValue(PropertyNamespace.EXPERT_LEVEL));

        TableColumn selectedColumn = new TableColumn();
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(soCalledFriendBooleanTableColumn -> new CheckBoxTableCell<SuggestedTaskData, BooleanProperty>());

        TableColumn topicCol = new TableColumn("Topic (story points)");
        topicCol.setResizable(false);
        topicCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SuggestedTaskData, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SuggestedTaskData, String> p) {
                return p.getValue().nameProperty().concat(Bindings.format(" (" + FormatUtils.getProperDoubleFormat(false) + ")", p.getValue().storyPointsProperty().multiply(100.0)));
            }
        });

        topicCol.setMinWidth(150);

        TableColumn parentCol = new TableColumn("Parent (Completed %)");
        parentCol.setResizable(false);
        parentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SuggestedTaskData, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SuggestedTaskData, String> p) {
                return p.getValue().parentTaskNameProperty().concat(Bindings.format(" (" + FormatUtils.getProperDoubleFormat(true) + ")", p.getValue().parentTaskCompleteProperty().multiply(100.0)));
            }
        });
        parentCol.setMinWidth(150);

        selectedTable.getColumns().addAll(selectedColumn, topicCol, parentCol);

        BorderPane buttonPane = new BorderPane();

        Button changeSelectedButton = new Button("Change suggestion on selected");
        changeSelectedButton.setWrapText(true);
        changeSelectedButton.setOnAction(event -> {
            List<Task> toChange = new ArrayList<Task>();
            List<Task> alreadyPlanner = new ArrayList<Task>();
            ObservableList<SuggestedTaskData> newList = FXCollections.observableArrayList();
            for (SuggestedTaskData data : selectedTable.getItems()) {
                if (data.getSelected()) {
                    toChange.add(DataConverter.convertSuggestedTaskToTask(data));
                } else {
                    newList.add(data);
                    alreadyPlanner.add(DataConverter.convertSuggestedTaskToTask(data));
                }
            }
            double storyPoints = 0;
            for (Task change : toChange) {
                storyPoints += change.getStoryPoints() - change.getStoryPoints() * change.getProgress();
            }
            List<Task> list = Services.get(SuggestionService.class).suggest(storyPoints, toChange, alreadyPlanner);
            for (Task task :list) {
                newList.add(DataConverter.convertTaskToSuggestedTask(task));
            }

            selectedTable.getItems().clear();
            selectedTable.setItems(newList);

        });

        buttonPane.setRight(changeSelectedButton);

        leftColumn.getChildren().addAll(storyPointsOnLastPeriods, lastSuggestionMade, learningDifficulty, selectedTable, changeSelectedButton);
        rightColumn.getChildren().addAll(completeChart);

        completeChart.setTitle("Last periods");
        xAxis.setLabel("Periods");
        yAxis.setLabel("Completed story points");

        mainLayout.getChildren().addAll(leftColumn, rightColumn);
        this.setContent(mainLayout);

        controller.updateChart();
    }

    public TableView getTable() {
        return selectedTable;
    }

    public BarChart<String, Number> getBarChart() {
        return completeChart;
    }

    public StringProperty getLastSuggestionMadeDate() {
        return lastSuggestionMadeDate;
    }
}
