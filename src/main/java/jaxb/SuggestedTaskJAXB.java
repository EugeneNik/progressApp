package jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Евгений on 28.06.2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SuggestedTaskJAXB {

    private long id;

    public SuggestedTaskJAXB() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
