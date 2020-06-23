package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@XStreamAlias("structure")
public class Structure
{
    private String type;
    private  Automaton automaton;
}
