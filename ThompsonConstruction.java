import java.io.*;
import java.util.*;
class ThompsonConstruction {
    private static int stateCounter = 0;
    
    static class NFAFragment {
        String startState;
        String acceptState;
        NFA nfa;
        
        NFAFragment(NFA nfa, String startState, String acceptState) {
            this.nfa = nfa;
            this.startState = startState;
            this.acceptState = acceptState;
        }
    }

    private static String createState() {
        return "q" + (stateCounter++);
    }

    public static NFA buildNFA(String regex) {
        stateCounter = 0;
        NFAFragment fragment = parseRegex(regex);

        // Ensure numbers and identifiers start in separate states


        fragment.nfa.displayTransitions();
        return fragment.nfa;
    }

    private static NFAFragment parseRegex(String regex) {
        if (regex.isEmpty()) {
            return createEmptyNFA();
        }
        
        


        Stack<NFAFragment> fragments = new Stack<>();
        Stack<Character> operators = new Stack<>();

        if (regex.equals("[0-9][0-9]*")) {
            System.out.println("Processing number regex: " + regex);
            NFAFragment digitClass = handleCharacterClass("0-9");
            NFAFragment repeatDigits = kleeneStar(digitClass);
            fragments.push(concat(digitClass, repeatDigits));
        }
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);

            System.out.println("Processing character: " + c);
            
            switch (c) {
                case '(':
                    int closingIndex = findClosingParenthesis(regex, i);
                    if (closingIndex == -1) {
                        throw new IllegalArgumentException("Unmatched parenthesis at position " + i);
                    }
                    NFAFragment subexpr = parseRegex(regex.substring(i + 1, closingIndex));
                    fragments.push(subexpr);
                    i = closingIndex;
                    break;

                case '[':
                    int closingBracket = regex.indexOf(']', i);
                    if (closingBracket == -1) {
                        throw new IllegalArgumentException("Unmatched bracket at position " + i);
                    }
                    String charClass = regex.substring(i + 1, closingBracket);
                    System.out.println("Detected character class: " + charClass);

                    NFAFragment charClassNFA = handleCharacterClass(charClass);
                    fragments.push(charClassNFA);
                    i = closingBracket;
                    break;



                case ']':
                    throw new IllegalArgumentException("Unexpected metacharacter: ] at position " + i);

                case '|':
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        processOperator(fragments, operators.pop());
                    }
                    operators.push(c);
                    break;

                case '*':
                    if (fragments.isEmpty()) {
                        throw new IllegalArgumentException("No expression before * at position " + i);
                    }
                    fragments.push(kleeneStar(fragments.pop()));
                    break;

                case '+':
                    if (fragments.isEmpty()) {
                        throw new IllegalArgumentException("No expression before + at position " + i);
                    }
                    NFAFragment plus = fragments.pop();
                    fragments.push(concat(plus, kleeneStar(copyNFAFragment(plus))));
                    break;
                case '\'':
                    if (i + 4 < regex.length() && regex.charAt(i + 3) == '\'' && regex.charAt(i + 1) == '[') {
                        // Detected '[a-zA-Z]' inside single quotes
                        int closinggBracket = regex.indexOf(']', i + 2);
                        if (closinggBracket == -1 || closinggBracket + 1 >= regex.length() || regex.charAt(closinggBracket + 1) != '\'') {
                            throw new IllegalArgumentException("Invalid character class format: " + regex);
                        }

                        System.out.println("Processing full character literal: " + regex.substring(i, closinggBracket + 2));

                        NFAFragment openQuote = createBasicNFA('\'');  // Create NFA for opening quote
                        NFAFragment charheuClassNFA = handleCharacterClass(regex.substring(i + 2, closinggBracket)); // Process a-zA-Z
                        NFAFragment closeQuote = createBasicNFA('\'');  // Create NFA for closing quote

                        NFAFragment fullCharClass = concat(openQuote, concat(charheuClassNFA, closeQuote)); // Wrap with quotes
                        fragments.push(fullCharClass);

                        i = closinggBracket + 1; // Move past closing bracket and closing quote
                    }
                    break;

