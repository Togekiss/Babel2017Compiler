package analitzadorSintactic;

import main.Error;
import main.Token;
import taulasimbols.Bloc;
import taulasimbols.Funcio;
import taulasimbols.ITipus;
import taulasimbols.Parametre;
import taulasimbols.TaulaSimbols;
import taulasimbols.TipusCadena;
import taulasimbols.TipusIndefinit;
import taulasimbols.TipusPasParametre;
import taulasimbols.TipusSimple;

import analitzadorLexicografic.Alex;
import analitzadorSemantic.Asem;
import analitzadorSemantic.Semantic;

public class Asin {

	private Alex alex;
	private Asem asem;
	private Error error;
	private Token lookAhead;
	private TaulaSimbols taulaSimbols;

	//CONSTRUCTOR
	public Asin (String args, String name) {

		alex = new Alex(args);
		asem = new Asem();
		error = new Error(name);
		taulaSimbols = new TaulaSimbols();
		lookAhead = alex.getToken();
		alex.writeToken(lookAhead);

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

		if (lookAhead.esEOF()) {
			error.tancaFitxer();
			alex.tancaFitxer();
			System.out.println("Taula de simbols:");
			System.out.println(taulaSimbols.toXml());
			return true;
		}
		else {
			Error.escriuError(26, "", alex.getLiniaActual(), "");
			error.tancaFitxer();
			alex.tancaFitxer();
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
		Acceptar("identificador");
		Acceptar("igual");
		sem = EXPRESIO(sem);
		//expresio ja porta token, tipus, valor, estatic
		//Comprovar que tipus i valor tenen sentit, i que es estatic
		System.out.println(sem.toString());
		asem.afegirConstant(sem, taulaSimbols, alex.getLiniaActual());
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
		asem.afegirVariable(sem, taulaSimbols, alex.getLiniaActual());
		Acceptar("punt_i_coma");
		return;

	}


	private void DECL_FUNC() {

		//creem descriptor per omplir llistat de parametres??
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
			funcio.setTipus(new TipusSimple(lookAhead.getLexema(), 0));
			Acceptar("tipus_simple");
					
			asem.afegirFuncio(funcio, taulaSimbols, alex.getLiniaActual());
			Acceptar("punt_i_coma");
						
			DECL_CONST_VAR();

			Acceptar("func");
			
			LL_INST();
			
			Acceptar("fifunc");
			Acceptar("punt_i_coma");
			
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

		param = PER(param); //no hauria de treure error
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
		sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
		
		switch (lookAhead.getTipus()) {

		case "tipus_simple": 
			sem.setValue("TIPUS", new TipusSimple(lookAhead.getLexema(), 0, 0, 0));
			sem.setValue("ESTATIC", false);
			sem.setValue("VALOR", "null");
			Acceptar("tipus_simple");
			return sem;

		case "vector":			
			Acceptar("vector");
			Acceptar("claudator_obert");
			sem2 = EXPRESIO(sem2);
			int dim1;
			//comprovem 1a dimensio del array
			if (((TipusSimple)sem2.getValue("TIPUS")).getNom().equals("sencer") &&
					((boolean)sem2.getValue("ESTATIC")) == true)
				dim1 = (int)sem2.getValue("VALOR");
			else {
				//TODO ERROR
				dim1 = Integer.MAX_VALUE;
			}

			Acceptar("rang");
			sem2 = EXPRESIO(sem2);
			int dim2;
			//comprovem 2a dimensio del array
			if (((TipusSimple)sem2.getValue("TIPUS")).getNom().equals("sencer") &&
					((boolean)sem2.getValue("ESTATIC")) == true)
				dim2 = (int)sem2.getValue("VALOR");
			else {
				//TODO ERROR
				dim2 = Integer.MIN_VALUE;
			}
			
			Acceptar("claudator_tancat");
			Acceptar("de");
			
			sem.setValue("TIPUS", asem.TIPUS_comprovaArray(dim1, dim2, lookAhead.getLexema()));
			Acceptar("tipus_simple");

			sem.setValue("ESTATIC", false);
			sem.setValue("VALOR", "null");
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
			sem = asem.EXPRESIO1_operar(sem, sem2);
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
		asem.EXPRESIO_SIMPLE_operar(sem);
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
			//operar sem amb sem2 segons operador i guardar a sem
			asem.EXPRESIO_SIMPLE1_operar(sem, sem2);
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
			sem = OP_TERME(sem); //mai donara error
			sem2 = FACTOR(sem2);
			//operar sem i sem2 segons OP_TERME
			asem.TERME_operar(sem, sem2);
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

		switch (lookAhead.getTipus()) {

		case "ct_enter":
			sem.setValue("TIPUS", new TipusSimple("sencer", 0, 0, 0));
			sem.setValue("VALOR", Integer.parseInt(lookAhead.getLexema()));
			sem.setValue("ESTATIC", true);
			Acceptar("ct_enter"); 
			return sem;

		case "ct_logica":
			sem.setValue("TIPUS", new TipusSimple("logic", 0, 0, 0));
			sem.setValue("VALOR", lookAhead.getLexema().equals("cert")?true:false);
			sem.setValue("ESTATIC", true);
			Acceptar("ct_logica"); 
			return sem;

		case "ct_cadena":
			sem.setValue("TIPUS", new TipusCadena("cadena", 0, lookAhead.getLexema().length()));
			sem.setValue("VALOR", lookAhead.getLexema());
			sem.setValue("ESTATIC", true);
			Acceptar("ct_cadena");
			return sem;

		case "identificador":
			//TODO buscar si id existeix i agafar tipus i valor i estatic
			Acceptar("identificador");
			sem.setValue("TIPUS", new TipusIndefinit("indefinit", 0));
			sem.setValue("VALOR", 0);
			sem.setValue("ESTATIC", false);
			sem = FACTOR1(sem);
			return sem;	

		case "parentesi_obert":
			Acceptar("parentesi_obert");
			sem = EXPRESIO(sem);
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
			//TODO es funcio
			//basicament per passar-li el descriptor de la funcio i index param = 0
			LL_EXPRESIO(sem2); 
			Acceptar("parentesi_tancat");
			//retornar tipus de retorn de funcio
			return sem;

		case "claudator_obert":
			Acceptar("claudator_obert"); 
			//TODO es vector
			sem2 = EXPRESIO(sem2);
			//comprovar que expressio es int i esta dins el rang (si es estatica)
			Acceptar("claudator_tancat"); 
			//retornar tipus d'element de vector
			return sem;

		default:
			return sem;

		}
	}


	private Semantic LL_EXPRESIO(Semantic sem) {

		//TODO s'ha de comprovar que la expresio numero x correspongui
		//amb el parametre numero x de la funcio
		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "suma":
		case "resta":
		case "not":
		case "ct_entera":
		case "ct_logica":
		case "ct_cadena":
		case "identificador":
		case "parentesi_obert":
			sem2 = EXPRESIO(sem2);
			//TODO index param ++
			LL_EXPRESIO1(sem);
			return sem;

		default: return sem;

		}

	}

	private void LL_EXPRESIO1 (Semantic sem) {

		switch (lookAhead.getTipus()) {

		case "coma":
			Acceptar("coma");
			LL_EXPRESIO(sem);
			return;

		default: 
			return;

		}
	}

	private void LL_VAR () {

		Semantic sem = new Semantic();

		sem = VAR(sem);
		//comprovar que sem tipus == tipus simple
		if (!(sem.getValue("TIPUS") instanceof TipusSimple)) {
			//TODO ERROR
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
		sem = asem.VAR_esVariable(sem, taulaSimbols);
		sem = VAR1(sem);
		return sem;

	}

	private Semantic VAR1 (Semantic sem) {

		Semantic sem2 = new Semantic();

		switch (lookAhead.getTipus()) {

		case "claudator_obert":
			//TODO comprovar que identificador es array
			Acceptar("claudator_obert");
			sem2 = EXPRESIO(sem2);
			//comprovar que tipus sem2 == sencer
			//i si es estatic, esta dins el rang de id
			sem = asem.VAR1_comprovaArray(sem, sem2, taulaSimbols);
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
			if (sem.getValue("TIPUS") != sem2.getValue("TIPUS")) {
				//TODO error
			}
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
			LL_INST();
			Acceptar("fins"); // fins 	 
			sem = EXPRESIO(sem);
			//comprovar que sem tipus == logic
			if (!asem.esLogic(sem)) {
				//TODO error
			}
			return;

		case "mentre":
			Acceptar("mentre");
			sem = EXPRESIO(sem);
			//comprovar que sem tipus == logic
			if (!asem.esLogic(sem)) {
				//TODO error
			}
			Acceptar("fer");
			LL_INST();
			Acceptar("fimentre");	

			return;

		case "si":
			Acceptar("si"); 
			sem = EXPRESIO(sem);
			//comprovar que sem tipus == logic
			if (!asem.esLogic(sem)) {
				//TODO error
			}
			Acceptar("llavors");
			LL_INST();
			SINO();
			Acceptar("fisi"); 

			return;

		case "retornar":
			Acceptar("retornar"); 
			sem = EXPRESIO(sem);
			//TODO comprovar que blocactual != 0
			//comprovar que exp tipus == retorn funcio tipus
			//comprovar que com a minim nhi hagi un
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


	private void INSTRUCCIO1 () {
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
	}

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
			sem = EXPRESIO(sem);
			//comprovar que tipus == tipus simple o cadena
			if (asem.LL_EXP_ESCRIURE_esValid(sem)) {
				//TODO error
			}
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
