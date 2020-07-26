package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import model.Automaton;
import model.State;
import model.Transition;

public class AutomatonController2 {
    public static List<State> states = new ArrayList<>();

    public static List<Transition> transitions = new ArrayList<>();

    public static int countState = 0;
    public static final String LAMBDA = "λ";
    public static final List<Character> caracteresEspeciais = Arrays.asList('(', ')', '*', '+');
    public static final Set<String> alfabeto = new HashSet<>();


    public static void main(String[] args) {
        //String expressao = "1(0*0)+(11*)+(1)00";
                        //String expressao = "1*011*01";
        //String expressao = "1(01)*+0(01)*";
        //String expressao = "1*01+1*01";
                    //String expressao = "1*01+1*01+1*(01)";
        //String expressao = "1(01)*+0(01)*";
        //String expressao = "1*10(01)*";
        String expressao = "1*00*1";
        //String expressao = "0*11*";
        //String expressao = "(00+01(11)*10+(1+01(11)*0)(0(11)*0)*(1+0(11)*10))*";

        String sentenca = "0101";

        AutomatonController2 controller = new AutomatonController2();
        Automaton automaton = controller.converteExpressão(expressao);
        boolean result = controller.executaMaquinaEstados(sentenca, sentenca.split(""), automaton);

        System.out.println("The result is " + result);
    }


    public Automaton converteExpressão(String expressao) {
        //final String expressaoInaltareda = new String(expressao);

        defineAlfabeto(expressao);

        //String[] simbolos = sentenca.split("");

        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, true, false));
        countState++;

        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
        countState++;

        String[] split = expressao.split("");

        if (expressao.contains("+")) {
            do {
                String[] split1 = expressao.substring(0, expressao.indexOf("+")).split("");
                geracaoAFN(split1);
                expressao = expressao.replace(expressao.substring(0, expressao.indexOf("+") + 1), "");
            } while (expressao.contains("+"));
            String[] split2 = expressao.substring(expressao.indexOf("+") + 1, expressao.length()).split("");
            geracaoAFN(split2);
        }
        else {
            geracaoAFN(split);
        }

        //Remove estados duplicados
        //states = states.stream().filter(distinctByKey(State::getId)).collect(Collectors.toList());

        Automaton automatonAfn = new Automaton();
        automatonAfn.setStates(states);
        automatonAfn.setTransitions(transitions);

