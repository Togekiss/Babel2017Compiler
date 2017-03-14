package analitzadorSintactic;

import main.Error;
import main.Token;
import analitzadorLexicografic.Alex;

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
								FACTOR1();
								break;						
		case "parentesi_obert":	Acceptar("parentesi_obert");
								EXPRESSIO();
								Acceptar("parentesi_tancat"); // parentesi tancat
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void FACTOR1 () {
		switch (lookAhead.getTipus()) {
		case "parentesi_obert":	Acceptar("parentesi_obert");
								LL_EXPRESSIO();
								Acceptar("parentesi_tancat"); // parentesi tancat
								break;
		case "claudator_obert":	Acceptar("claudator_obert");
								EXPRESSIO();
								Acceptar("claudator_tancat"); // claudator tancat
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
								LL_EXPRESSIO1();
								break;
		case "resta":			EXPRESSIO();
								LL_EXPRESSIO1();
								break;			
		case "not":				EXPRESSIO();
								LL_EXPRESSIO1();
								break;
		/*						
		case "ct_enter":		return; //FIRST de FACTOR
		case "ct_logica":		return; //FIRST de FACTOR
		case "ct_cadena":		return; //FIRST de FACTOR
		case "identificador":	return; //FIRST de FACTOR					
		case "parentesi_obert":	return; //FIRST de FACTOR
		*/
		case "ct_enter":		EXPRESSIO();
								LL_EXPRESSIO1();
								break;
		case "ct_logica":		EXPRESSIO();
								LL_EXPRESSIO1();
								break;
		case "ct_cadena":		EXPRESSIO();
								LL_EXPRESSIO1();
								break;
		case "identificador":	EXPRESSIO();
								LL_EXPRESSIO1();
								break;					
		case "parentesi_obert":	EXPRESSIO();
								LL_EXPRESSIO1();
								break;						
		
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_EXPRESSIO1 () {
		switch (lookAhead.getTipus()) {
		case "coma":			Acceptar("coma");
								LL_EXPRESSIO();
								break;
		case "parentesi_tancat":return; //FOLLOW de LL_EXPRESSIO
		
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_VAR () {
		switch (lookAhead.getTipus()) {
		case "identificador":	VAR();
								LL_VAR1();
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_VAR1 () {
		switch (lookAhead.getTipus()) {
		case "coma":			Acceptar("coma");
								LL_VAR();
								break;
		case "parentesi_tancat":return; //FOLLOW de LL_EXPRESSIO
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void VAR () {
		switch (lookAhead.getTipus()) {
		case "identificador":	Acceptar("identificador");
								VAR1();
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void VAR1 () {
		switch (lookAhead.getTipus()) {
		case "claudator_obert":Acceptar("claudator_obert");
								EXPRESSIO();
								Acceptar("claudator_tancat");
								break;
								
								
		case "igual":			return; //FOLLOW de VAR
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_INST () {
		switch (lookAhead.getTipus()) {
		case "identificador":	INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "escriure":		INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "llegir":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "cicle":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "mentre":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "si":				INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "retornar":		INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "percada":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
								 
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_INST1 () {
		switch (lookAhead.getTipus()) {
		case "identificador":	INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "escriure":		INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "llegir":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "cicle":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "mentre":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "si":				INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "retornar":		INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
								break;
		case "percada":			INSTRUCCIO();
								Acceptar("punt_i_coma"); // ;
								LL_INST1();
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
								Acceptar("igual"); // =
								INSTRUCCIO1();
								break;
		case "escriure":		Acceptar("escriure"); // escriure
								Acceptar("parentesi_obert"); // (
								LL_EXP_ESCRIURE(); 	 
								Acceptar("parentesi_tancat"); // )
								break;		
		case "llegir":			Acceptar("llegir"); // llegir
								Acceptar("parentesi_obert"); // (
								LL_VAR(); 	 
								Acceptar("parentesi_tancat"); // )
								break;		
		case "cicle":			Acceptar("cicle"); // cicle
								LL_INST();
								Acceptar("fins"); // fins 	 
								EXPRESSIO();
								break;	
		case "mentre":			Acceptar("mentre"); // mentre
								EXPRESSIO();
								Acceptar("fer"); // fer	 
								LL_INST();
								Acceptar("fimentre"); // fimentre	 
								break;	
		case "si":				Acceptar("si"); // si
								EXPRESSIO();
								Acceptar("llavors"); // llavors	 
								LL_INST();
								SINO();
								Acceptar("fisi"); // fisi	 
								break;		
		case "retornar":		Acceptar("retornar"); // retornar
								EXPRESSIO(); 
								break;		
		case "percada":				Acceptar("percada"); // percada
								Acceptar("identificador"); // id
								Acceptar("en"); // en
								Acceptar("identificador"); // id
								Acceptar("fer"); // fer	
								LL_INST();
								Acceptar("fiper"); // fiper	 
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
								EXPRESSIO();
								Acceptar("parentesi_tancat"); // )
								Acceptar("interrogant"); // ?
								EXPRESSIO();
								Acceptar("dos_punts"); // :
								EXPRESSIO();
								break;						
						
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_EXP_ESCRIURE () {
		switch (lookAhead.getTipus()) {
		case "suma":			EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;
		case "resta":			EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;			
		case "not":				EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;
		case "ct_enter":		EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;
		case "ct_logica":		EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;
		case "ct_cadena":		EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;
		case "identificador":	EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;		
		case "parentesi_obert":	EXPRESSIO();
								LL_EXP_ESCRIURE1();
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_EXP_ESCRIURE1 () {
		switch (lookAhead.getTipus()) {
		case "coma":			Acceptar("coma");
								EXPRESSIO();
								break;
						
		case "parentesi_tancat":return; //FOLLOW de LL_EXP_ESCRIURE
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void SINO () {
		switch (lookAhead.getTipus()) {
		case "sino":			Acceptar("sino");
								LL_INST();
								break;
		case "fisi":			return; //FOLLOW de SINO					
		default:				System.out.println("Error");
								break;
								
		}
	}
}
