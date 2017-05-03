package analitzadorSemantic;

import taulasimbols.Bloc;
import taulasimbols.Constant;
import taulasimbols.DimensioArray;
import taulasimbols.Funcio;
import taulasimbols.ITipus;
import taulasimbols.Parametre;
import taulasimbols.TaulaSimbols;
import taulasimbols.TipusArray;
import taulasimbols.TipusCadena;
import taulasimbols.TipusSimple;
import taulasimbols.Variable;

public class Asem {


	public void afegirConstant(Semantic sem, TaulaSimbols ts, int l) {

		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN"))) {
			System.out.println(l + ", Constant [" + sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}
		//TODO Si el valor es igual a CONSTANT + VALOR, comprovar la constant
		//TIPUS CORRECTE?
		if (!(sem.getValue("TIPUS") instanceof TipusSimple) &&
				!(sem.getValue("TIPUS") instanceof TipusCadena) &&
				!(boolean)sem.getValue("ESTATIC")) {
			System.out.println(l + ", Expresio invalida per constant");	
			return; 
		}

		//AFEGIR
		ts.obtenirBloc(ts.getBlocActual()).inserirConstant(new Constant(
				(String)sem.getValue("TOKEN"),
				(ITipus)sem.getValue("TIPUS"),
				sem.getValue("VALOR")
				));
	}


	public void afegirVariable(Semantic sem, TaulaSimbols ts, int l) {

		if (ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN"))) {
			System.out.println(l + ", Variable [" + sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}

		if (!(sem.getValue("TIPUS") instanceof TipusSimple) &&
			!(sem.getValue("TIPUS") instanceof TipusArray)) {
			System.out.println(l + ", Tipus invalid per variable");	
			return; 
		}

		ts.obtenirBloc(ts.getBlocActual()).inserirVariable(new Variable(
				(String)sem.getValue("TOKEN"),
				(ITipus)sem.getValue("TIPUS"),
				0
				));
	}

	public void afegirFuncio (Funcio funcio, TaulaSimbols ts, int l) {

		//TODO COMPROVAR RETORNAR

		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixProcediment(funcio.getNom())) {
			System.out.println(l + ", Funcio [" + funcio.getNom() + "] doblement definida");				
			return;
		}

		ts.obtenirBloc(ts.getBlocActual()).inserirProcediment(funcio);

		//creem nou bloc
		ts.setBlocActual(1);
		ts.inserirBloc(new Bloc());

		//posem els parametres com a variables al nou bloc
		for (int i = 0; i < funcio.getNumeroParametres(); i++) {			
			Semantic sem = new Semantic();
			sem.setValue("TOKEN", funcio.obtenirParametre(i).getNom());
			sem.setValue("TIPUS", funcio.obtenirParametre(i).getTipus());
			
			afegirVariable(sem, ts, l);
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
	//holaa
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
