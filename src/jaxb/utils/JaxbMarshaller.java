package jaxb.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Acer on 04.11.2014.
 */
public class JaxbMarshaller {

    public static <T> Object marshall(T object, Class<T> clazz, String fileName) {
        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            OutputStream os = null;
            os = new FileOutputStream(fileName);
            marshaller.marshal(object, os);
            os.flush();
            os.close();
        } catch (JAXBException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }
}
