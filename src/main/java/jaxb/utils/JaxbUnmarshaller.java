package jaxb.utils;

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
    public static <T> T unmarshall(String name, Class<T> clazz) {
        InputStream input = null;
        try {
            input = new FileInputStream(name);
        } catch (FileNotFoundException e) {
            return SimpleJaxbConverter.convert(clazz);
        }
        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            T object = (T) unmarshaller.unmarshal(input);
            return object;
        } catch (JAXBException e) {
            System.err.println(e);
        }
        return null;
    }
}
