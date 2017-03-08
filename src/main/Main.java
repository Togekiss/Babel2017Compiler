package main;
import analitzadorSintactic.Asin;


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
		
		Asin asin = new Asin(args[0], name);
		asin.start();
	}
}
