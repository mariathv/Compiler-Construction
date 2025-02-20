import java.util.*;

class DFA {
    private Set<String> states;
    private Map<String, Map<Character, String>> transitions;
    private String startState;
    private Set<String> finalStates;
    private Utilities utils = new Utilities();

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

    
    // RE -> NFA -> DFA 
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
        System.out.printf("%-15s%-15s%-15s\n", "State", "Symbol", "Next State");
        System.out.println("----------------------------------------");

        for (String state : transitions.keySet()) {
            for (char symbol : transitions.get(state).keySet()) {
                System.out.printf("%-15s%-15s%-15s\n", state, symbol, transitions.get(state).get(symbol));
            }
        }

        System.out.println(utils.CYAN + "\nFinal States:" + utils.RESET);
        if (finalStates.isEmpty()) {
            System.out.println("No final states.");
        } else {
            for (String finalState : finalStates) {
                System.out.println(finalState);
            }
        }
    }

    public boolean parse(String input) {
    
        String currentState = startState; //starting state of the dfa
        Set<String> visitedStates = new HashSet<>(); // to store unique visited states (for display after)
        
        for (char symbol : input.toCharArray()) {
            visitedStates.add(currentState);
            if (transitions.containsKey(currentState) && transitions.get(currentState).containsKey(symbol)) {
                currentState = transitions.get(currentState).get(symbol);
            }else {
            	return false;
            
            }
            // for humdun
            // "q0": { 'a' -> "q1" },
            // condition checks if q0 exists in the table. if it does, it checks if it has a
            // transition to any other state with key_symbol (lets say a)
        }

        visitedStates.add(currentState);
        System.out.printf("%-20s%-25s\n", utils.GREEN + input + utils.RESET, visitedStates);


        
        return finalStates.contains(currentState);
    }
}