package jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikiforov on 26.05.2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HistoriesJAXB {

    @XmlElementWrapper
    List<HistoryJAXB> histories;

    public HistoriesJAXB() {
        histories = new ArrayList<>();
    }

    public List<HistoryJAXB> getHistories() {
        return histories;
    }

    public void setHistories(List<HistoryJAXB> histories) {
        this.histories = histories;
    }
}
