import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.*;


public class LexicalAnalyzerCLI {
    
    private static SymbolTable symbolTable = new SymbolTable();
    private static LexicalAnalyzer analyzer = new LexicalAnalyzer();
    private static Scanner scanner = new Scanner(System.in);
    private static List<String> tokens = new ArrayList<>();
    private static Utilities utils = new Utilities();

    
    public static void main(String[] args) {
        int choice;
        utils.clearScreen();
        while (true) {
            utils.displayMenu("LEXICAL ANALYZER");
            System.out.println(utils.YELLOW + "(1)" + utils.RESET + " * Analyze Code");
            System.out.println(utils.YELLOW + "(2)" + utils.RESET + " * Input From File");
            System.out.println(utils.YELLOW + "(3)" + utils.RESET + " * View Token List");
            System.out.println(utils.YELLOW + "(4)" + utils.RESET + " * View Symbol Table");
            System.out.println(utils.YELLOW + "(5)" + utils.RESET + " * Display Transitions");
            System.out.println(utils.YELLOW + "(0)" + utils.RESET + " * Exit");
            System.out.print("Enter your choice: ");
            
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                	utils.clearScreen();
                    analyzeCode();
                    break;
                case 2:
                	inputFromFile();
                	break;
                case 3:
                    displayTokens();
                    break;
                case 4:
                    displaySymbolTable();
                    break;
                case 5:
                	displayTransitionTables();
                	break;
                case 0:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;  
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void inputFromFile() {
        System.out.println("Enter File name (.mh //" + utils.YELLOW + " must be in the same directory" + utils.RESET + ") : ");
        
        String fileName;
        while (true) { 
            fileName = scanner.nextLine().trim();
            
            if (!fileName.endsWith(".mh")) {
                System.err.println(utils.RED + "Error: Invalid file type! Please enter a .mh file." + utils.RESET);
                System.out.println("Try again (.mh file required): ");
                continue;
            }

            File file = new File(fileName);
            if (!file.exists()) {
                System.err.println(utils.RED + "Error: File not found! Ensure it exists in the same directory." + utils.RESET);
                System.out.println("Try again (.mh file required): ");
                continue; 
            }

            break; //
        }

        StringBuilder inputProgram = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                inputProgram.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        System.out.println(utils.CYAN + "\nUNIQUE STATES VISITED \n----------------------" + utils.RESET);
        tokens = analyzer.tokenize(inputProgram.toString().replaceAll("[;]", " "), symbolTable);
        System.out.println(utils.GREEN + "\nTotal Tokens: " + tokens.size() + utils.RESET);
        displaySymbolTable();
    }

    private static void analyzeCode() {
        System.out.println("Enter the source code to analyze (type 'END' on a new line to finish):");
        
        StringBuilder inputCode = new StringBuilder();
        String line;
        
        while (true) {
            line = scanner.nextLine();
            
            if (line.equals("END")) {
                break;
            }

            inputCode.append(line).append("\n");
        }
        System.out.println(utils.CYAN +  "\nUNIQUE STATES VISITED \n----------------------" + utils.RESET);
        tokens = analyzer.tokenize(inputCode.toString(), symbolTable);
        System.out.println(utils.GREEN + "\nTotal Tokens: " + tokens.size() + utils.RESET);
        
        displaySymbolTable();
    }

    private static void displayTokens() {
    	System.out.println(utils.CYAN +  "\nTOKENS LIST \n----------------------" + utils.RESET);
        if (tokens.isEmpty()) {
            System.out.println("No tokens generated. Please analyze code first.");
            return;
        }
        for (String token : tokens) {
            System.out.println(token);
        }
    }
    
    private static void displayTransitionTables() {
    	System.out.println(utils.CYAN +  "\n** Display Transition Table For **\n----------------------" + utils.RESET);
    	System.out.println("(1) ** Identifiers\n(2) ** Keywords\n(3) ** Operators\n(4) ** Integer\n(5) ** Character\n");
    	
    	int choice = scanner.nextInt();
    	
    	if(choice == 1) {
    		analyzer.identifierDFA.displayTransitions();
    	}else if(choice ==2) {
    		analyzer.keywordDFA.displayTransitions(); 
    	}
    	else if(choice ==3) {
    		analyzer.operatorDFA.displayTransitions();
    	}
    	else if(choice ==4) {
    		analyzer.integerDFA.displayTransitions();
    	}else if(choice == 5) {
    		analyzer.charDFA.displayTransitions();
    	}
    }

    private static void displaySymbolTable() {
    	System.out.println(utils.CYAN +  "\nSYMBOL TABLE \n----------------------" + utils.RESET);
        symbolTable.display();
    }
}
