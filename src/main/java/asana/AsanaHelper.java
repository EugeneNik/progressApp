package asana;

import com.asana.models.Story;
import common.property.PropertyManager;
import common.property.PropertyNamespace;
import common.service.base.Services;
import common.service.custom.AsanaService;
import data.Task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by DARIA on 17.04.2015.
 */
public class AsanaHelper {
    /**
     * use Asana lib for it
     */
    public static AsanaConnector connector = new AsanaConnector(PropertyManager.getValue(PropertyNamespace.APP_KEY));

    public static void parseAndFill(Task root, double current, double full) throws IOException {
        Task currentTheme = root;
        List<com.asana.models.Task> array = Services.get(AsanaService.class).getClient().tasks.findByProject(Long.toString(root.getId())).execute();
        int length = array.size();
        AsanaService service = Services.get(AsanaService.class);
        int j = 0;
        for (com.asana.models.Task object : array) {
            Services.get(AsanaService.class).updateProgress(current + (double) (j + 1) / length, full);
            if (service.isStopped()) {
                return;
            }
            String theme = new String(object.name.getBytes(), "UTF-8");
            Long id = Long.parseLong(object.id);
            com.asana.models.Task descriptions = Services.get(AsanaService.class).getClient().tasks.findById(object.id).execute();
            List<Story> story = Services.get(AsanaService.class).getClient().stories.findByTask(Long.toString(id)).execute();
            String comment = getNotes(descriptions) + getComments(story);
            if (theme.endsWith(":")) {
                Task subTheme = new Task(id, theme, 1L, 0.0, 0.0, false, 0L, root);
                subTheme.setDescription(comment);
                int index = root.getSubtasks().indexOf(subTheme);
                if (index < 0) {
                    double progresses[] = new double[root.getSubtasks().size()];
                    int i = 0;
                    for (Task task : root.getSubtasks()) {
                        progresses[i++] = task.getProgress();
                        task.getManager().reset();
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
                Task task = new Task(id, theme, 0L, 8.0, 0.0, false, 0L, currentTheme);
                task.setDescription(comment);
                if (currentTheme.getSubtasks().indexOf(task) < 0) {
                    double progresses[] = new double[currentTheme.getSubtasks().size()];
                    int i = 0;
                    for (Task iter : currentTheme.getSubtasks()) {
                        progresses[i++] = iter.getProgress();
                        iter.getManager().reset();
                    }
                    currentTheme.getSubtasks().add(task);
                    i = 0;
                    for (Task iter : currentTheme.getSubtasks()) {
                        if (i == currentTheme.getSubtasks().size() - 1) {
                            continue;
                        }
                        iter.setCompleted(progresses[i++]);
                    }
                    task.getManager().addStoryPoints(task.getStoryPoints());
                } else {
                    currentTheme.getSubtasks().get(currentTheme.getSubtasks().indexOf(task)).setTask(theme);
                    try {
                        currentTheme.getSubtasks().get(currentTheme.getSubtasks().indexOf(task)).setDescription(comment);
                    } catch (Exception e) {
                        System.err.println("Exception on name :" + comment + " where \"" + getNotes(descriptions) + "\" is notes and \"" + getComments(story) + "\" is comment");
                    }
                }
            }
            j++;
        }
    }

    private static String getComments(List<Story> stories) throws UnsupportedEncodingException {
        StringBuilder comment = new StringBuilder();
        for (Story story : stories) {
            if (story.type.equals("comment")) {
                comment.append(new String(story.text.getBytes(), "UTF-8"));
                comment.append("\n");
            }
        }
        return comment.toString();
    }

    private static String getNotes(com.asana.models.Task task) throws UnsupportedEncodingException {
        StringBuilder comment = new StringBuilder();
        String notes = task.notes;
        if (notes != null && !notes.equals("")) {
            comment.append(new String(notes.getBytes(), "UTF-8"));
            comment.append("\n");
        }
        return comment.toString();
    }

}
