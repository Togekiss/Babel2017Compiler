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
		return true;
		
		case 22: writer.println("[ERR_SIN_2] " + numLinia + ", Hi ha codi abans de l'inici del programa");
		return true;
		
		case 23: writer.println("[ERR_SIN_3] " + numLinia + ", La construccio de la declaracio de la constant no es correcta");
		return true;
		
		case 24: writer.println("[ERR_SIN_4] " + numLinia + ", La construccio de la declaracio de la variable no es correcta");
		return true;
		
		case 25: writer.println("[ERR_SIN_5] " + numLinia + ", La capcalera de la funcio conte errors");
		return true;
		
		case 26: writer.println("[ERR_SIN_6] " + numLinia + ", Hi ha codi despres del fi del programa");
		return true;
		
		case 27: writer.println("[ERR_SIN_7] " + numLinia + ", Construccio de " + variableError + " incorrecta");
		return true;
		
		case 28: writer.println("[ERR_SIN_8] " + numLinia + ", el factor conte errors");
		return true;
		
		case 31: writer.println("[ERR_SEM_1] " + numLinia + ", Constant [" + variableError + "] doblement definida");
		return true;
		
		case 32: writer.println("[ERR_SEM_2] " + numLinia + ", Variable [" + variableError + "] doblement definida");
		return true;
		
		case 33: writer.println("[ERR_SEM_3] " + numLinia + ", Funci� [" + variableError + "] doblement definida");
		return true;
		
		case 34: writer.println("[ERR_SEM_4] " + numLinia + ", Par�metre [" + variableError + "] doblement definit");
		return true;
		
		case 35: writer.println("[ERR_SEM_5] " + numLinia + ", L�mits decreixents en vector");
		return true;
		
		case 36: writer.println("[ERR_SEM_6] " + numLinia + ", El tipus ha de ser TIPUS SIMPLE");
		return true;
		
		case 37: writer.println("[ERR_SEM_7] " + numLinia + ", El rang del vector ha de ser SENCER i ESTATIC");
		return true;
		
		case 38: writer.println("[ERR_SEM_8] " + numLinia + ", La condici� no �s de tipus LOGIC");
		return true;
		
		case 39: writer.println("[ERR_SEM_9] " + numLinia + ", L�identificador [" + variableError + "] no ha estat declarat");
		return true;
		
		case 310: writer.println("[ERR_SEM_10] " + numLinia + ", L�identificador [" + variableError + "] en la instrucci� LLEGIR no �s una variable de tipus simple");
		return true;
		
		case 311: writer.println("[ERR_SEM_11] " + numLinia + ", L�identificador [" + variableError + "] en part esquerra d�assignaci� no �s una variable ");
		return true;
		//Modificar
		case 312: writer.println("[ERR_SEM_12] " + numLinia + ", La variable i l'expressi� de assignaci� tenen tipus diferents. El tipus de la variable �s [" + variableEsperada + "] i el de l�expressi� �s [" + variableError + "]");
		return true;
		
		case 313: writer.println("[ERR_SEM_13] " + numLinia + ", El tipus de l��ndex d�acc�s del vector no �s SENCER");
		return true;
		
		case 314: writer.println("[ERR_SEM_14] " + numLinia + ", El tipus de la expressi� en ESCRIURE no �s simple o no �s una constant cadena");
		return true;
		
		case 315: writer.println("[ERR_SEM_15] " + numLinia + ", La funci� en declaraci� t� " + variableEsperada + " par�metres mentre que en �s t� " + variableError);
		return true;
		
		case 316: writer.println("[ERR_SEM_16] " + numLinia + ", El tipus del par�metre n�mero " + variableError + " de la funci� no coincideix amb el tipus en la seva declaraci� [" + variableEsperada + "]");
		return true;
		
		case 317: writer.println("[ERR_SEM_17] " + numLinia + ", El par�metre n�mero " + variableError + " de la funci� no es pot passar per refer�ncia");
		return true;
		//Modificar
		case 318: writer.println("[ERR_SEM_18] " + numLinia + ", La funci� [" + variableError + "] ha de ser del tipus [" + variableEsperada + "] per� en la expressi� del seu valor el tipus �s [" + variableError + "]");
		return true;
		
		case 319: writer.println("[ERR_SEM_19] " + numLinia + ", Retornar fora de funci�");
		return true;
		
		case 320: writer.println("[ERR_SEM_20] " + numLinia + ", L�expressi� no �s est�tica");
		return true;
		
		case 321: writer.println("[ERR_SEM_21] " + numLinia + ", No hi ha cap retornar en la funci� " + variableError);
		return true;

		default: return false;
		}
	}
	
	public void tancaFitxer () { writer.close(); }

}
