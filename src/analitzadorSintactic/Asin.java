package analitzadorSintactic;

import main.Error;
import main.Token;
import analitzadorLexicografic.Alex;

public class Asin {
	private Alex alex;
	private Error error;
	
	public Asin (String args, String name) {
		alex = new Alex(args);
		error = new Error(name);
	}
	
	
	public void start () {
		Token token = new Token("null", "null");

		
		while (!token.esEOF()) {
			token = alex.getToken();
			alex.writeToken(token);
		}
		
		error.tancaFitxer();
		alex.tancaFitxer();
		
		System.out.println("Anàlisi lexicogràfic finalitzat.");
	}
}
