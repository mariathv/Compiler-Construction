
public class Utilities {
	public  final String RESET = "\u001B[0m";
    public  final String BLACK = "\u001B[30m";
    public  final String RED = "\u001B[31m";
    public  final String GREEN = "\u001B[32m";
    public  final String YELLOW = "\u001B[33m";
    public  final String BLUE = "\u001B[34m";
    public  final String PURPLE = "\u001B[35m";
    public  final String CYAN = "\u001B[36m";
    public  final String WHITE = "\u001B[37m";
    public  final String YELLOW_BG = "\033[43m";
    
    Utilities() {
    	
    }
    
    public void displayMenu(String heading) {
        int boxWidth = heading.length() + 4;
        System.out.print(CYAN);
        printBorder(boxWidth, '┌', '┐');
        System.out.println("│ " + YELLOW + heading + PURPLE + " │");
        printBorder(boxWidth, '└', '┘');
        System.out.print(RESET);
    }
    
    private void printBorder(int width, char leftCorner, char rightCorner) {
        System.out.print(leftCorner);
        for (int i = 0; i < width - 2; i++) {
            System.out.print("─");
        }
        System.out.println(rightCorner);
    }
    
    public  void clearScreen() {
        // ANSI escape code to clear the screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
