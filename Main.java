import java.io.*;
import java.util.*;
public class Main {
	//lexical analyzer WONT be checking  for syntax error (whether ; exists, whether two identifiers are used with the smae name, IT WILL SOLELY CHECK FOR TOKENS)
    public static void main(String[] args) {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        SymbolTable symbolTable = new SymbolTable();
        Utilities utils = new Utilities();
        
//        String inputProgram = """
//                int x = 10;
//                chr y = 'A';
//                true;
//                false;
//                0my_var = 25;
//                my_var = 12 + 25;
//                dec ee = 311.22
//                strg ww = "hel$#@^&"
//                //maria
//                """;
        
        String filePath = "heuheu.mh"; // Your custom file
        StringBuilder inputProgram = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	inputProgram.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
	       
        System.out.println(utils.CYAN +  "\nUNIQUE STATES VISITED \n----------------------" + utils.RESET);
        List<String> tokens = lexer.tokenize(inputProgram.toString().replaceAll("[;]", " "),symbolTable);
        
        
        System.out.println(utils.CYAN +  "\nSYMBOL TABLE \n----------------------" + utils.RESET);
        symbolTable.display();
    	
    }
}