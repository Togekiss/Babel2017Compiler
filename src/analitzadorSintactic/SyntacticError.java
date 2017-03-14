package analitzadorSintactic;

@SuppressWarnings("serial")
public class SyntacticError extends Exception{

	public SyntacticError() { super(); }
	
	public SyntacticError(String message) { super(message); }
	
	public SyntacticError(String message, Throwable cause) { super(message, cause); }
	
	public SyntacticError(Throwable cause) { super(cause); }
	
}
