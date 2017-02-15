import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class Error {
	private PrintWriter writer;
	
	public Error (String nomFitxer) {
		try {
			writer = new PrintWriter(nomFitxer + ".err", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}
	
	public boolean escriuError (int codiError, String variableError, int numLinia) {
		
		switch (codiError) {
		case 1: writer.println("[ERR_LEX_1] " + numLinia + ", Caràcter[" + variableError + "] desconegut ");
				return true;
		case 2: writer.println("[WAR_LEX_2] " + numLinia + ", Identificador[" + variableError + "] excedeix el nombre màxim de caràcters (32) ");
				return true;
		default: return false;
		}
	}
	
	public void tancaFitxer () { writer.close(); }

}
