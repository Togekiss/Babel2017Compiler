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
		return true;
		
		case 21: writer.println("[ERR_SIN_1] " + numLinia + ", S'esperaven els tokens " + variableEsperada + 
				" pero ha aparegut a l'entrada el token " + variableError);
		System.out.println("[ERR_SIN_1] " + numLinia + ", S'esperaven els tokens " + variableEsperada + 
			" pero ha aparegut en l'entrada el token " + variableError);
		return true;
		
		case 22: writer.println("[ERR_SIN_2] " + numLinia + ", Hi ha codi abans de l'inici del programa");
		System.out.println("[ERR_SIN_2] " + numLinia + ", Hi ha codi abans de l'inici del programa");
		return true;
		
		case 23: writer.println("[ERR_SIN_3] " + numLinia + ", La construccio de la declaracio de la constant no es correcta");
		System.out.println("[ERR_SIN_3] " + numLinia + ", La construcció de la declaració de la constant no és correcta");
		return true;
		
		case 24: writer.println("[ERR_SIN_4] " + numLinia + ", La construccio de la declaracio de la variable no es correcta");
		System.out.println("[ERR_SIN_4] " + numLinia + ", La construcció de la declaració de la variable no és correcta");
		return true;
		
		case 25: writer.println("[ERR_SIN_5] " + numLinia + ", La capcalera de la funcio conte errors");
		System.out.println("[ERR_SIN_5] " + numLinia + ", La capçalera de la funció conté errors");
		return true;
		
		case 26: writer.println("[ERR_SIN_6] " + numLinia + ", Hi ha codi despres del fi del programa");
		System.out.println("[ERR_SIN_6] " + numLinia + ", Hi ha codi després del fi del programa");
		return true;
		
		case 27: writer.println("[ERR_SIN_7] " + numLinia + ", Construccio de " + variableError + " incorrecta");
		System.out.println("[ERR_SIN_7] " + numLinia + ", Construcció de " + variableError + " incorrecta");
		return true;
		
		case 28: writer.println("[ERR_SIN_8] " + numLinia + ", el factor conte errors");
		System.out.println("[ERR_SIN_8] " + numLinia + ", el factor conté errors");
		return true;
		
		case 29: writer.println("[ERR_SIN_9] El procediment principal conte errors");
		System.out.println("[ERR_SIN_9] El procediment principal conté errors");
		return true;
		
		case 210: writer.println("[ERR_SIN_10] " + numLinia + ", Error en la llista de parametres.");
		System.out.println("[ERR_SIN_10] " + numLinia + ", Error en la llista de paràmetres.");
				return true;
		
		default: return false;
		}
	}
	
	public void tancaFitxer () { writer.close(); }

}
