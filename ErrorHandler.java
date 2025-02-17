
public class ErrorHandler {
	String error;
	int line;
	
	Utilities utils = new Utilities();
	
	ErrorHandler (){
		line = -1;
		error = "";
	}
	
	void setError(String err, int Line){
		error = err;
		line = Line;
	}
	
	void displayErr() {
		System.out.println(utils.YELLOW + "(" + line + ")  **  " + error);
	}
}
