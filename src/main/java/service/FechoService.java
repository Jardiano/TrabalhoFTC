package service;

import java.util.List;
import model.Automaton;
import model.State;
import model.Transition;

public class FechoService
{
    public Automaton getAutomaton(String expressao, String[] split, List<State> states, List<Transition> transitions, int countState, int indexStates)
    {
        for(int i =0; i< expressao.length(); i++){
            if(states.isEmpty()){
                states.add(new State(String.valueOf(countState),"q"+countState,0.0,0.0,true,false));
                countState++;
            }else if( !split[i].equals("*")
                && !(i == expressao.length() - 1)
                && i+1 <= expressao.length()
                && !split[i+1].equals("*")){
                states.add(new State(String.valueOf(countState),"q"+countState,0.0,0.0,false,false));
                countState++;
            }
            else if(i == expressao.length() - 1 ){
                if(split[i].equals("*")){
                    states.get(states.size()-1).setFinalState(true);
                }else{
                    states.add(new State(String.valueOf(countState),"q"+countState,0.0,0.0,false,true));
                    countState++;
                }
            }

            if(split[i].equals("*")){
                transitions.add(new Transition(states.get(countState-1).getName(),states.get(countState-1).getName(), split[i-1]));
            }else if(states.size() > 1 ){
                if(!split[i-1].equals("*") || i == expressao.length() - 1 ){
                    if(i == expressao.length() - 1 && !split[i].equals("*")){
                        transitions.add(new Transition(states.get(indexStates).getName(),states.get(states.size()-1).getName(), split[i]));
                        indexStates++;
                    }
                    else if(!split[i].equals("*")){
                        if(split[i].equals(split[i-1])){
                            transitions.add(new Transition(states.get(indexStates).getName(),states.get(states.size()-2).getName(), split[i-1]));
                            indexStates++;
                        }else{
                            transitions.add(new Transition(states.get(indexStates).getName(),states.get(states.size()-1).getName(), split[i-1]));
                            indexStates++;
                        }
                    }
                }
            }
        }

        Automaton automaton = new Automaton();
        automaton.setStates(states);
        automaton.setTransitions(transitions);
        return automaton;
    }
}
