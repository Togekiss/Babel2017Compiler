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
		diccionari.put("sencer", "tipus_simple");
		diccionari.put("logic", "tipus_simple");
		diccionari.put("func", "func");
		diccionari.put("fifunc", "fifunc");
		diccionari.put("perref", "perref");
		diccionari.put("perval", "perval");
		diccionari.put("vector", "vector");
		diccionari.put("de", "de");
		diccionari.put("not", "not");
		diccionari.put("or", "or");
		diccionari.put("and", "and");
		diccionari.put("cert", "ct_logica");
		diccionari.put("fals", "ct_logica");
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

		String lexema = "null";
		String tipus = "null";
		boolean error = true;

		while (error) {

			lexema = "";
			error = false;
			
			try {
				
				while (charActual == '\n' || charActual == '\t' || charActual == ' ' ) {
					if (charActual == '\n') liniaActual++;
					charActual = (char) br.read();
				}

				switch (charActual) {

				case ('"'):
					do {
						lexema = lexema + charActual;
						charActual = (char) br.read();
					} while (charActual != '"');
				
					lexema = lexema + charActual;
					tipus = "ct_cadena";
					charActual = (char) br.read();
					
					break;
					
				case ('/'):
					charActual = (char) br.read();
				
					if (charActual == '/') {
						while (charActual != '\n') charActual = (char) br.read();
					} else {
						tipus = "divisio";
						lexema = "/";
					}
					
					break;
					
				case ('='): 
					charActual = (char) br.read();
				
					if (charActual == '=') {
						tipus = "igual_que";
						lexema = "==";
						charActual = (char) br.read();
					} else {
						tipus = "igual";
						lexema = "=";
					}
					
					break;
					
				case ('>'):
					charActual = (char) br.read();
					if (charActual == '=') {
						tipus = "major_igual_que";
						lexema = ">=";
						charActual = (char) br.read();
					} else {
						tipus = "major_que";
						lexema = ">";
					}
					
					break;
					
				case ('<'):
					charActual = (char) br.read();
				
					if (charActual == '>') {
						tipus = "diferent_que";
						lexema = "<>";
						charActual = (char) br.read();
					} else if (charActual == '=') {
						tipus = "menor_igual_que";
						lexema = "<=";
						charActual = (char) br.read();
					} else {
						tipus = "menor_que";
						lexema = "<";
					}
					
					break;
					
				case (';'): 
					tipus = "punt_i_coma";
					lexema = ";";
					charActual = (char) br.read();
					
					break;
					
				case (','): 
					tipus = "coma";
					lexema = ",";
					charActual = (char) br.read();
					
					break;
					
				case (':'): 
					tipus = "dos_punts";
					lexema = ":";
					charActual = (char) br.read();
					
					break;
					
				case ('('):
					tipus = "parentesi_obert";
					lexema = "(";
					charActual = (char) br.read();
					
					break;
					
				case (')'):
					tipus = "parentesi_tancat";
					lexema = ")";
					charActual = (char) br.read();
					
					break;
					
				case ('['):
					tipus = "claudator_obert";
					lexema = "(";
					charActual = (char) br.read();
					
					break;
					
				case (']'):
					tipus = "claudator_tancat";
					lexema = "]";
					charActual = (char) br.read();
					
					break;
					
				case ('+'):
					tipus = "suma";
					lexema = "+";
					charActual = (char) br.read();
					
					break;
					
				case ('*'):
					tipus = "multiplicacio";
					lexema = "*";
					charActual = (char) br.read();
					
					break;
					
				case ('.'):
					charActual = (char) br.read();
					
					if (charActual == '.') {
						tipus = "rang";
						lexema = "..";
						charActual = (char) br.read();
					} else {
						error = true;
						Error.escriuError(1, ".", liniaActual);
					}
					
				case ('-'):
					tipus = "resta";
					lexema = "-";
					charActual = (char) br.read();
					
					break;
					
				case ('?'):
					tipus = "interrogant";
					lexema = "?";
					charActual = (char) br.read();
					
					break;
					
				case ('\u001a'):
					tipus = "EOF";
					lexema = "EOF";
					
					break;
					
				default:
					if (esLletra(charActual)) {
						do {
							lexema = lexema + charActual;
							charActual = (char) br.read();
						} while (esLletra(charActual) || esDigit(charActual) || charActual == '_');
						
						lexema = lexema.toLowerCase();
						
						if (diccionari.containsKey(lexema)) {
							tipus = diccionari.get(lexema);
						} else {
							tipus = "identificador";
							
							if (lexema.length() > 31) {
								Error.escriuError(2, lexema, liniaActual);
								lexema = lexema.substring(0, 31);
							}
						}
						
					} else if (esDigit(charActual)) {
						do {
							lexema = lexema + charActual;
							charActual = (char) br.read();
						} while (esDigit(charActual));
						
						tipus = "ct_enter";
						
					} else {
						System.out.println("caracter erroni" + charActual);
						error = true;
						Error.escriuError(1, charActual + "", liniaActual);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
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
