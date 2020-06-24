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
        /*if(expressao.contains("(") || expressao.contains(")")){

        }*/

        String expressao = "1*01*";

        String sentenca = "110110";
        String[] split = sentenca.split("");

        Automaton automaton = new Automaton();
        List<State> states = new ArrayList<State>();
        List<Transition> transitions = new ArrayList<Transition>();

        int countState = 0;

        states.add(new State(String.valueOf(countState),"q"+countState,0.0,0.0,true,false,true));
        countState++;
        states.add(new State(String.valueOf(countState),"q"+countState,0.0,0.0,false,true,true));
        countState++;

        transitions.add(new Transition(states.get(0).getName(),states.get(0).getName(), "1"));
        transitions.add(new Transition(states.get(0).getName(),states.get(1).getName(), "0"));
        transitions.add(new Transition(states.get(1).getName(),states.get(1).getName(), "1"));

        //int indiceTransicoes = 0;
        int indiceEstados = 0;

        State lastState = new State();
        for(int i=0; i<sentenca.length(); i++){

            //Transition currentTransition = transitions.get(indiceTransicoes);
            State currentState = states.get(indiceEstados);
            System.out.println("Estado atual " + currentState.getName() + " Valor de entrada "+ split[i]);
/*            if(split[i].equals(currentTransition.getRead())
            && currentState.hasLoop())
            {*/
                for(Transition transition: transitions){
                    if(transition.getFrom().equals(currentState.getName())
                        && transition.getTo().equals(currentState.getName())
                        && split[i].equals(transition.getRead())){
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = states.get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                    }
                    else if(transition.getFrom().equals(currentState.getName())
                        && split[i].equals(transition.getRead())){
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = states.get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                    }

                }
            //}
            lastState = currentState;
        }

        if(lastState.isFinalState()){
            System.out.println("Sentença Aceita");
        }else{
            System.out.println("Sentença Rejeitada");
        }

        /*for(Character s: expressao.toCharArray()){

               new Transition("q0","q0","1");
                if(s.equals('*')){

                }

                if(s.equals('+')){


                }

                if(s.equals('U')){

                }


            }*/
    }


}
