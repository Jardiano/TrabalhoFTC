package service;

import java.util.List;
import model.Automaton;
import model.State;
import model.Transition;

public class UniaoService
{

    FechoService fechoService = new FechoService();


    public Automaton getAutomaton(String expressao, String[] split, List<State> states, List<Transition> transitions, int countState, int indexStates, State raiz)
    {
        Automaton automaton;

        // Aplicação da união
        if (expressao.contains("+"))
        {
            String primeiraParte = expressao.substring(0, expressao.indexOf("+"));
            String[] splitPrimeiraParte = primeiraParte.split("");
            for (int i = 0; i < splitPrimeiraParte.length; i++)
            {
                if (i == splitPrimeiraParte.length - 1 && splitPrimeiraParte.length == 1)
                {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                    countState++;

                    transitions.add(new Transition(raiz.getName(), states.get(states.size() - 1).getName(), splitPrimeiraParte[i]));
                    indexStates++;

                }
                else if (i == 0)
                {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                    countState++;

                    transitions.add(new Transition(raiz.getName(), states.get(states.size() - 1).getName(), splitPrimeiraParte[i]));
                    indexStates++;

                }
                else
                {
                    if (i == splitPrimeiraParte.length - 1)
                    {
                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                        countState++;
                    }
                    else
                    {
                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                        countState++;
                    }

                    transitions.add(new Transition(states.get(indexStates).getName(), states.get(states.size() - 1).getName(), splitPrimeiraParte[i]));
                    indexStates++;
                }

            }

            String segundaParte = expressao.substring(expressao.indexOf("+") + 1, expressao.length());
            String[] splitSegundaParte = segundaParte.split("");

            for (int i = 0; i < splitSegundaParte.length; i++)
            {
                if (i == splitSegundaParte.length - 1 && splitSegundaParte.length == 1)
                {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                    countState++;

                    transitions.add(new Transition(raiz.getName(), states.get(states.size() - 1).getName(), splitSegundaParte[i]));
                    indexStates++;

                }
                else if (i == 0)
                {
                    states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                    countState++;

                    transitions.add(new Transition(raiz.getName(), states.get(states.size() - 1).getName(), splitSegundaParte[i]));
                    indexStates++;

                }
                else
                {
                    if (i == splitSegundaParte.length - 1)
                    {
                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, true));
                        countState++;
                    }
                    else
                    {
                        states.add(new State(String.valueOf(countState), "q" + countState, 0.0, 0.0, false, false));
                        countState++;
                    }

                    transitions.add(new Transition(states.get(indexStates).getName(), states.get(states.size() - 1).getName(), splitSegundaParte[i]));
                    indexStates++;
                }

            }
            automaton = new Automaton();
            automaton.setStates(states);
            automaton.setTransitions(transitions);
        }
        else
        {
            automaton = fechoService.getAutomaton(expressao, split, states, transitions, countState, indexStates);
        }

        return automaton;
    }

}
