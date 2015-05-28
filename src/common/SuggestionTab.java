package common;

import common.service.SuggestionService;
import javafx.scene.control.Tab;

/**
 * Created by nikiforov on 25.05.2015.
 */
public class SuggestionTab extends Tab {

    public SuggestionTab() {

        SuggestionService suggestionService = new SuggestionService();
//        List<Task> tasks = suggestionService.suggest();

        this.setText("Suggestions");
        this.setClosable(false);
    }
}
