import java.io.*;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        
        String inputProgram = """
                intgr x = 10;
                chr y = 'A';
                true;
                false;
                my_var = 25;
                """;
        
        System.out.println("Tokenized Output:");
        List<String> tokens = lexer.tokenize(inputProgram.replaceAll("[;]", " "));
        for (String token : tokens) {
            System.out.println(token);
        }
    }
}