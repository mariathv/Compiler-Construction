import java.util.*;

class NFA {
    private Set<String> states;
    private Map<String, Map<Character, Set<String>>> transitions;
    private String startState;
    private Set<String> finalStates;
    private static int stateCounter = 0; // Unique state numbering

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
        transitions.get(fromState).putIfAbsent(symbol, new HashSet<>());
        transitions.get(fromState).get(symbol).add(toState);
    }

    public void addEpsilonTransition(String fromState, String toState) {
        addTransition(fromState, '\u03B5', toState); // 'ε' transition
    }

    public void displayTransitions() {
        System.out.println("\nNFA Transition Table:");
        for (String state : transitions.keySet()) {
            for (char symbol : transitions.get(state).keySet()) {
                System.out.println(state + " -- " + symbol + " --> " + transitions.get(state).get(symbol));
            }
        }
    }	 	

    // ===========================
    // 🚀 Thompson's Construction Algorithm
    // ===========================

    public static NFA fromRegex(String regex) {
    	
        NFA nfa = new NFA();
        int counter = 0; // Start counter from 1 instead of 0
        String Start = "q" + counter; 
        nfa.setStartState(Start);
        
        for (int i = 0; i < regex.length(); i++) {
            char ch = regex.charAt(i);

            if (ch == '|') {
            	String state = "q" + counter;
            	nfa.addState(state, true);
            	i++;
            	ch = regex.charAt(i);
            	counter++;
            	state = "q" + counter;
            	nfa.addTransition("q0", ch, state);
            } 
            else if(ch == '[')
            {
            	i++;
            	for(int j = 0; ; j++,i++)
            	{
            		ch = regex.charAt(i);
            		if(ch == ']')
            		{
            			counter++;
            			break;
            		}
            		else if(ch == '-')
            		{
            			char ch1 = regex.charAt(i-1);
            			char ch2 = regex.charAt(i+1);
            			String state1 = "q" + counter;
            			String state2 = "q" + (counter+1);
            			for (char c = ch1; c <= ch2; c++) {
            				nfa.addTransition(state1, c, state2);
            			}
            			i+=1;
            		}
            		else
            		{
            			String state1 = "q" + counter;
            			String state2 = "q" + (counter+1);
            			nfa.addTransition(state1, ch, state2);
            		}
            		
            	}
            }
            else if(ch == '*')
            {
            	if(regex.charAt(i-1) != ']')
            		continue;
            	int j = i;
            	while (regex.charAt(j) != '[')
            		j--;
            	j++;
            	for(; ; j++)
            	{
            		ch = regex.charAt(j);
            		if(ch == ']')
            		{
            			break;
            		}
            		else if(ch == '-')
            		{
            			char ch1 = regex.charAt(j-1);
            			char ch2 = regex.charAt(j+1);
            			String state1 = "q" + counter;
            			String state2 = "q" + (counter);
            			for (char c = ch1; c <= ch2; c++) {
            				nfa.addTransition(state1, c, state2);
            			}
            			j+=1;
            		}
            		else
            		{
            			String state1 = "q" + counter;
            			String state2 = "q" + (counter);
            			nfa.addTransition(state1, ch, state2);
            		}
            		
            	}
            	
            }
            else {
                String newStart = "q" + counter;
                counter++;
                String newFinal = "q" + counter;

                nfa.addTransition(newStart, ch, newFinal);
            }
        }

        String Final = "q" + counter;
        nfa.addState(Final, true);
        
        return nfa;
    }



    // ===========================
    // 🎯 DFA Conversion Method (Closure-based)
    // ===========================

    public DFA toDFA() {
        DFA dfa = new DFA();
        Map<Set<String>, String> stateMapping = new HashMap<>();
        Queue<Set<String>> queue = new LinkedList<>();

        Set<String> startClosure = epsilonClosure(Set.of(startState));
        queue.add(startClosure);
        stateMapping.put(startClosure, "q0");
        dfa.setStartState("q0");

        int dfaStateCounter = 1;

        while (!queue.isEmpty()) {
            Set<String> nfaStates = queue.poll();
            String dfaState = stateMapping.get(nfaStates);

            if (!Collections.disjoint(nfaStates, finalStates)) {
                dfa.addState(dfaState, true);
            } else {
                dfa.addState(dfaState, false);
            }

            Map<Character, Set<String>> newTransitions = new HashMap<>();

            for (String nfaState : nfaStates) {
                if (transitions.containsKey(nfaState)) {
                    for (Map.Entry<Character, Set<String>> entry : transitions.get(nfaState).entrySet()) {
                        char symbol = entry.getKey();
                        newTransitions.putIfAbsent(symbol, new HashSet<>());
                        newTransitions.get(symbol).addAll(entry.getValue());
                    }
                }
            }

            for (Map.Entry<Character, Set<String>> entry : newTransitions.entrySet()) {
                char symbol = entry.getKey();
                Set<String> nextStates = epsilonClosure(entry.getValue());

                if (!stateMapping.containsKey(nextStates)) {	
                    String newDFAState = "q" + dfaStateCounter++;
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
            if (transitions.containsKey(state) && transitions.get(state).containsKey('\u03B5')) {
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
