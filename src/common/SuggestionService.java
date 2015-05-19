package common;

import data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Евгений on 18.05.2015.
 */
public class SuggestionService {

    private Task tree;
    private double maxStoryPoints = 30;

    public SuggestionService(Task root) {
        //read last start
        //read period duration
        //read max story points (will be calculated according last x periods)
        //create timer to next
        this.tree = root;
    }

    public List<Task> suggest() {
        List<Task> nominees = calcNominees(tree, new ArrayList<>());
        HashMap<Task, Double> taskToStoryPoint = new HashMap<>();
        double currentList = 0;
        List<Task> suggestions = new ArrayList<>();
        for (Task nominee : nominees) {
            taskToStoryPoint.put(nominee, nominee.getStoryPoints() - nominee.getStoryPoints()*nominee.getProgress());
            if (nominee.getProgress() > 0.0) {
                currentList += nominee.getStoryPoints() - nominee.getStoryPoints()*nominee.getProgress();
                suggestions.add(nominee);
            }
        }
        for (Task task : taskToStoryPoint.keySet()) {
            if (task.getStoryPoints() + currentList <= maxStoryPoints + maxStoryPoints * 0.1) {
                currentList += task.getStoryPoints();
                suggestions.add(task);
            }
        }
        return suggestions;
    }

    private List<Task> calcNominees(Task task, List<Task> list) {
        for (int i = 0; i < task.getSubtasks().size(); i++) {
            if (task.getSubtasks().get(i).isLeaf() && !task.getSubtasks().get(i).getCompleted() && !task.getSubtasks().get(i).getTask().endsWith(":")) {
                list.add(task.getSubtasks().get(i));
            }
            list = calcNominees(task.getSubtasks().get(i), list);
        }
        return list;
    }
}