                case '?':
                    if (fragments.isEmpty()) {
                        throw new IllegalArgumentException("No expression before ? at position " + i);
                    }
                    NFAFragment optional = fragments.pop();
                    fragments.push(union(optional, createEmptyNFA()));
                    break;
                default:
                    if (isMetacharacter(c)) {
                        throw new IllegalArgumentException("Unexpected metacharacter: " + c);
                    }
                    
                    NFAFragment keywordNFA = createBasicNFA(c);
                    
                    while (i + 1 < regex.length() && !isOperator(regex.charAt(i + 1)) && regex.charAt(i + 1) != '|' && regex.charAt(i + 1) != '[' && regex.charAt(i + 1) != ']') {
                        keywordNFA = concat(keywordNFA, createBasicNFA(regex.charAt(++i)));
                    }
                    
                    fragments.push(keywordNFA);
            }
        }

        while (!operators.isEmpty()) {
            processOperator(fragments, operators.pop());
        }

        if (fragments.isEmpty()) {
            return createEmptyNFA();
        }

        return fragments.pop();
    }


    private static void processOperator(Stack<NFAFragment> fragments, char operator) {
        switch (operator) {
            case '|':
                if (fragments.size() < 2) {
                    throw new IllegalArgumentException("Not enough operands for union");
                }
                NFAFragment right = fragments.pop();
                NFAFragment left = fragments.pop();
                fragments.push(union(left, right));
                break;
            case '.':  // Concatenation
                if (fragments.size() < 2) {
                    throw new IllegalArgumentException("Not enough operands for concatenation");
                }
                NFAFragment second = fragments.pop();
                NFAFragment first = fragments.pop();
                fragments.push(concat(first, second));
                break;
        }
    }

    private static NFAFragment createBasicNFA(char symbol) {
        NFA nfa = new NFA();
        String start = createState();
        String accept = createState();
        
        nfa.addState(start, false);
        nfa.addState(accept, true);
        nfa.setStartState(start);
        nfa.addTransition(start, symbol, accept);
        
        return new NFAFragment(nfa, start, accept);
    }

    private static NFAFragment createEmptyNFA() {
        NFA nfa = new NFA();
        String state = createState();
        nfa.addState(state, true);
        nfa.setStartState(state);
        return new NFAFragment(nfa, state, state);
    }

    private static NFAFragment concat(NFAFragment first, NFAFragment second) {
        // Ensure the first NFA's accept state is no longer final
        first.nfa.setFinalState(first.acceptState, false);
        
        // Add ε-transition from first's accept state to second's start state
        first.nfa.addTransition(first.acceptState, 'ε', second.startState);
        
        // Merge second's states and transitions into first's NFA
        copyNFAStates(first.nfa, second.nfa);
        
        return new NFAFragment(first.nfa, first.startState, second.acceptState);
    }

    
    

    private static NFAFragment union(NFAFragment a, NFAFragment b) {
        NFA nfa = new NFA();
        String newStart = createState();
        String newAccept = createState();
        
        // Add new start and accept states
        nfa.addState(newStart, false);
        nfa.addState(newAccept, true);
        nfa.setStartState(newStart);
        
        // Copy states and transitions from both NFAs
        copyNFAStates(nfa, a.nfa);
        copyNFAStates(nfa, b.nfa);
        
        // Add ε-transitions from new start state to both original start states
        nfa.addTransition(newStart, 'ε', a.startState);
        nfa.addTransition(newStart, 'ε', b.startState);
        
        // Add ε-transitions from both original accept states to new accept state
        nfa.addTransition(a.acceptState, 'ε', newAccept);
        nfa.addTransition(b.acceptState, 'ε', newAccept);
        
        return new NFAFragment(nfa, newStart, newAccept);
    }

    private static NFAFragment kleeneStar(NFAFragment fragment) {
        NFA nfa = new NFA();
        String newStart = createState();
        String newAccept = createState();
        
        nfa.addState(newStart, false);
        nfa.addState(newAccept, true);
        nfa.setStartState(newStart);
        
        // Copy all states and transitions from the original NFA
        copyNFAStates(nfa, fragment.nfa);
        
        // Add ε-transitions for kleene star
        nfa.addTransition(newStart, 'ε', fragment.startState);  // Allow zero occurrences
        nfa.addTransition(newStart, 'ε', newAccept);           // Allow zero occurrences
        nfa.addTransition(fragment.acceptState, 'ε', fragment.startState);  // Allow multiple occurrences
        nfa.addTransition(fragment.acceptState, 'ε', newAccept);           // Allow one or more occurrences
        
        return new NFAFragment(nfa, newStart, newAccept);
    }

    private static NFAFragment handleCharacterClass(String charClass) {
        if (charClass.isEmpty()) {
            throw new IllegalArgumentException("Empty character class");
        }

        NFAFragment result = null;

        // Check for surrounding single quotes and remove them
        boolean hasQuotes = charClass.length() == 3 && charClass.charAt(0) == '\'' && charClass.charAt(2) == '\'';

        for (int i = 0; i < charClass.length(); i++) {
            if (i + 2 < charClass.length() && charClass.charAt(i + 1) == '-') {
                char start = charClass.charAt(i);
                char end = charClass.charAt(i + 2);

                if (start == '\\' || end == '\\' || end < start) {
                    System.out.println("Ignoring invalid range: " + start + "-" + end);
                    continue;
                }

                System.out.println("Detected range: " + start + "-" + end);
                NFAFragment rangeFragment = createCharacterRange(start, end);
                result = (result == null) ? rangeFragment : union(result, rangeFragment);
                i += 2;
            } else {
                System.out.println("Single character: " + charClass.charAt(i));
                NFAFragment charFragment = createBasicNFA(charClass.charAt(i));
                result = (result == null) ? charFragment : union(result, charFragment);
            }
        }

        // If the character class was a single quoted letter, concatenate `'` at start and end
        if (hasQuotes) {
            NFAFragment openQuote = createBasicNFA('\'');
            NFAFragment closeQuote = createBasicNFA('\'');
            result = concat(openQuote, result);
            result = concat(result, closeQuote);
        }

        return result;
    }


    private static NFAFragment createCharacterRange(char start, char end) {
        if (end < start) {
            throw new IllegalArgumentException("Invalid character range: " + start + "-" + end);
        }

        NFAFragment result = createBasicNFA(start);
        for (char c = (char)(start + 1); c <= end; c++) {
            result = union(result, createBasicNFA(c));
        }
        return result;
    }

    private static NFAFragment copyNFAFragment(NFAFragment fragment) {
        NFA newNFA = new NFA();
        copyNFAStates(newNFA, fragment.nfa);
        return new NFAFragment(newNFA, fragment.startState, fragment.acceptState);
    }

    private static void copyNFAStates(NFA target, NFA source) {
        // Copy all states
        for (String state : source.getStates()) {
            target.addState(state, source.getFinalStates().contains(state));
        }
        
        // Copy all transitions
        for (Map.Entry<String, Map<Character, Set<String>>> stateEntry : source.getTransitions().entrySet()) {
            String fromState = stateEntry.getKey();
            for (Map.Entry<Character, Set<String>> symbolEntry : stateEntry.getValue().entrySet()) {
                char symbol = symbolEntry.getKey();
                for (String toState : symbolEntry.getValue()) {
                    target.addTransition(fromState, symbol, toState);
                }
            }
        }
    }

    private static int findClosingParenthesis(String regex, int openIndex) {
        int count = 1;
        for (int i = openIndex + 1; i < regex.length(); i++) {
            if (regex.charAt(i) == '(') count++;
            else if (regex.charAt(i) == ')') count--;
            if (count == 0) return i;
        }
        return -1;
    }

    private static boolean isMetacharacter(char c) {
        return "()[]*+?|".indexOf(c) != -1;
    }

    private static boolean isOperator(char c) {
        return "*+?|".indexOf(c) != -1;
    }
}