import java.util.*;

class DFA {
    private Set<String> states;
    private Map<String, Map<Character, String>> transitions;
    private String startState;
    private Set<String> finalStates;

    public DFA() {
        this.states = new HashSet<>(); // set for de uniqueness
        this.transitions = new HashMap<>();
        this.finalStates = new HashSet<>();
    }

    public void addState(String state, boolean isFinal) {
        states.add(state);
        if (isFinal) {
            finalStates.add(state);
        }
    }

    public void setStartState(String state) {
        this.startState = state;
        states.add(state);
    }

    public void addTransition(String fromState, char symbol, String toState) {
        transitions.putIfAbsent(fromState, new HashMap<>());
        transitions.get(fromState).put(symbol, toState);
    }

    public void displayTransitions() {
        System.out.println("\nDFA Transition Table:");
        for (String state : transitions.keySet()) {
            for (char symbol : transitions.get(state).keySet()) {
                System.out.println(state + " | " + symbol + " | " + transitions.get(state).get(symbol));
            }
        }
    }

    public void parse(String input) {
        String currentState = startState;
        Set<String> visitedStates = new HashSet<>(); // to store unique visited states (for display after)

        for (char symbol : input.toCharArray()) {
            visitedStates.add(currentState);
            if (transitions.containsKey(currentState) && transitions.get(currentState).containsKey(symbol)) {
                currentState = transitions.get(currentState).get(symbol);
            }
            // for humdun
            // "q0": { 'a' -> "q1" },
            // condition checks if q0 exists in the table. if it does, it checks if it has a
            // transition to any other state with key_symbol (lets say a)
        }

        visitedStates.add(currentState);
        System.out.println("Unique states visited: " + visitedStates);
    }
}
