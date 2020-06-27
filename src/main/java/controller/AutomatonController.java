package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Automaton;
import model.State;
import model.Transition;

public class AutomatonController
{
    public static List<State> states = new ArrayList<State>();

    public static List<Transition> transitions = new ArrayList<Transition>();

    public static int countState = 0;
    public static int indexStates = 0;


    public static void main(String[] args)
    {
        //JOptionPane.showInputDialog(" Digite a expressão! ");
        AutomatonController controller = new AutomatonController();
        controller.converteExpressão();
    }


    public void converteExpressão()
    {
        String expressao = "1(0*0)+(11*)+(1)00";
        //String expressao = "1*011*01";

        String[] split = expressao.split("");

        String sentenca = "111111";
        //String sentenca = "01101";

        String[] simbolos = sentenca.split("");

        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, true, false));
        countState++;

        State raiz = states.get(0);

        if(expressao.contains("(") || expressao.contains(")")){
            for(int i = 0; i < split.length || expressao.contains("(")|| expressao.contains(")"); i++){
                String subExpressao = expressao.substring(expressao.indexOf("(")+1,expressao.indexOf(")"));
                String[] split1 = subExpressao.split("");
                if(subExpressao.contains("+")){
                    aplicaUniaoEFecho(subExpressao, split1, raiz);
                }else{
                    aplicaFechoEConcatenacao(subExpressao, split1);
                }

                expressao = expressao.replace("(".concat(subExpressao).concat(")"), "");

                expressao = expressao.replaceFirst("[.+]","");

                split = expressao.split("");

                if(i >= 0){
                    countState--;
                }

            }
        }else if (expressao.contains("+"))
        {
            aplicaUniaoEFecho(expressao, split, raiz);
        }
        else
        {
            aplicaFechoEConcatenacao(expressao, split);
        }

        // Aplicação da união
        //Automaton automaton = uniaoService.getAutomaton(expressao, split, states, transitions, countState, indexStates, raiz);

        //Tratamento para concatenação e fecho
        //Automaton automaton2 = fechoService.getAutomaton(expressao, split, states, transitions, countState, indexStates);

        Automaton automaton = new Automaton();
        automaton.setStates(states);
        automaton.setTransitions(transitions);

        executaMaquinaEstados(sentenca, simbolos, automaton);

    }


    private void aplicaUniaoEFecho(String expressao, String[] split, State raiz)
    {
        for (int i = 0; i < expressao.length(); i++)
        {
            if (!split[i].equals("*") && !split[i].equals("+")
                && !(i == expressao.length() - 1)
                && i + 1 <= expressao.length()
                && !split[i + 1].equals("*"))
            {
                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                countState++;

                transitions.add(new Transition(states.get(countState - 2).getName(), states.get(countState - 1).getName(), split[i]));
                indexStates++;

            }
            else if (i != expressao.length() - 1
                && (split[i].equals("*") || split[i].equals("+") ))
            {
                if(split[i].equals("*")){
                    transitions.add(new Transition(states.get(countState - 1).getName(), states.get(countState - 1).getName(), split[i - 1]));
                    indexStates++;
                }else if(split[i].equals("+")){
                    states.get(states.size() - 1).setFinalState(true);
                    transitions.add(new Transition(raiz.getName(), states.get(countState-1).getName().replace(countState-1+"",countState+""), split[i + 1]));
                }
            }
            else if (i == expressao.length() - 1)
            {
                if (split[i].equals("*") || split[i].equals("+"))
                {
                    states.get(states.size() - 1).setFinalState(true);
                    transitions.add(new Transition(states.get(countState - 1).getName(), states.get(countState - 1).getName(), split[i - 1]));
                    countState++;
                    indexStates++;
                }
                else
                {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                    transitions.add(new Transition(states.get(countState - 1).getName(), states.get(countState).getName(), split[i]));
                    countState++;
                    indexStates++;
                }
            }

        }
    }


    private void aplicaFechoEConcatenacao(String expressao, String[] split)
    {
        for (int i = 0; i < expressao.length(); i++)
        {
            if (!split[i].equals("*")
                && !(i == expressao.length() - 1)
                && i + 1 <= expressao.length()
                && !split[i + 1].equals("*"))
            {
                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                countState++;

                transitions.add(new Transition(states.get(countState - 2).getName(), states.get(countState - 1).getName(), split[i]));
                indexStates++;

            }
            else if (i != expressao.length() - 1
                && split[i].equals("*"))
            {
                transitions.add(new Transition(states.get(countState - 1).getName(), states.get(countState - 1).getName(), split[i - 1]));
                indexStates++;
            }
            else if (i == expressao.length() - 1)
            {
                if (split[i].equals("*"))
                {
                    states.get(states.size() - 1).setFinalState(true);
                    transitions.add(new Transition(states.get(countState - 1).getName(), states.get(countState - 1).getName(), split[i - 1]));
                    countState++;
                    indexStates++;
                }
                else
                {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                    transitions.add(new Transition(states.get(countState - 1).getName(), states.get(countState).getName(), split[i]));
                    countState++;
                    indexStates++;
                }
            }

        }
    }


    private void executaMaquinaEstados(String sentenca, String[] simbolos, Automaton automaton)
    {
        int indiceEstados = 0;
        boolean isSimbolAccepted = true;
        State lastState = new State();
        if (automaton.isUndefinedAutomaton())
        {
            System.out.println("Automato não determinístico");

            for (int i = 0; i < sentenca.length(); i++)
            {
                isSimbolAccepted = false;
                State currentState = automaton.getStates().get(indiceEstados);
                System.out.println("Estado atual " + currentState.getName() + " Valor de entrada " + simbolos[i]);
                List<Transition> transitionsByState = automaton.getTransitionsByState(currentState);

                Set<String> acceptedValues = new HashSet<>();
                transitionsByState.forEach(v -> acceptedValues.add(v.getRead()));

                for (Transition transition : transitionsByState)
                {
                    if (transition.getFrom().equals(currentState.getName())
                        && transition.getTo().equals(currentState.getName())
                        && simbolos[i].equals(transition.getRead()))
                    {
                        isSimbolAccepted = true;
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = automaton.getStates().get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                    }
                    else if (!acceptedValues.contains(simbolos[i]))
                    {
                        i = sentenca.length();
                        break;
                    }
                    else if (transition.getFrom().equals(currentState.getName())
                        && simbolos[i].equals(transition.getRead()))
                    {
                        isSimbolAccepted = true;
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = automaton.getStates().get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                    }
                }
                lastState = currentState;
            }

        }
        else
        {

            for (int i = 0; i < sentenca.length(); i++)
            {
                if (isSimbolAccepted)
                {
                    isSimbolAccepted = false;
                    State currentState = automaton.getStates().get(indiceEstados);
                    System.out.println("Estado atual " + currentState.getName() + " Valor de entrada " + simbolos[i]);
                    for (Transition transition : automaton.getTransitionsByState(currentState))
                    {
                        if (transition.getFrom().equals(currentState.getName())
                            && transition.getTo().equals(currentState.getName())
                            && simbolos[i].equals(transition.getRead()))
                        {
                            isSimbolAccepted = true;
                            indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                            currentState = automaton.getStates().get(indiceEstados);
                            System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                            break;
                        }
                        else if (transition.getFrom().equals(currentState.getName())
                            && simbolos[i].equals(transition.getRead()))
                        {
                            isSimbolAccepted = true;
                            indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                            currentState = automaton.getStates().get(indiceEstados);
                            System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                            break;
                        }
                    }
                    lastState = currentState;
                }
            }
        }

        if (!lastState.isFinalState() || !isSimbolAccepted)
        {
            System.out.println("Sentença Rejeitada");
        }
        else
        {
            System.out.println("Sentença Aceita");
        }
    }


}
