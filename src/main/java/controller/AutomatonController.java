package controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Automaton;
import model.State;
import model.Transition;

public class AutomatonController
{
    public static void main(String[] args)
    {
        //JOptionPane.showInputDialog(" Digite a expressão! ");
        AutomatonController controller = new AutomatonController();
        controller.converteExpressão();
    }


    public void converteExpressão(){
        String expressao = "1*01*";

        String[] split = expressao.split("");

        String sentenca = "0";

        String[] simbolos = sentenca.split("");

        List<State> states = new ArrayList<State>();
        List<Transition> transitions = new ArrayList<Transition>();

        int countState = 0;
        int indexStates = 0;

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

        executaMaquinaEstados(sentenca, simbolos, automaton);

    }


    private void executaMaquinaEstados(String sentenca, String[] simbolos, Automaton automaton)
    {
        int indiceEstados = 0;
        boolean isSimbolAccepted = false;
        State lastState = new State();
        for(int i=0; i<sentenca.length(); i++){
            isSimbolAccepted = false;
            State currentState = automaton.getStates().get(indiceEstados);
            System.out.println("Estado atual " + currentState.getName() + " Valor de entrada "+ simbolos[i]);
                for(Transition transition: automaton.getTransitions()){
                    if(transition.getFrom().equals(currentState.getName())
                        && transition.getTo().equals(currentState.getName())
                        && simbolos[i].equals(transition.getRead())){
                        isSimbolAccepted = true;
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = automaton.getStates().get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                        break;
                    }
                    else if(transition.getFrom().equals(currentState.getName())
                        && simbolos[i].equals(transition.getRead())){
                        isSimbolAccepted = true;
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = automaton.getStates().get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                        break;
                    }
                }
            lastState = currentState;
        }

        if(!lastState.isFinalState() || ! isSimbolAccepted){
            System.out.println("Sentença Rejeitada");
        }else{
            System.out.println("Sentença Aceita");
        }
    }


}
