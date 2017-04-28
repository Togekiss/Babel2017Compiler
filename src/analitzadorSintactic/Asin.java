package analitzadorSintactic;

import main.Error;
import main.Token;
import taulasimbols.Bloc;
import taulasimbols.ITipus;
import taulasimbols.TaulaSimbols;
import taulasimbols.TipusArray;
import taulasimbols.TipusCadena;
import taulasimbols.TipusIndefinit;
import taulasimbols.TipusSimple;

import java.util.ArrayList;
import java.util.Arrays;

import analitzadorLexicografic.Alex;
import analitzadorSemantic.Asem;
import analitzadorSemantic.Semantic;

public class Asin {
	
	private Alex alex;
	private Asem asem;
	private Error error;
	private Token lookAhead;
	private TaulaSimbols taulaSimbols;
	//private Semantic semantic;
	
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
	private void consumir (ArrayList<String> l) {
		
		while (!l.contains(lookAhead.getTipus())) {
			lookAhead = alex.getToken();
		}
	}
	
	
	
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
		Semantic semantic = new Semantic();
		
		Acceptar("const");
		if (lookAhead.getTipus().equals("identificador"))
			semantic.setValue("TOKEN", lookAhead.getLexema());
		Acceptar("identificador");
		Acceptar("igual");
		semantic = EXPRESIO(semantic);
semantic.setValue("TIPUS", new TipusSimple("undefined", 0, 0, 0));
semantic.setValue("VALOR", "null");
semantic.setValue("ESTATIC", true);
		asem.afegirConstant(semantic, taulaSimbols);
		Acceptar("punt_i_coma");
		return;
		
	}


	private void DECL_VAR() {
		Semantic semantic = new Semantic();
		
		Acceptar("var");
		if (lookAhead.getTipus().equals("identificador"))
			semantic.setValue("TOKEN", lookAhead.getLexema());
		Acceptar("identificador");
		Acceptar("dos_punts");
		semantic = TIPUS(semantic);
		asem.afegirVariable(semantic, taulaSimbols);
		Acceptar("punt_i_coma");
		return;

	}


	private void DECL_FUNC() {

		switch (lookAhead.getTipus()) {
	
			case "funcio":
			Acceptar("funcio"); //no hauria de treure error
			Acceptar("identificador");
			Acceptar("parentesi_obert");
			LL_PARAM();
			Acceptar("parentesi_tancat");
			Acceptar("dos_punts");
			Acceptar("tipus_simple");
			Acceptar("punt_i_coma");
				
				DECL_CONST_VAR();
				
			Acceptar("func");
				LL_INST();
			Acceptar("fifunc");
			Acceptar("punt_i_coma");
				DECL_FUNC();
				return;
			
			default:
				return;

		}

	}
	
	
	private void LL_PARAM() {
		
		switch (lookAhead.getTipus()) {
		
			case "perref":
			case "perval":
				LL_PARAM1();
				return;
			
			default:
				return;
		
		}
		
	}
	
	
	private void LL_PARAM1() {
		
		Semantic semantic = new Semantic();
		
		PER(); //no hauria de treure error
		Acceptar("identificador");
		Acceptar("dos_punts");
		semantic = TIPUS(semantic);
		LL_PARAM11();
		return;
		
	}
	
	private void PER() {
		
		switch (lookAhead.getTipus()) {
		
			case "perref":
				Acceptar("perref");
				return;
				
			case "perval":
				Acceptar("perval");
				return;
				
			default: return;
		
		}
		
		
	}
	
	private void LL_PARAM11() {
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_PARAM1();
				return;
					
			default:
				return;
		
		}
		
	}
	
	private Semantic TIPUS(Semantic sem) {
		
		switch (lookAhead.getTipus()) {
			
			case "tipus_simple": 
				sem.setValue("TIPUS", new TipusSimple(lookAhead.getLexema(), 0, 0, 0));
				sem.setValue("ESTATIC", false);
				sem.setValue("VALOR", "null");
				Acceptar("tipus_simple");
				return sem;
				
			case "vector":
//TODO
				Acceptar("vector");
				sem.setValue("TIPUS", new TipusArray());
sem.setValue("ESTATIC", false);
sem.setValue("VALOR", "null");
				Acceptar("claudator_obert");
				sem = EXPRESIO(sem);
				Acceptar("rang");
				sem = EXPRESIO(sem);
				Acceptar("claudator_tancat");
				Acceptar("de");
				Acceptar("tipus_simple");
				return sem;
				
			default: return sem;
				
		}
	}
	
	
	private Semantic EXPRESIO(Semantic sem) {
		
//TODO COMENCA EXPRESSIO
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
//TODO operar sem i sem2 segons operador
				return sem;
				
			default: 
				return sem;
		
		}
	}
	
	
	private Semantic EXPRESIO_SIMPLE(Semantic sem) {
		
		sem = OP_INICI_EXP(sem); 
		sem  = TERME(sem);
//TODO operar op inici exp amb terme
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
//TODO operar sem amb sem2 segons operador i guardar a sem
				sem = EXPRESIO_SIMPLE1(sem);
				return sem;
				
			default:
				return sem;
		
		}
		
	}
	
	private Semantic TERME(Semantic sem) {
		
		sem = FACTOR(sem);
//TODO anem per aqui
		TERME1();
//operar factor i terme1
		return sem;
		
	}
	
	private void TERME1() {
		
		switch (lookAhead.getTipus()) {
		
			case "multiplicacio":
			case "divisio":
			case "and":
					OP_TERME(); //mai donara error
					FACTOR();
				TERME1();
				return;
				
			default: 
				return;
			
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
				sem.setValue("OPERADOR", lookAhead.getLexema());
				Acceptar("suma");
				return sem;
							
			case "resta":
				sem.setValue("OPERADOR", lookAhead.getLexema());
				Acceptar("resta");
				return sem;
							
			case "or":
				sem.setValue("OPERADOR", lookAhead.getLexema());
				Acceptar("or");
				return sem;
				
			default: return sem;
				
		}
	}
	
	private void OP_TERME () {
		
		switch (lookAhead.getTipus()) {
		
			case "multiplicacio":
				Acceptar("multiplicacio");
				return;
				
			case "divisio":
				Acceptar("divisio");
				return;
				
			case "and":
				Acceptar("and");
				return;	
				
			default: return;
								
		}
		
	}
	
	private Semantic FACTOR (Semantic sem) {
		
		switch (lookAhead.getTipus()) {
		
			case "ct_enter":
				sem.setValue("TIPUSS", new TipusSimple("sencer", 0, 0, 0));
				sem.setValue("VALORS", Integer.parseInt(lookAhead.getLexema()));
				sem.setValue("ESTATICS", true);
				Acceptar("ct_enter"); //no tirara error
				return sem;
				
			case "ct_logica":
				sem.setValue("TIPUSS", new TipusSimple("logic", 0, 0, 0));
				sem.setValue("VALORS", lookAhead.getLexema().equals("cert")?true:false);
				sem.setValue("ESTATICS", true);
				Acceptar("ct_logica"); //no tirara error
				return sem;
				
			case "ct_cadena":
				sem.setValue("TIPUSS", new TipusCadena("cadena", 0, lookAhead.getLexema().length()));
				sem.setValue("VALORS", lookAhead.getLexema());
				sem.setValue("ESTATICS", true);
				Acceptar("ct_cadena"); //no tirara error
				return sem;
				
			case "identificador":
//TODO buscar si id existeix i agafar tipus i valor i estatic
				Acceptar("identificador"); //no tirara errror
				sem = FACTOR1(sem);
				return sem;	
				
			case "parentesi_obert":
				Acceptar("parentesi_obert"); //no tirara errror
				sem = EXPRESIO(sem);
				Acceptar("parentesi_tancat"); //pot tirar error
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
	
	
	private void LL_EXPRESIO(Semantic sem) {
		
		//s'ha de comprovar que la expresio numero x correspongui
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
				//index param ++
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
			
			default: 
				return;
								
		}
	}
	
	private void LL_VAR () {
		
		VAR();
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
	
	private void VAR () {
		
		Acceptar("identificador");
		VAR1();
		return;
		
	}
	
	private void VAR1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "claudator_obert":
				Acceptar("claudator_obert");
				semantic = EXPRESIO(semantic);
				Acceptar("claudator_tancat");
				return;						
									
			default:
				return;
								
		}
	}
	
	private void LL_INST () {
		
		INSTRUCCIO(); //pot tirar error (switch)
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
			INSTRUCCIO(); //pot tirar error
			Acceptar("punt_i_coma"); //
				LL_INST1();	
				return;
				
			default:
				return;
								
		}
	}
	
	private void INSTRUCCIO () {
		
		switch (lookAhead.getTipus()) {
		
			case "identificador":
					VAR();//pot tirar error
					Acceptar("igual");
					INSTRUCCIO1();
				return;
				
			case "escriure":
				Acceptar("escriure"); // mai donara error
					Acceptar("parentesi_obert"); // (
					LL_EXP_ESCRIURE(); 	 
					Acceptar("parentesi_tancat"); // )
				return;		
				
			case "llegir":
				Acceptar("llegir"); // mai donara error
					Acceptar("parentesi_obert"); // (
					LL_VAR(); 	 
					Acceptar("parentesi_tancat"); // )

				return;
				
			case "cicle":
				Acceptar("cicle"); // mai donara error
				LL_INST();
					Acceptar("fins"); // fins 	 
				semantic = EXPRESIO(semantic);
				return;
				
			case "mentre":
				Acceptar("mentre"); // mai donara error
				semantic = EXPRESIO(semantic);
					Acceptar("fer");

				LL_INST();
					Acceptar("fimentre");	

				return;
				
			case "si":
				Acceptar("si"); // mai donara error
				semantic = EXPRESIO(semantic);
					Acceptar("llavors");

				LL_INST();
				SINO();
					Acceptar("fisi"); // fisi	 

				return;
				
			case "retornar":
				Acceptar("retornar"); // mai donara error
				semantic = EXPRESIO(semantic);
				return;
				
			case "percada":
				Acceptar("percada"); // mai donara error
					Acceptar("identificador"); // id
					Acceptar("en"); // en
					Acceptar("identificador"); // id
					Acceptar("fer");

				LL_INST();
				
					Acceptar("fiper"); // fiper	

				return;
									
			default: return;
								
		}
	}

	
	private void INSTRUCCIO1 () {
		
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
					Acceptar("parentesi_obert"); // (
					semantic = EXPRESIO(semantic);
					Acceptar("parentesi_tancat"); // )
					Acceptar("interrogant"); // ?
					semantic = EXPRESIO(semantic);
					Acceptar("dos_punts"); // :
					semantic = EXPRESIO(semantic);

				return;
							
			default: return;
									
		}
	}
	
	private void LL_EXP_ESCRIURE () {
		
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
				LL_EXP_ESCRIURE1();
				return;
				
			default: return;
									
		}
	}
	
	private void LL_EXP_ESCRIURE1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
					Acceptar("coma"); //mai tirara error
				semantic = EXPRESIO(semantic);
				return;
							
			default:
				return;
								
		}
	}
	
	private void SINO () {
		
		switch (lookAhead.getTipus()) {
		
			case "sino":
					Acceptar("sino"); //mai tirara error
				LL_INST();
				return;
				
			default:
				return;
								
		}
	}
}
