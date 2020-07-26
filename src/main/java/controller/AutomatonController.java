package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Automaton;
import model.State;
import model.Transition;

public class AutomatonController {
    public static List<State> states = new ArrayList<State>();

    public static List<Transition> transitions = new ArrayList<Transition>();

    public static int countState = 0;
    public static int indexStates = 0;
    public static int levelParenteses = 0;
    public static State raiz;


/*    public static void main(String[] args) {
        AutomatonController controller = new AutomatonController();
        controller.converteExpressão();
    }*/

    public void converteExpressão() {
        //String expressao = "1(0*0)+(11*)+(1)00";
        //String expressao = "1*011*01";
        //String expressao = "1*01+1*01";
        //String expressao = "1(01)*+0(01)*";
        //String expressao = "1*011*01";
        String expressao = "1*00*1";
        //String expressao = "(00+01(11)*10+(1+01(11)*0)(0(11)*0)*(1+0(11)*10))*";

        String sentenca = "11111111110000000011111111111000000001";

        final String expressaoInaltareda = expressao;

        String[] split = expressao.split("");


        String[] simbolos = sentenca.split("");

        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, true, false));
        countState++;

        raiz = states.get(0);

        if (false/*expressao.contains("(") || expressao.contains(")")*/) {
            for (int i = 0; i < split.length || expressao.contains("(") || expressao.contains(")"); i++) {
                String subExpressao = expressao.substring(expressao.indexOf("(") + 1, expressao.indexOf(")"));
                String[] split1 = subExpressao.split("");
                if (subExpressao.contains("+")) {
                    aplicaUniaoEFecho(subExpressao, split1, raiz);
                }
                else {
                    aplicaFechoEConcatenacao(subExpressao, split1);
                }

                expressao = expressao.replace("(".concat(subExpressao).concat(")"), "");

                expressao = expressao.replaceFirst("[.+]", "");

                split = expressao.split("");

                if (i >= 0) {
                    countState--;
                }

            }
        }
        else if (expressao.contains("+")) {
            aplicaUniaoEFecho(expressao, split, raiz);
        }
        else {
            aplicaFechoEConcatenacao(expressao, split, false);
        }

        // Aplicação da união
        //Automaton automaton = uniaoService.getAutomaton(expressao, split, states, transitions, countState, indexStates, raiz);

        //Tratamento para concatenação e fecho
        //Automaton automaton2 = fechoService.getAutomaton(expressao, split, states, transitions, countState, indexStates);

        Automaton automaton = new Automaton();
        automaton.setStates(states);
        automaton.setTransitions(transitions);

        executaMaquinaEstados(sentenca, simbolos, automaton, expressaoInaltareda);

    }


    private void aplicaUniaoEFecho(String expressao, String[] split, State raiz) {

        String primeiraParate = expressao.substring(0, expressao.indexOf("+"));
        String segundaParte = expressao.substring(expressao.indexOf("+") + 1, expressao.length());

        aplicaFechoEConcatenacao(primeiraParate, primeiraParate.split(""));

        aplicaFechoEConcatenacao(segundaParte, segundaParte.split(""), true);
    }
    /*private void aplicaUniaoEFecho(String expressao, String[] split, State raiz)
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

                transitions.add(new Transition(states.get(countState - 2).getId(), states.get(countState - 1).getId(), split[i]));
                indexStates++;

            }
            else if (i != expressao.length() - 1
                && (split[i].equals("*") || split[i].equals("+") ))
            {
                if(split[i].equals("*")){
                    transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState - 1).getId(), split[i - 1]));
                    indexStates++;
                }else if(split[i].equals("+")){
                    states.get(states.size() - 1).setFinalState(true);
                    transitions.add(new Transition(raiz.getId(), states.get(countState-1).getId().replace(countState-1+"",countState+""), split[i + 1]));
                }
            }
            else if (i == expressao.length() - 1)
            {
                if (split[i].equals("*") || split[i].equals("+"))
                {
                    states.get(states.size() - 1).setFinalState(true);
                    transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState - 1).getId(), split[i - 1]));
                    countState++;
                    indexStates++;
                }
                else
                {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                    transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), split[i]));
                    countState++;
                    indexStates++;
                }
            }

        }
    }*/


    private void aplicaFechoEConcatenacao(String expressao, String[] split) {
        for (int i = 0; i < expressao.length(); i++) {
            if (!split[i].equals("*") && !(i == expressao.length() - 1) && i + 1 <= expressao.length() && !split[i + 1].equals("*")) {
                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                countState++;

                transitions.add(new Transition(states.get(countState - 2).getId(), states.get(countState - 1).getId(), split[i]));
                indexStates++;

            }
            else if (i != expressao.length() - 1 && split[i].equals("*")) {
                transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState - 1).getId(), split[i - 1]));
                indexStates++;
            }
            else if (i == expressao.length() - 1) {
                if (split[i].equals("*")) {
                    states.get(states.size() - 1).setFinalState(true);
                    transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState - 1).getId(), split[i - 1]));
                    countState++;
                    indexStates++;
                }
                else {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                    transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), split[i]));
                    countState++;
                    indexStates++;
                }
            }

        }
    }


    private void aplicaFechoEConcatenacao(String expressao, String[] split, boolean isSegundaParte) {
        String expressaoParenteses = "";
        for (int i = 0; i < expressao.length(); i++) {
            if (split[i].equals("(")) {
                expressaoParenteses = expressao.substring(expressao.indexOf("(") + 1, expressao.indexOf(")"));
                aplicaFechoEConcatenacao(expressaoParenteses, expressaoParenteses.split(""), false);
                i = i + expressaoParenteses.length();
                continue;
            }
            else if (split[i].equals(")")) {
                if (i < expressao.length() - 1 && split[i + 1].equals("*")) {
                    transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState - 2).getId(), String.valueOf(expressaoParenteses.charAt(0))));
                    indexStates++;
                }
                continue;
            }

            if (!split[i].equals("*") && !(i == expressao.length() - 1) && i + 1 <= expressao.length() && !split[i + 1].equals("*")) {
                states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                countState++;

                if (isSegundaParte) {
                    transitions.add(new Transition(raiz.getId(), states.get(states.size() - 1).getId(), split[i]));
                    isSegundaParte = false;
                }
                else {
                    transitions.add(new Transition(states.get(countState - 2).getId(), states.get(countState - 1).getId(), split[i]));
                    indexStates++;

                }

            }
            else if (i != expressao.length() - 1 && split[i].equals("*")) {
                transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState - 1).getId(), split[i - 1]));
                indexStates++;
            }
            else if (i == expressao.length() - 1) {
                if (split[i].equals("*")) {
                    if (!split[i - 1].equals(")")) {
                        states.get(states.size() - 1).setFinalState(true);
                        transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState - 1).getId(), split[i - 1]));
                        countState++;
                        indexStates++;
                    }
                }
                else {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                    transitions.add(new Transition(states.get(countState - 1).getId(), states.get(countState).getId(), split[i]));
                    countState++;
                    indexStates++;
                }
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

/*        GeradorXMLController geradorXml = new GeradorXMLController();
        geradorXml.geraXML(automaton, expressao);*/
    }


}
