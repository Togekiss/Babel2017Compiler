
public class Main {
	
	public static void main(String[] args) {
		String name = new String();
		try {
			name = args[0].substring(0, args[0].length() - 4);
		} catch (Exception e) {
			System.out.println("S'ha de passar el nom del fitxer .bab per referència.");
			System.exit(2);
		}
		
		if (!args[0].substring(args[0].length() - 4, args[0].length()).equals(".bab")) {
			System.out.println("Has de compilar un fitxer .bab.");
			System.exit(2);
		}
		
		
		//System.out.println("Imprimint tokens del fitxer " + args[0]);
		
		Alex alex = new Alex(args[0]);
		Error error = new Error(name);
		
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
