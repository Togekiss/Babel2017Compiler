package analitzadorSintactic;

import codeGeneration.CodeGenOut;
import main.Error;
import main.Token;
import taulasimbols.Bloc;
import taulasimbols.Funcio;
import taulasimbols.ITipus;
import taulasimbols.Parametre;
import taulasimbols.TaulaSimbols;
import taulasimbols.TipusArray;
import taulasimbols.TipusCadena;
import taulasimbols.TipusIndefinit;
import taulasimbols.TipusPasParametre;
import taulasimbols.TipusSimple;
import analitzadorLexicografic.Alex;
import analitzadorSemantic.Asem;
import analitzadorSemantic.Semantic;

public class Asin {

	private Alex alex;
	private Error error;
	private Asem asem;
	private CodeGenOut gc;
	private Token lookAhead;
	private TaulaSimbols taulaSimbols;
	private boolean hiHaReturn;
	private ITipus tipusReturn;

	//CONSTRUCTOR
	public Asin (String args, String name) {

		alex = new Alex(args);
		error = new Error(name);
		gc = new CodeGenOut(name);
		asem = new Asem(gc);
		taulaSimbols = new TaulaSimbols();
		lookAhead = alex.getToken();
		alex.writeToken(lookAhead);
		hiHaReturn = false;
		tipusReturn = new TipusIndefinit("indefinit", 4);

	}


	//ACCEPTAR UN TOKEN
	private void Acceptar (String token) {

		if (lookAhead.getTipus().equals(token)) {
			lookAhead = alex.getToken();
			alex.writeToken(lookAhead);

		}

	}



	//CONSUMIR TOKENS FINS TROBAR UN DEL CONJUNT DE SINCRONITZACIO 
	/*private void consumir (ArrayList<String> l) {

		while (!l.contains(lookAhead.getTipus())) {
			lookAhead = alex.getToken();
		}
	}*/



	//SIMBOL AXIOMA
	public boolean P() {

		taulaSimbols.setBlocActual(0);
		taulaSimbols.inserirBloc(new Bloc());
		DECL();

		Acceptar("prog");

		PROG();

		System.out.println("Taula de simbols:");
		System.out.println(taulaSimbols.toXml());

		if (lookAhead.esEOF()) {
			error.tancaFitxer();
			alex.tancaFitxer();
			gc.gc("jr $ra");         
			gc.tancaFitxer();
			return true;
		}
		else {
			Error.escriuError(26, "", alex.getLiniaActual(), "");
			error.tancaFitxer();
			alex.tancaFitxer();
			gc.gc("jr $ra");   
			gc.tancaFitxer();
			return false;
		}





	}

	private void PROG() {
		LL_INST();

		Acceptar("fiprog");
		return;
	}


	private void DECL() {

		DECL_CONST_VAR();
		DECL_FUNC();
		return;

	}


	private void DECL_CONST_VAR() {

		switch (lookAhead.getTipus()) {

		case "const":
			DECL_CONST();
			DECL_CONST_VAR();
			return;

		case "var":
			DECL_VAR();
			DECL_CONST_VAR();
			return;

		default: 
			return;
		}
	}


	private void DECL_CONST(){
		Semantic sem = new Semantic();

		Acceptar("const");
		if (lookAhead.getTipus().equals("identificador"))
			sem.setValue("TOKEN", lookAhead.getLexema());
		String cte = lookAhead.getLexema();
		Acceptar("identificador");
		Acceptar("igual");
		sem = EXPRESIO(sem);
		//expresio ja porta token, tipus, valor, estatic
		//Comprovar que tipus i valor tenen sentit, i que es estatic
		//Tornem a posar token per si sha perdut evaluant lexprexio
		sem.setValue("TOKEN", cte);
		asem.afegirConstant(sem, taulaSimbols, alex.getLiniaActual());
		if (!(sem.getValue("TIPUS") instanceof TipusCadena))
			gc.freeRegistre((int) sem.getValue("REG"));
		Acceptar("punt_i_coma");
		return;

	}


	private void DECL_VAR() {
		Semantic sem = new Semantic();

		Acceptar("var");
		if (lookAhead.getTipus().equals("identificador"))
			sem.setValue("TOKEN", lookAhead.getLexema());
		Acceptar("identificador");
		Acceptar("dos_punts");
		sem = TIPUS(sem);
		sem.setValue("VALOR", -1);
		gc.gc("li   $t0, 0");
		gc.gc("sw   $t0, -" + asem.getDespl() + "($gp)");
		asem.afegirVariable(sem, taulaSimbols, alex.getLiniaActual());
		Acceptar("punt_i_coma");
		return;

	}


	private void DECL_FUNC() {

		//creem descriptor per omplir llistat de parametres
		Funcio funcio = new Funcio();


		switch (lookAhead.getTipus()) {

		case "funcio":
			Acceptar("funcio"); 
			funcio.setNom(lookAhead.getLexema());
			Acceptar("identificador");
			Acceptar("parentesi_obert");
			funcio = LL_PARAM(funcio);
			Acceptar("parentesi_tancat");
			Acceptar("dos_punts");
			funcio.setTipus(new TipusSimple(lookAhead.getLexema(), 4, -2147483648, 2147483647));
			tipusReturn = new TipusSimple(lookAhead.getLexema(), 4, -2147483648, 2147483647);
			hiHaReturn = false;
			Acceptar("tipus_simple");

			asem.afegirFuncio(funcio, taulaSimbols, alex.getLiniaActual());
			Acceptar("punt_i_coma");

			DECL_CONST_VAR();

			Acceptar("func");

			LL_INST();

			Acceptar("fifunc");
			Acceptar("punt_i_coma");

			//hi ha algun return
			if (!hiHaReturn) {
				//error: no hi ha cap return
				Error.escriuError(321, funcio.getNom(), alex.getLiniaActual(), "");
				System.out.println("[ERR_SEM_21] " + alex.getLiniaActual() + ", No hi ha cap retornar en la funció " + funcio.getNom());
			}

			taulaSimbols.esborrarBloc(1);
			taulaSimbols.setBlocActual(0);
			DECL_FUNC();
			return;

		default:
			return;

		}

	}


