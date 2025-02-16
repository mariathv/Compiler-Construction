import java.io.*;
import java.util.*;

class LexicalAnalyzer {
    private DFA identifierDFA, integerDFA, keywordDFA, charDFA, operatorDFA;
    private Set<String> keywords = new HashSet<>(Arrays.asList("true", "false", "intgr", "chr"));

    public LexicalAnalyzer() {
        identifierDFA = createIdentifierDFA();
        integerDFA = createIntegerDFA();
        charDFA = createCharacterDFA();
        operatorDFA = createOperatorDFA();
    }

    private DFA createIdentifierDFA() {
    	DFA identifierDFA = new DFA();
    	identifierDFA.setStartState("q0");
    	identifierDFA.addState("q1", true);  
    	identifierDFA.addState("q2", true);  

    	for (char c = 'a'; c <= 'z'; c++) {
    	    identifierDFA.addTransition("q0", c, "q1");
    	    identifierDFA.addTransition("q1", c, "q2");
    	    identifierDFA.addTransition("q2", c, "q2");
    	}
    	for (char c = 'A'; c <= 'Z'; c++) {
    	    identifierDFA.addTransition("q0", c, "q1");
    	    identifierDFA.addTransition("q1", c, "q2");
    	    identifierDFA.addTransition("q2", c, "q2");
    	}
    	identifierDFA.addTransition("q0", '_', "q1");
    	identifierDFA.addTransition("q1", '_', "q2");
    	identifierDFA.addTransition("q2", '_', "q2");

    	for (char c = '0'; c <= '9'; c++) {
    	    identifierDFA.addTransition("q1", c, "q2");
    	    identifierDFA.addTransition("q2", c, "q2");
    	}
        return identifierDFA;
    }

    private DFA createIntegerDFA() {
        DFA dfa = new DFA();
        dfa.setStartState("q0");
        dfa.addState("q1", true);

        for (char c = '0'; c <= '9'; c++) {
            dfa.addTransition("q0", c, "q1");
            dfa.addTransition("q1", c, "q1");
        }
        return dfa;
    }

    private DFA createCharacterDFA() {
        DFA dfa = new DFA();
        dfa.setStartState("q0");
        dfa.addState("q3", true);

        dfa.addTransition("q0", '\'', "q1");
        for (char c = 32; c <= 126; c++) {
            if (c != '\'')
                dfa.addTransition("q1", c, "q2");
        }
        dfa.addTransition("q2", '\'', "q3");
        return dfa;
    }

    private DFA createOperatorDFA() {
        DFA dfa = new DFA();
        dfa.setStartState("q0");
        dfa.addState("q1", true);
        dfa.addTransition("q0", '=', "q1");
        return dfa;
    }

    public List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        String[] words = input.split("\\s+");

        for (String word : words) {
            if (keywords.contains(word)) {
                tokens.add("KEYWORD: " + word);
            } else if (identifierDFA.parse(input)) {
                tokens.add("IDENTIFIER: " + word);
            } else if (integerDFA.parse(word)) {
                tokens.add("INTEGER: " + word);
            } else if (charDFA.parse(word)) {
                tokens.add("CHARACTER: " + word);
            } else if (operatorDFA.parse(word)) {
                tokens.add("OPERATOR: " + word);
            } else {
                tokens.add("UNKNOWN: " + word);
            }
        }
        return tokens;
    }
}