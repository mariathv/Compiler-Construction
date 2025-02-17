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
    //closure method for converting to DFA
    public DFA toDFA() {
        DFA dfa = new DFA();
        Map<Set<String>, String> stateMapping = new HashMap<>(); // in form {{set(reachable via null) == null closure}, state} e.g {{q2,q3}, q1}
        Queue<Set<String>> queue = new LinkedList<>();
        
        Set<String> startClosure = epsilonClosure(Set.of(startState)); //compute the null closure oif the starting state (init stepp)
        queue.add(startClosure); 
        stateMapping.put(startClosure, "q0"); //mapping to state
        dfa.setStartState("q0");
        
        int stateCounter = 1; //counter for dfa states
        
        while (!queue.isEmpty()) {
            Set<String> nfaStates = queue.poll();
            String dfaState = stateMapping.get(nfaStates);
            
            if (!Collections.disjoint(nfaStates, finalStates)) { //disjoint == if any of the nfaStates matches finalStates, mark it as a final state
                dfa.addState(dfaState, true);
            } else {
                dfa.addState(dfaState, false);
            }
            
            Map<Character, Set<String>> newTransitions = new HashMap<>();
            
            //grouping nfa states by SYMBOL
            for (String nfaState : nfaStates) { // suppose {q0, q1} , iterate thru both
                if (transitions.containsKey(nfaState)) { //check if transition tables has entries with the nfa state, or in other words if the state has any transition
                    for (Map.Entry<Character, Set<String>> entry : transitions.get(nfaState).entrySet()) {//get all transitions in form q0 = {'a' -> {q1,q2}, 'b' -> {q1,q3}} 
                        char symbol = entry.getKey(); //entry in format :: { a -> {q0, q1}} , extract its symbol
                        newTransitions.putIfAbsent(symbol, new HashSet<>());
                        newTransitions.get(symbol).addAll(entry.getValue());
                    }
                }
            }
            //find the next nfa states reachable by that symbol
            for (Map.Entry<Character, Set<String>> entry : newTransitions.entrySet()) {
                char symbol = entry.getKey();
                Set<String> nextStates = epsilonClosure(entry.getValue());
                //if not already mapped, create a new state 
                if (!stateMapping.containsKey(nextStates)) {
                    String newDFAState = "q" + stateCounter++;
                    stateMapping.put(nextStates, newDFAState);
                    queue.add(nextStates);
                }
                
                dfa.addTransition(dfaState, symbol, stateMapping.get(nextStates));
            }
        }
        
        return dfa;
    }
    
    private Set<String> epsilonClosure(Set<String> states) {
        Stack<String> stack = new Stack<>();
        Set<String> closure = new HashSet<>(states);
        stack.addAll(states);
        
        while (!stack.isEmpty()) {
            String state = stack.pop();
            if (transitions.containsKey(state) && transitions.get(state).containsKey('\u03B5')) { // ε transitions
                for (String next : transitions.get(state).get('\u03B5')) {
                    if (!closure.contains(next)) {
                        closure.add(next);
                        stack.push(next);
                    }
                }
            }
        }
        
        return closure;
    }
}
