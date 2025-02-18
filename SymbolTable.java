import java.util.*;

public class SymbolTable {
	  static class Symbol {
	        private final String name;
	        private final String type;
	        private final int scope;
	        private final int memoryAddress;
	        private final String value;

	        public Symbol(String name, String type, int scope, int memoryAddress, String value) {
	            this.name = name;
	            this.type = type;
	            this.scope = scope;
	            this.memoryAddress = memoryAddress;
	            this.value = value;
	        }

	        public String getName() {
	            return name;
	        }

	        public String getType() {
	            return type;
	        }

	        public int getScope() {
	            return scope;
	        }

	        public int getMemoryAddress() {
	            return memoryAddress;
	        }

	        public String getValue() {
	            return value;
	        }
	}
	  
	 
    private final List<Symbol> table;  
    private int memoryCounter = 1000; // simulated memory, handle real mem later>>?
    private int currentScope = 0; // 0 for global , 1 for local
    private Utilities utils = new Utilities();
    
    public SymbolTable() {
        this.table = new ArrayList<>();
    }

    public void insert(String name, String type, int scope, String value) {
        Symbol symbol = new Symbol(name, type, scope, memoryCounter++, value);
        table.add(symbol);
    }

    public Symbol lookup(String name) {
        for (Symbol symbol : table) {
            if (symbol.getName().equals(name)) {
                return symbol;
            }
        }
        return null; 
    }

    public int getCurrentScope() {
        return currentScope;
    }

    public void display() {
        System.out.printf("%-10s %-15s %-10s %-10s %-10s\n", "Name", "Type", "Scope", "Address", "Value");
        System.out.println("---------------------------------------------------");

        for (Symbol symbol : table) {
        	System.out.printf("%-10s %-15s %-10s %-10d %-10s\n",
        		    symbol.getName(), symbol.getType(),
        		    symbol.getScope() == 0 ? "Global" : "Local", 
        		    symbol.getMemoryAddress(),
        		    symbol.getValue() == null ? "null" : symbol.getValue());

        }
    }

  
}
