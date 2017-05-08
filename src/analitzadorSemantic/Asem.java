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
import taulasimbols.TipusIndefinit;
import taulasimbols.TipusSimple;
import taulasimbols.Variable;

public class Asem {


	public void afegirConstant(Semantic sem, TaulaSimbols ts, int l) {

		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixProcediment((String)sem.getValue("TOKEN"))) {
			//TODO error constant ja declarada
			System.out.println(l + ", Constant [" + sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}
		//TIPUS CORRECTE?
		if (!(sem.getValue("TIPUS") instanceof TipusSimple) &&
				!(sem.getValue("TIPUS") instanceof TipusCadena) &&
				!(boolean)sem.getValue("ESTATIC")) {
			//TODO error constant invalida
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

		//mirar que no existeixi
		if (ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixProcediment((String)sem.getValue("TOKEN"))) {
			//TODO error variable ja declarada
			System.out.println(l + ", Variable [" + sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}

		if (!(sem.getValue("TIPUS") instanceof TipusSimple) &&
				!(sem.getValue("TIPUS") instanceof TipusArray)) {
			//TODO error tipus invalid per variable
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

		//TODO que no existeixi ni variable ni constant tampoc
		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixProcediment(funcio.getNom()) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixConstant((funcio.getNom())) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixConstant(funcio.getNom())) {
			//TODO error funcio ja declarada
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

	public TipusArray TIPUS_comprovaArray(int dim1, int dim2, String tipus) {

		TipusArray a;

		if (dim1 < dim2) {
			String nom;
			if (tipus.equals("sencer")) nom = "S_" + dim1 + "_" + dim2;
			else nom = "L_" + dim1 + "_" + dim2;
			a =  new TipusArray(nom, dim1 - dim2, new TipusSimple(tipus, 0));
			a.inserirDimensio(new DimensioArray(new TipusSimple("sencer", 0), dim1, dim2));
		} else {
			a = new TipusArray("I_0_0", 0, new TipusIndefinit("indefinit", 0));
			//TODO tira error
		}

		return a;

	}


	public Semantic EXPRESIO1_operar(Semantic sem, Semantic sem2) {
		//operadors relacionals

		//si no son del mateix tipus o un d'ells es indefinit
		if (sem.getValue("TIPUS") != sem2.getValue("TIPUS") ||
				sem.getValue("TIPUS").equals("indefinit") ||
				sem2.getValue("TIPUS").equals("indefinit")) {
			//TODO salta error "no son del mateix tipus"
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}	

		//si no son de tipus simple
		if (!(sem.getValue("TIPUS") instanceof TipusSimple) ||
				!(sem2.getValue("TIPUS") instanceof TipusSimple)) {
			//TODO salta error "tipus no valid per operacions relacionals"
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}

		//si un d'ells no es estatic
		if (!(boolean)sem.getValue("ESTATIC") || !(boolean)sem2.getValue("ESTATIC")) {
			sem.setValue("TIPUS", new TipusSimple("logic", 0));
			sem.setValue("VALOR", "desconegut");
			sem.setValue("ESTATIC", false);

			return sem;
		}

		//si els dos son tipus simples i son estatics es pot calcular

		if (((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer")) {
			int vsem1 = (int)sem.getValue("VALOR");
			int vsem2 = (int)sem2.getValue("VALOR");

			switch ((String)sem.getValue("OPERADOR")) {

			case "==":
				if (vsem1 == vsem2)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case ">":
				if (vsem1 > vsem2)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case ">=":
				if (vsem1 >= vsem2)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case "<":
				if (vsem1 < vsem2)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case "<=":
				if (vsem1 <= vsem2)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			default: sem.setValue("VALOR", false);

			}

		} else {

			boolean vsem1 = (boolean)sem.getValue("VALOR");
			boolean vsem2 = (boolean)sem2.getValue("VALOR");

			switch ((String)sem.getValue("OPERADOR")) {

			case "==":
				if (vsem1 == vsem2)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case ">":
				if (vsem1 == true && vsem2 == false)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case ">=":
				if (vsem1 == true)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case "<":
				if (vsem1 == false && vsem2 == true)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			case "<=":
				if (vsem1 == false)
					sem.setValue("VALOR", true);
				else sem.setValue("VALOR", false);
				break;
			default: sem.setValue("VALOR", false);

			}
		}

		sem.setValue("TIPUS", new TipusSimple("logic", 0));
		sem.setValue("ESTATIC", true);

		return sem;

	}



	public Semantic EXPRESIO_SIMPLE_operar(Semantic sem) {
		//suma, resta, not

		//si es indefinit o no es tipus simple o operador no coincideix amb tipus
		if (sem.getValue("TIPUS").equals("indefinit")
				|| !(sem.getValue("TIPUS") instanceof TipusSimple)
				|| ((String)sem.getValue("OPERADOR")).equals("not")
				&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")
				|| (((String)sem.getValue("OPERADOR")).equals("suma")
						|| ((String)sem.getValue("OPERADOR")).equals("resta"))
				&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer")) {
			//TODO salta error "tipus invalid"
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}	


		//si no es estatic
		if (!(boolean)sem.getValue("ESTATIC")) {
			sem.setValue("VALOR", "desconegut");
			return sem;
		}

		//si es estatic es pot calcular
		switch ((String)sem.getValue("OPERADOR")) {

		case "not":
			sem.setValue("VALOR", !((boolean)sem.getValue("VALOR")));
			break;
		case "resta":
			sem.setValue("VALOR", 0 - ((int)sem.getValue("VALOR")));
			break;
		default:
		}

		return sem;
	}



	public Semantic EXPRESIO_SIMPLE1_operar(Semantic sem, Semantic sem2) {
		//suma, resta, or

		//si no son del mateix tipus o un d'ells es indefinit
		if (sem.getValue("TIPUS") != sem2.getValue("TIPUS") ||
				sem.getValue("TIPUS").equals("indefinit") ||
				sem2.getValue("TIPUS").equals("indefinit")) {
			//TODO salta error "no son del mateix tipus"
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}	

		//si no son de tipus simple o operador no coincideix amb tipus
		if (!(sem.getValue("TIPUS") instanceof TipusSimple) ||
				!(sem2.getValue("TIPUS") instanceof TipusSimple
						|| ((String)sem.getValue("OPERADOR")).equals("or")
						&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")
						|| (((String)sem.getValue("OPERADOR")).equals("suma")
								|| ((String)sem.getValue("OPERADOR")).equals("resta"))
						&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer"))) {
			//TODO salta error "tipus no valid per aquesta operacio"
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}


		//si un d'ells no es estatic
		if (!(boolean)sem.getValue("ESTATIC") || !(boolean)sem2.getValue("ESTATIC")) {
			sem.setValue("VALOR", "desconegut");
			sem.setValue("ESTATIC", false);

			return sem;
		}

		//si es estatic es pot calcular
		switch ((String)sem.getValue("OPERADOR")) {

		case "or":
			sem.setValue("VALOR", (boolean)sem.getValue("VALOR") || (boolean)sem2.getValue("VALOR"));
			break;
		case "suma":
			sem.setValue("VALOR", ((int)sem.getValue("VALOR")) + ((int)sem2.getValue("VALOR")));
			break;
		case "resta":
			sem.setValue("VALOR", ((int)sem.getValue("VALOR")) - ((int)sem2.getValue("VALOR")));
			break;
		default:
		}

		return sem;
	}






	public Semantic TERME_operar(Semantic sem, Semantic sem2) {
		//multiplicacio, divisio, and

		//si no son del mateix tipus o un d'ells es indefinit
		if (sem.getValue("TIPUS") != sem2.getValue("TIPUS") ||
				sem.getValue("TIPUS").equals("indefinit") ||
				sem2.getValue("TIPUS").equals("indefinit")) {
			//TODO salta error "no son del mateix tipus"
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}	

		//si no son de tipus simple o operador no coincideix amb tipus
		if (!(sem.getValue("TIPUS") instanceof TipusSimple) ||
				!(sem2.getValue("TIPUS") instanceof TipusSimple
						|| ((String)sem.getValue("OPERADOR")).equals("and")
						&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")
						|| (((String)sem.getValue("OPERADOR")).equals("multiplicacio")
								|| ((String)sem.getValue("OPERADOR")).equals("divisio"))
						&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer"))) {
			//TODO salta error "tipus no valid per aquesta operacio"
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}


		//si un d'ells no es estatic
		if (!(boolean)sem.getValue("ESTATIC") || !(boolean)sem2.getValue("ESTATIC")) {
			sem.setValue("VALOR", "desconegut");
			sem.setValue("ESTATIC", false);

			return sem;
		}

		//si es estatic es pot calcular
		switch ((String)sem.getValue("OPERADOR")) {

		case "and":
			sem.setValue("VALOR", (boolean)sem.getValue("VALOR") && (boolean)sem2.getValue("VALOR"));
			break;
		case "multiplicacio":
			sem.setValue("VALOR", ((int)sem.getValue("VALOR")) * ((int)sem2.getValue("VALOR")));
			break;
		case "divisio":
			if (((int)sem2.getValue("VALOR")) == 0) {
				//TODO error "no es pot dividir per 0"
				sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
				sem.setValue("VALOR", "indefinit");
				sem.setValue("ESTATIC", false);
				break;
			}
			sem.setValue("VALOR", ((int)sem.getValue("VALOR")) / ((int)sem2.getValue("VALOR")));
			break;
		default:
		}

		return sem;
	}



	public boolean esLogic(Semantic sem) {

		if (!(sem.getValue("TIPUS") instanceof TipusSimple)) return false;

		if (((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")) return true;
		else return false;

	}


	public boolean LL_EXP_ESCRIURE_esValid(Semantic sem) {
		//comprovar que tipus == tipus simple o cadena

		if (sem.getValue("TIPUS") instanceof TipusCadena
				|| sem.getValue("TIPUS") instanceof TipusSimple)
			return true;
		else return false;

	}


	public Semantic VAR_esVariable(Semantic sem, TaulaSimbols ts) {

		//si existeix a aquest bloc
		if (ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus());
			return sem;
		}

		//si estavem a bloc 1 i existeix a bloc 0
		else if (ts.getBlocActual() > 0 && ts.obtenirBloc(0).existeixVariable((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ts.obtenirBloc(0).obtenirVariable((String)sem.getValue("TOKEN")).getTipus());
			return sem;
		}
		else {
			//si es constant o funcio
			if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN")) ||
					ts.obtenirBloc(ts.getBlocActual()).existeixProcediment((String)sem.getValue("TOKEN")) ||
					ts.getBlocActual() > 0 && ts.obtenirBloc(0).existeixConstant((String)sem.getValue("TOKEN")) ||
					ts.getBlocActual() > 0 && ts.obtenirBloc(0).existeixProcediment((String)sem.getValue("TOKEN"))) {
				//TODO error: nomes es poden fer servir variables
			} else {
				//error: variable no declarada
				//creem variable fantasma
				creaVariableFantasma(sem, ts);
			}
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			return sem;
		}
	}
	
	private void creaVariableFantasma(Semantic sem, TaulaSimbols ts) {
		ts.obtenirBloc(ts.getBlocActual()).inserirVariable(new Variable(
				(String)sem.getValue("TOKEN"),
				new TipusIndefinit("indefinit", 0),
				0
				));
	}

	public Semantic VAR1_comprovaArray(Semantic sem, Semantic sem2, TaulaSimbols ts) {

		//comprova si sem es array
		if (!(sem.getValue("TIPUS") instanceof TipusArray)) {
			//TODO error: variable no declarada com array
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			return sem;
		}
		
		//comprova si sem2 es sencer
		if (!(sem2.getValue("TIPUS") instanceof TipusSimple) || !((TipusSimple)sem2.getValue("TIPUS")).getNom().equals("sencer")) {
			//TODO error: rang invalid
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			return sem;
		}
		
		//si sem2 es estatic, comprova que estigui dins el rang
		if ((boolean)sem2.getValue("ESTATIC")) {
			int v = (int)sem2.getValue("VALOR");
			if ((int)((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitInferior() > v ||
					(int)((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitSuperior() < v) {
				//TODO error: index fora de rang
				sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
				return sem;
			}
		}
		
		sem.setValue("TIPUS",((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).getTipusElements());
		return sem;

	}
	
	public Semantic FACTOR_getIdentificador(Semantic sem, TaulaSimbols ts) {
		
		//si esta al bloc 1, primer busca alla
		if (ts.getBlocActual() > 0) {
			if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN"))) {
				sem.setValue("TIPUS", ts.obtenirBloc(ts.getBlocActual()).obtenirConstant((String)sem.getValue("TOKEN")).getTipus());
				sem.setValue("VALOR", ts.obtenirBloc(ts.getBlocActual()).obtenirConstant((String)sem.getValue("TOKEN")).getValor());
				sem.setValue("ESTATIC", true);
				return sem;
			}
			if (ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN"))) {
				sem.setValue("TIPUS", ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus());
				sem.setValue("VALOR", "desconegut");
				sem.setValue("ESTATIC", false);
				return sem;
			}
			if (ts.obtenirBloc(ts.getBlocActual()).existeixProcediment((String)sem.getValue("TOKEN"))) {
				sem.setValue("TIPUS", ((Funcio)ts.obtenirBloc(ts.getBlocActual()).obtenirProcediment((String)sem.getValue("TOKEN"))).getTipus());
				sem.setValue("VALOR", "desconegut");
				sem.setValue("ESTATIC", false);
				return sem;
			}
			
		}
		
		//sino, busca al bloc 0
		if (ts.obtenirBloc(0).existeixConstant((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ts.obtenirBloc(0).obtenirConstant((String)sem.getValue("TOKEN")).getTipus());
			sem.setValue("VALOR", ts.obtenirBloc(0).obtenirConstant((String)sem.getValue("TOKEN")).getValor());
			sem.setValue("ESTATIC", true);
			return sem;
		}
		if (ts.obtenirBloc(0).existeixVariable((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ts.obtenirBloc(0).obtenirVariable((String)sem.getValue("TOKEN")).getTipus());
			sem.setValue("VALOR", "desconegut");
			sem.setValue("ESTATIC", false);
			return sem;
		}
		if (ts.obtenirBloc(0).existeixProcediment((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ((Funcio)ts.obtenirBloc(0).obtenirProcediment((String)sem.getValue("TOKEN"))).getTipus());
			sem.setValue("VALOR", "desconegut");
			sem.setValue("ESTATIC", false);
			return sem;
		}
		
		//TODO sino ERROR variable no definida
		creaVariableFantasma(sem, ts);
		sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
		sem.setValue("VALOR", "indefinit");
		sem.setValue("ESTATIC", false);
		return sem;
	}
	
	public Semantic FACTOR1_buscaFuncio(Semantic sem, TaulaSimbols ts) {
		
		if (!ts.obtenirBloc(0).existeixProcediment((String)sem.getValue("TOKEN"))) {
			//TODO error: no es una funcio
			sem.setValue("FUNCIO", new TipusIndefinit("indefinit", 0));
			sem.setValue("INDEX", 0);
			return sem;
		}
		
		sem.setValue("FUNCIO", ts.obtenirBloc(0).obtenirProcediment((String)sem.getValue("TOKEN")));
		sem.setValue("INDEX", 0);
		return sem;
	}
	
	public Semantic LL_EXPRESIO_comprovaParametre(Semantic sem, Semantic sem2) {
		
		Funcio f = (Funcio)sem.getValue("FUNCIO");
		
		if ((int)sem.getValue("INDEX") >= f.getNumeroParametres()) {
			//TODO error
			return sem;
		}
		
		if (!f.obtenirParametre((int)sem.getValue("INDEX")).getTipus().equals((ITipus)sem2.getValue("TIPUS"))) {
			//TODO error: parametres no coincideixen
		}
		
		sem.setValue("INDEX", (int)sem.getValue("INDEX")+1);
		return sem;
	}
	

	/*public boolean EXP_tipusExpressio (Semantic sem) {
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
	}*/




}
