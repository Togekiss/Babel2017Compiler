
public class Main {
	
	public static void main(String[] args) {
		String name = new String();
		char c;
		int i = 0;
		
		c = args[0].charAt(i);
		while (c != '.') { 
			name += c;
			c = args[0].charAt(++i);
		}
		
		System.out.println("Imprimint tokens del fitxer " + args[0]);
		
		Alex alex = new Alex(args[0]);
		Error error = new Error(name);
		
		Token token = new Token("null", "null");
		
		while (!token.esEOF()) {
			token = alex.getToken();
			System.out.println("<" + token.getTipus() + ", " + token.getLexema() + ">");
		}
		error.tancaFitxer();
	}
}
