package analitzadorSemantic;

import taulasimbols.Constant;
import taulasimbols.DimensioArray;
import taulasimbols.ITipus;
import taulasimbols.Parametre;
import taulasimbols.TaulaSimbols;
import taulasimbols.TipusArray;
import taulasimbols.TipusSimple;
import taulasimbols.Variable;

public class Asem {
	
	
	
	public void afegirConstant(Semantic sem, TaulaSimbols ts) {
		
		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN"))) {
System.out.println("#, Constant [" + sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}
		//TODO Si el valor es igual a CONSTANT + VALOR, comprovar la constant
		//TIPUS CORRECTE?
		if (!sem.getValue("TIPUS").equals("sencer") &&
			!sem.getValue("TIPUS").equals("logic") &&
			!sem.getValue("TIPUS").equals("ct_cadena") &&
			!(boolean)sem.getValue("ESTATIC")) {
System.out.println("#, Expresio invalida per constant");	
			return; 
		}
		
		//AFEGIR
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
	
	public void afegirFuncio (Semantic sem, TaulaSimbols ts) {
		
		//TODO COMPROVAR RETORNAR
		
		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN"))) {
			System.out.println("#, Funcio [" + sem.getValue("TOKEN") + "] doblement definida");				
						return;
					}
		
	}
	
	public boolean EXP_tipusExpressio (Semantic sem) {
		if (sem.getValue("TIPUS") == null) return false;
		return false;
	}
	
	public boolean EXP_tipusIndex (Semantic sem, TaulaSimbols ts) {
		DimensioArray dimensio = ((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio((int)sem.getValue("VALUE"));
		if (sem.getValue("TIPUS") != dimensio.getTipusLimit() || sem.getValue("TIPUS") != "sencer") return false;
		if ((boolean)sem.getValue("ESTATIC") == true && ((int)sem.getValue("VALUE") > (int)dimensio.getLimitSuperior() 
				|| (int)sem.getValue("VALUE") < (int)dimensio.getLimitInferior())) return false;
		return true; 
	}
	
	//PER FACTOR i TERME
	public boolean EXP_tipusOperands (Semantic sem1, Semantic sem2) {
		return sem1.getValue("TIPUS") == sem2.getValue("TIPUS");
	}
	
	public boolean EXP_tipusOperadorRelacional (Semantic sem1, Semantic sem2) {
		return EXP_tipusOperands(sem1, sem2) && sem1.getValue("TIPUS") instanceof TipusSimple && sem1.getValue("TIPUS") instanceof TipusSimple;
	}
	
	public boolean EXP_paramFuncio (Semantic sem, TaulaSimbols ts) {
		Parametre param = ts.obtenirBloc(0).obtenirProcediment((String)sem.getValue("FUNCNAME")).obtenirParametre((int)sem.getValue("NPARAM"));
		if (param.getTipusPasParametre().toString() == "REFERENCIA" && !ts.obtenirBloc(0).existeixVariable((String)sem.getValue("TOKEN"))) {
			return false;
		}
		return sem.getValue("TIPUS") == param.getTipus().getNom();
	}
	
	public boolean EXP_midaFuncio (Semantic sem, TaulaSimbols ts) {
		return (int)sem.getValue("NPARAM") == ts.obtenirBloc(0).obtenirProcediment((String)sem.getValue("FUNCNAME")).getNumeroParametres();
	}
	
	public boolean INST_tipusOperands (Semantic sem1, Semantic sem2, TaulaSimbols ts) {
		return sem1.getValue("TIPUS") == sem2.getValue("TIPUS") && !ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem1.getValue("TOKEN"));
	}
	
	
	
	
}
