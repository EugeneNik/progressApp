package common;

import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.Services;
import common.service.custom.PredictionService;
import controller.SuggestionController;
import data.SuggestedTaskData;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import utils.FormatUtils;

import java.util.Date;


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
        completeChart = new BarChart<>(xAxis, yAxis);

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
        topicCol.setCellValueFactory(controller.getTopicColumnCellValueFactory());

        topicCol.setMinWidth(150);

        TableColumn parentCol = new TableColumn("Parent (Completed %)");
        parentCol.setResizable(false);
        parentCol.setCellValueFactory(controller.getParentColumnCellValueFactory());
        parentCol.setMinWidth(150);

        selectedTable.getColumns().addAll(selectedColumn, topicCol, parentCol);

        BorderPane buttonPane = new BorderPane();

        Button changeSelectedButton = new Button("Change suggestion on selected");
        changeSelectedButton.setWrapText(true);
        changeSelectedButton.setOnAction(controller.getOnChangeButtonPressListener());

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

    public TableView<SuggestedTaskData> getTable() {
        return selectedTable;
    }

    public BarChart<String, Number> getBarChart() {
        return completeChart;
    }

    public StringProperty getLastSuggestionMadeDate() {
        return lastSuggestionMadeDate;
    }
}