	private Funcio LL_PARAM(Funcio funcio) {

		switch (lookAhead.getTipus()) {

		case "perref":
		case "perval":
			funcio = LL_PARAM1(funcio);
			return funcio;

		default:
			return funcio;

		}

	}


	private Funcio LL_PARAM1(Funcio funcio) {

		Semantic semantic = new Semantic();
		Parametre param = new Parametre();

		param = PER(param);
		param.setNom(lookAhead.getLexema());
		Acceptar("identificador");
		Acceptar("dos_punts");
		semantic = TIPUS(semantic);
		param.setTipus((ITipus)semantic.getValue("TIPUS"));
		funcio.inserirParametre(param);
		funcio = LL_PARAM11(funcio);
		return funcio;

	}

	private Parametre PER(Parametre param) {

		switch (lookAhead.getTipus()) {

		case "perref":
			param.setTipusPasParametre(TipusPasParametre.REFERENCIA);
			Acceptar("perref");
			return param;

		case "perval":
			param.setTipusPasParametre(TipusPasParametre.VALOR);
			Acceptar("perval");
			return param;

		default: return param;

		}


	}

	private Funcio LL_PARAM11(Funcio funcio) {

		switch (lookAhead.getTipus()) {

		case "coma":
			Acceptar("coma");
			funcio = LL_PARAM1(funcio);
			return funcio;

		default:
			return funcio;

		}

	}

	private Semantic TIPUS(Semantic sem) {

		Semantic sem2 = new Semantic();
		sem.setValue("TIPUS", new TipusIndefinit("indefinit", 4));

		switch (lookAhead.getTipus()) {

		case "tipus_simple": 
			sem.setValue("TIPUS", new TipusSimple(lookAhead.getLexema(), 4, -2147483648, 2147483647));
			sem.setValue("ESTATIC", false);
			sem.setValue("VALOR", "null");
			sem.setValue("TAMANY", 4);
			Acceptar("tipus_simple");
			return sem;

		case "vector":			
			Acceptar("vector");
			Acceptar("claudator_obert");
			sem2 = EXPRESIO(sem2);

			//System.out.println("DIMENSIO 1\n" + sem2.prettyPrint());

			int dim1;
			//comprovem 1a dimensio del array
			if ( sem2.getValue("TIPUS") instanceof TipusSimple &&
					((TipusSimple)sem2.getValue("TIPUS")).getNom().equals("sencer") &&
					((boolean)sem2.getValue("ESTATIC")) == true) {
				dim1 = (int)sem2.getValue("VALOR");
				gc.freeRegistre((int)sem2.getValue("REG"));
			} else {
				//ERROR
				Error.escriuError(37, "", alex.getLiniaActual(), "");
				System.out.println("[ERR_SEM_7] " + alex.getLiniaActual() + ", El rang del vector ha de ser SENCER i ESTATIC");
				dim1 = Integer.MAX_VALUE;
			}

			Acceptar("rang");
			sem2 = EXPRESIO(sem2);

			//System.out.println("DIMENSIO 2\n" + sem2.prettyPrint());

			int dim2;
			//comprovem 2a dimensio del array
			if (sem2.getValue("TIPUS") instanceof TipusSimple &&
					((TipusSimple)sem2.getValue("TIPUS")).getNom().equals("sencer") &&
					((boolean)sem2.getValue("ESTATIC")) == true)
				dim2 = (int)sem2.getValue("VALOR");
			else {
				//ERROR
				Error.escriuError(37, "", alex.getLiniaActual(), "");
				System.out.println("[ERR_SEM_7] " + alex.getLiniaActual() + ", El rang del vector ha de ser SENCER i ESTATIC");
				dim2 = Integer.MIN_VALUE;
			}
			if (!(sem2.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem2.getValue("REG"));
			Acceptar("claudator_tancat");
			Acceptar("de");

			sem.setValue("TIPUS", asem.TIPUS_comprovaArray(dim1, dim2, lookAhead.getLexema(), alex.getLiniaActual()));
			Acceptar("tipus_simple");

			sem.setValue("ESTATIC", false);
			sem.setValue("VALOR", "null");
			sem.setValue("TAMANY", 4 * (dim2 - dim1 + 1));
			return sem;

		default: return sem;

		}
	}


	private Semantic EXPRESIO(Semantic sem) {

		sem = EXPRESIO_SIMPLE(sem);
		sem = EXPRESIO1(sem);
		
		return sem;

	}


	private Semantic EXPRESIO1(Semantic sem) {

		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "oper_rel": 
			sem.setValue("OPERADOR", lookAhead.getLexema());
			Acceptar("oper_rel");
			sem2 = EXPRESIO_SIMPLE(sem2);
			//operar sem i sem2 segons operador
			sem = asem.EXPRESIO1_operar(sem, sem2, alex.getLiniaActual());
			gc.freeRegistre((int) sem2.getValue("REG"));
			sem.removeAttribute("OPERADOR");
			return sem;

		default: 
			return sem;

		}
	}


