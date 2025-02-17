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
        
//        String inputProgram = """
//                intgr x = 10;
//                chr y = 'A';
//                boo b = true;
//                dcl z = 2.1;
//        		dcl e = 3.11115;
//        		intgr e = 2^2;
//                true;
//                false;
//                my_var = 25;
//                inp(num);
//                out("yaya");
//                out(numm);
//                string = "i want to kill myself"
//                ## dont do this
//                ## kys please
//        		   dont do this yes hahaj
//                """;
        
        System.out.println("Tokenized Output:");
        List<String> tokens = lexer.tokenize(inputProgram.replaceAll("[;]", " "));
        for (String token : tokens) {
            System.out.println(token);
        }
    }
}