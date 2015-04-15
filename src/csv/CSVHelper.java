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
                Task currentTask = new Task(themeName.hashCode(), themeName, 0.0, false, root);
                int index = root.getSubtasks().indexOf(currentTask);
                if (index < 0) {
                    root.getSubtasks().add(currentTask);
                } else {
                    currentTask = root.getSubtasks().get(index);
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
                Long id = Long.parseLong(values[0]);
                if (theme.endsWith(":")) {
                    Task subTheme = new Task(id, values[4], 0.0, false, root);
                    int index = root.getSubtasks().indexOf(subTheme);
                    if (index < 0) {
                        double progresses[] = new double[root.getSubtasks().size()];
                        int i = 0;
                        for (Task task : root.getSubtasks()) {
                            progresses[i++] = task.getProgress();
                            task.setCompleted(0.0);
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
                    }
                    currentTheme = subTheme;
                } else {
                    Task task = new Task(id, values[4], 0.0, false, currentTheme);
                    if (currentTheme.getSubtasks().indexOf(task) < 0) {
                        double progresses[] = new double[currentTheme.getSubtasks().size()];
                        int i = 0;
                        for (Task iter : currentTheme.getSubtasks()) {
                            progresses[i++] = iter.getProgress();
                            iter.setCompleted(0.0);
                        }
                        currentTheme.getSubtasks().add(task);
                        i = 0;
                        for (Task iter : currentTheme.getSubtasks()) {
                            if (i == currentTheme.getSubtasks().size() - 1) {
                                continue;
                            }
                            iter.setCompleted(progresses[i++]);
                        }
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
            out.println(root.getId() + "," + root.getTask() + "," + root.getDescription() + "," + root.getProgress() + "," + level);
            return;
        } else {
            out.println(root.getId() + "," + root.getTask() + "," + root.getDescription() + "," + root.getProgress() + "," + level);
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
                    Long id = Long.parseLong(values[0]);
                    String theme = values[1];
                    int currentLevel = Integer.parseInt(values[4]);
                    if (currentLevel <= level) {
                        currentTheme = updateToProper(currentTheme, currentLevel, level);
                    }
                    String comments = values[2];
                    double progress = Double.parseDouble(values[3]);
                    Task subTheme = new Task(id, theme, progress, progress == 1.0, currentTheme);
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
