package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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


    public List<Transition> getTransitionsByState(final State state)
    {
        return transitions.stream()
            .filter(t -> t.getFrom().equals(state.getName()))
            .sorted(Comparator.comparing(Transition::getTo).reversed())
            .collect(Collectors.toList());
    }


    public boolean isUndefinedAutomaton()
    {
        Map<String, Map<String, Long>> collect = transitions.stream()
            .collect(
                Collectors.groupingBy(
                    Transition::getFrom,
                    Collectors.groupingBy(Transition::getRead, Collectors.counting())));

        Map<String, Long> transitionsWithSameValue = new HashMap<>();

        collect.forEach((k,v)-> v.forEach((a,b)-> {
            transitionsWithSameValue.put(k.concat("-").concat(a),b);
            System.out.println("State: "+ k + " Digit: " +  a + " Number of transitions: " + b);
        }));

        return transitionsWithSameValue.values().stream().anyMatch(v -> v > 1);
    }

    public State getStateById(String id){
        return states.stream().filter(state -> state.getId().equals(id)).findFirst().get();
    }

    public List<Transition> getTransitionsFromStateId(String stateid){
        return transitions.stream().filter(transition -> transition.getFrom().equals(stateid))
            .sorted(Comparator.comparing(Transition::getTo))
            .collect(Collectors.toList());
    }

    public List<Transition> getTransitionsToStateId(String stateid){
        return transitions.stream().filter(transition -> transition.getFrom().equals(stateid))
            .sorted(Comparator.comparing(Transition::getTo))
            .collect(Collectors.toList());
    }

    public List<Transition> getAllTransitionsWithRelatedStateById(String stateid){
        return transitions.stream().filter(transition -> transition.getFrom().equals(stateid) || transition.getTo().equals(stateid))
            .collect(Collectors.toList());
    }

}
