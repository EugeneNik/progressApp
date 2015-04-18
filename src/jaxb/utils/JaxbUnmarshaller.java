package jaxb.utils;

import data.Task;
import jaxb.TaskJAXB;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Acer on 05.11.2014.
 */
public class JaxbUnmarshaller {
    public static TaskJAXB unmarshall(String name) {
        InputStream input = null;
        try {
            input = new FileInputStream(name);
        } catch (FileNotFoundException e) {
            return JaxbConverter.convertToJaxb(new Task("Root".hashCode(), "Root", 0.0, false, null));
        }
        try {
            JAXBContext jc = JAXBContext.newInstance(TaskJAXB.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            TaskJAXB object = (TaskJAXB) unmarshaller.unmarshal(input);
            return object;
        } catch (JAXBException e) {
            System.err.println(e);
        }
        return null;
    }
}
