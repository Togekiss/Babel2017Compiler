import java.io.File;
import java.util.Hashtable;

public class Alex {
	
	private File fitxer;
	private int lineaActual;
	private char charActual;
	private Hashtable diccionari;
	
	
	public Alex() {
		
	}
	
	public Token getToken() {
		
		String lexema = "";
		String tipus = "";
		
		
		return new Token(tipus, lexema);
	}
	
	

}
