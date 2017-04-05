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
	
	public boolean fiOk() {
		if (lookAhead.esEOF()) return true;
		else return false;
	}
	
	
	private void Acceptar (String token) throws SyntacticError {
		
		if (lookAhead.getTipus().equals(token)) {
			
			System.out.println(lookAhead.getLexema() + " ACCEPTAT");
			lookAhead = alex.getToken();
			alex.writeToken(lookAhead);
			
		} else 
			throw new SyntacticError(lookAhead.getLexema());
			
	}
	
	public boolean P() {
		

		System.out.println("\tDins P");
		DECL();
		try {
			Acceptar("prog");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
		}
		LL_INST();
		try {
			Acceptar("fiprog");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
		}
		
		if (lookAhead.esEOF()) {
			error.tancaFitxer();
			alex.tancaFitxer();
			return true;
		}
		else {
			Error.escriuError(26, "", alex.getLiniaActual(), "");
			error.tancaFitxer();
			alex.tancaFitxer();
			return false;
		}
		
		

	}


	private void DECL() {

		System.out.println("\tDins DECL");
		DECL_CONST_VAR();
		DECL_FUNC();
		return;

	}


	private void DECL_CONST_VAR() {

		System.out.println("\tDins CONST_VAR");
		switch (lookAhead.getTipus()) {

			case "const":
				try {
					DECL_CONST();
				} catch (SyntacticError e) {
					Error.escriuError(23, "", alex.getLiniaActual(), "");
				}
				DECL_CONST_VAR();
				return;
	
			case "var":
				DECL_VAR();
				DECL_CONST_VAR();
				return;
	
			default: return;
		}
	}


	private void DECL_CONST() throws SyntacticError{
		
		System.out.println("\tDins DECL_CONST");
		Acceptar("const");
		Acceptar("identificador");
		Acceptar("igual");
		EXPRESIO();
		Acceptar("punt_i_coma");
		return;
		
	}


	private void DECL_VAR() {

		System.out.println("\tDins DECL_VAR");
		try {
			Acceptar("var");
			Acceptar("identificador");
			Acceptar("dos_punts");
			TIPUS();
			Acceptar("punt_i_coma");
			
		} catch (SyntacticError e) {
			Error.escriuError(24, "", alex.getLiniaActual(), "");
		}
		return;

	}


	private void DECL_FUNC() {

		System.out.println("\tDins DECL_FUNC");
		switch (lookAhead.getTipus()) {
	
			case "funcio":
				try {
					Acceptar("funcio"); //no hauria de treure error
					Acceptar("identificador");
					Acceptar("parentesi_obert");
					LL_PARAM();
					Acceptar("parentesi_tancat");
					Acceptar("dos_punts");
					Acceptar("tipus_simple");
					Acceptar("punt_i_coma");
				} catch (SyntacticError e) {
					Error.escriuError(25, "", alex.getLiniaActual(), "");
				}
				DECL_CONST_VAR();
				try {
					Acceptar("func");
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[func]");
				}
				LL_INST();
				try {
					Acceptar("fifunc");
					Acceptar("punt_i_coma");
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}
				DECL_FUNC();
				return;
			
			default: return;

		}

	}
	
	
	private void LL_PARAM() {
		
		System.out.println("\tDins LL_PARAM");
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
		
		System.out.println("\tDins LL_PARAM1");
		try {
			PER(); //no hauria de treure error
			Acceptar("identificador");
			Acceptar("dos_punts");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
		}
		
		TIPUS();
		LL_PARAM11();
		return;
		
	}
	
	private void PER() throws SyntacticError {
		
		System.out.println("\tDins PER");
		switch (lookAhead.getTipus()) {
		
			case "perref":
				try {
					Acceptar("perref");
				} catch (SyntacticError e) { }
				return;
				
			case "perval":
				try {
					Acceptar("perval");
				} catch (SyntacticError e) { }
				return;
				
			default: throw new SyntacticError("perref, perval");
		
		}
		
		
	}
	
	private void LL_PARAM11() {
		
		System.out.println("\tDins LL_PARAM11");
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
		
		System.out.println("\tDins TIPUS");
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
		
		System.out.println("\tDins EXPRESIO");
		EXPRESIO_SIMPLE();
		EXPRESIO1();
		return;
		
	}
	
	
	private void EXPRESIO1() {
		
		System.out.println("\tDins EXPRESIO1 amb token " + lookAhead.getTipus());
		switch (lookAhead.getTipus()) {
		
			case "oper_rel": 
			try {
				Acceptar("oper_rel");
			} catch (SyntacticError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				EXPRESIO_SIMPLE();
				return;
				
			default: return;
		
		}
	}
	
	
	private void EXPRESIO_SIMPLE() {
		
		System.out.println("\tDins EXPRESIO_SIMPLE");
		try {
			OP_INICI_EXP();
		} catch (SyntacticError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TERME();
		EXPRESIO_SIMPLE1();
		return;
		
	}
	
	
	private void EXPRESIO_SIMPLE1() {
		
		System.out.println("\tDins EXPRESIO_SIMPLE1");
		switch (lookAhead.getTipus()) {
		
			case "suma":
			case "resta":
			case "or":
			try {
				OP_EXP();
			} catch (SyntacticError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				TERME();
				EXPRESIO_SIMPLE1();
				return;
				
			default: return;
		
		}
		
	}
	
	private void TERME() {
		
		System.out.println("\tDins TERME");
		try {
			FACTOR();
		} catch (SyntacticError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TERME1();
		return;
		
	}
	
	private void TERME1() {
		
		System.out.println("\tDins TERME1");
		switch (lookAhead.getTipus()) {
		
			case "multiplicacio":
			case "divisio":
			case "and":
			try {
				OP_TERME();
				FACTOR();
			} catch (SyntacticError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				TERME1();
				return;
				
			default: return;
			
		}
		
	}
	
	
	private void OP_INICI_EXP() throws SyntacticError {
		
		System.out.println("\tDins INICI_EXP");
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
		
		System.out.println("\tDins OP_EXP");
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
		
		System.out.println("\tDins OP_TERME");
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
		
		System.out.println("\tDins FACTOR");
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
		
		System.out.println("\tDins FACTOR1");
		switch (lookAhead.getTipus()) {
		
			case "parentesi_obert":
				try {
					Acceptar("parentesi_obert");
					LL_EXPRESIO();
					Acceptar("parentesi_tancat"); // parentesi tancat
				} catch (SyntacticError e) { }
				return;
				
			case "claudator_obert":
				try {
					Acceptar("claudator_obert");
					EXPRESIO();
					Acceptar("claudator_tancat"); // claudator tancat
				} catch (SyntacticError e) { }
				return;
			
			default: return;
								
		}
	}
	
	
	private void LL_EXPRESIO () {
		
		System.out.println("\tDins LL_EXPRESIO");
		EXPRESIO();
		LL_EXPRESIO1();
		
	}
	
	private void LL_EXPRESIO1 () {
		
		System.out.println("\tDins LL_EXPRESIO1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				try {
					Acceptar("coma");
					LL_EXPRESIO();
				} catch (SyntacticError e) { }
				return;
			
			default: return;
								
		}
	}
	
	private void LL_VAR () {
		
		System.out.println("\tDins LL_VAR");
		VAR();
		LL_VAR1();
		
	}
	
	private void LL_VAR1 () {
		
		System.out.println("\tDins LL_VAR1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				try {
					Acceptar("coma");
					LL_VAR();
				} catch (SyntacticError e) { }
				return;
			
			default: return;
								
		}
	}
	
	private void VAR () {
		
		System.out.println("\tDins VAR");
		try {
			Acceptar("identificador");
		} catch (SyntacticError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VAR1();
		
	}
	
	private void VAR1 () {
		
		System.out.println("\tDins VAR1");
		switch (lookAhead.getTipus()) {
		
			case "claudator_obert":
				
				try {
					Acceptar("claudator_obert");
					EXPRESIO();
					Acceptar("claudator_tancat");
				} catch (SyntacticError e) { }	
				return;						
									
			default: return;
								
		}
	}
	
	private void LL_INST () {
		
		System.out.println("\tDins LL_INST");
		try {
			INSTRUCCIO();
			Acceptar("punt_i_coma");
		} catch (SyntacticError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LL_INST1();
		return;
		
	}
	
	private void LL_INST1 () {
		
		System.out.println("\tDins LL_INST1");
		switch (lookAhead.getTipus()) {
		
			case "identificador":	
			case "escriure":		
			case "llegir":			
			case "cicle":			
			case "mentre":			
			case "si":				
			case "retornar":		
			case "percada":
				try {
					INSTRUCCIO();
					Acceptar("punt_i_coma"); // ;
					LL_INST1();
				} catch (SyntacticError e) { }	
				return;
				
			default: return;
								
		}
	}
	
	private void INSTRUCCIO () throws SyntacticError {
		
		System.out.println("\tDins INSTRUCCIO");
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
				} catch (SyntacticError e) {
					
				}
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
				} catch (SyntacticError e) { 
					System.out.println("\tERROR EN EL SI");
				}	
				return;
				
			case "retornar":
				Acceptar("retornar"); // retornar
					EXPRESIO();
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
		
		System.out.println("\tDins INSTRUCCIO1");
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
		
		System.out.println("\tDins LL_EXP_ESCRIURE");
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
					LL_EXP_ESCRIURE1();
				return;
				
			default: throw new SyntacticError("SYNTAX ERROR: UNEXPECTED " + lookAhead.getTipus());
									
		}
	}
	
	private void LL_EXP_ESCRIURE1 () {
		
		System.out.println("\tDins LL_EXP_ESCRIURE1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
			try {
				Acceptar("coma");
			} catch (SyntacticError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					EXPRESIO();
				return;
							
			default: return;
								
		}
	}
	
	private void SINO () {
		
		System.out.println("\tDins SINO");
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