/*        GeradorXMLController geradorXml = new GeradorXMLController();
        geradorXml.geraXML(automatonAfn, expressao,null);*/

        Automaton automatonAfd = convertToAfd(automatonAfn);

        //executaMaquinaEstados(sentenca, simbolos, automatonAfd, expressaoInaltareda);
        //executaMaquinaEstados(sentenca, simbolos, automatonAfn, expressaoInaltareda);

        return automatonAfd;
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    private Automaton convertToAfd(Automaton automatonAfn) {

        /*GeradorXMLController geradorXml = new GeradorXMLController();
        geradorXml.geraXML(automatonAfn, "expressao", null);*/


        List<State> statesAfd = new ArrayList<State>();

        List<Transition> transitionsAfd = new ArrayList<Transition>();

        boolean hasLambda = true;
        int i = 0;
        List<String> removedState = new ArrayList<>();
        while (hasLambda) {
            State state = states.get(i);
            List<Transition> transitionsByStateId = automatonAfn.getTransitionsFromStateId(states.get(i).getId());

            for (Transition t : transitionsByStateId) {
                if (t.getTo().equals(t.getFrom()) && !removedState.contains(state.getId())) {
                    Optional<Transition> first = transitionsByStateId.stream().filter(ts -> !ts.getRead().equals(LAMBDA)).findFirst();
                if(first.isPresent()){
                    String read = first.get().getRead();
                        if (t.getRead().equals(LAMBDA)) {
                            t.setRead(read);
                        }
                }
                }
                else if (t.getRead().equals(LAMBDA) && !removedState.contains(state.getId())) {
                    boolean removed = transitions.removeIf(transition1 -> transition1.getRead().equals(t.getRead()) && transition1.getTo()
                        .equals(t.getTo()) && transition1.getFrom().equals(t.getFrom()));

                    if (Integer.parseInt(t.getFrom()) < Integer.parseInt(t.getTo()) || t.getTo().equals("1")) {
                        if (removed) {
                            removedState.add(t.getTo());
                        }
                        updateAndRemoveState(automatonAfn, state, t);
                        i--;
                    }
                    else if (!t.getTo().equals("1")) {
                        State state1 = automatonAfn.getStateById(t.getTo());
                        List<Transition> transitionsByStateId1 = automatonAfn.getTransitionsFromStateId(state1.getId());

                        transitionsByStateId1.forEach(transition -> {
                            if (transition.getFrom().equals(transition.getTo())) {
                                transition.setFrom(t.getFrom());
                                transition.setTo(t.getFrom());
                            }
                            else if (!transition.getTo().equals(t.getFrom()) && !transition.getRead().equals(LAMBDA)) {
                                transitions.add(new Transition(t.getFrom(), transition.getTo(), transition.getRead()));
                            }
                        });
                    }
                }
            }

            if (i == states.size() - 1) {
                hasLambda = false;
            }
            else if (i < 0) {
                i = 0;
            }

            if (transitionsByStateId.stream().noneMatch(transition -> transition.getRead().equals(LAMBDA))) {
                i++;
            }
            statesAfd = states;
            transitionsAfd = transitions;
        }

        //removeNoDeterministTransitions(statesAfd, transitionsAfd);

        Automaton automatonAfd = new Automaton();
        automatonAfd.setStates(statesAfd);
        automatonAfd.setTransitions(transitionsAfd);
        return automatonAfd;
    }


    private void removeNoDeterministTransitions(List<State> statesAfd, List<Transition> transitionsAfd) {
        final List<Transition> trst = transitionsAfd;
        statesAfd.forEach(state -> {
            List<Transition> collect = trst.stream().filter(transition -> transition.getFrom().equals(state.getId())).collect(Collectors.toList());
            alfabeto.forEach(caracter -> {
                long count = collect.stream().filter(t -> t.getRead().equals(caracter)).sorted(Comparator.comparing(Transition::getTo).reversed()).count();
                if (count > 1) {
                    Transition trt = collect.stream().filter(t -> t.getRead().equals(caracter)).sorted(Comparator.comparing(Transition::getTo)).findFirst().get();
                    transitions.removeIf(transition -> transition.getFrom().equals(trt.getFrom()) && transition.getTo().equals(trt.getTo()) && trt.getRead().equals(trt.getRead()));

                }
            });
        });
    }


    private void updateAndRemoveState(Automaton automatonAfn, State state, Transition transition) {
        List<Transition> listTransations = automatonAfn.getAllTransitionsWithRelatedStateById(transition.getTo());
        for (Transition t : listTransations) {
            if (t.getFrom().equals(transition.getTo())) {
                updateState(automatonAfn, state, t.getFrom());
                t.setFrom(state.getId());
            }
            else if (t.getTo().equals(transition.getTo())) {
                updateState(automatonAfn, state, t.getTo());
                t.setTo(state.getId());
            }
        }

        if (transition.getTo().equals("1")) {
            updateState(automatonAfn, state, transition.getTo());
        }
        states.removeIf(state1 -> state1.getId().equals(transition.getTo()));
    }


    private State updateState(Automaton automatonAfn, State state, String to) {
        State stateById = automatonAfn.getStateById(to);
        if (stateById.isInitialState()) {
            state.setInitialState(true);
        }
        if (stateById.isFinalState()) {
            state.setFinalState(true);
        }
        return stateById;
    }


    private void defineAlfabeto(String expressao) {
        Stream<Character> caractereStream = expressao.chars().mapToObj(c -> (char) c).filter(c -> !caracteresEspeciais.contains(c));
        caractereStream.forEach(c -> alfabeto.add(String.valueOf(c)));
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
                    idTransitionFrom = states.get(countState - 2).getId();
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


    public boolean executaMaquinaEstados(String sentenca, String[] simbolos, Automaton automaton) {
        int indiceEstados = 0;
        boolean isSimbolAccepted = true;
        State lastState = new State();

/*        if (automaton.isUndefinedAutomaton()) {
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
        else {*/
        /*GeradorXMLController geradorXml = new GeradorXMLController();
        geradorXml.geraXML(automaton, "expressao", null);*/

        State currentState = null;
        for (int i = 0; i < sentenca.length(); i++) {
            if(!alfabeto.contains(simbolos[i])){
                lastState.setFinalState(false);
                break;
            }

            if (isSimbolAccepted) {
                isSimbolAccepted = false;
                if (i == 0) {
                    currentState = automaton.getStates().get(i);
                }
                System.out.println("Estado atual " + currentState.getId() + " Valor de entrada " + simbolos[i]);
                for (Transition transition : automaton.getTransitionsFromStateId(currentState.getId())) {
                    if (transition.getFrom().equals(currentState.getId()) && transition.getTo().equals(currentState.getId()) && simbolos[i].equals(transition.getRead())) {
                        isSimbolAccepted = true;
                        currentState = automaton.getStateById(transition.getTo());
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                        break;
                    }
                    else if (transition.getFrom().equals(currentState.getId()) && simbolos[i].equals(transition.getRead())) {
                        isSimbolAccepted = true;
                        currentState = automaton.getStateById(transition.getTo());
                        System.out.println("Transição de " + transition.getFrom() + " para " + transition.getTo());
                        break;
                    }
                }
                lastState = currentState;
            }
        }
        //}

        if (lastState.isFinalState()) {
            System.out.println("Sentença Aceita");
            return true;
        }
        else {
            System.out.println("Sentença Rejeitada");
            return false;
        }

    }


}
