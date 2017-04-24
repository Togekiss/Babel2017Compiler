package analitzadorSemantic;

import taulasimbols.Constant;
import taulasimbols.ITipus;
import taulasimbols.TaulaSimbols;

public class Asem {
	
	
	
	public void afegirConstant(Semantic sem, TaulaSimbols ts) {
		
		if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("lexema"))) {
System.out.println("#, Constant [" + sem.getValue("lexema") + "] doblement definida");				
			return;
		}
	
		if (!sem.getValue("tipus").equals("sencer") &&
			!sem.getValue("tipus").equals("logic") &&
			!sem.getValue("tipus").equals("ct_cadena") &&
			!(boolean)sem.getValue("esEstatic")) {
System.out.println("#, Expresio invalida per constant");	
			return; 
		}
		
		ts.obtenirBloc(ts.getBlocActual()).inserirConstant(new Constant(
					(String)sem.getValue("lexema"),
					(ITipus)sem.getValue("tipus"),
					sem.getValue("valor")
				));
	}
	
}
