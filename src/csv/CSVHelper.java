package csv;

import data.Task;

import java.io.*;

/**
 * Created by DARIA on 12.04.2015.
 */
public class CSVHelper {

    public static Task parseCSV(String directory, Task root) {
        File file = new File(directory);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File singleCSV : files) {
                String themeName = singleCSV.getName().replace(".csv", "");
                themeName = themeName.replace("_", " ");
                themeName += ":";
                Task currentTask = new Task(themeName, 0.0, false, root);
                if (root.getSubtasks().indexOf(currentTask) < 0) {
                    root.getSubtasks().add(currentTask);
                }
                parseAndFill(singleCSV, currentTask);
            }
        }
        return root;
    }

    private static void parseAndFill(File file, Task root) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            Task currentTheme = root;
            while ((s = reader.readLine()) != null) {
                String values[] = s.split(",");
                String theme = values[4];
                if (theme.endsWith(":")) {
                    Task subTheme = new Task(values[4], 0.0, false, root);
                    if (root.getSubtasks().indexOf(subTheme) < 0) {
                        root.getSubtasks().add(subTheme);
                    }
                    currentTheme = subTheme;
                } else {
                    Task task = new Task(values[4], 0.0, false, currentTheme);
                    if (currentTheme.getSubtasks().indexOf(task) < 0) {
                        currentTheme.getSubtasks().add(task);
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println(file.getAbsolutePath() + " can't be parsed");
        }
    }

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
            out.println(root.getTask() + "," + root.getDescription() + "," + root.getProgress() + "," + level);
            return;
        } else {
            out.println(root.getTask() + "," + root.getDescription() + "," + root.getProgress() + "," + level);
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
                    String values[] = s.split(",");
                    String theme = values[0];
                    int currentLevel = Integer.parseInt(values[3]);
                    if (currentLevel <= level) {
                        currentTheme = updateToProper(currentTheme, currentLevel, level);
                    }
                    String comments = values[1];
                    if (!comments.equals("")) {
                        int a =0;
                    }
                    double progress = Double.parseDouble(values[2]);
                    Task subTheme = new Task(theme, progress, progress == 1.0, currentTheme);
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
