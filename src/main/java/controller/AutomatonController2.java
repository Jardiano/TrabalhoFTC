package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Automaton;
import model.State;
import model.Transition;

public class AutomatonController2 {
    public static List<State> states = new ArrayList<State>();

    public static List<Transition> transitions = new ArrayList<Transition>();

    public static int countState = 0;
    public static int indexStates = 0;
    public static int levelParenteses = 0;
    public static State raiz;
    public static final String LAMBDA = "λ";


    public static void main(String[] args) {
        AutomatonController2 controller = new AutomatonController2();
        controller.converteExpressão();
    }


    public void converteExpressão() {
        //String expressao = "1(0*0)+(11*)+(1)00";
        //String expressao = "1*011*01";
        //String expressao = "1*01+1*01";
        String expressao = "1*01+1*01+1*(01)";
        //String expressao = "1(01)*+0(01)*";
        //String expressao = "1*10(01)*";
        //String expressao = "1*1001*";
        //String expressao = "(00+01(11)*10+(1+01(11)*0)(0(11)*0)*(1+0(11)*10))*";
        final String expressaoInaltareda = expressao;

        String sentenca = "111111";

        String[] simbolos = sentenca.split("");

        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, true, false));
        countState++;

        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
        countState++;

        String[] split = expressao.split("");

        if (expressao.contains("+")) {
            do {
                String[] split1 = expressao.substring(0, expressao.indexOf("+")).split("");
                geracaoAFN(split1);
                expressao = expressao.replace(expressao.substring(0, expressao.indexOf("+")+1),"");
            } while (expressao.contains("+"));
            String[] split2 = expressao.substring(expressao.indexOf("+") + 1, expressao.length()).split("");
            geracaoAFN(split2);
        }
        else {
            geracaoAFN(split);
        }

        Automaton automaton = new Automaton();
        automaton.setStates(states);
        automaton.setTransitions(transitions);

        executaMaquinaEstados(sentenca, simbolos, automaton, expressaoInaltareda);

    }


    private void geracaoAFN(String[] split) {
        for (int i = 0; i < split.length; i++) {

            if (split[i].equals("*")) {
                transitions.get(transitions.size() - 1).setRead(transitions.get(transitions.size() - 1).getRead().concat(split[i]));
                if (i == split.length - 1) {
                    transitions.add(new Transition(states.get(states.size() - 1).getId(), states.get(1).getId(), LAMBDA));
                }
                continue;
            }

            if (i == 0) {
                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                transitions.add(new Transition(states.get(countState - 2).getId(), states.get(countState).getId(), LAMBDA));
                countState++;

            }
            else {
                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), LAMBDA));
                countState++;
            }

            String value = "";
            states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
            if (split[i].equals("(")) {
                String parenteses = split[i];
                int index = 1;
                for (int j = i + 1; index > 0; i++) {
                    if (split[i].equals("(") && !parenteses.equals("(")) {
                        index++;
                    }
                    else if (split[i].equals("(") && parenteses.equals("(")) {
                        continue;
                    }
                    else if (split[i].equals(")")) {
                        index--;
                    }
                    parenteses = parenteses.concat(split[i]);
                }
                i--;
                value = parenteses;
            }
            else {
                value = split[i];
            }
            transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), value));
            countState++;
        }

        if (!split[split.length - 1].equals("*")) {
            transitions.add(new Transition(states.get(countState - 1).getId(), "1", LAMBDA));
        }

        List<Transition> localTransitions = new ArrayList<>(transitions);

        String readTransition = "";
        String idTransitionFrom = "";
        String idTransitionTo = "";

        for (Transition transition : localTransitions) {
            String value = transition.getRead();

            if (value.contains("(")) {
                readTransition = value.replaceFirst("[(]", "");
                readTransition = readTransition.substring(0, readTransition.lastIndexOf(")"));
                idTransitionFrom = transition.getFrom();
                idTransitionTo = transition.getTo();
            }

            if (value.contains("*")) {
                value = value.replace("*", "");

                transition.setRead(LAMBDA);
                transitions.add(new Transition(transition.getTo(), transition.getFrom(), LAMBDA));

                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                transitions.add(new Transition(transition.getFrom(), states.get(countState).getId(), LAMBDA));
                countState++;

                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                transitions.add(new Transition(states.get(countState).getId(), transition.getTo(), LAMBDA));

                transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), value));
                countState++;

                if (value.contains("(")) {
                    readTransition = value.replaceFirst("[(]", "");
                    readTransition = readTransition.substring(0, readTransition.lastIndexOf(")"));
                    idTransitionFrom = states.get(countState - 2).getId();//transition.getFrom();
                    idTransitionTo = states.get(countState - 1).getId();
                }

            }

            if (!readTransition.isEmpty()) {
                String[] split1 = readTransition.split("");
                for (int i = 0; i < split1.length; i++) {
                    if (i == 0) {
                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                        transitions.add(new Transition(idTransitionFrom, states.get(countState).getId(), LAMBDA));
                        countState++;

                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                        transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), split1[i]));
                        countState++;

                    }
                    else {
                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                        transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), LAMBDA));
                        countState++;

                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                        transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), split1[i]));
                        countState++;
                    }

                    if (i == split1.length - 1) {
                        transitions.add(new Transition(states.get(countState - 1).getId(), idTransitionTo, LAMBDA));
                        countState++;
                    }
                }
                readTransition = "";
                final String currentTransitionFrom = idTransitionFrom;
                final String currentTransitionTo = idTransitionTo;

                transitions.removeIf(transition1 -> transition1.getFrom().equals(currentTransitionFrom) && transition1.getTo().equals(currentTransitionTo));
            }
        }
    }


    private void executaMaquinaEstados(String sentenca, String[] simbolos, Automaton automaton, String expressao) {
        int indiceEstados = 0;
        boolean isSimbolAccepted = true;
        State lastState = new State();
        if (automaton.isUndefinedAutomaton()) {
            System.out.println("Automato não determinístico");

            for (int i = 0; i < sentenca.length(); i++) {
                isSimbolAccepted = false;
                State currentState = automaton.getStates().get(indiceEstados);
                System.out.println("Estado atual " + currentState.getName() + " Valor de entrada " + simbolos[i]);
                List<Transition> transitionsByState = automaton.getTransitionsByState(currentState);

                Set<String> acceptedValues = new HashSet<>();
                transitionsByState.forEach(v -> acceptedValues.add(v.getRead()));

                for (Transition transition : transitionsByState) {
                    if (transition.getFrom().equals(currentState.getId()) && transition.getTo().equals(currentState.getId()) && simbolos[i].equals(transition.getRead())) {
                        isSimbolAccepted = true;
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = automaton.getStates().get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                    }
                    else if (!acceptedValues.contains(simbolos[i])) {
                        i = sentenca.length();
                        break;
                    }
                    else if (transition.getFrom().equals(currentState.getId()) && simbolos[i].equals(transition.getRead())) {
                        isSimbolAccepted = true;
                        indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                        currentState = automaton.getStates().get(indiceEstados);
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                    }
                }
                lastState = currentState;
            }

        }
        else {

            for (int i = 0; i < sentenca.length(); i++) {
                if (isSimbolAccepted) {
                    isSimbolAccepted = false;
                    State currentState = automaton.getStates().get(indiceEstados);
                    System.out.println("Estado atual " + currentState.getId() + " Valor de entrada " + simbolos[i]);
                    for (Transition transition : automaton.getTransitionsByState(currentState)) {
                        if (transition.getFrom().equals(currentState.getId()) && transition.getTo().equals(currentState.getId()) && simbolos[i].equals(transition.getRead())) {
                            isSimbolAccepted = true;
                            indiceEstados = Integer.parseInt(transition.getTo().replace("q", ""));
                            currentState = automaton.getStates().get(indiceEstados);
                            System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                            break;
                        }
                        else if (transition.getFrom().equals(currentState.getId()) && simbolos[i].equals(transition.getRead())) {
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

        if (!lastState.isFinalState() || !isSimbolAccepted) {
            System.out.println("Sentença Rejeitada");
        }
        else {
            System.out.println("Sentença Aceita");
        }

        GeradorXMLController geradorXml = new GeradorXMLController();
        geradorXml.geraXML(automaton, expressao);
    }


}
