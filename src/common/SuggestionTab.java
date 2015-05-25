package common;

import data.Task;
import javafx.scene.control.Tab;

import java.util.List;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class SuggestionTab extends Tab {

    public SuggestionTab(Task root) {

        SuggestionService suggestionService = new SuggestionService(root);
//        List<Task> tasks = suggestionService.suggest();

        this.setText("Suggestions");
        this.setClosable(false);
    }
}
