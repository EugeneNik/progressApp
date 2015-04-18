package jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DARIA on 18.04.2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskJAXB {

    private long id;
    private String name;
    private String comment;
    private double progress;

    @XmlElementWrapper
    private List<TaskJAXB> tasks;

    public TaskJAXB() {
        this.tasks = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public List<TaskJAXB> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskJAXB> tasks) {
        this.tasks = tasks;
    }
}
