package analitzadorSintactic;

import main.Error;
import main.Token;

import java.util.ArrayList;
import java.util.Arrays;

import analitzadorLexicografic.Alex;

public class Asin {
	
	private Alex alex;
	private Error error;
	private Token lookAhead;
	
	//CONSTRUCTOR
	public Asin (String args, String name) {
		
		alex = new Alex(args);
		error = new Error(name);
		lookAhead = alex.getToken();
		alex.writeToken(lookAhead);
		
	}
	
	
	//ACCEPTAR UN TOKEN
	private void Acceptar (String token) throws SyntacticError {
		
		if (lookAhead.getTipus().equals(token)) {
			
System.out.println("\t" + lookAhead.getLexema() + " ACCEPTAT");
			lookAhead = alex.getToken();
			alex.writeToken(lookAhead);
			
		} else 
			throw new SyntacticError(lookAhead.getLexema());
	}
	
	
	
	//CONSUMIR TOKENS FINS TROBAR UN DEL CONJUNT DE SINCRONITZACIO 
	private void consumir (ArrayList<String> l) {
		
		while (!l.contains(lookAhead.getTipus())) {
			lookAhead = alex.getToken();
		}
	}
	
	
	
	//SIMBOL AXIOMA
	public boolean P() {
		
System.out.println("Dins P");
		DECL();
		
		try {
			Acceptar("prog");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
			//TODO SI DESPRES DE CONSUMIR ARRIBA AL TOKEN AL QUE TOCAVA, ES QUE HI HAVIA TOKEN DE MES!!!
			//TODO COMPTANT LES VEGADES QUE CONSUMEIX PODEM SABER QUANTS TOKENS SOBREN
			consumir(new ArrayList<String>(Arrays.asList("prog","identificador", "escriure", "llegir", "cicle", "mentre", "si", "percada", "retornar", "fiprog", "eof")));
		}
		
		LL_INST();
		
		try {
			Acceptar("fiprog");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
			consumir(new ArrayList<String>(Arrays.asList("fiprog", "eof")));
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

System.out.println("Dins DECL");
		DECL_CONST_VAR();
		DECL_FUNC();
		return;

	}


	private void DECL_CONST_VAR() {

System.out.println("Dins CONST_VAR");
		switch (lookAhead.getTipus()) {

			case "const":
				try {
					DECL_CONST();
				} catch (SyntacticError e) {
					Error.escriuError(23, "", alex.getLiniaActual(), "");
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "funcio", "prog", "eof")));
				}
				DECL_CONST_VAR();
				return;
	
