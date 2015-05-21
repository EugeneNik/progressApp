package asana;

import common.property.PropertyManager;
import common.property.PropertyNamespace;
import data.Task;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by DARIA on 17.04.2015.
 */
public class AsanaHelper {
    public static AsanaConnector connector = new AsanaConnector(PropertyManager.getValue(PropertyNamespace.APP_KEY));

    @Deprecated
    public static Task parseAsana(Task root, javafx.concurrent.Task process) {
        JSONArray array = connector.getProjects().getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            JSONObject project = array.getJSONObject(i);
            String themeName = project.getString("name");
            Long id = project.getLong("id");
            themeName += ":";
            Task currentTask = new Task(id, themeName, 0.0, 0.0, false, root);
            int index = root.getSubtasks().indexOf(currentTask);
            if (index < 0) {
                root.getSubtasks().add(currentTask);
            } else {
                currentTask = root.getSubtasks().get(index);
                currentTask.setTask(themeName);
            }
            parseAndFill(currentTask);

        }
        return root;
    }

    public static void parseAndFill(Task root) {
        Task currentTheme = root;
        JSONArray array = connector.getProjectTasks(Long.toString(root.getId())).getJSONArray("data");

        for (int j = 0; j < array.length(); j++) {
            JSONObject object = array.getJSONObject(j);
            String theme = object.getString("name");
            Long id = object.getLong("id");
            JSONObject description = connector.getTaskDescription(Long.toString(id)).getJSONObject("data");
            JSONArray story = connector.getTaskStory(Long.toString(id)).getJSONArray("data");
            String comment = getNotes(description) + getComments(story);
            if (theme.endsWith(":")) {
                Task subTheme = new Task(id, theme, 0.0, 0.0, false, root);
                subTheme.setDescription(comment);
                int index = root.getSubtasks().indexOf(subTheme);
                if (index < 0) {
                    double progresses[] = new double[root.getSubtasks().size()];
                    int i = 0;
                    for (Task task : root.getSubtasks()) {
                        progresses[i++] = task.getProgress();
                        task.reset();
                    }
                    root.getSubtasks().add(subTheme);
                    i = 0;
                    for (Task task : root.getSubtasks()) {
                        if (i == root.getSubtasks().size() - 1) {
                            continue;
                        }
                        task.setCompleted(progresses[i++]);
                    }
                } else {
                    subTheme = root.getSubtasks().get(index);
                    subTheme.setDescription(comment);
                    subTheme.setTask(theme);
                }
                currentTheme = subTheme;
            } else {
                Task task = new Task(id, theme, 8.0, 0.0, false, currentTheme);
                task.setDescription(comment);
                if (currentTheme.getSubtasks().indexOf(task) < 0) {
                    double progresses[] = new double[currentTheme.getSubtasks().size()];
                    int i = 0;
                    for (Task iter : currentTheme.getSubtasks()) {
                        progresses[i++] = iter.getProgress();
                        iter.reset();
                    }
                    currentTheme.getSubtasks().add(task);
                    i = 0;
                    for (Task iter : currentTheme.getSubtasks()) {
                        if (i == currentTheme.getSubtasks().size() - 1) {
                            continue;
                        }
                        iter.setCompleted(progresses[i++]);
                    }
                    task.addStoryPoints(task.getStoryPoints());
                } else {
                    currentTheme.getSubtasks().get(currentTheme.getSubtasks().indexOf(task)).setTask(theme);
                    currentTheme.getSubtasks().get(currentTheme.getSubtasks().indexOf(task)).setDescription(comment);
                }
            }
        }
    }

    private static String getComments(JSONArray story) {
        StringBuilder comment = new StringBuilder();
        for (int i = 0; i < story.length(); i++) {
            JSONObject storyPoint = story.getJSONObject(i);
            if (storyPoint.getString("type").equals("comment")) {
                comment.append(storyPoint.getString("text"));
                comment.append("\n");
            }
        }
        return comment.toString();
    }

    private static String getNotes(JSONObject story) {
        StringBuilder comment = new StringBuilder();
        String notes = story.getString("notes");
        if (!notes.equals("")) {
            comment.append(notes);
            comment.append("\n");
        }
        return comment.toString();
    }

}