	private Semantic EXPRESIO_SIMPLE(Semantic sem) {

		sem = OP_INICI_EXP(sem); 
		sem  = TERME(sem);
		//operar op inici exp amb terme
		sem = asem.EXPRESIO_SIMPLE_operar(sem, alex.getLiniaActual());
		sem.removeAttribute("OPERADOR");
		sem = EXPRESIO_SIMPLE1(sem);
		return sem;

	}


	private Semantic EXPRESIO_SIMPLE1(Semantic sem) {

		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "suma":
		case "resta":
		case "or":
			sem = OP_EXP(sem); 
			sem2 = TERME(sem2);

			//System.out.println("DINS EXP_SIMPLE1 ABANS DE OPERAR");
			//System.out.println("SEM" + sem.prettyPrint());
			//System.out.println("SEM2" + sem2.prettyPrint());

			//operar sem amb sem2 segons operador i guardar a sem
			sem = asem.EXPRESIO_SIMPLE1_operar(sem, sem2, alex.getLiniaActual());
			gc.freeRegistre((int) sem2.getValue("REG"));
			//System.out.println("DESPRES D'OPERAR" + sem.prettyPrint());
			sem.removeAttribute("OPERADOR");
			sem = EXPRESIO_SIMPLE1(sem);
			return sem;

		default:
			return sem;

		}

	}

	private Semantic TERME(Semantic sem) {

		sem = FACTOR(sem);
		sem = TERME1(sem);
		return sem;

	}

	private Semantic TERME1(Semantic sem) {

		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "multiplicacio":
		case "divisio":
		case "and":
			sem = OP_TERME(sem); 
			sem2 = FACTOR(sem2);
			//operar sem i sem2 segons OP_TERME
			asem.TERME_operar(sem, sem2, alex.getLiniaActual());
			gc.freeRegistre((int) sem2.getValue("REG"));
			sem.removeAttribute("OPERADOR");
			sem = TERME1(sem);
			return sem;

		default: 
			return sem;

		}

	}


	private Semantic OP_INICI_EXP(Semantic sem) {

		switch(lookAhead.getTipus()) {

		case "suma":
			sem.setValue("OPERADOR", "suma");
			Acceptar("suma");
			return sem;

		case "resta":
			sem.setValue("OPERADOR", "resta");
			Acceptar("resta");
			return sem;

		case "not":
			sem.setValue("OPERADOR", "not");
			Acceptar("not");
			return sem;

		default:
			return sem;

		}


	}

	private Semantic OP_EXP (Semantic sem) {

		//System.out.println("DINS OP_EXP");

		switch (lookAhead.getTipus()) {

		case "suma":
			sem.setValue("OPERADOR", "suma");
			Acceptar("suma");
			return sem;

		case "resta":
			sem.setValue("OPERADOR", "resta");
			Acceptar("resta");
			return sem;

		case "or":
			sem.setValue("OPERADOR", "or");
			Acceptar("or");
			return sem;

		default: return sem;

		}
	}

	private Semantic OP_TERME (Semantic sem) {

		switch (lookAhead.getTipus()) {

		case "multiplicacio":
			sem.setValue("OPERADOR", "multiplicacio");
			Acceptar("multiplicacio");
			return sem;

		case "divisio":
			sem.setValue("OPERADOR", "divisio");
			Acceptar("divisio");
			return sem;

		case "and":
			sem.setValue("OPERADOR", "and");
			Acceptar("and");
			return sem;	

		default: return sem;

		}

	}

	private Semantic FACTOR (Semantic sem) {

		//System.out.println("DINS FACTOR");
		switch (lookAhead.getTipus()) {

		case "ct_enter":
			sem.setValue("TIPUS", new TipusSimple("sencer", 4, -2147483648, 2147483647));
			sem.setValue("VALOR", Integer.parseInt(lookAhead.getLexema()));
			sem.setValue("ESTATIC", true);
			sem.setValue("REFERENCIA", false);
			//System.out.println(sem.prettyPrint());
			Acceptar("ct_enter"); 
			int reg1 = gc.getRegistre();
			gc.gc("li $" + gc.getNomRegistre(reg1) + ", " + sem.getValue("VALOR"));
			sem.setValue("REG", reg1);
			return sem;

		case "ct_logica":
			sem.setValue("TIPUS", new TipusSimple("logic", 4, -2147483648, 2147483647));
			sem.setValue("VALOR", lookAhead.getLexema().equals("cert")?0x00000001:0);
			sem.setValue("ESTATIC", true);
			sem.setValue("REFERENCIA", false);
			//System.out.println(sem.prettyPrint());
			Acceptar("ct_logica"); 
			int reg2 = gc.getRegistre();
			gc.gc("li $" + gc.getNomRegistre(reg2) + ", " + sem.getValue("VALOR"));
			sem.setValue("REG", reg2);
			return sem;

		case "ct_cadena":
			sem.setValue("TIPUS", new TipusCadena("cadena", lookAhead.getLexema().length(), lookAhead.getLexema().length()));
			sem.setValue("VALOR", lookAhead.getLexema());
			sem.setValue("ESTATIC", true);
			sem.setValue("REFERENCIA", false);
			//System.out.println(sem.prettyPrint());
			//TODO codi cadena
			/*String eti = gc.demanarEtiqueta();
			gc.gc(".data");
			gc.gcEtiqueta(eti + ": .asciiz " + sem.getValue("VALOR"));
			gc.gc(".text");
			sem.setValue("REG", eti);*/
			Acceptar("ct_cadena");
			return sem;

		case "identificador":
			//buscar si id existeix i agafar tipus i valor i estatic
			sem.setValue("TOKEN", lookAhead.getLexema());
			sem = asem.FACTOR_getIdentificador(sem, taulaSimbols, alex.getLiniaActual());
			Acceptar("identificador");
			sem.setValue("REFERENCIA", true);
			sem = FACTOR1(sem);
			return sem;

		case "parentesi_obert":
			Acceptar("parentesi_obert");
			sem = EXPRESIO(sem);
			//if (!(sem.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem.getValue("REG"));
			Acceptar("parentesi_tancat");
			return sem;

		default: return sem;

		}
	}

	private Semantic FACTOR1 (Semantic sem) {
		//sem es el descriptor del identificador del que ve

		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "parentesi_obert":
			Acceptar("parentesi_obert"); 
			sem.setValue("REFERENCIA", false);
			//es funcio
			//basicament per passar-li el descriptor de la funcio i index param = 0
			sem2 = asem.FACTOR1_buscaFuncio(sem, taulaSimbols, alex.getLiniaActual());
			LL_EXPRESIO(sem2); 
			Acceptar("parentesi_tancat");
			//comprovar que num parametres ok
			if (!(sem2.getValue("FUNCIO") instanceof Funcio) || (int)sem2.getValue("INDEX") != ((Funcio)sem2.getValue("FUNCIO")).getNumeroParametres()) {
				//error: num de parametres incorrecte
				int nparam = 0;
				if (sem2.getValue("FUNCIO") instanceof Funcio) nparam = ((Funcio)sem2.getValue("FUNCIO")).getNumeroParametres();
				else {
					nparam = 0;
				}
				Error.escriuError(315, (int)sem2.getValue("INDEX") + "", alex.getLiniaActual(), nparam + "");
				System.out.println("[ERR_SEM_15] " + alex.getLiniaActual() + ", La funció en declaració té " + nparam + " paràmetres mentre que en ús té " + (int)sem2.getValue("INDEX"));
			}
			//retornar tipus de retorn de funcio
			return sem;

		case "claudator_obert":
			Acceptar("claudator_obert"); 
			//es vector
			sem2 = EXPRESIO(sem2);
			
			//System.out.println(sem);
			//System.out.println(sem2);
			
			//comprovar que expressio es int i esta dins el rang (si es estatica)
			sem = asem.VAR1_comprovaArray(sem, sem2, taulaSimbols, alex.getLiniaActual());
			//gc.freeRegistre((int) sem.getValue("REG"));
			//if (!(sem2.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem2.getValue("REG"));
			Acceptar("claudator_tancat"); 
			//retornar tipus d'element de vector
			//TODO ID ES UN VECTOR
			
			if (sem.getValue("REG") != null) {
				String etiqueta = gc.demanarEtiqueta();
				gc.gc("b	" + etiqueta);
				gc.gcEtiqueta((String)sem.getValue("LABEL") + ":");
				gc.gc("li   $v0, 4");
				gc.gc("la   $a0, error");
				gc.gc("sw   $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", 0($gp)");
				gc.gc("syscall");
				gc.gc("li   $v0, 10");
				gc.gc("syscall");
				gc.gcEtiqueta(etiqueta + ":");
				
				int registre = gc.getRegistre();
				int registre2 = gc.getRegistre();
				if (registre != -1 && registre2 != -1) {
					gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + (int)sem.getValue("LIMIT")); //li
					gc.gc("li   $" + gc.getNomRegistre(registre2) + ", 4");
					gc.gc("mul   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2));
					gc.gc("li   $" + gc.getNomRegistre(registre2) + ", " + (int)sem.getValue("DESPL")); //Adreça vector respecte $gp
					gc.gc("subu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre));
					gc.gc("li   $" + gc.getNomRegistre(registre2) + ", 4");
					gc.gc("mul   $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre(registre2));
					gc.gc("addu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre));
					gc.gc("la   $" + gc.getNomRegistre(registre2) + ", 0($gp)"); //Adreça $gp
					gc.gc("subu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre)); //-Adreça($gp)
					gc.gc("lw	$" + gc.getNomRegistre(registre2) + ", 0($" + gc.getNomRegistre(registre) + ")");
					gc.freeRegistre(registre);
					gc.freeRegistre((int)sem.getValue("REG"));
					gc.freeRegistre((int)sem2.getValue("REG"));
					sem.setValue("REG", registre2);
				} else { System.out.println("No queden registres!"); }
		
			}else {
				sem.setValue("VALOR", (int)sem.getValue("DESPL") + ((int)sem.getValue("VALOR") -  (int)sem.getValue("LIMIT")) * 4);
				int reg = gc.getRegistre();
				if (reg != -1) {
					gc.gc("lw   $" + gc.getNomRegistre(reg) + ", -" + sem.getValue("VALOR") + "($gp)");
					sem.setValue("REG", reg);
				} else { System.out.println("No queden registres!"); }
			}
			
			return sem;

		default:
			//TODO QUAN VA A ID SENSE RES MES
			int registre = gc.getRegistre();
			if (registre != -1) {
				
				if ((boolean)sem.getValue("ESTATIC") == true && sem.getValue("ESCRIURE") == null) {
					//TODO asdsad
					gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + sem.getValue("VALOR"));
				} else  if ((boolean)sem.getValue("ESTATIC") == false) {
					gc.gc("lw   $" + gc.getNomRegistre(registre) + ", -" + sem.getValue("DESPL") + "($gp)");
				}
				
				sem.setValue("REG", registre);
			} else { System.out.println("No queden registres!"); }

			return sem;	

		}
	}


	private void LL_EXPRESIO(Semantic sem) {


		//s'ha de comprovar que la expresio numero x correspongui
		//amb el parametre numero x de la funcio
		Semantic sem2 = new Semantic();
		sem2.setValue("REFERENCIA", true);

		switch (lookAhead.getTipus()) {

		case "suma":
		case "resta":
		case "not":
		case "ct_enter":
		case "ct_logica":
		case "ct_cadena":
		case "identificador":
		case "parentesi_obert":
			sem2 = EXPRESIO(sem2);
			//index param ++
			sem = asem.LL_EXPRESIO_comprovaParametre(sem, sem2, alex.getLiniaActual());
			if (!(sem2.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem2.getValue("REG"));
			LL_EXPRESIO1(sem);
			return;

		default: return;

		}

	}

	private void LL_EXPRESIO1 (Semantic sem) {

		switch (lookAhead.getTipus()) {

		case "coma":
			Acceptar("coma");
			LL_EXPRESIO(sem);
			return;

		default: if (!(sem.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem.getValue("REG"));
			return;

		}
	}

	private void LL_VAR () {

		Semantic sem = new Semantic();

		sem = VAR(sem);
		//comprovar que sem tipus == tipus simple
		
		if (!(sem.getValue("TIPUS") instanceof TipusSimple)) {
			//ERROR
			Error.escriuError(36, "", alex.getLiniaActual(), "");
			System.out.println("[ERR_SEM_6] " + alex.getLiniaActual() + ", El tipus ha de ser TIPUS SIMPLE");
		} else if (sem.getValue("TIPUS") instanceof TipusSimple && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer")){
			gc.gc("li   $v0, 5");
			gc.gc("syscall");
			//gc.gc("sw $v0, -" + taulaSimbols.obtenirBloc(0).obtenirVariable((String)sem.getValue("TOKEN")).getDesplacament() + "($gp)");
		} else if (sem.getValue("TIPUS") instanceof TipusSimple && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")){
			gc.gc("li   $v0, 5");
			gc.gc("syscall");
			int registre = gc.getRegistre();
			if (registre != -1) {
				String eti = gc.demanarEtiqueta();
				gc.gc("li   $" + gc.getNomRegistre(registre) + ", 2");
				gc.gc("blt	$v0, $" + gc.getNomRegistre(registre) + ", " + eti);
				gc.gc("li   $v0, 4");
				gc.gc("la   $a0, error2");
				gc.gc("syscall");
				gc.gc("li   $v0, 10");
				gc.gc("syscall");
				gc.gcEtiqueta(eti + ":");
			} else { System.out.println("No queden registres disponibles"); }	
		}
		
		if (sem.getValue("VALOR") == null && sem.getValue("REG") == null 
				&& (sem.getValue("TIPUS") instanceof TipusSimple && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer") ||
						sem.getValue("TIPUS") instanceof TipusSimple && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic"))) {
			gc.gc("sw $v0, -" + taulaSimbols.obtenirBloc(0).obtenirVariable((String)sem.getValue("TOKEN")).getDesplacament() + "($gp)");
		}
		
		if (sem.getValue("VALOR") != null && (int)sem.getValue("VALOR") != -1) {
			int registre = gc.getRegistre();
			if (registre != -1) {
				sem.setValue("VALOR", (int)sem.getValue("DESP") + ((int)sem.getValue("VALOR") - (int)sem.getValue("LIMIT")) * 4);
				gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + (int)sem.getValue("VALOR")); 
				gc.gc("sw   $v0, -" + gc.getNomRegistre(registre) + "($gp)");
			} else { System.out.println("No queden registres!"); }
		}
		
		if (sem.getValue("LABEL") != null) {
			int registre = gc.getRegistre();
			int registre2 = gc.getRegistre();
			
			if (registre != -1 && registre2 != -1) {
				
				gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + (int)sem.getValue("LIMIT")); //li
				gc.gc("li   $" + gc.getNomRegistre(registre2) + ", 4");
				gc.gc("mul   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2));
				gc.gc("li   $" + gc.getNomRegistre(registre2) + ", " + (int)sem.getValue("DESPL")); //Adreça vector respecte $gp
				gc.gc("subu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre));
				gc.gc("li   $" + gc.getNomRegistre(registre2) + ", 4");
				gc.gc("mul   $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre(registre2));
				gc.gc("addu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre));
				gc.gc("la   $" + gc.getNomRegistre(registre2) + ", 0($gp)"); //Adreça $gp
				gc.gc("subu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre)); //-Adreça($gp)
				gc.gc("sw   $v0, 0($" + gc.getNomRegistre(registre) + ")");
				
				String etiqueta = gc.demanarEtiqueta();
				gc.gc("b	" + etiqueta);
				gc.gcEtiqueta((String)sem.getValue("LABEL") + ":");
				gc.gc("li   $v0, 4");
				gc.gc("la   $a0, error");
				gc.gc("syscall");
				gc.gc("li   $v0, 10");
				gc.gc("syscall");
				gc.gcEtiqueta(etiqueta + ":");
				gc.freeRegistre((int)sem.getValue("REG"));
				gc.freeRegistre(registre);
				gc.freeRegistre(registre2);
				
			} else { System.out.println("No queden registres!"); }
		}

		LL_VAR1();
		return;
	}

	private void LL_VAR1 () {

		switch (lookAhead.getTipus()) {

		case "coma":
			Acceptar("coma");
			LL_VAR();
			return;

		default:
			return;

		}
	}

	private Semantic VAR (Semantic sem) {

		sem.setValue("TOKEN", lookAhead.getLexema());
		Acceptar("identificador");
		//comprovar que id es variable
		//retorna tipus variable o variable fantasma
		sem = asem.VAR_esVariable(sem, taulaSimbols, alex.getLiniaActual());
		sem = VAR1(sem);
		return sem;

	}

	private Semantic VAR1 (Semantic sem) {

		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "claudator_obert":
			//comprovar que identificador es array
			Acceptar("claudator_obert");
			sem2 = EXPRESIO(sem2);
			
			//comprovar que tipus sem2 == sencer
			//i si es estatic, esta dins el rang de id
			sem = asem.VAR1_comprovaArray(sem, sem2, taulaSimbols, alex.getLiniaActual());
			//if (!(sem2.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem2.getValue("REG"));
			Acceptar("claudator_tancat");
			return sem;						

		default:
			return sem;

		}
	}

	private void LL_INST () {
		INSTRUCCIO();
		Acceptar("punt_i_coma");
		LL_INST1();
		return;

	}

	private void LL_INST1 () {

		switch (lookAhead.getTipus()) {

		case "identificador":	
		case "escriure":		
		case "llegir":			
		case "cicle":			
		case "mentre":			
		case "si":				
		case "retornar":		
		case "percada":
			INSTRUCCIO(); 
			Acceptar("punt_i_coma"); 
			LL_INST1();	
			return;

		default:
			return;

		}
	}

	private void INSTRUCCIO () {

		Semantic sem = new Semantic();
		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "identificador":
			sem = VAR(sem);
			Acceptar("igual");
			sem2 = EXPRESIO(sem2);
			
			//comprovar que tipus sem1 == tipus sem2
			if (!sem.getValue("TIPUS").equals(sem2.getValue("TIPUS"))) {
				//error

				//System.out.println("SEM " + ((ITipus)sem.getValue("TIPUS")).toXml());
				//System.out.println("SEM2 " + ((ITipus)sem2.getValue("TIPUS")).toXml());
				Error.escriuError(312, ((ITipus)sem2.getValue("TIPUS")).getNom(), alex.getLiniaActual(), ((ITipus)sem.getValue("TIPUS")).getNom());
				System.out.println("igualacio [ERR_SEM_12] " + alex.getLiniaActual() +
						", La variable i l'expressió de assignació tenen tipus diferents. El tipus de la variable és ["
						+ ((ITipus)sem.getValue("TIPUS")).getNom() + "] i el de l’expressió és [" + ((ITipus)sem2.getValue("TIPUS")).getNom() + "]");

			}

			int despl = taulaSimbols.obtenirBloc(taulaSimbols.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getDesplacament();
			
			if (sem.getValue("VALOR") != null) {
				if (sem.getValue("REG") == null) {
					//Es vector i valor estatic
					System.out.println(sem.getValue("VALOR"));
					despl += ((int)sem.getValue("VALOR") - (int)sem.getValue("LIMIT")) * 4;
					int registre = gc.getRegistre();
					if (registre != -1) {
						gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + sem2.getValue("VALOR"));
						gc.gc("sw   $" + gc.getNomRegistre(registre) + ", -" + despl + "($gp)");
						gc.freeRegistre(registre);
					} else { System.out.println("No queden registres!"); }
				} else {
					//Es vector i valor no estatic
					int registre = gc.getRegistre();
					int registre2 = gc.getRegistre();
					if (registre != -1 && registre2 != -1) {
						gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + (int)sem.getValue("LIMIT")); //li
						gc.gc("li   $" + gc.getNomRegistre(registre2) + ", 4");
						gc.gc("mul   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2));
						gc.gc("li   $" + gc.getNomRegistre(registre2) + ", " + despl); //Adreça vector respecte $gp
						gc.gc("subu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre));
						gc.gc("li   $" + gc.getNomRegistre(registre2) + ", 4");
						gc.gc("mul   $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", $" + gc.getNomRegistre(registre2));
						gc.gc("addu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre));
						gc.gc("la   $" + gc.getNomRegistre(registre2) + ", 0($gp)"); //Adreça $gp
						gc.gc("subu   $" + gc.getNomRegistre(registre) + ", $" + gc.getNomRegistre(registre2) + ", $" + gc.getNomRegistre(registre)); //-Adreça($gp)
						//gc.gc("li   $" + gc.getNomRegistre(registre2) + ", " + sem2.getValue("VALOR"));
						gc.gc("sw   $" + gc.getNomRegistre((int)sem2.getValue("REG")) + ", 0($" + gc.getNomRegistre(registre) + ")");
						String etiqueta = gc.demanarEtiqueta();
						gc.gc("b	" + etiqueta);
						gc.gcEtiqueta((String)sem.getValue("LABEL") + ":");
						gc.gc("li   $v0, 4");
						gc.gc("la   $a0, error");
						gc.gc("syscall");
						gc.gc("li   $v0, 10");
						gc.gc("syscall");
						gc.gcEtiqueta(etiqueta + ":");
						gc.freeRegistre((int)sem.getValue("REG"));
						gc.freeRegistre(registre);
						gc.freeRegistre(registre2);
					} else { System.out.println("No queden registres!"); }
				}
			} else if ((sem.getValue("TIPUS") instanceof TipusArray)) {	
				int despl1 = taulaSimbols.obtenirBloc(0).obtenirVariable((String)sem2.getValue("TOKEN")).getDesplacament();
				int despl2 = taulaSimbols.obtenirBloc(0).obtenirVariable((String)sem.getValue("TOKEN")).getDesplacament();
			
				int li = (int)((TipusArray)taulaSimbols.obtenirBloc(taulaSimbols.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitInferior();
				int ls = (int)((TipusArray)taulaSimbols.obtenirBloc(taulaSimbols.getBlocActual()).obtenirVariable((String)sem.getValue("TOKEN")).getTipus()).obtenirDimensio(0).getLimitSuperior();

				ls -= li;
				ls++;
				
				int registre = gc.getRegistre();
				if (registre != -1) {
					for (int i = 0; i < ls * 4; i += 4) {
						gc.gc("lw   $" + gc.getNomRegistre(registre) + ", -" + (i+despl1) + "($gp)" );
						gc.gc("sw   $" + gc.getNomRegistre(registre) + ", -" + (i+despl2) + "($gp)" );
					}
					gc.freeRegistre(registre);
				} else { System.out.println("No queden registres!"); }
				
			
			
			} else if (!(sem.getValue("TIPUS") instanceof TipusIndefinit)) {
				//int registre = gc.getRegistre();
				//if (registre != -1) {
					//gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + sem2.getValue("VALOR"));
					//gc.gc("sw   $" + gc.getNomRegistre(registre) + ", -" + despl + "($gp)");
					//gc.freeRegistre(registre);
				//} else { System.out.println("No queden registres!"); }
				
				gc.gc("sw   $" + gc.getNomRegistre((int)sem2.getValue("REG")) + ", -" + despl + "($gp)");
				if (sem.getValue("REG") != null) gc.freeRegistre((int)sem.getValue("REG"));
				gc.freeRegistre((int)sem2.getValue("REG"));
			}

			/*int registre = gc.getRegistre();
			if (registre != -1) {
				gc.gc("li   $" + gc.getNomRegistre(registre) + ", " + sem2.getValue("VALOR"));
				gc.gc("sw   $" + gc.getNomRegistre(registre) + ", -" + despl + "($gp)");
				gc.freeRegistre(registre);
			} else { System.out.println("No queden registres!"); }
*/			if (!(sem2.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem2.getValue("REG"));
			return;

		case "escriure":
			Acceptar("escriure"); 
			Acceptar("parentesi_obert"); 
			LL_EXP_ESCRIURE(); 	 
			Acceptar("parentesi_tancat");
			return;		

		case "llegir":
			Acceptar("llegir"); 
			Acceptar("parentesi_obert"); 
			LL_VAR(); 	 
			Acceptar("parentesi_tancat"); 

			return;

		case "cicle":
			Acceptar("cicle");
			String etiqueta = gc.demanarEtiqueta();
			gc.gcEtiqueta(etiqueta + ":");
			LL_INST();
			Acceptar("fins"); // fins 	 
			sem = EXPRESIO(sem);
			//comprovar que sem tipus == logic
			if (!asem.esLogic(sem)) {
				//error
				Error.escriuError(38, "", alex.getLiniaActual(), "");
				System.out.println("[ERR_SEM_8] " + alex.getLiniaActual() + ", La condició no és de tipus LOGIC");
			}
			
			if (sem.getValue("VALOR") != null) {
				if ((int)sem.getValue("VALOR") == -1) 
					gc.gc("beqz	$" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + etiqueta);
				else if ((int)sem.getValue("VALOR") == 0)
					gc.gc("b	" + etiqueta);
			}	
				
			if (!(sem.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem.getValue("REG"));
			return;

		case "mentre":
			Acceptar("mentre");
			String etiqueta2 = gc.demanarEtiqueta();
			String etiqueta3 = gc.demanarEtiqueta();
			gc.gcEtiqueta(etiqueta2 + ":");
			sem = EXPRESIO(sem);
			//comprovar que sem tipus == logic
			if (!asem.esLogic(sem)) {
				//error
				Error.escriuError(38, "", alex.getLiniaActual(), "");
				System.out.println("[ERR_SEM_8] " + alex.getLiniaActual() + ", La condició no és de tipus LOGIC");
			}
			Acceptar("fer");
				
			if (sem.getValue("VALOR") != null) {
				if ((int)sem.getValue("VALOR") == -1) 
					gc.gc("beqz	$" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + etiqueta3);
				else if ((int)sem.getValue("VALOR") == 0)
					gc.gc("b	" + etiqueta3);
			}	
			if (!(sem.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem.getValue("REG"));
			LL_INST();
			gc.gc("b	" + etiqueta2);
			gc.gcEtiqueta(etiqueta3 + ":");
			Acceptar("fimentre");	

			return;

		case "si":
			Acceptar("si"); 
			String etiqueta4 = gc.demanarEtiqueta();
			String etiqueta5 = gc.demanarEtiqueta();
			sem = EXPRESIO(sem);
			//comprovar que sem tipus == logic
			if (!asem.esLogic(sem)) {
				//error
				Error.escriuError(38, "", alex.getLiniaActual(), "");
				System.out.println("[ERR_SEM_8] " + alex.getLiniaActual() + ", La condició no és de tipus LOGIC");
			}
			Acceptar("llavors");
			
			if (sem.getValue("VALOR") != null) {
				System.out.println(sem.getValue("VALOR"));
				if ((int)sem.getValue("VALOR") == -1) 
					gc.gc("beqz	$" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + etiqueta4);
				else if ((int)sem.getValue("VALOR") == 0)
					gc.gc("b	" + etiqueta4);
			}	
			
			if (!(sem.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem.getValue("REG"));
			LL_INST();
			gc.gc("b	" + etiqueta5);
			gc.gcEtiqueta(etiqueta4 + ":");
			SINO();
			Acceptar("fisi"); 
			gc.gcEtiqueta(etiqueta5 + ":");

			return;

		case "retornar":
			Acceptar("retornar"); 
			sem = EXPRESIO(sem);
			//comprovar que blocactual != 0
			//comprovar que exp tipus == retorn funcio tipus
			asem.INSTRUCCIO_comprovaRetornar(sem, tipusReturn, taulaSimbols.getBlocActual(), alex.getLiniaActual());
			if (!(sem.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem.getValue("REG"));
			hiHaReturn = true;
			return;

		case "percada":
			Acceptar("percada"); 
			Acceptar("identificador"); 
			Acceptar("en");
			Acceptar("identificador"); 
			Acceptar("fer");

			LL_INST();

			Acceptar("fiper"); 

			return;

		default: return;

		}
	}


	/*private void INSTRUCCIO1 () {
		//Crec que ja no el fem servir but still

		Semantic semantic = new Semantic();

		switch (lookAhead.getTipus()) {

		case "suma":			
		case "resta":						
		case "not":				
		case "ct_enter":		
		case "ct_logica":		
		case "ct_cadena":		
		case "identificador":			
		case "parentesi_obert":
			semantic = EXPRESIO(semantic);
			return;


		case "si":
			Acceptar("si");
			Acceptar("parentesi_obert");
			semantic = EXPRESIO(semantic);
			Acceptar("parentesi_tancat");
			Acceptar("interrogant");
			semantic = EXPRESIO(semantic);
			Acceptar("dos_punts");
			semantic = EXPRESIO(semantic);

			return;

		default: return;

		}
	}*/

	private void LL_EXP_ESCRIURE () {

		Semantic sem = new Semantic();

		switch (lookAhead.getTipus()) {

		case "suma":			
		case "resta":						
		case "not":				
		case "ct_enter":		
		case "ct_logica":		
		case "ct_cadena":		
		case "identificador":		
		case "parentesi_obert":
			sem.setValue("ESCRIURE", 1);
			sem = EXPRESIO(sem);
			//comprovar que tipus == tipus simple o cadena
			if (!asem.LL_EXP_ESCRIURE_esValid(sem)) {
				//error
				Error.escriuError(314, "", alex.getLiniaActual(), "");
				System.out.println("[ERR_SEM_14] " + alex.getLiniaActual() + ", El tipus de la expressió en ESCRIURE no és simple o no és una constant cadena");
			}
			//System.out.println(sem);
			//if (sem.getValue("TIPUS") instanceof TipusSimple) System.out.println(((TipusSimple)sem.getValue("TIPUS")).getNom());
			if ((sem.getValue("TIPUS") instanceof TipusCadena) || taulaSimbols.obtenirBloc(0).existeixConstant((String)sem.getValue("TOKEN"))
				&& taulaSimbols.obtenirBloc(0).obtenirConstant((String)sem.getValue("TOKEN")).getTipus() 
				instanceof TipusCadena) {
				
				String eti = gc.demanarEtiqueta();
				gc.gc(".data");
				gc.gcEtiqueta(eti + ": .asciiz " + sem.getValue("VALOR"));
				gc.gc(".text");
				
				gc.gc("li   $v0, 4");
				gc.gc("la   $a0, " + eti);
				gc.gc("syscall");
			}else if ((boolean)sem.getValue("ESTATIC") == true && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer")) {
				gc.gc("li   $v0, 1");
				gc.gc("li   $a0, " + sem.getValue("VALOR"));
				gc.gc("syscall");
			}else if ((boolean)sem.getValue("ESTATIC") == true && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")) {
				gc.gc("li   $v0, 4");
				String eti = gc.demanarEtiqueta();
				String eti2 = gc.demanarEtiqueta();
				int registre = gc.getRegistre();
				gc.gc("li   $" + gc.getNomRegistre(registre) + ", $" + sem.getValue("VALOR"));
				gc.gc("beqz   $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + eti);
				gc.gc("la   $a0, cert");
				gc.gc("b	" + eti2);
				gc.gcEtiqueta(eti + ":");
				gc.gc("la   $a0, fals");
				gc.gcEtiqueta(eti2 + ":");
				gc.gc("syscall");
				gc.freeRegistre(registre);
			} else if (sem.getValue("TIPUS") instanceof TipusSimple && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("sencer")){
				gc.gc("li   $v0, 1");
				gc.gc("move   $a0, $" + gc.getNomRegistre((int)(sem.getValue("REG"))));
				gc.gc("syscall");
			} else if (sem.getValue("TIPUS") instanceof TipusSimple && ((TipusSimple)sem.getValue("TIPUS")).getNom().equals("logic")){
				gc.gc("li   $v0, 4");
				String eti = gc.demanarEtiqueta();
				String eti2 = gc.demanarEtiqueta();
				gc.gc("beqz   $" + gc.getNomRegistre((int)sem.getValue("REG")) + ", " + eti);
				gc.gc("la   $a0, cert");
				gc.gc("b	" + eti2);
				gc.gcEtiqueta(eti + ":");
				gc.gc("la   $a0, fals");
				gc.gcEtiqueta(eti2 + ":");
				gc.gc("syscall");
			}
				
			if (!(sem.getValue("TIPUS") instanceof TipusCadena))gc.freeRegistre((int)sem.getValue("REG"));
			LL_EXP_ESCRIURE1();
			return;

		default: return;

		}
	}

	private void LL_EXP_ESCRIURE1 () {

		switch (lookAhead.getTipus()) {

		case "coma":
			Acceptar("coma");
			LL_EXP_ESCRIURE();
			return;

		default:
			return;

		}
	}

	private void SINO () {
		
		switch (lookAhead.getTipus()) {
		
		case "sino":
			Acceptar("sino");
			LL_INST();
			return;

		default: 
			return;

		}
	}
}