			case "var":
				try {
					DECL_VAR();
				} catch (SyntacticError e) {
					Error.escriuError(24, "", alex.getLiniaActual(), "");
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "funcio", "prog", "eof")));
				}
				DECL_CONST_VAR();
				return;
	
			default: return;
		}
	}


	private void DECL_CONST() throws SyntacticError{
		
System.out.println("Dins DECL_CONST");
		
		Acceptar("const");
		Acceptar("identificador");
		Acceptar("igual");
		EXPRESIO();
		Acceptar("punt_i_coma");
		return;
		
	}


	private void DECL_VAR() throws SyntacticError{

System.out.println("Dins DECL_VAR");
	
		Acceptar("var");
		Acceptar("identificador");
		Acceptar("dos_punts");
		TIPUS();
		Acceptar("punt_i_coma");
		return;

	}


	private void DECL_FUNC() {

System.out.println("Dins DECL_FUNC");
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
					//si la caguen a la declaracio i a func, es menja tota la funció :I
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "func", "fifunc", "eof")));
				}
				
				DECL_CONST_VAR();
				
				try {
					Acceptar("func");
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					consumir(new ArrayList<String>(Arrays.asList("prog","identificador", "escriure", "llegir", "cicle", "mentre", "si", "percada", "retornar", "fifunc", "eof")));
				}
				LL_INST();
				try {
					Acceptar("fifunc");
					Acceptar("punt_i_coma");
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					
					//same, millor que no la caguin dos cops seguits :I
					consumir(new ArrayList<String>(Arrays.asList("funcio", "prog", "punt_i_coma", "eof")));
					if (lookAhead.getTipus().equals("punt_i_coma"))
						try { Acceptar("punt_i_coma");} catch (SyntacticError e1){} //no generara error 
				}
				DECL_FUNC();
				return;
			
			default: return;

		}

	}
	
	
	private void LL_PARAM() throws SyntacticError {
		
System.out.println("Dins LL_PARAM");
		switch (lookAhead.getTipus()) {
		
			case "perref":
			case "perval":
				LL_PARAM1();
				return;
			
			default: return;
		
		}
		
	}
	
	
	private void LL_PARAM1() throws SyntacticError {
		
System.out.println("Dins LL_PARAM1");
		
		PER(); //no hauria de treure error
		Acceptar("identificador");
		Acceptar("dos_punts");
		TIPUS();
		LL_PARAM11();
		return;
		
	}
	
	private void PER() throws SyntacticError {
		
System.out.println("Dins PER");
		switch (lookAhead.getTipus()) {
		
			case "perref":
				Acceptar("perref");
				return;
				
			case "perval":
				Acceptar("perval");
				return;
				
			default: throw new SyntacticError("perref, perval");
		
		}
		
		
	}
	
	private void LL_PARAM11() throws SyntacticError{
		
System.out.println("Dins LL_PARAM11");
		switch (lookAhead.getTipus()) {
		
			case ",":
				Acceptar("coma");
				LL_PARAM1();
				return;
					
			default: return;
		
		}
		
	}
	
	private void TIPUS() throws SyntacticError {
		
System.out.println("Dins TIPUS");
		switch (lookAhead.getTipus()) {
			
			case "tipus_simple": 
				Acceptar("tipus_simple");
				return;
				
			case "vector":
				Acceptar("vector");
				Acceptar("claudator_obert");
				EXPRESIO();
				Acceptar("rang");
				EXPRESIO();
				Acceptar("claudator_tancat");
				Acceptar("de");
				Acceptar("tipus_simple");
				return;
				
			default: throw new SyntacticError("tipus_simple, vector");
				
		}
	}
	
	
	private void EXPRESIO() {
		
System.out.println("Dins EXPRESIO");
		EXPRESIO_SIMPLE();
		EXPRESIO1();
		return;
		
	}
	
	
	private void EXPRESIO1() {
		
System.out.println("Dins EXPRESIO1");
		switch (lookAhead.getTipus()) {
		
			case "oper_rel": 
				try {
					Acceptar("oper_rel"); //mai donara error	
				} catch (SyntacticError e) {}
				EXPRESIO_SIMPLE();
				return;
				
			default: return;
		
		}
	}
	
	
	private void EXPRESIO_SIMPLE() {
		
System.out.println("Dins EXPRESIO_SIMPLE");
		try {
			OP_INICI_EXP(); //mai donara error
		} catch (SyntacticError e) { }
		TERME();
		EXPRESIO_SIMPLE1();
		return;
		
	}
	
	
	private void EXPRESIO_SIMPLE1() {
		
System.out.println("Dins EXPRESIO_SIMPLE1");
		switch (lookAhead.getTipus()) {
		
			case "suma":
			case "resta":
			case "or":
				try {
					OP_EXP(); //mai donara error
				} catch (SyntacticError e) { }
				TERME();
				EXPRESIO_SIMPLE1();
				return;
				
			default: return;
		
		}
		
	}
	
	private void TERME() {
		
System.out.println("Dins TERME");
		try {
			FACTOR();
		} catch (SyntacticError e) {
			Error.escriuError(28, "", alex.getLiniaActual(), "");
			//TODO catch de factor
			consumir(new ArrayList<String>(Arrays.asList("")));
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
					OP_TERME(); //mai donara error
					FACTOR();
				} catch (SyntacticError e) {
					Error.escriuError(28, "", alex.getLiniaActual(), "");
					//TODO catch de factor
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
				
			default: throw new SyntacticError("+, -, or");
				
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
				
			default: throw new SyntacticError("*, /, and");
								
		}
		
	}
	
	private void FACTOR () throws SyntacticError {
		
		System.out.println("\tDins FACTOR");
		switch (lookAhead.getTipus()) {
		
			case "ct_enter":
				Acceptar("ct_enter"); //no tirara error
				return;
				
			case "ct_logica":
				Acceptar("ct_logica"); //no tirara error
				return;
				
			case "ct_cadena":
				Acceptar("ct_cadena"); //no tirara error
				return;
				
			case "identificador":
				Acceptar("identificador"); //no tirara errror
				FACTOR1();
				return;	
				
			case "parentesi_obert":
				Acceptar("parentesi_obert"); //no tirara errror
				EXPRESIO();
				Acceptar("parentesi_tancat"); //pot tirar error
				return;
				
			default: throw new SyntacticError("ct_enter, ct_logica, ct_cadena, identificador, (");
								
		}
	}
	
	private void FACTOR1 () {
		
		System.out.println("\tDins FACTOR1");
		switch (lookAhead.getTipus()) {
		
			case "parentesi_obert":
				try {
					Acceptar("parentesi_obert"); //no tirara error
					LL_EXPRESIO(); //tira error (falta ,)
					Acceptar("parentesi_tancat"); // pot tirar error
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}
				return;
				
			case "claudator_obert":
				try {
					Acceptar("claudator_obert"); //no tirara error
					EXPRESIO();
					Acceptar("claudator_tancat"); // pot tirar error
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}
				return;
			
			default: return;
								
		}
	}
	
	
	private void LL_EXPRESIO() throws SyntacticError {
		
		System.out.println("\tDins LL_EXPRESIO");
		EXPRESIO();
		LL_EXPRESIO1();
		
	}
	
	private void LL_EXPRESIO1 () throws SyntacticError{
		
		System.out.println("\tDins LL_EXPRESIO1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_EXPRESIO();
				return;
			
			default: return;
								
		}
	}
	
	private void LL_VAR () throws SyntacticError {
		
		System.out.println("\tDins LL_VAR");
		VAR();
		LL_VAR1();
		
	}
	
	private void LL_VAR1 () throws SyntacticError {
		
		System.out.println("\tDins LL_VAR1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_VAR();
				return;
			
			default: return;
								
		}
	}
	
	private void VAR () throws SyntacticError {
		
		System.out.println("\tDins VAR");
		Acceptar("identificador");
		VAR1();
		
	}
	
	private void VAR1 () throws SyntacticError {
		
		System.out.println("\tDins VAR1");
		switch (lookAhead.getTipus()) {
		
			case "claudator_obert":
				Acceptar("claudator_obert");
				EXPRESIO();
				Acceptar("claudator_tancat");
				return;						
									
			default: return;
								
		}
	}
	
	private void LL_INST () {
		
		System.out.println("\tDins LL_INST");
		try {
			INSTRUCCIO(); //pot tirar error (switch)
			Acceptar("punt_i_coma");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
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
					INSTRUCCIO(); //pot tirar error
					Acceptar("punt_i_coma"); //
					LL_INST1();
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}	
				return;
				
			default: return;
								
		}
	}
	
	private void INSTRUCCIO () throws SyntacticError {
		
		System.out.println("\tDins INSTRUCCIO");
		switch (lookAhead.getTipus()) {
		
			case "identificador":
				try {
					VAR();//pot tirar error
					Acceptar("igual");
					INSTRUCCIO1();
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}	
				return;
				
			case "escriure":
				Acceptar("escriure"); // mai donara error
				try {
					Acceptar("parentesi_obert"); // (
					LL_EXP_ESCRIURE(); 	 
					Acceptar("parentesi_tancat"); // )
				} catch (SyntacticError e) {
					Error.escriuError(27, "escriure", alex.getLiniaActual(), "");
				}
				return;		
				
			case "llegir":
				Acceptar("llegir"); // mai donara error
				try {
					Acceptar("parentesi_obert"); // (
					LL_VAR(); 	 
					Acceptar("parentesi_tancat"); // )
				} catch (SyntacticError e) {
					Error.escriuError(27, "llegir", alex.getLiniaActual(), "");
				}	
				return;
				
			case "cicle":
				Acceptar("cicle"); // mai donara error
				LL_INST();
				try {
					Acceptar("fins"); // fins 	 
				} catch (SyntacticError e) {
					Error.escriuError(27, "cicle", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}
				EXPRESIO();
				return;
				
			case "mentre":
				Acceptar("mentre"); // mai donara error
				EXPRESIO();
				try {
					Acceptar("fer");
				} catch (SyntacticError e) {
					Error.escriuError(27, "mentre", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}
				LL_INST();
				try {	
					Acceptar("fimentre");	
				} catch (SyntacticError e) {
					Error.escriuError(27, "mentre", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}	
				return;
				
			case "si":
				Acceptar("si"); // mai donara error
				EXPRESIO();
				try {
					Acceptar("llavors");
				} catch (SyntacticError e) {
					Error.escriuError(27, "si", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}
				LL_INST();
				SINO();
				try {
					Acceptar("fisi"); // fisi	 
				} catch (SyntacticError e) { 
					Error.escriuError(27, "si", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}	
				return;
				
			case "retornar":
				Acceptar("retornar"); // mai donara error
				EXPRESIO();
				return;
				
			case "percada":
				Acceptar("percada"); // mai donara error
				try {
					Acceptar("identificador"); // id
					Acceptar("en"); // en
					Acceptar("identificador"); // id
					Acceptar("fer");
				} catch (SyntacticError e) {
					Error.escriuError(27, "percada", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}	// fer	
				LL_INST();
				
				try {
					Acceptar("fiper"); // fiper	
				} catch (SyntacticError e) {
					Error.escriuError(27, "percada", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}

				return;
									
			default: throw new SyntacticError("identificador, escriure, llegir, cicle, mentre, si, percada, retornar");
								
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
				try {
					Acceptar("parentesi_obert"); // (
					EXPRESIO();
					Acceptar("parentesi_tancat"); // )
					Acceptar("interrogant"); // ?
					EXPRESIO();
					Acceptar("dos_punts"); // :
					EXPRESIO();
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
				}	
				return;
							
			default: throw new SyntacticError("si, +, -, not, ct_enter, ct_logica, ct_cadena, identificador, (");
									
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
				
			default: throw new SyntacticError("+, -, not, ct_enter, ct_logica, ct_cadena, identificador, (");
									
		}
	}
	
	private void LL_EXP_ESCRIURE1 () throws SyntacticError {
		
		System.out.println("\tDins LL_EXP_ESCRIURE1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma"); //mai tirara error
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
					Acceptar("sino"); //mai tirara error
				} catch (SyntacticError e) { }
				LL_INST();
				return;
				
			default: return;
								
		}
	}
}
