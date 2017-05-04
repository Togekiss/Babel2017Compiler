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
		//TODO que no existeixi ni variable ni funcio tampoc
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

		//TODO que no existeixi ni constant ni funcio tampoc
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

		//TODO que no existeixi ni variable ni constant tampoc
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
