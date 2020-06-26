package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@XStreamAlias("state")
public class State
{
    @XStreamAsAttribute
    private String id;
    @XStreamAsAttribute
    private String name;
    private double x;
    private double y;
    @XStreamAlias("initial")
    private  boolean initialState;
    @XStreamAlias("final")
    private boolean finalState;


    @Override
    public String toString()
    {
        return "State{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", initialState=" + initialState +
            ", finalState=" + finalState +
            '}';
    }
}
