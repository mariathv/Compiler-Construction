import java.util.*;

class RegularExpression {
    private String pattern;
    private static final Map<String, String> regexPatterns = new HashMap<>();

    static {
        // Predefined Regular Expressions
    	regexPatterns.put("KEYWORDS", "true|false|intgr|chr|dec|boo|strg|input|output");
        regexPatterns.put("IDENTIFIERS", "[a-z_][a-z0-9_]*");
        regexPatterns.put("CONSTANTS", "[0-9][0-9]*");
        regexPatterns.put("OPERATORS", "+|-|*|%|=");
        regexPatterns.put("CHARACTER", "'[a-zA-Z]'");
        regexPatterns.put("DECIMAL", "[0-9][0-9]*.[0-9][0-9]*");
        regexPatterns.put("LITERAL", "\"[!-@^-~][!-@^-~]*\"");
    }		

    public RegularExpression(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public NFA toNFA() {
        return NFA.fromRegex(pattern);
    }

    public static NFA getNFAForType(String type) {
        if (!regexPatterns.containsKey(type)) {
            throw new IllegalArgumentException("Invalid regex type: " + type);
        }
        return NFA.fromRegex(regexPatterns.get(type));
    }

    public static void displayRegexPatterns() {
        System.out.println("\nStored Regular Expressions:");
        regexPatterns.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
