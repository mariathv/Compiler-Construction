import java.io.*;
import java.util.*;

class LexicalAnalyzer {
	
    
    private DFA identifierDFA, integerDFA, keywordDFA, charDFA, operatorDFA;
    private Set<String> keywords = new HashSet<>(Arrays.asList("true", "false", "intgr", "chr"));
    private ErrorHandler errHandler = new ErrorHandler();
    private Utilities utils = new Utilities();

    public LexicalAnalyzer() {
        identifierDFA = createIdentifierNFA().toDFA();
        integerDFA = createIntegerNFA().toDFA();
        charDFA = createCharacterNFA().toDFA();
        operatorDFA = createOperatorNFA().toDFA();
    }

    private NFA createIdentifierNFA() {
        NFA identifierNFA = new NFA();
        identifierNFA.setStartState("q0");
        identifierNFA.addState("q1", true);

        for (char c = 'a'; c <= 'z'; c++) {
            identifierNFA.addTransition("q0", c, "q1");
            identifierNFA.addTransition("q1", c, "q1");
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            identifierNFA.addTransition("q0", c, "q1");
            identifierNFA.addTransition("q1", c, "q1");
        }
        identifierNFA.addTransition("q0", '_', "q1");
        identifierNFA.addTransition("q1", '_', "q1");

        for (char c = '0'; c <= '9'; c++) {
            identifierNFA.addTransition("q1", c, "q1");
        }
        return identifierNFA;
    }

    private NFA createIntegerNFA() {
        NFA nfa = new NFA();
        nfa.setStartState("q0");
        nfa.addState("q1", true);

        for (char c = '0'; c <= '9'; c++) {
            nfa.addTransition("q0", c, "q1");
            nfa.addTransition("q1", c, "q1");
        }
        return nfa;
    }

    private NFA createCharacterNFA() {
        NFA nfa = new NFA();
        nfa.setStartState("q0");
        nfa.addState("q3", true);

        nfa.addTransition("q0", '\'', "q1");
        for (char c = 32; c <= 126; c++) {
            if (c != '\'')
                nfa.addTransition("q1", c, "q2");
        }
        nfa.addTransition("q2", '\'', "q3");
        return nfa;
    }

    private NFA createOperatorNFA() {
        NFA nfa = new NFA();
        nfa.setStartState("q0");
        nfa.addState("q1", true);
        nfa.addTransition("q0", '=', "q1");
        return nfa;
    }

    public List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        String[] lines = input.split("\n");  // split input by lines
        int lineNumber = 1;

        for (String line : lines) {
            String[] words = line.split("\\s+");  // split words by spaces

            for (String word : words) {
                if (word.isEmpty()) continue;  

                if (keywords.contains(word)) {
                    tokens.add("KEYWORD: " + word);
                } else if (identifierDFA.parse(word)) {
                    tokens.add("IDENTIFIER: " + word);
                } else if (integerDFA.parse(word)) {
                    tokens.add("INTEGER: " + word);
                } else if (charDFA.parse(word)) {
                    tokens.add("CHARACTER: " + word);
                } else if (operatorDFA.parse(word)) {
                    tokens.add("OPERATOR: " + word);
                } else {
                	

                    errHandler.setError(utils.RED + "Unrecognized Token on Line " + lineNumber + utils.RESET, lineNumber);
                    errHandler.displayErr();
                    tokens.add("UNKNOWN (Line " + lineNumber + "): " + word);
                }
            }
            lineNumber++;  
        }
        return tokens;
    }
}
