import java.io.File;
import java.util.Hashtable;

public class Alex {
	
	private File fitxer;
	private int lineaActual;
	private char charActual;
	private Hashtable<String, String> diccionari;
	
	
	public Alex() {
		
	   diccionari = new Hashtable<String, String>();
	   diccionari.put("prog", "prog");
	   diccionari.put("fiprog", "fiprog");
	   diccionari.put("const", "const");
	   diccionari.put("var", "var");
	   diccionari.put("funcio", "funcio");
	   diccionari.put("tipus_simple", "sencer");
	   diccionari.put("tipus_simple", "logic");
	   diccionari.put("func", "func");
	   diccionari.put("fifunc", "fifunc");
	   diccionari.put("perref", "perref");
	   diccionari.put("perval", "perval");
	   diccionari.put("vector", "vector");
	   diccionari.put("de", "de");
	   diccionari.put("not", "not");
	   diccionari.put("or", "or");
	   diccionari.put("and", "and");
	   diccionari.put("ct_logica", "cert");
	   diccionari.put("ct_logica", "fals");
	   diccionari.put("escriure", "escriure");
	   diccionari.put("llegir", "llegir");
	   diccionari.put("cicle", "cicle");
	   diccionari.put("mentre", "mentre");
	   diccionari.put("fer", "fer");
	   diccionari.put("fimentre", "fimentre");
	   diccionari.put("si", "si");
	   diccionari.put("llavors", "llavors");
	   diccionari.put("sino", "sino");
	   diccionari.put("fisi", "fisi");
	   diccionari.put("retornar", "retornar");
	   diccionari.put("percada", "percada");
	   diccionari.put("en", "en");
	   diccionari.put("fiper", "fiper");
	}
	
	public Token getToken() {
		
		String lexema = "";
		String tipus = "";
		
		
		return new Token(tipus, lexema);
	}
	
	

}
