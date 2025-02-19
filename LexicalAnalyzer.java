import java.io.*;
import java.util.*;

class LexicalAnalyzer {
	
    
    public DFA identifierDFA, integerDFA, keywordDFA, charDFA, operatorDFA, decimalDFA, stringDFA;
    private ErrorHandler errHandler = new ErrorHandler();
    private Utilities utils = new Utilities();

    public LexicalAnalyzer() {
    	keywordDFA =  RegularExpression.getNFAForType("KEYWORDS").toDFA();
 //   	RegularExpression.getNFAForType("KEYWORDS").displayTransitions();	
//    	keywordDFA.displayTransitions();
    	
    	identifierDFA = RegularExpression.getNFAForType("IDENTIFIERS").toDFA();    	
//    	RegularExpression.getNFAForType("IDENTIFIERS").displayTransitions();
//    	identifierDFA.displayTransitions();
//        
        integerDFA = RegularExpression.getNFAForType("CONSTANTS").toDFA();
//        RegularExpression.getNFAForType("CONSTANTS").displayTransitions();
//        integerDFA.displayTransitions();
   
        
        charDFA = RegularExpression.getNFAForType("CHARACTER").toDFA();
//        RegularExpression.getNFAForType("CHARACTER").displayTransitions();
//        charDFA.displayTransitions();
        
        operatorDFA = RegularExpression.getNFAForType("OPERATORS").toDFA();
//        RegularExpression.getNFAForType("OPERATORS").displayTransitions();
//        operatorDFA.displayTransitions();
        
        decimalDFA = RegularExpression.getNFAForType("DECIMAL").toDFA();
        stringDFA = RegularExpression.getNFAForType("LITERAL").toDFA();
        //RegularExpression.getNFAForType("LITERAL").displayTransitions();
    }

//    private NFA createIdentifierNFA() {
//        NFA identifierNFA = new NFA();
//        identifierNFA.setStartState("q0");
//        identifierNFA.addState("q1", true);
//
//        for (char c = 'a'; c <= 'z'; c++) {
//            identifierNFA.addTransition("q0", c, "q1");
//            identifierNFA.addTransition("q1", c, "q1");
//        }
//        for (char c = 'A'; c <= 'Z'; c++) {
//            identifierNFA.addTransition("q0", c, "q1");
//            identifierNFA.addTransition("q1", c, "q1");
//        }
//        identifierNFA.addTransition("q0", '_', "q1");
//        identifierNFA.addTransition("q1", '_', "q1");
//
//        for (char c = '0'; c <= '9'; c++) {
//            identifierNFA.addTransition("q1", c, "q1");
//        }
//        return identifierNFA;
//    }
//
//    private NFA createIntegerNFA() {
//        NFA nfa = new NFA();
//        nfa.setStartState("q0");
//        nfa.addState("q1", true);
//
//        for (char c = '0'; c <= '9'; c++) {
//            nfa.addTransition("q0", c, "q1");
//            nfa.addTransition("q1", c, "q1");
//        }
//        return nfa;
//    }
//
//    private NFA createCharacterNFA() {
//        NFA nfa = new NFA();
//        nfa.setStartState("q0");
//        nfa.addState("q3", true);
//
//        nfa.addTransition("q0", '\'', "q1");
//        for (char c = 32; c <= 126; c++) {
//            if (c != '\'')
//                nfa.addTransition("q1", c, "q2");
//        }
//        nfa.addTransition("q2", '\'', "q3");
//        return nfa;
//    }
//
//    private NFA createOperatorNFA() {
//        NFA nfa = new NFA();
//        nfa.setStartState("q0");
//        nfa.addState("q1", true);
//        nfa.addTransition("q0", '=', "q1");
//        nfa.addTransition("q0", '+', "q1");
//        nfa.addTransition("q0", '-', "q1");
//        nfa.addTransition("q0", '%', "q1");
//        nfa.addTransition("q0", '/', "q1");
//        return nfa;
//    }
    
    public String removeDeComments(String input) {
        // Remove single-line comments (//...)
        input = input.replaceAll("//.*", "");

        // Remove multi-line comments (/* ... */)
        input = input.replaceAll("/\\*.*?\\*/", "");

        return input;
    }


    public List<String> tokenize(String input, SymbolTable symbolTable) {
        List<String> tokens = new ArrayList<>();
        
        input = input.replace(";", "");

        input = removeDeComments(input);
        String[] lines = input.split("\n");
        int lineNumber = 1;
        

        for (String line : lines) {
            String[] words = line.split("\\s+");

            for (String word : words) {
                if (word.isEmpty()) continue;

                if (keywordDFA.parse(word)) { //DFA here later
                    tokens.add("KEYWORD: " + word);
                    symbolTable.insert(word, "KEYWORD", symbolTable.getCurrentScope(), null);
                } else if (identifierDFA.parse(word)) {
                    tokens.add("IDENTIFIER: " + word);
                    symbolTable.insert(word, "IDENTIFIER", symbolTable.getCurrentScope(), null);
                } else if (integerDFA.parse(word)) {
                    tokens.add("INTEGER: " + word);
                    symbolTable.insert(word, "INTEGER", symbolTable.getCurrentScope(), word);
                }else if(decimalDFA.parse(word)) {
                	tokens.add("DECIMAL: " + word);
                    symbolTable.insert(word, "DECIMAL", symbolTable.getCurrentScope(), word);	
                } else if (charDFA.parse(word)) {
                    tokens.add("CHARACTER: " + word);
                    symbolTable.insert(word, "CHARACTER", symbolTable.getCurrentScope(), word);
                } else if (stringDFA.parse(word)){ 
                	tokens.add("LITERAL: " + word);
                	symbolTable.insert(word, "LITERAL", symbolTable.getCurrentScope(), word);
                } else if (operatorDFA.parse(word)) {
                    tokens.add("OPERATOR: " + word);
                    symbolTable.insert(word, "OPERATOR", symbolTable.getCurrentScope(), null);
                } else {
                	errHandler.setError(utils.RED + "Unrecognized Token on Line " + lineNumber + " ** ( - " + word +  " ) " + utils.RESET, lineNumber);
                    errHandler.displayErr();
                    tokens.add("UNKNOWN (Line  " + lineNumber + "): " + word);
                }
            }
            lineNumber++;
        }
        return tokens;
    }

}
