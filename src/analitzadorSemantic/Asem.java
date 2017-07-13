package analitzadorSemantic;

import codeGeneration.CodeGenOut;
import main.Error;
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
import taulasimbols.TipusPasParametre;
import taulasimbols.TipusSimple;
import taulasimbols.Variable;

public class Asem {

	/*private Error error;
	
	public Asem(Error error) {
		this.error = error;
	}*/

	private CodeGenOut gc;
	private int despl = 0;
	
	public Asem (CodeGenOut gc) { this.gc = gc; }
	
	public int getDespl () { return despl; }

	public void afegirConstant(Semantic sem, TaulaSimbols ts, int l) {

		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixProcediment((String)sem.getValue("TOKEN"))) {
			//error constant ja declarada
			Error.escriuError(31, (String)sem.getValue("TOKEN"), l, "");
			System.out.println("[ERR_SEM_1] " + l + ", Constant [" + sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}
		//TIPUS CORRECTE?
		if (!(sem.getValue("TIPUS") instanceof TipusSimple) &&
				!(sem.getValue("TIPUS") instanceof TipusCadena) ||
				!(boolean)sem.getValue("ESTATIC")) {
			//error constant invalida
			Error.escriuError(320, "", 0, "");
			System.out.println("[ERR_SEM_20] " + l + ", L’expressió no és estàtica");	
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

		//System.out.println("AFEGINT VARIABLE");
		//System.out.println(sem.prettyPrint());
		
		//mirar que no existeixi
		if (ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN")) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixProcediment((String)sem.getValue("TOKEN"))) {
			//error variable ja declarada
			Error.escriuError(32, (String)sem.getValue("TOKEN"), l, "");
			System.out.println("[ERR_SEM_2] " + l + ", Variable [" + (String)sem.getValue("TOKEN") + "] doblement definida");				
			return;
		}

		if (!(sem.getValue("TIPUS") instanceof TipusSimple) &&
				!(sem.getValue("TIPUS") instanceof TipusArray)) {
			//error tipus invalid per variable
			Error.escriuError(36, "", l, "");
			System.out.println("[ERR_SEM_6] " + l + ", El tipus ha de ser tipus simple");	
			return; 
		}

		ts.obtenirBloc(ts.getBlocActual()).inserirVariable(new Variable(
				(String)sem.getValue("TOKEN"),
				(ITipus)sem.getValue("TIPUS"),
				despl
				));
		despl = despl + (int)sem.getValue("TAMANY");
	}

	public void afegirFuncio (Funcio funcio, TaulaSimbols ts, int l) {

		//que no existeixi ni variable ni constant tampoc
		//EXISTEIX?
		if (ts.obtenirBloc(ts.getBlocActual()).existeixProcediment(funcio.getNom()) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixConstant((funcio.getNom())) ||
				ts.obtenirBloc(ts.getBlocActual()).existeixConstant(funcio.getNom())) {
			//error funcio ja declarada
			Error.escriuError(33, funcio.getNom(), l, "");
			System.out.println("[ERR_SEM_3] " + l + ", Funció [" + funcio.getNom() + "] doblement definida");				
			//return;
		}

		ts.obtenirBloc(ts.getBlocActual()).inserirProcediment(funcio);

		//creem nou bloc
		ts.setBlocActual(1);
		ts.inserirBloc(new Bloc());
		despl = 0;

		//posem els parametres com a variables al nou bloc
		for (int i = 0; i < funcio.getNumeroParametres(); i++) {			
			Semantic sem = new Semantic();
			sem.setValue("TOKEN", funcio.obtenirParametre(i).getNom());
			sem.setValue("TIPUS", funcio.obtenirParametre(i).getTipus());

			afegirVariable(sem, ts, l);
		}

	}

	public TipusArray TIPUS_comprovaArray(int dim1, int dim2, String tipus, int l) {

		TipusArray a;

		if (dim1 < dim2) {
			String nom;
			if (tipus.equals("sencer")) nom = "S_" + dim1 + "_" + dim2;
			else nom = "L_" + dim1 + "_" + dim2;
			a =  new TipusArray(nom, (dim1 - dim2 + 1)*4, new TipusSimple(tipus, 4, -2147483648, 2147483647));
			a.inserirDimensio(new DimensioArray(new TipusSimple("sencer", 4, -2147483648, 2147483647), dim1, dim2));
		} else {
			a = new TipusArray("I_0_0", 0, new TipusIndefinit("indefinit", 4));
			//tira error
			Error.escriuError(5, "", l, "");
			System.out.println("[ERR_SEM_5] " + l + ", Límits decreixents en vector");
		}

		return a;

	}


	public Semantic EXPRESIO1_operar(Semantic sem, Semantic sem2, int l) {
		//operadors relacionals
		
		sem.setValue("REFERENCIA", false);
		
		//si no son del mateix tipus o un d'ells es indefinit
		if (!sem.getValue("TIPUS").equals(sem2.getValue("TIPUS")) ||
				sem.getValue("TIPUS").equals("indefinit") ||
				sem2.getValue("TIPUS").equals("indefinit")) {
			//salta error "no son del mateix tipus"
			Error.escriuError(318, ((ITipus)sem.getValue("TIPUS")).getNom(), l, ((ITipus)sem2.getValue("TIPUS")).getNom());
			System.out.println("[ERR_SEM_18] " + l + ", No es poden operar expressions de tipus diferents, en aquest cas ["
			+ ((ITipus)sem.getValue("TIPUS")).getNom() + "] i [" + ((ITipus)sem2.getValue("TIPUS")).getNom() + "]");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			//sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}	

		//si no son de tipus simple
		if (!(sem.getValue("TIPUS") instanceof TipusSimple) ||
				!(sem2.getValue("TIPUS") instanceof TipusSimple)) {
			//salta error "tipus no valid per operacions relacionals"
			Error.escriuError(36, "", l, "");
			System.out.println("[ERR_SEM_6] " + l + ", El tipus ha de ser TIPUS SIMPLE");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			//sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}

		//si un d'ells no es estatic
		if (!(boolean)sem.getValue("ESTATIC") || !(boolean)sem2.getValue("ESTATIC")) {
			sem.setValue("TIPUS", new TipusSimple("logic", 4, -2147483648, 2147483647));
			sem.setValue("VALOR", -1);
			sem.setValue("ESTATIC", false);

			//TODO codi operadors relacionals
			switch ((String)sem.getValue("OPERADOR")) {

			case "==":
				gc.gc("seq $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case ">":
				gc.gc("sgt $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case ">=":
				gc.gc("sge $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case "<":
				gc.gc("slt $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case "<=":
				gc.gc("sle $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case "<>":
				gc.gc("sne $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			default: gc.gc("li $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", 0");


			}
			
			return sem;
		}

		//si els dos son tipus simples i son estatics es pot calcular

		if (((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer")) {
			int vsem1 = (int)sem.getValue("VALOR");
			int vsem2 = (int)sem2.getValue("VALOR");

			switch ((String)sem.getValue("OPERADOR")) {

			case "==":
				if (vsem1 == vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case ">":
				if (vsem1 > vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case ">=":
				if (vsem1 >= vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case "<":
				if (vsem1 < vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case "<=":
				if (vsem1 <= vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case "<>":
				if (vsem1 != vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			default: sem.setValue("VALOR", 0);

			}

		} else {

			int vsem1 = (int)sem.getValue("VALOR");
			int vsem2 = (int)sem2.getValue("VALOR");

			switch ((String)sem.getValue("OPERADOR")) {

			case "==":
				if (vsem1 == vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case ">":
				if (vsem1 == 0x00000001 && vsem2 == 0)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case ">=":
				if (vsem1 == 0x00000001)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case "<":
				if (vsem1 == 0 && vsem2 == 0x00000001)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case "<=":
				if (vsem1 == 0)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			case "<>":
				if (vsem1 != vsem2)
					sem.setValue("VALOR", 0x00000001);
				else sem.setValue("VALOR", 0);
				break;
			default: sem.setValue("VALOR", 0);

			}
		}

		sem.setValue("TIPUS", new TipusSimple("logic", 4, -2147483648, 2147483647));
		sem.setValue("ESTATIC", true);
		gc.gc("li $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + sem.getValue("VALOR"));
		return sem;

	}



	public Semantic EXPRESIO_SIMPLE_operar(Semantic sem, int l) {
		//suma, resta, not
		
		
		//si no hi ha res que operar
		if (sem.getValue("OPERADOR") == null) 
			return sem;

		sem.setValue("REFERENCIA", false);
		
		//si es indefinit o no es tipus simple o operador no coincideix amb tipus
		if (sem.getValue("TIPUS").equals("indefinit")
				|| !(sem.getValue("TIPUS") instanceof TipusSimple)
				|| ((String)sem.getValue("OPERADOR")).equals("not")
				&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")
				|| (((String)sem.getValue("OPERADOR")).equals("suma")
						|| ((String)sem.getValue("OPERADOR")).equals("resta"))
				&& !((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer")) {
			//salta error "tipus invalid"
			Error.escriuError(322, ((ITipus)sem.getValue("TIPUS")).getNom(), l, "");
			System.out.println("[ERR_SEM_22] " + l + ", Tipus [" + ((ITipus)sem.getValue("TIPUS")).getNom() + "] invàlid per aquest tipus d'operació");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			//sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}	


		//si no es estatic
		if (!(boolean)sem.getValue("ESTATIC")) {
			sem.setValue("VALOR", -1);
			//TODO codi not/negat
			switch ((String)sem.getValue("OPERADOR")) {
			case "not":
				gc.gc("not $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")));
				gc.gc("andi $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", 0x00000001");
				break;
			case "resta":
				gc.gc("neg $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")));
				break;
			default:
			}
			return sem;
		}

		//si es estatic es pot calcular
		switch ((String)sem.getValue("OPERADOR")) {

		case "not":
			if ((int)sem.getValue("VALOR") == 0) 
				sem.setValue("VALOR", 0x00000001);
			else
				sem.setValue("VALOR", 0);
			break;
		case "resta":
			sem.setValue("VALOR", 0 - ((int)sem.getValue("VALOR")));
			break;
		default:
		}
		gc.gc("li $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + sem.getValue("VALOR"));
		return sem;
	}



	public Semantic EXPRESIO_SIMPLE1_operar(Semantic sem, Semantic sem2, int l) {
		//suma, resta, or
		
		sem.setValue("REFERENCIA", false);

		//si no son del mateix tipus o un d'ells es indefinit
		if (!sem.getValue("TIPUS").equals(sem2.getValue("TIPUS")) ||
				sem.getValue("TIPUS").equals("indefinit") ||
				sem2.getValue("TIPUS").equals("indefinit")) {
			//salta error "no son del mateix tipus"
			Error.escriuError(318, ((ITipus)sem.getValue("TIPUS")).getNom(), l, ((ITipus)sem2.getValue("TIPUS")).getNom());
			System.out.println("[ERR_SEM_18] " + l + ", No es poden operar expressions de tipus diferents, en aquest cas ["
			+ ((ITipus)sem.getValue("TIPUS")).getNom() + "] i [" + ((ITipus)sem2.getValue("TIPUS")).getNom() + "]");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			//sem.setValue("VALOR", "indefinit");
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
			//salta error "tipus no valid per aquesta operacio"
			Error.escriuError(322, ((ITipus)sem.getValue("TIPUS")).getNom(), l, "");
			System.out.println("[ERR_SEM_22] " + l + ", Tipus [" + ((ITipus)sem.getValue("TIPUS")).getNom() + "] invàlid per aquest tipus d'operació");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			//sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}


		//si un d'ells no es estatic
		if (!(boolean)sem.getValue("ESTATIC") || !(boolean)sem2.getValue("ESTATIC")) {
			sem.setValue("VALOR", -1);
			sem.setValue("ESTATIC", false);
			//TODO codi operacio or/+/-
			switch ((String)sem.getValue("OPERADOR")) {

			case "or":
				gc.gc("or $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case "suma":
				gc.gc("add $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case "resta":
				gc.gc("sub $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			default:
			}
			return sem;
		}

		//si es estatic es pot calcular
		switch ((String)sem.getValue("OPERADOR")) {

		case "or":
			sem.setValue("VALOR", (boolean)sem.getValue("VALOR") || (boolean)sem2.getValue("VALOR"));
			if ((int)sem.getValue("VALOR") == 0) 
				sem.setValue("VALOR", 0);
			else
				sem.setValue("VALOR", 0x00000001);
			break;
		case "suma":
			sem.setValue("VALOR", ((int)sem.getValue("VALOR")) + ((int)sem2.getValue("VALOR")));
			break;
		case "resta":
			sem.setValue("VALOR", ((int)sem.getValue("VALOR")) - ((int)sem2.getValue("VALOR")));
			break;
		default:
		}
		gc.gc("li $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + sem.getValue("VALOR"));
		return sem;
	}






	public Semantic TERME_operar(Semantic sem, Semantic sem2, int l) {
		//multiplicacio, divisio, and

		sem.setValue("REFERENCIA", false);
		
		//si no son del mateix tipus o un d'ells es indefinit
		if (!sem.getValue("TIPUS").equals(sem2.getValue("TIPUS")) ||
				sem.getValue("TIPUS").equals("indefinit") ||
				sem2.getValue("TIPUS").equals("indefinit")) {
			//salta error "no son del mateix tipus"
			Error.escriuError(318, ((ITipus)sem.getValue("TIPUS")).getNom(), l, ((ITipus)sem2.getValue("TIPUS")).getNom());
			System.out.println("[ERR_SEM_18] " + l + ", No es poden operar expressions de tipus diferents, en aquest cas ["
			+ ((ITipus)sem.getValue("TIPUS")).getNom() + "] i [" + ((ITipus)sem2.getValue("TIPUS")).getNom() + "]");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			//sem.setValue("VALOR", "indefinit");
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
			//salta error "tipus no valid per aquesta operacio"
			Error.escriuError(322, ((ITipus)sem.getValue("TIPUS")).getNom(), l, "");
			System.out.println("[ERR_SEM_22] " + l + ", Tipus [" + ((ITipus)sem.getValue("TIPUS")).getNom() + "] invàlid per aquest tipus d'operació");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			//sem.setValue("VALOR", "indefinit");
			sem.setValue("ESTATIC", false);

			return sem;
		}


		//si un d'ells no es estatic
		if (!(boolean)sem.getValue("ESTATIC") || !(boolean)sem2.getValue("ESTATIC")) {
			sem.setValue("VALOR", -1);
			sem.setValue("ESTATIC", false);
			//TODO codi operacio and/*/%
			switch ((String)sem.getValue("OPERADOR")) {
			case "and":
				gc.gc("and $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case "multiplicacio":
				gc.gc("mul $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			case "divisio":
				//TODO comprovar divisio 0 amb assembler?? NO VA
				//gc.gc("beqz $" + gc.getNomRegistre((int)sem2.getValue("REG")) + ", div0");		
				gc.gc("div $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre((int)sem2.getValue("REG")));
				break;
			default:
			}
			
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
				//error "no es pot dividir per 0"
				Error.escriuError(323, "", l, "");
				System.out.println("[ERR_SEM_23] " + l + ", No es pot dividir per 0");
				sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
				sem.setValue("VALOR", "indefinit");
				sem.setValue("ESTATIC", false);
				break;
			}
			sem.setValue("VALOR", ((int)sem.getValue("VALOR")) / ((int)sem2.getValue("VALOR")));
			break;
		default:
		}
		gc.gc("li $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + sem.getValue("VALOR"));
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


	public Semantic VAR_esVariable(Semantic sem, TaulaSimbols ts, int l) {

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
				//error: nomes es poden fer servir variables
				Error.escriuError(311, (String)sem.getValue("TOKEN"), l, "");
				System.out.println("[ERR_SEM_11] " + l + ", L’identificador [" + (String)sem.getValue("TOKEN") + "] no és una variable ");
			} else {
				//error: variable no declarada
				Error.escriuError(39, (String)sem.getValue("TOKEN"), l, "");
				System.out.println("[ERR_SEM_9] " + l + ", L’identificador [" + (String)sem.getValue("TOKEN") + "] no ha estat declarat");
				//creem variable fantasma
				creaVariableFantasma(sem, ts);
			}
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			return sem;
		}
	}
	
	private void creaVariableFantasma(Semantic sem, TaulaSimbols ts) {
		ts.obtenirBloc(ts.getBlocActual()).inserirVariable(new Variable(
				(String)sem.getValue("TOKEN"),
				new TipusIndefinit("indefinit", 4),
				despl
				));
		despl = despl + 4;
	}

	public Semantic VAR1_comprovaArray(Semantic sem, Semantic sem2, TaulaSimbols ts, int l) {

		//comprova si sem es array
		if (!(sem.getValue("TIPUS") instanceof TipusArray)) {
			//error: variable no declarada com array
			Error.escriuError(322, ((ITipus)sem.getValue("TIPUS")).getNom(), l, "");
			System.out.println("[ERR_SEM_22] " + l + ", Tipus [" + ((ITipus)sem.getValue("TIPUS")).getNom() + "] invalid per aquest tipus d'operació");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			return sem;
		}
		
		//comprova si sem2 es sencer
		if (!(sem2.getValue("TIPUS") instanceof TipusSimple) || !((TipusSimple)sem2.getValue("TIPUS")).getNom().equals("sencer")) {
			//error: rang invalid
			Error.escriuError(313, "", l, "");
			System.out.println("[ERR_SEM_13] " + l + ", El tipus de l’índex d’accés del vector no és SENCER");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
			return sem;
		}
		int desplaçamentIndex = 0;
		int registre = gc.getRegistre();
		//si sem2 es estatic, comprova que estigui dins el rang
		if ((boolean)sem2.getValue("ESTATIC")) {
			int v = (int)sem2.getValue("VALOR");
			desplaçamentIndex = v;
			if ((int)((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitInferior() > v ||
					(int)((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitSuperior() < v) {
				//error: index fora de rang
				Error.escriuError(324, "", l, "");
				System.out.println("[ERR_SEM_24] " + l + ", índex estàtic fora de rang");
				sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
				return sem;
			}
			
		} else {
			//TODO anar a variable a agafar valor
			int registre2 = gc.getRegistre();
			
			if (registre != -1 && registre2 != -1) {
				String etiqueta = gc.demanarEtiqueta();
				gc.gc("lw   $" + gc.getNomRegistre(registre) + ", -" + (int)sem2.getValue("DESPL") + "($gp)");
				gc.gc("li   $" + gc.getNomRegistre(registre2) + ", " + (int)((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitInferior());
				gc.gc("blt   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", " + etiqueta);
				gc.gc("li   $" + gc.getNomRegistre(registre2) + ", " + (int)((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitSuperior());
				gc.gc("bgt   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", " + etiqueta);
				//printar error
				sem.setValue("LABEL", etiqueta);
				gc.freeRegistre(registre2);
			} else { System.out.println("No queden registres lliures!"); }
		}
		
		sem.setValue("LIMIT", (int)((TipusArray)ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitInferior());
		sem.setValue("TIPUS", ((TipusArray)FACTOR_getIdentificador(sem, ts, l).getValue("TIPUS")).getTipusElements());
		if (desplaçamentIndex != 0){
			gc.freeRegistre(registre);
			sem.setValue("VALOR", desplaçamentIndex);
		}
		else sem.setValue("REG", registre);
		
		return sem;

	}
	
	public Semantic FACTOR_getIdentificador(Semantic sem, TaulaSimbols ts, int l) {
		
		
		//System.out.println("=============\nID!!\n=========");
		//System.out.println("Buscant id " + (String)sem.getValue("TOKEN") + " al bloc " + ts.getBlocActual());
		
		//si esta al bloc 1, primer busca alla
		if (ts.getBlocActual() > 0) {
			
			//System.out.println("Estem a bloc 1");
			if (ts.obtenirBloc(ts.getBlocActual()).existeixConstant((String)sem.getValue("TOKEN"))) {
				sem.setValue("TIPUS", ts.obtenirBloc(ts.getBlocActual()).obtenirConstant((String)sem.getValue("TOKEN")).getTipus());
				sem.setValue("VALOR", ts.obtenirBloc(ts.getBlocActual()).obtenirConstant((String)sem.getValue("TOKEN")).getValor());
				sem.setValue("ESTATIC", true);
				sem.setValue("REFERENCIA", false);
				//System.out.println("CONSTANT!!\n" + sem.prettyPrint());
				return sem;
			}
			if (ts.obtenirBloc(ts.getBlocActual()).existeixVariable((String)sem.getValue("TOKEN"))) {
				sem.setValue("TIPUS", ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus());
				sem.setValue("VALOR", -1);
				sem.setValue("ESTATIC", false);
				sem.setValue("DESPL", ts.obtenirBloc(ts.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getDesplacament());
				//System.out.println("VARIABLE!!\n" + sem.prettyPrint());

				return sem;
			}
			if (ts.obtenirBloc(ts.getBlocActual()).existeixProcediment((String)sem.getValue("TOKEN"))) {
				sem.setValue("TIPUS", ((Funcio)ts.obtenirBloc(ts.getBlocActual()).obtenirProcediment((String)sem.getValue("TOKEN"))).getTipus());
				sem.setValue("VALOR", -1);
				sem.setValue("ESTATIC", false);
				sem.setValue("REFERENCIA", false);
				//System.out.println("FUNCIO!!\n" + sem.prettyPrint());
				return sem;
			}
			
		}
		
		//System.out.println("busquem al bloc 0");
		//sino, busca al bloc 0
		if (ts.obtenirBloc(0).existeixConstant((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ts.obtenirBloc(0).obtenirConstant((String)sem.getValue("TOKEN")).getTipus());
			sem.setValue("VALOR", ts.obtenirBloc(0).obtenirConstant((String)sem.getValue("TOKEN")).getValor());
			sem.setValue("ESTATIC", true);
			sem.setValue("REFERENCIA", false);
			//System.out.println("CONSTANT!!\n" + sem.prettyPrint());
			return sem;
		}
		if (ts.obtenirBloc(0).existeixVariable((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ts.obtenirBloc(0).obtenirVariable((String)sem.getValue("TOKEN")).getTipus());
			sem.setValue("VALOR", -1);
			sem.setValue("ESTATIC", false);
			sem.setValue("DESPL", ts.obtenirBloc(0).obtenirVariable((String)sem.getValue("TOKEN")).getDesplacament());
			//System.out.println("VARIABLE!!\n" + sem.prettyPrint());
			return sem;
		}
		if (ts.obtenirBloc(0).existeixProcediment((String)sem.getValue("TOKEN"))) {
			sem.setValue("TIPUS", ((Funcio)ts.obtenirBloc(0).obtenirProcediment((String)sem.getValue("TOKEN"))).getTipus());
			sem.setValue("VALOR", -1);
			sem.setValue("ESTATIC", false);
			sem.setValue("REFERENCIA", false);
			//System.out.println("FUNCIO!!\n" + sem.prettyPrint());
			return sem;
		}
		
		//sino ERROR variable no definida
		Error.escriuError(39, (String)sem.getValue("TOKEN"), l, "");
		System.out.println("[ERR_SEM_9] " + l + ", L’identificador [" + (String)sem.getValue("TOKEN") + "] no ha estat declarat");
		creaVariableFantasma(sem, ts);
		sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));
		sem.setValue("VALOR", "indefinit");
		sem.setValue("ESTATIC", false);
		sem.setValue("REFERENCIA", false);
		return sem;
	}
	
	
	
	public Semantic FACTOR1_buscaFuncio(Semantic sem, TaulaSimbols ts, int l) {
		
		if (!ts.obtenirBloc(0).existeixProcediment((String)sem.getValue("TOKEN"))) {
			//error: no es una funcio
			Error.escriuError(322, ((ITipus)sem.getValue("TIPUS")).getNom(), l, "");
			System.out.println("[ERR_SEM_22] " + l + ", Tipus [" + ((ITipus)sem.getValue("TIPUS")).getNom() + "] invalid per aquest tipus d'operació");
			sem.setValue("FUNCIO", new TipusIndefinit("indefinit", 4));
			sem.setValue("INDEX", 0);
			return sem;
		}
		sem.setValue("FUNCIO", ts.obtenirBloc(0).obtenirProcediment((String)sem.getValue("TOKEN")));
		sem.setValue("INDEX", 0);
		return sem;
	}
	
	public Semantic LL_EXPRESIO_comprovaParametre(Semantic sem, Semantic sem2, int l) {
		
		
		if (!(sem.getValue("FUNCIO") instanceof Funcio)) {
			//error id no es una funcio
			Error.escriuError(322, ((ITipus)sem.getValue("TIPUS")).getNom(), l, "");
			System.out.println("[ERR_SEM_22] " + l + ", Tipus [" + ((ITipus)sem.getValue("TIPUS")).getNom() + "] invalid per aquest tipus d'operació");
			return sem;
		}
		
		Funcio f = (Funcio)sem.getValue("FUNCIO");
		
		if ((int)sem.getValue("INDEX") >= f.getNumeroParametres()) {
			//error mes parametres dels que toca
			Error.escriuError(316, (int)sem.getValue("INDEX") + "", l, "parametre inexistent");
			System.out.println("[ERR_SEM_16] " + l + ", El tipus del parametre numero " + (int)sem.getValue("INDEX") + " de la funcio no coincideix amb el tipus en la seva declaracio [parametre inexistent]");
			sem.setValue("INDEX", (int)sem.getValue("INDEX")+1);
			return sem;
		}
		
		
		if (sem2.getValue("TIPUS") instanceof TipusIndefinit ||
				!f.obtenirParametre((int)sem.getValue("INDEX")).getTipus().equals((ITipus)sem2.getValue("TIPUS"))) {
			//error tipus no coincideicen
			Error.escriuError(316, (int)sem.getValue("INDEX") + "", l, f.obtenirParametre((int)sem.getValue("INDEX")).getNom());
			System.out.println("[ERR_SEM_16] " + l + ", El tipus del parametre numero " + (int)sem.getValue("INDEX") + " de la funcio no coincideix amb el tipus en la seva declaracio [" + f.obtenirParametre((int)sem.getValue("INDEX")).getNom() + "]");
			sem.setValue("INDEX", (int)sem.getValue("INDEX")+1);
			return sem;
		}
		
		if (!(boolean)sem2.getValue("REFERENCIA") &&
				f.obtenirParametre((int)sem.getValue("INDEX")).getTipusPasParametre().equals(TipusPasParametre.REFERENCIA)) {
			//error no es pot passar per referencia
			Error.escriuError(317, (int)sem.getValue("INDEX") + "", l, "");
			System.out.println("[ERR_SEM_17] " + l + ", El parametre numero " + (int)sem.getValue("INDEX") + " de la funcio no es pot passar per referencia");
			sem.setValue("INDEX", (int)sem.getValue("INDEX")+1);
			return sem;
		}
		
		
		sem.setValue("INDEX", (int)sem.getValue("INDEX")+1);
		return sem;
	}
	
	
	public void INSTRUCCIO_comprovaRetornar(Semantic sem, ITipus tipus, int bloc, int l) {
		//comprovar que blocactual != 0
		//comprovar que exp tipus == retorn funcio tipus
		
		if (bloc == 0) {
			//error: no es pot retornar al main
			Error.escriuError(319, "", l, "");
			System.out.println("[ERR_SEM_19] " + l + ", Retornar fora de funcio");
			return;
		}
		
		if (!((ITipus)sem.getValue("TIPUS")).equals(tipus)) {
			//error: tipus de retorn incorrecte
			Error.escriuError(318, ((ITipus)sem.getValue("TIPUS")).getNom(), l, tipus.getNom());
			System.out.println("[ERR_SEM_18] " + l + ", No es poden operar expressions de tipus diferents, en aquest cas [" + ((ITipus)sem.getValue("TIPUS")).getNom() + "] i [" + tipus.getNom() + "]");
			return;
		}
		
		return;
		
	}
	

}
