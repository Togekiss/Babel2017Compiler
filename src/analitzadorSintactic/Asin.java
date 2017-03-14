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
	
	private void Acceptar (Token token) {
		if (token == lookAhead) {
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
		case "suma":	Acceptar(lookAhead);
						break;
		case "resta":	Acceptar(lookAhead);
						break;
		case "or":		Acceptar(lookAhead);
						break;	
		default:		System.out.println("Error");
						break;
		}
	}
	
	private void OP_TERME () {
		switch (lookAhead.getTipus()) {
		case "multiplicacio":	Acceptar(lookAhead);
								break;
		case "divisio":			Acceptar(lookAhead);
								break;
		case "and":				Acceptar(lookAhead);
								break;	
		default:				System.out.println("Error");
								break;
		}
	}
	
	private void FACTOR () {
		switch (lookAhead.getTipus()) {
		case "ct_enter":		Acceptar(lookAhead);
								break;
		case "ct_logica":		Acceptar(lookAhead);
								break;
		case "ct_cadena":		Acceptar(lookAhead);
								break;
		case "identificador":	Acceptar(lookAhead);
								FACTOR1();
								break;						
		case "parentesi_obert":	Acceptar(lookAhead);
								EXPRESSIO();
								Acceptar(lookAhead); // parentesi tancat
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void FACTOR1 () {
		switch (lookAhead.getTipus()) {
		case "parentesi_obert":	Acceptar(lookAhead);
								LL_EXPRESSIO();
								Acceptar(lookAhead); // parentesi tancat
								break;
		case "claudator_obert":	Acceptar(lookAhead);
								EXPRESSIO();
								Acceptar(lookAhead); // claudator tancat
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
		case "coma":			Acceptar(lookAhead);
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
		case "coma":			Acceptar(lookAhead);
								LL_VAR();
								break;
		case "parentesi_tancat":return; //FOLLOW de LL_EXPRESSIO
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void VAR () {
		switch (lookAhead.getTipus()) {
		case "identificador":	Acceptar(lookAhead);
								VAR1();
								break;
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void VAR1 () {
		switch (lookAhead.getTipus()) {
		case "claudator_obert":Acceptar(lookAhead);
								EXPRESSIO();
								Acceptar(lookAhead);
								break;
								
								
		case "igual":			return; //FOLLOW de VAR
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_INST () {
		switch (lookAhead.getTipus()) {
		case "identificador":	INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "escriure":		INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "llegir":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "cicle":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "mentre":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "si":				INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "retornar":		INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "percada":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
								 
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void LL_INST1 () {
		switch (lookAhead.getTipus()) {
		case "identificador":	INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "escriure":		INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "llegir":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "cicle":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "mentre":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "si":				INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "retornar":		INSTRUCCIO();
								Acceptar(lookAhead); // ;
								LL_INST1();
								break;
		case "percada":			INSTRUCCIO();
								Acceptar(lookAhead); // ;
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
								Acceptar(lookAhead); // =
								INSTRUCCIO1();
								break;
		case "escriure":		Acceptar(lookAhead); // escriure
								Acceptar(lookAhead); // (
								LL_EXP_ESCRIURE(); 	 
								Acceptar(lookAhead); // )
								break;		
		case "llegir":			Acceptar(lookAhead); // llegir
								Acceptar(lookAhead); // (
								LL_VAR(); 	 
								Acceptar(lookAhead); // )
								break;		
		case "cicle":			Acceptar(lookAhead); // cicle
								LL_INST();
								Acceptar(lookAhead); // fins 	 
								EXPRESSIO();
								break;	
		case "mentre":			Acceptar(lookAhead); // mentre
								EXPRESSIO();
								Acceptar(lookAhead); // fer	 
								LL_INST();
								Acceptar(lookAhead); // fimentre	 
								break;	
		case "si":				Acceptar(lookAhead); // si
								EXPRESSIO();
								Acceptar(lookAhead); // llavors	 
								LL_INST();
								SINO();
								Acceptar(lookAhead); // fisi	 
								break;		
		case "retornar":		Acceptar(lookAhead); // retornar
								EXPRESSIO(); 
								break;		
		case "percada":				Acceptar(lookAhead); // percada
								Acceptar(lookAhead); // id
								Acceptar(lookAhead); // en
								Acceptar(lookAhead); // id
								Acceptar(lookAhead); // fer	
								LL_INST();
								Acceptar(lookAhead); // fiper	 
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
		
		case "parentesi_obert": Acceptar(lookAhead); // (
								EXPRESSIO();
								Acceptar(lookAhead); // )
								Acceptar(lookAhead); // ?
								EXPRESSIO();
								Acceptar(lookAhead); // :
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
		case "coma":			Acceptar(lookAhead);
								EXPRESSIO();
								break;
						
		case "parentesi_tancat":return; //FOLLOW de LL_EXP_ESCRIURE
		default:				System.out.println("Error");
								break;
								
		}
	}
	
	private void SINO () {
		switch (lookAhead.getTipus()) {
		case "sino":			Acceptar(lookAhead);
								LL_INST();
								break;
		case "fisi":			return; //FOLLOW de SINO					
		default:				System.out.println("Error");
								break;
								
		}
	}
}
