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
	

	
	private void Acceptar (String token) throws SyntacticError {
		
		if (lookAhead.getTipus().equals(token)) {
			
			lookAhead = alex.getToken();
			alex.writeToken(lookAhead);
			
		} else 
			throw new SyntacticError("ACCEPT ERROR: EXPECTED " + token + " BUT RECEIVED " + lookAhead.getTipus());
			
		if (lookAhead.esEOF()) {
			
			error.tancaFitxer();
			alex.tancaFitxer();
			//exit
		}
	}
	
	public void P() {

		DECL();
		try {
			Acceptar("prog");
		} catch (SyntacticError e) { }
		LL_INST();
		try {
			Acceptar("fiprog");
		} catch (SyntacticError e) { }
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
	
			default: return;
		}
	}


	private void DECL_CONST() {
		
		
		try {
			Acceptar("const");
			Acceptar("identificador");
			Acceptar("igual");
			EXPRESIO();
			Acceptar("punt_i_coma");
			
		} catch (SyntacticError e) { }
		return;
		
	}


	private void DECL_VAR() {

		try {
			Acceptar("var");
			Acceptar("identificador");
			Acceptar("dos_punts");
			TIPUS();
			Acceptar("punt_i_coma");
			
		} catch (SyntacticError e) { }
		return;

	}


	private void DECL_FUNC() {

		switch (lookAhead.getTipus()) {
	
			case "funcio":
				try {
					Acceptar("funcio");
					Acceptar("identificador");
					Acceptar("parentesi_obert");
					LL_PARAM();
					Acceptar("parentesi_tancat");
					Acceptar("dos_punts");
					Acceptar("tipus_simple");
					Acceptar("punt_i_coma");
				} catch (SyntacticError e) { }
				DECL_CONST_VAR();
				try {
					Acceptar("func");
				} catch (SyntacticError e) { }
				LL_INST();
				try {
					Acceptar("fifunc");
					Acceptar("punt_i_coma");
				} catch (SyntacticError e) { }
				DECL_FUNC();
				return;
			
			default: return;

		}

	}
	
	
	private void LL_PARAM() {
		
		switch (lookAhead.getTipus()) {
		
			case "perref":
			case "perval":
			try {
				LL_PARAM1();
			} catch (SyntacticError e) { }
				return;
			
			default: return;
		
		}
		
	}
	
	
	private void LL_PARAM1() throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "perref":
				try {
					Acceptar("perref");
					Acceptar("identificador");
					Acceptar("dos_punts");
				} catch (SyntacticError e) { }
				
				TIPUS();
				LL_PARAM11();
				return;
				
			case "perval":
				try {
					Acceptar("perval");
					Acceptar("identificador");
					Acceptar("dos_punts");
				} catch (SyntacticError e) { }
				
				TIPUS();
				LL_PARAM11();
				return;
				
			default: throw new SyntacticError("SYNTAX ERROR: EXPECTED perref OR perval BUT RECEIVED " + lookAhead.getTipus());
		
		}
		
	}
	
	private void LL_PARAM11() {
		
		switch (lookAhead.getTipus()) {
		
			case ",":
				try {
					Acceptar("coma");
				} catch (SyntacticError e) { }
				try {
					LL_PARAM1();
				} catch (SyntacticError e) { }
				return;
					
			
			default: return;
		
		}
		
	}
	
	private void TIPUS() throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
			
			case "tipus_simple": 
				try {
					Acceptar("tipus_simple");
				} catch (SyntacticError e) { }
				return;
				
			case "vector":
				try {
					Acceptar("vector");
					Acceptar("claudator_obert");
					EXPRESIO();
					Acceptar("rang");
					EXPRESIO();
					Acceptar("claudator_tancat");
					Acceptar("de");
					Acceptar("tipus_simple");
				} catch (SyntacticError e) { }//
				
				return;
				
			default: throw new SyntacticError("SYNTAX ERROR: EXPECTED tipus_simple OR vector BUT RECEIVED " + lookAhead.getTipus());
				
		}
	}
	
	
	private void EXPRESIO() {
		
		EXPRESIO_SIMPLE();
		EXPRESIO1();
		return;
		
	}
	
	
	private void EXPRESIO1() {
		
		switch (lookAhead.getTipus()) {
		
			case "op_rel": 
				Acceptar("op_rel");
				EXPRESIO_SIMPLE();
				return;
				
			default: return;
		
		}
	}
	
	
	private void EXPRESIO_SIMPLE() {
		
		OP_INICI_EXP();
		TERME();
		EXPRESIO_SIMPLE1();
		return;
		
	}
	
	
	private void EXPRESIO_SIMPLE1() {
		
		switch (lookAhead.getTipus()) {
		
			case "suma":
			case "resta":
			case "or":
				OP_EXP();
				TERME();
				EXPRESIO_SIMPLE1();
				return;
				
			default: return;
		
		}
		
	}
	
	private void TERME() {
		
		FACTOR();
		TERME1();
		return;
		
	}
	
	private void TERME1() {
		
		switch (lookAhead.getTipus()) {
		
			case "multiplicacio":
			case "divisio":
			case "and":
				OP_TERME();
				FACTOR();
				TERME1();
				return;
				
			default: return;
			
		}
		
	}
	
	
	private void OP_INICI_EXP() {
		
		switch(lookAhead.getTipus()) {
		
			case "suma":
				Acceptar("suma");
				return;
				
			case "resta":
				Acceptar("resta");
				return;
				
			case "not":
				Acceptar("not");
				return;
				
			default: return;
		
		}
		
		
	}
		
	private void OP_EXP () throws SyntacticError {
		switch (lookAhead.getTipus()) {
		
			case "suma":
				Acceptar("suma");
				return;
							
			case "resta":
				Acceptar("resta");
				return;
							
			case "or":
				Acceptar("or");
				return;
				
			default: throw new SyntacticError("SYNTAX ERROR: EXPECTED suma OR resta OR or BUT RECEIVED " + lookAhead.getTipus());
				
		}
	}
	
	private void OP_TERME () throws SyntacticError {
		
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
				
			default: throw new SyntacticError("SYNTAX ERROR: EXPECTED multiplicacio OR divisio OR and BUT RECEIVED " + lookAhead.getTipus());
								
		}
		
	}
	
	private void FACTOR () throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "ct_enter":
				Acceptar("ct_enter");
				return;
				
			case "ct_logica":
				Acceptar("ct_logica");
				return;
				
			case "ct_cadena":
				Acceptar("ct_cadena");
				return;
				
			case "identificador":
				Acceptar("identificador");
				FACTOR1();
				return;	
				
			case "parentesi_obert":
				Acceptar("parentesi_obert");
				try {
					EXPRESIO();
					Acceptar("parentesi_tancat"); // parentesi tancat
				} catch (SyntacticError e) { }
				return;
				
			default: throw new SyntacticError("SYNTAX ERROR: EXPECTED ct OR identificador OR parentesi_obert BUT RECEIVED " + lookAhead.getTipus());
								
		}
	}
	
	private void FACTOR1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "parentesi_obert":
				Acceptar("parentesi_obert");
				try { 
					LL_EXPRESIO();
					Acceptar("parentesi_tancat"); // parentesi tancat
				} catch (SyntacticError e) { }
				return;
				
			case "claudator_obert":
				Acceptar("claudator_obert");
				try {
					EXPRESIO();
					Acceptar("claudator_tancat"); // claudator tancat
				} catch (SyntacticError e) { }
				return;
			
			default: return;
								
		}
	}
	
	
	private void LL_EXPRESIO () {
		
		EXPRESIO();
		LL_EXPRESIO1();
		
	}
	
	private void LL_EXPRESIO1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				try {
					LL_EXPRESIO();
				} catch (SyntacticError e) { }
				return;
			
			default: return;
								
		}
	}
	
	private void LL_VAR () {
		
		VAR();
		LL_VAR1();
		
	}
	
	private void LL_VAR1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				try {
					LL_VAR();
				} catch (SyntacticError e) { }
				return;
			
			default: return;
								
		}
	}
	
	private void VAR () {
		
		Acceptar("identificador");
		VAR1();
		
	}
	
	private void VAR1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "claudator_obert":
				Acceptar("claudator_obert");
				try {
					EXPRESIO();
					Acceptar("claudator_tancat");
				} catch (SyntacticError e) { }	
				return;						
									
			default: return;
								
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
				try {
					Acceptar("punt_i_coma"); // ;
					LL_INST1();
				} catch (SyntacticError e) { }	
				return;
				
			default: return;
								
		}
	}
	
	private void INSTRUCCIO () throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "identificador":
				VAR();
				try {
					Acceptar("igual"); // =
					INSTRUCCIO1();
				} catch (SyntacticError e) { }	
				return;
				
			case "escriure":
				Acceptar("escriure"); // escriure
				try {
					Acceptar("parentesi_obert"); // (
					LL_EXP_ESCRIURE(); 	 
					Acceptar("parentesi_tancat"); // )
				} catch (SyntacticError e) { }
				return;		
				
			case "llegir":
				Acceptar("llegir"); // llegir
				try {
					Acceptar("parentesi_obert"); // (
					LL_VAR(); 	 
					Acceptar("parentesi_tancat"); // )
				} catch (SyntacticError e) { }	
				return;
				
			case "cicle":
				Acceptar("cicle"); // cicle
				try {
					LL_INST();
					Acceptar("fins"); // fins 	 
					EXPRESIO();
				} catch (SyntacticError e) { }
				return;
				
			case "mentre":
				Acceptar("mentre"); // mentre
				try {	
					EXPRESIO();
					Acceptar("fer"); // fer	 
					LL_INST();
					Acceptar("fimentre"); // fimentre	
				} catch (SyntacticError e) { }	
				return;
				
			case "si":
				Acceptar("si"); // si
				try {	
					EXPRESIO();
					Acceptar("llavors"); // llavors	 
					LL_INST();
					SINO();
					Acceptar("fisi"); // fisi	 
				} catch (SyntacticError e) { }	
				return;
				
			case "retornar":
				Acceptar("retornar"); // retornar
				try {
					EXPRESIO();
				} catch (SyntacticError e) { }
				return;
				
			case "percada":
				Acceptar("percada"); // percada
				try {
					Acceptar("identificador"); // id
					Acceptar("en"); // en
					Acceptar("identificador"); // id
					Acceptar("fer"); // fer	
					LL_INST();
					Acceptar("fiper"); // fiper	 
				} catch (SyntacticError e) { }	
				return;
									
			default: throw new SyntacticError("SYNTAX ERROR: UNEXPECTED " + lookAhead.getTipus());
								
		}
	}

	
	private void INSTRUCCIO1 () throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "suma":			
			case "resta":						
			case "not":				
			case "ct_enter":		
			case "ct_logica":		
			case "ct_cadena":		
			case "identificador":			
			case "parentesi_obert":	EXPRESIO();
		
			
			case "si":
				Acceptar("si");
				Acceptar("parentesi_obert"); // (
				try {
					EXPRESIO();
					Acceptar("parentesi_tancat"); // )
					Acceptar("interrogant"); // ?
					EXPRESIO();
					Acceptar("dos_punts"); // :
					EXPRESIO();
				} catch (SyntacticError e) { }	
				return;
							
			default: throw new SyntacticError("SYNTAX ERROR: UNEXPECTED " + lookAhead.getTipus());
									
		}
	}
	
	private void LL_EXP_ESCRIURE () throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "suma":			
			case "resta":						
			case "not":				
			case "ct_enter":		
			case "ct_logica":		
			case "ct_cadena":		
			case "identificador":		
			case "parentesi_obert":
				EXPRESIO();
				try {
					LL_EXP_ESCRIURE1();
				} catch (SyntacticError e) { }
				return;
				
			default: throw new SyntacticError("SYNTAX ERROR: UNEXPECTED " + lookAhead.getTipus());
									
		}
	}
	
	private void LL_EXP_ESCRIURE1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				try {
					EXPRESIO();
				} catch (SyntacticError e) { }
				return;
							
			default: return;
								
		}
	}
	
	private void SINO () {
		
		switch (lookAhead.getTipus()) {
		
			case "sino":
				try {
					Acceptar("sino");
				} catch (SyntacticError e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				LL_INST();
				return;
				
			default: return;
								
		}
	}
}
