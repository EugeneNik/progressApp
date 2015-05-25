package common.service;

import common.SystemConstants;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import data.Task;

import java.util.*;

/**
 * Created by Евгений on 18.05.2015.
 */
public class SuggestionService implements Service {

    private Task tree;
    private Timer timerToNextStart = null;
    ServiceListener listener;
    private double maxStoryPoints = 30;


    public SuggestionService(Task root) {
        if (!ServiceCache.isInited(getClass())) {
            ServiceCache.init(getClass());
            customInitialization(root);
        } else {
            throw new UnsupportedOperationException("Suggestion Service is initialized use Services.get");
        }
    }

    private void customInitialization(Task root) {

        //read max story points (will be calculated according last x periods)

        long lastStart = PropertyManager.getValue(PropertyNamespace.LAST_ANALYZATION_MADE);
        int frequency = PropertyManager.getValue(PropertyNamespace.ANALYZER_FREQUENCY);

        timerToNextStart = new Timer();
        listener = new SuggestionListener();
        timerToNextStart.schedule(new TimerTask() {
            @Override
            public void run() {
                suggest();
            }
        }, new Date(lastStart + frequency * SystemConstants.MILLIS_IN_DAY));

        this.tree = root;
    }

    public List<Task> suggest() {
        List<Task> nominees = calcNominees(tree, new ArrayList<>());
        HashMap<Task, Double> taskToStoryPoint = new HashMap<>();
        double currentList = 0;
        List<Task> suggestions = new ArrayList<>();
        for (Task nominee : nominees) {
            taskToStoryPoint.put(nominee, nominee.getStoryPoints() - nominee.getStoryPoints() * nominee.getProgress());
            if (nominee.getProgress() > 0.0) {
                currentList += nominee.getStoryPoints() - nominee.getStoryPoints() * nominee.getProgress();
                suggestions.add(nominee);
            }
        }
        for (Task task : taskToStoryPoint.keySet()) {
            if (task.getStoryPoints() + currentList <= maxStoryPoints && task.getStoryPoints() + currentList <= maxStoryPoints + maxStoryPoints * 0.1) {
                currentList += task.getStoryPoints();
                suggestions.add(task);
            }
        }
        PropertyManager.setValue(PropertyNamespace.LAST_ANALYZATION_MADE, Calendar.getInstance().getTimeInMillis());
        System.out.println(suggestions.toString());
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
