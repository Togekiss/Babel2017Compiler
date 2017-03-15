package analitzadorSintactic;

import main.Error;
import main.Token;
import analitzadorLexicografic.Alex;
// Recuperació fins a \n
public class Asin {
	private Alex alex;
	private Error error;
	private Token lookAhead;
	
	public Asin (String args, String name) {
		alex = new Alex(args);
		error = new Error(name);
		lookAhead = alex.getToken();
		alex.writeToken(lookAhead);
	}
	
	/*public void start () {
		
		while (!lookAhead.esEOF()) {
			lookAhead = alex.getToken();
			alex.writeToken(lookAhead);
		}
		
		//System.out.println("Anàlisi lexicogràfic finalitzat.");
	}*/
	
	private void Acceptar (String token) {
		if (lookAhead.getTipus().equals(token)) {
			lookAhead = alex.getToken();
			alex.writeToken(lookAhead);
		} else {
			//TIRAR ERROR
		}
		if (lookAhead.esEOF()) {
			error.tancaFitxer();
			alex.tancaFitxer();
			//exit
		}
	}
	
	public void P() {

		DECL();
		Acceptar("prog");
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
	
			case "funcio": return;
	
			case "func": return;
	
			default: Error.escriuError(0, "", 0);
		}
	}


	private void DECL_CONST() {

		Acceptar("const");
		Acceptar("identificador");
		Acceptar("igual");
		EXPRESIO();
		Acceptar("punt_i_coma");
		return;

	}


	private void DECL_VAR() {

		Acceptar("var");
		Acceptar("identificador");
		Acceptar("dos_punts");
		TIPUS();
		Acceptar("punt_i_coma");
		return;

	}


	private void DECL_FUNC() {

		switch (lookAhead.getTipus()) {
	
			case "funcio": 
				Acceptar("funcio");
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
			
			case "prog": return;
			
			default: Error.escriuError(0, "", 0);

		}

	}
	
	
	private void LL_PARAM() {
		
		switch (lookAhead.getTipus()) {
		
			case "perref":
				LL_PARAM1();
				return;
				
			case "perval":
				LL_PARAM1();
				return;
				
			case ")": return;
			
			default: Error.escriuError(0, "", 0);
		
		}
		
	}
	
	
	private void LL_PARAM1() {
		
		switch (lookAhead.getTipus()) {
		
			case "perref":
				Acceptar("perref");
				Acceptar("identificador");
				Acceptar("dos_punts");
				TIPUS();
				LL_PARAM11();
				return;
				
			case "perval":
				Acceptar("perval");
				Acceptar("identificador");
				Acceptar("dos_punts");
				TIPUS();
				LL_PARAM11();
				return;
				
			default: Error.escriuError(0, "", 0);
		
		}
		
	}
	
	private void LL_PARAM11() {
		
		switch (lookAhead.getTipus()) {
		
			case ",":
				Acceptar("coma");
				LL_PARAM1();
				return;
					
			case ")": return;
			
			default: Error.escriuError(0, "", 0);
		
		}
		
	}
	
	private void TIPUS() {
		
		switch (lookAhead.getTipus()) {
		
		case "sencer": 
			Acceptar("tipus_simple");
			return;
		case "logic":
			Acceptar("tipus_simple");//
		
		
		}
		
	private void OP_EXP () {
		switch (lookAhead.getTipus()) {
		case "suma":	Acceptar("suma");
						break;
		case "resta":	Acceptar("resta");
						break;
		case "or":		Acceptar("or");
						break;	
		default:		System.out.println("Error");
						break;
		}
	}
	
	private void OP_TERME () {
		switch (lookAhead.getTipus()) {
		case "multiplicacio":	Acceptar("multiplicacio");
								break;
		case "divisio":			Acceptar("divisio");
								break;
		case "and":				Acceptar("and");
								break;	
		default:				System.out.println("Error");
								break;
		}
	}
	
	private void FACTOR () {
		switch (lookAhead.getTipus()) {
		case "ct_enter":		Acceptar("ct_enter");
								break;
		case "ct_logica":		Acceptar("ct_logica");
								break;
		case "ct_cadena":		Acceptar("ct_cadena");
								break;
		case "identificador":	Acceptar("identificador");
								try { FACTOR1(); } catch (SyntacticError e) { }
								break;						
		case "parentesi_obert":	Acceptar("parentesi_obert");
								try {
									EXPRESSIO();
									Acceptar("parentesi_tancat"); // parentesi tancat
								} catch (SyntacticError e) { }
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void FACTOR1 () {
		switch (lookAhead.getTipus()) {
		case "parentesi_obert":	Acceptar("parentesi_obert");
								try { 
									LL_EXPRESSIO();
									Acceptar("parentesi_tancat"); // parentesi tancat
								} catch (SyntacticError e) { }
								break;
		case "claudator_obert":	Acceptar("claudator_obert");
								try {
									EXPRESSIO();
									Acceptar("claudator_tancat"); // claudator tancat
								} catch (SyntacticError e) { }
		case "multiplicacio":	return;
		case "divisio":			return;
		case "and":				return;		
		case "suma":			return;	
		case "resta":			return;
		case "or":				return;		
		case "punt_i_coma":		return;	
		case "claudator_tancat":return;
		case "parentesi_tancat":return;
		case "rang":			return;
		case "dos_punts":		return;
		case "fer":				return;
		case "llavors":			return;
		case "oper_rel":		return;
		default:				System.out.println("Error");
								break;
								
		}
	}
	//REVISAR
	private void LL_EXPRESSIO () {
		switch (lookAhead.getTipus()) {
		case "suma":			EXPRESSIO();
								try { LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;
		case "resta":			EXPRESSIO();
								try { LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;			
		case "not":				EXPRESSIO();
								try { LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;
		/*						
		case "ct_enter":		return; //FIRST de FACTOR
		case "ct_logica":		return; //FIRST de FACTOR
		case "ct_cadena":		return; //FIRST de FACTOR
		case "identificador":	return; //FIRST de FACTOR					
		case "parentesi_obert":	return; //FIRST de FACTOR
		*/
		case "ct_enter":		EXPRESSIO();
								try { LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;
		case "ct_logica":		EXPRESSIO();
								try { LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;
		case "ct_cadena":		EXPRESSIO();
								try{ LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;
		case "identificador":	EXPRESSIO();
								try { LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;					
		case "parentesi_obert":	EXPRESSIO();
								try { LL_EXPRESSIO1(); } catch (SyntacticError e) { }
								break;						
		
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_EXPRESSIO1 () {
		switch (lookAhead.getTipus()) {
		case "coma":			Acceptar("coma");
								try { LL_EXPRESSIO(); } catch (SyntacticError e) { }
								break;
		case "parentesi_tancat":return; //FOLLOW de LL_EXPRESSIO
		
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_VAR () {
		switch (lookAhead.getTipus()) {
		case "identificador":	VAR();
								try { LL_VAR1(); } catch (SyntacticError e) { }
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_VAR1 () {
		switch (lookAhead.getTipus()) {
		case "coma":			Acceptar("coma");
								try { LL_VAR(); } catch (SyntacticError e) { }
								break;
		case "parentesi_tancat":return; //FOLLOW de LL_EXPRESSIO
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void VAR () {
		switch (lookAhead.getTipus()) {
		case "identificador":	Acceptar("identificador");
								try { VAR1(); } catch (SyntacticError e) { }
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void VAR1 () {
		switch (lookAhead.getTipus()) {
		case "claudator_obert": Acceptar("claudator_obert");
								try {
									EXPRESSIO();
									Acceptar("claudator_tancat");
								} catch (SyntacticError e) { }	
								break;
								
								
		case "igual":			return; //FOLLOW de VAR
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_INST () {
		switch (lookAhead.getTipus()) {
		case "identificador":	INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "escriure":		INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "llegir":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "cicle":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "mentre":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "si":				INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "retornar":		INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "percada":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
								 
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_INST1 () {
		switch (lookAhead.getTipus()) {
		case "identificador":	INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "escriure":		INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "llegir":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "cicle":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "mentre":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "si":				INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "retornar":		INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "percada":			INSTRUCCIO();
								try {
									Acceptar("punt_i_coma"); // ;
									LL_INST1();
								} catch (SyntacticError e) { }	
								break;
		case "fiprog":		    return;		
		case "fifunc":			return;	
		case "fins":			return;
		case "fimentre":		return;	
		case "sino":			return;	
		case "fisi":			return;	
		case "fiper":			return;	
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void INSTRUCCIO () {
		switch (lookAhead.getTipus()) {
		case "identificador":	VAR();
								try {
									Acceptar("igual"); // =
									INSTRUCCIO1();
								} catch (SyntacticError e) { }	
								break;
		case "escriure":		Acceptar("escriure"); // escriure
								try {
									Acceptar("parentesi_obert"); // (
									LL_EXP_ESCRIURE(); 	 
									Acceptar("parentesi_tancat"); // )
								} catch (SyntacticError e) { }
								break;		
		case "llegir":			Acceptar("llegir"); // llegir
								try {
									Acceptar("parentesi_obert"); // (
									LL_VAR(); 	 
									Acceptar("parentesi_tancat"); // )
								} catch (SyntacticError e) { }	
								break;		
		case "cicle":			Acceptar("cicle"); // cicle
								try {
									LL_INST();
									Acceptar("fins"); // fins 	 
									EXPRESSIO();
								} catch (SyntacticError e) { }
								break;	
		case "mentre":			Acceptar("mentre"); // mentre
								try {	
									EXPRESSIO();
									Acceptar("fer"); // fer	 
									LL_INST();
									Acceptar("fimentre"); // fimentre	
								} catch (SyntacticError e) { }	
								break;	
		case "si":				Acceptar("si"); // si
								try {	
									EXPRESSIO();
									Acceptar("llavors"); // llavors	 
									LL_INST();
									SINO();
									Acceptar("fisi"); // fisi	 
								} catch (SyntacticError e) { }	
								break;		
		case "retornar":		Acceptar("retornar"); // retornar
								try { EXPRESSIO(); } catch (SyntacticError e) { }
								break;		
		case "percada":			Acceptar("percada"); // percada
								try {
									Acceptar("identificador"); // id
									Acceptar("en"); // en
									Acceptar("identificador"); // id
									Acceptar("fer"); // fer	
									LL_INST();
									Acceptar("fiper"); // fiper	 
								} catch (SyntacticError e) { }	
								break;							
								
		default:				System.out.println("Error");
								break;
								
		}
	}
	// REVISAR
	private void INSTRUCCIO1 () {
		switch (lookAhead.getTipus()) {
		
		case "suma":			EXPRESSIO();
								break;
		case "resta":			EXPRESSIO();
								break;			
		case "not":				EXPRESSIO();
								break;
		case "ct_enter":		EXPRESSIO();
								break;
		case "ct_logica":		EXPRESSIO();
								break;
		case "ct_cadena":		EXPRESSIO();
								break;
		case "identificador":	EXPRESSIO();
								break;		
		
		//DOBLE CAS
		case "parentesi_obert":	EXPRESSIO();
		break;
		
		case "parentesi_obert": Acceptar("parentesi_obert"); // (
								try {
									EXPRESSIO();
									Acceptar("parentesi_tancat"); // )
									Acceptar("interrogant"); // ?
									EXPRESSIO();
									Acceptar("dos_punts"); // :
									EXPRESSIO();
								} catch (SyntacticError e) { }	
								break;						
						
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_EXP_ESCRIURE () {
		switch (lookAhead.getTipus()) {
		case "suma":			EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;
		case "resta":			EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;			
		case "not":				EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;
		case "ct_enter":		EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;
		case "ct_logica":		EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;
		case "ct_cadena":		EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;
		case "identificador":	EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;		
		case "parentesi_obert":	EXPRESSIO();
								try { LL_EXP_ESCRIURE1(); } catch (SyntacticError e) { }
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_EXP_ESCRIURE1 () {
		switch (lookAhead.getTipus()) {
		case "coma":			Acceptar("coma");
								try { EXPRESSIO(); } catch (SyntacticError e) { }
								break;
						
		case "parentesi_tancat":return; //FOLLOW de LL_EXP_ESCRIURE
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void SINO () {
		switch (lookAhead.getTipus()) {
		case "sino":			Acceptar("sino");
								try { LL_INST(); } catch (SyntacticError e) { }
								break;
		case "fisi":			return; //FOLLOW de SINO					
		default:				System.out.println("Error");
								break;
								
		}
	}
}
