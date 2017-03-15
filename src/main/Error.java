package main;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class Error {
	private static PrintWriter writer;
	
	public Error (String nomFitxer) {
		try {
			nomFitxer += ".err";
			writer = new PrintWriter(nomFitxer, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}
	
	public static boolean escriuError (int codiError, String variableError, int numLinia, String variableEsperada) {
		
		switch (codiError) {
		case 1: writer.println("[ERR_LEX_1] " + numLinia + ", Caracter[" + variableError + "] desconegut");
				return true;
				
		case 2: writer.println("[WAR_LEX_1] " + numLinia + ", Identificador[" + variableError + "] excedeix el nombre maxim de caracters (32)");
				return true;
				
		case 3: writer.println("[ERR_LEX_2] " + numLinia + ", Cadena oberta a final de linia o de fitxer");
		
		case 4: writer.println("[ERR_SIN_1] " + numLinia + ", S’esperaven els tokens " + variableEsperada + 
				" però ha aparegut en l’entrada el token " + variableError);
		
		case 5: writer.println("[ERR_SIN_2] " + numLinia + ", Oblit del token " + variableEsperada);
		
		case 6: writer.println("[ERR_SIN_3] " + numLinia + ", La construcció de la declaració de la constant no és correcta");
		
		case 7: writer.println("[ERR_SIN_4] " + numLinia + ", La construcció de la declaració de la variable no és correcta");
		
		case 8: writer.println("[ERR_SIN_5] " + numLinia + ", La capçalera de la funció conté errors");
		
		case 9: writer.println("[ERR_SIN_6] " + numLinia + ", Hi ha codi després del fi del programa");
		
		case 10: writer.println("[ERR_SIN_7] " + numLinia + ", Construcció de " + variableError + " incorrecta");
		
		case 11: writer.println("[ERR_SIN_8] " + numLinia + ", Expressió incompleta: s’esperava " + variableEsperada + " i ha aparegut " + variableError);
		
		case 12: writer.println("[ERR_SIN_9] El procediment principal conté errors");
		
		default: return false;
		}
	}
	
	public void tancaFitxer () { writer.close(); }

}
