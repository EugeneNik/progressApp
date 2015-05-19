package csv;

import data.Task;

import java.io.*;

/**
 * Created by DARIA on 12.04.2015.
 */

@Deprecated
public class CSVHelper {
    public static void saveBackup(String path, Task root) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(path));
            for (Task task : root.getSubtasks()) {
                printChildren(out, task, 1);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

        }
    }

    private static void printChildren(PrintWriter out, Task root, int level) {
        if (root.getSubtasks().isEmpty()) {
            out.println(root.getId() + ";" + root.getTask() + ";" + root.getDescription() + ";" + root.getProgress() + ";" + level);
            return;
        } else {
            out.println(root.getId() + ";" + root.getTask() + ";" + root.getDescription() + ";" + root.getProgress() + ";" + level);
            for (Task task : root.getSubtasks()) {
                printChildren(out, task, level + 1);
            }
        }
    }

    public static Task parseBackup(String path, Task task) {
        File file = new File(path);
        int level = 0;
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String s;
                Task currentTheme = task;
                while ((s = reader.readLine()) != null) {
                    String values[] = s.split(";");
                    Long id = Long.parseLong(values[0]);
                    String theme = values[1];
                    int currentLevel = Integer.parseInt(values[4]);
                    if (currentLevel <= level) {
                        currentTheme = updateToProper(currentTheme, currentLevel, level);
                    }
                    String comments = values[2];
                    double progress = Double.parseDouble(values[3]);
                    Task subTheme = new Task(id, theme, 8.0, progress, progress == 1.0, currentTheme);
                    subTheme.setDescription(comments);
                    currentTheme.getSubtasks().add(subTheme);
                    currentTheme = subTheme;
                    level = currentLevel;
                }
            } catch (IOException exeption) {
                System.out.println(file.getAbsolutePath() + " can't be parsed");
            }
        }
        return task;
    }

    private static Task updateToProper(Task task, int level, int currentLevel) {
        Task iter = task;
        while (level <= currentLevel) {
            iter = iter.getParent();
            level++;
        }
        return iter;
    }
}
