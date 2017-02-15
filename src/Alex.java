import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Alex {
	
	private BufferedReader br;
	private int liniaActual;
	private char charActual;
	private Hashtable<String, String> diccionari;
	
	
	public Alex(String nomFitxer) {
		
	   try {
		   
		br = new BufferedReader(new FileReader(nomFitxer));
		charActual = (char) br.read();
		liniaActual = 0;
		
	   } catch (IOException e) {
		e.printStackTrace();
	   }
	     
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
		
		while (charActual == '\n' || charActual == '\t' || charActual == ' ' ) {
			if (charActual == '\n') liniaActual++;
			try {
				charActual = (char) br.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		switch (charActual) {
		
		case ('"'): break;
		case ('/'): break;
		case ('='): break;
		case ('>'): break;
		case ('<'): break;
		case (';'): break;
		case (','): break;
		case (':'): break;
		case ('('): break;
		case (')'): break;
		case ('['): break;
		case (']'): break;
		case ('+'): break;
		case ('*'): break;
		case ('-'): break;
		case ('?'): break;
		case ('\u001a'): break;
		default:
			if (esLletra(charActual)) {
				//es lletra
			} else if (esDigit(charActual)) {
				//es digit
			} else {
				//error
			}
		}
		
		
		return new Token(tipus, lexema);
	}
	
	private boolean esLletra (char c) {
		return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
	}
	
	private boolean esDigit (char c) {
		return (c >= '0' && c <= '9');
	}
	
	public int getLiniaActual () { return liniaActual; }
	
}
