package common.service.custom;

import common.FileNamespace;
import common.custom.property.ExpertLevel;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.AbstractService;
import common.service.base.Services;
import common.service.base.TransPlatformService;
import data.Task;
import jaxb.SuggestedTaskJAXB;
import jaxb.SuggestionsJAXB;
import jaxb.utils.JaxbMarshaller;
import jaxb.utils.JaxbUnmarshaller;

import java.util.*;

/**
 * Created by Евгений on 18.05.2015.
 */
public class SuggestionService extends AbstractService {
    protected void customInitialization() {
    }

    public List<Task> suggest() {
        onStart();
        PredictionService predictionService = Services.get(PredictionService.class);
        predictionService.updateHistory();
        predictionService.savePredictions();

        double maxStoryPoints = predictionService.predict();

        List<Task> suggestions = suggest(maxStoryPoints, new ArrayList<>(), new ArrayList<>());

        log.info("Predicted story points:" + maxStoryPoints);
        PropertyManager.setValue(PropertyNamespace.LAST_ANALYZATION_MADE, Calendar.getInstance().getTimeInMillis());
        log.info(suggestions.toString());
        onFinish();
        return suggestions;
    }

    public List<Task> suggest(double maxStoryPoints, List<Task> toIgnoreList, List<Task> toForbidden) {
        List<Task> suggestions = new ArrayList<>();
        List<Task> nominees = calcNominees(TransPlatformService.getInstance().getRoot(), new ArrayList<>());
        Collections.shuffle(nominees);
        HashMap<Task, Double> taskToStoryPoint = new HashMap<>();
        double currentList = 0;
        for (Task nominee : nominees) {
            taskToStoryPoint.put(nominee, nominee.getStoryPoints() - nominee.getStoryPoints() * nominee.getProgress());
            if (nominee.getProgress() > 0.0) {
                if (toIgnoreList.contains(nominee) || toForbidden.contains(nominee)) {
                    continue;
                }
                currentList += nominee.getStoryPoints() - nominee.getStoryPoints() * nominee.getProgress();
                suggestions.add(nominee);
            }
        }
        ExpertLevel level = PropertyManager.getValue(PropertyNamespace.EXPERT_LEVEL);
        for (Task task : taskToStoryPoint.keySet()) {
            if (toIgnoreList.contains(task) || toForbidden.contains(task)) {
                continue;
            }
            if (currentList <= maxStoryPoints && taskToStoryPoint.get(task) + currentList <= maxStoryPoints + maxStoryPoints * level.getExpertIntensity()) {
                currentList += taskToStoryPoint.get(task);
                suggestions.add(task);
            }
        }
        for (Task ignored : toIgnoreList) {
            if (currentList <= maxStoryPoints && taskToStoryPoint.get(ignored) + currentList <= maxStoryPoints + maxStoryPoints * level.getExpertIntensity()) {
                currentList += taskToStoryPoint.get(ignored);
                suggestions.add(ignored);
            }
        }
        if (toIgnoreList.size() != 0 || toForbidden.size() != 0) {
            log.info("Replaned story points:" + maxStoryPoints);
            log.info(suggestions.toString());
        }
        saveSuggestions(suggestions);
        return suggestions;
    }

    public SuggestionsJAXB loadSuggestions() {
        SuggestionsJAXB suggestions = JaxbUnmarshaller.unmarshall(FileNamespace.SUGGESTIONS, SuggestionsJAXB.class);
        if (suggestions == null) {
            suggestions = new SuggestionsJAXB();
        }
        return suggestions;
    }

    private void saveSuggestions(List<Task> suggestions) {
        SuggestionsJAXB suggestionsJAXB = new SuggestionsJAXB();
        for (Task task : suggestions) {
            SuggestedTaskJAXB suggestedTaskJAXB = new SuggestedTaskJAXB();
            suggestedTaskJAXB.setId(task.getId());
            suggestionsJAXB.getSuggestions().add(suggestedTaskJAXB);
        }
        JaxbMarshaller.marshall(suggestionsJAXB, SuggestionsJAXB.class, FileNamespace.SUGGESTIONS);
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
