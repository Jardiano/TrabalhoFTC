package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.List;
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
public class Automaton
{
    @XStreamAlias("state")
    private List<State> states;
    @XStreamAlias("transition")
    private List<Transition> transitions;

}
