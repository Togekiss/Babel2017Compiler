package analitzadorSemantic;

import taulasimbols.Constant;
import taulasimbols.ITipus;
import taulasimbols.TaulaSimbols;
import taulasimbols.TipusSimple;
import taulasimbols.Variable;

public class Asem {
	
	
	
	public void afegirConstant(Semantic sem, TaulaSimbols ts) {
		
		if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN"))) {
System.out.println("#, Constant [" + sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}
	
		if (!sem.getValue("TIPUS").equals("sencer") &&
			!sem.getValue("TIPUS").equals("logic") &&
			!sem.getValue("TIPUS").equals("ct_cadena") &&
			!(boolean)sem.getValue("ESTATIC")) {
System.out.println("#, Expresio invalida per constant");	
			return; 
		}
		
		ts.obtenirBloc(ts.getBlocActual()).inserirConstant(new Constant(
					(String)sem.getValue("TOKEN"),
					(ITipus)sem.getValue("TIPUS"),
					sem.getValue("VALOR")
				));
	}
	
	
	public void afegirVariable(Semantic sem, TaulaSimbols ts) {
		
		if (ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN"))) {
			System.out.println("#, Variable [" + sem.getValue("TOKEN") + "] doblement definida");				
						return;
					}
				
					if (!(sem.getValue("TIPUS") instanceof TipusSimple)) {
			System.out.println("#, Tipus invalid per variable");	
						return; 
					}
					
					ts.obtenirBloc(ts.getBlocActual()).inserirVariable(new Variable(
								(String)sem.getValue("TOKEN"),
								(ITipus)sem.getValue("TIPUS"),
								0
							));
	}
	
}
