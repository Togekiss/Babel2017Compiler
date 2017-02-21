
public class Main {
	
	public static void main(String[] args) {
		
		System.out.println("Imprimint tokens del fitxer " + args[0]);
		
		Alex alex = new Alex(args[0]);
		
		Token token = new Token("null", "null");
		
		while (!token.esEOF()) {
			token = alex.getToken();
			System.out.println("<" + token.getTipus() + ", " + token.getLexema() + ">");
		}
	}
}
