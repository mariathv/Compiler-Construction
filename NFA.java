import java.util.*;

class NFA {
    private Set<String> states;
    private Map<String, Map<Character, Set<String>>> transitions;
    private String startState;
    private Set<String> finalStates;

    public NFA() {
        this.states = new HashSet<>();
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
        transitions.get(fromState).putIfAbsent(symbol, new HashSet<>()); // using put if absent here, cuz nfa can have
                                                                         // multiple transitions (from state and symbol)
        transitions.get(fromState).get(symbol).add(toState);
    }

    // explainationnn
    /*
     * suppose q1 was not in table
     * 1)
     * first line checks if its absent or not from transitions map which is map
     * inside a map
     * if its absent it puts the 'from state' and a new hashmap (corresponding) else
     * it skips
     * so the table becomes like
     * {
     * "q0" : {<empty hashmap>}
     * }
     * 
     * 2) second line, it checks if for "q0" (its gets like
     * transitions.get(fromState)) has a transition for the symbol given or not?
     * again putifAbsent is used cause q0 also has a map in its value (key : value);
     * suppose q0 is empty and symbol 'a' transition we have too add:
     * {
     * "q0" : {"a" : <empty>}
     * 
     * 3) last line simply adds the toState in the "symbol" we just added
     * q0" : {"a" : "q1"} (note that we are adding it to the value of "a" symbol
     * which is a SET this time)
     * 
     * }
     */

    public void displayTransitions() {
        System.out.println("\nNFA Transition Table:");
        for (String state : transitions.keySet()) {
            for (char symbol : transitions.get(state).keySet()) {
                System.out.println(state + " -- " + symbol + " --> " + transitions.get(state).get(symbol));
            }
        }
    }

    public void parse(String input) {
        Set<String> currentStates = new HashSet<>();
        currentStates.add(startState);
        Set<String> visitedStates = new HashSet<>();

        char[] symbols = input.toCharArray();
        for (int i = 0; i < symbols.length; i++) {
            char symbol = symbols[i];
            Set<String> nextStates = new HashSet<>();

            for (String state : currentStates) { // can be multuple cuz this is nfa
                visitedStates.add(state);
                if (transitions.containsKey(state) && transitions.get(state).containsKey(symbol)) { // thsi checks if
                                                                                                    // transition table
                                                                                                    // has the state
                                                                                                    // (which it should
                                                                                                    // duh) and then it
                                                                                                    // checks if that
                                                                                                    // state has any
                                                                                                    // transition to
                                                                                                    // another state on
                                                                                                    // the SYMBOL that
                                                                                                    // is currently
                                                                                                    // being parsed
                    nextStates.addAll(transitions.get(state).get(symbol));
                    // add it in the nextStates set (SET CUZ WE NEED UNIQUENESS)
                }
            }

            currentStates = nextStates;
        }

        System.out.println("Unique states visited: " + visitedStates);
    }
}
