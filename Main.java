public class Main {

    /*
     * WORK FLOW FOR RE NFA DFA
     */
    public static void main(String[] args) {
        // RE to NFA later
        NFA nfa = new NFA();
        nfa.setStartState("q0");
        nfa.addState("q1", false);
        nfa.addState("q2", true);

        nfa.addTransition("q0", 'a', "q1");
        nfa.addTransition("q1", 'b', "q2");
        nfa.addTransition("q2", 'a', "q2");

        nfa.displayTransitions();

        nfa.parse("aba");

        DFA dfa = new DFA();
        dfa.setStartState("q0");
        dfa.addState("q1", false);
        dfa.addState("q2", true);

        dfa.addTransition("q0", 'a', "q1");
        dfa.addTransition("q1", 'b', "q2");
        dfa.addTransition("q2", 'a', "q2"); 

        dfa.displayTransitions();

        dfa.parse("aba");
    }
}
