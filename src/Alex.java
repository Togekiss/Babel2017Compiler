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

		Token token = new Token("null", "");
		boolean seguent = true;

		while (seguent) {

			token.setLexema("");
			token.setTipus("null");
			seguent = false;
		
			
			try {
				
				while (charActual == '\n' || charActual == '\r' || charActual == '\t' || charActual == ' ' ) {
					if (charActual == '\n') {
						liniaActual++;
					}
					charActual = (char) br.read();
				}

				switch (charActual) {

				case ('"'):
					do {
						token.pushLexema(charActual);
						charActual = (char) br.read();
					} while (charActual != '"');
				
					token.pushLexema(charActual);
					token.setTipus("ct_cadena");
					charActual = (char) br.read();
					
					break;
					
				case ('/'):
					charActual = (char) br.read();
				
					if (charActual == '/') {
						while (charActual != '\n') charActual = (char) br.read();
						seguent = true;
					} else {
						token.setTipus("divisio");
						token.setLexema("/");
					}
					
					break;
					
				case ('='): 
					charActual = (char) br.read();
				
					if (charActual == '=') {
						token.setTipus("igual_que");
						token.setLexema("==");
						charActual = (char) br.read();
					} else {
						token.setTipus("igual");
						token.setLexema("=");
					}
					
					break;
					
				case ('>'):
					charActual = (char) br.read();
					if (charActual == '=') {
						token.setTipus("major_igual_que");
						token.setLexema(">=");
						charActual = (char) br.read();
					} else {
						token.setTipus("major_que");
						token.setLexema(">");
					}
					
					break;
					
				case ('<'):
					charActual = (char) br.read();
				
					if (charActual == '>') {
						token.setTipus("diferent_que");
						token.setLexema("<>");
						charActual = (char) br.read();
					} else if (charActual == '=') {
						token.setTipus("menor_igual_que");
						token.setLexema("<=");
						charActual = (char) br.read();
					} else {
						token.setTipus("menor_que");
						token.setLexema("<");
					}
					
					break;
					
				case (';'): 
					token.setTipus("punt_i_coma");
					token.setLexema(";");
					charActual = (char) br.read();
					
					break;
					
				case (','): 
					token.setTipus("coma");
					token.setLexema(",");
					charActual = (char) br.read();
					
					break;
					
				case (':'): 
					token.setTipus("dos_punts");
					token.setLexema(":");
					charActual = (char) br.read();
					
					break;
					
				case ('('):
					token.setTipus("parentesi_obert");
					token.setLexema("(");
					charActual = (char) br.read();
					
					break;
					
				case (')'):
					token.setTipus("parentesi_tancat");
					token.setLexema(")");
					charActual = (char) br.read();
					
					break;
					
				case ('['):
					token.setTipus("claudator_obert");
					token.setLexema("[");
					charActual = (char) br.read();
					
					break;
					
				case (']'):
					token.setTipus("claudator_tancat");
					token.setLexema("]");
					charActual = (char) br.read();
					
					break;
					
				case ('+'):
					token.setTipus("suma");
					token.setLexema("+");
					charActual = (char) br.read();
					
					break;
					
				case ('*'):
					token.setTipus("multiplicacio");
					token.setLexema("*");
					charActual = (char) br.read();
					
					break;
					
				case ('.'):
					charActual = (char) br.read();
					
					if (charActual == '.') {
						token.setTipus("rang");
						token.setLexema("..");
						charActual = (char) br.read();
					} else {
						seguent = true;
						Error.escriuError(1, ".", liniaActual);
					}
					
				case ('-'):
					token.setTipus("resta");
					token.setLexema("-");
					charActual = (char) br.read();
					
					break;
					
				case ('?'):
					token.setTipus("interrogant");
					token.setLexema("?");
					charActual = (char) br.read();
					
					break;
					
				case ((char) -1):
					token.setTipus("eof");
					token.setLexema("eof");
					
					break;
					
				default:
					if (esLletra(charActual)) {
						do {
							token.pushLexema(charActual);
							charActual = (char) br.read();
						} while (esLletra(charActual) || esDigit(charActual) || charActual == '_');
						
						token.lexToLowerCase();
						
						if (diccionari.containsKey(token.getLexema())) {
							token.setTipus(diccionari.get(token.getLexema()));
						} else {
							token.setTipus("identificador");
							
							if (token.getLexema().length() > 31) {
								Error.escriuError(2, token.getLexema(), liniaActual);
								token.trunkLexema();
							}
						}
						
					} else if (esDigit(charActual)) {
						do {
							token.pushLexema(charActual);
							charActual = (char) br.read();
						} while (esDigit(charActual));
						
						token.setTipus("ct_enter");
						
					} else {
						seguent = true;
						Error.escriuError(1, charActual + "", liniaActual);
						charActual = (char) br.read();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		printToken(token);
		return token;
	}

	private boolean esLletra (char c) {
		return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
	}

	private boolean esDigit (char c) {
		return (c >= '0' && c <= '9');
	}

	public int getLiniaActual () { return liniaActual; }
	
	private void printToken(Token token) {
		
	}

}
