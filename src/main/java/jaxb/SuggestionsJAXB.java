package jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Евгений on 28.06.2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SuggestionsJAXB {

    @XmlElementWrapper
    List<SuggestedTaskJAXB> suggestions;

    public SuggestionsJAXB() {
        suggestions = new ArrayList<>();
    }

    public List<SuggestedTaskJAXB> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<SuggestedTaskJAXB> suggestions) {
        this.suggestions = suggestions;
    }
}
