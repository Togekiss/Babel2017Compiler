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
			throw new SyntacticError(token);
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
			consumir(new ArrayList<String>(Arrays.asList("prog","identificador", "escriure", "llegir", "cicle", "mentre", "si", "percada", "retornar", "fiprog", "eof")));
			if (lookAhead.getTipus().equals("prog"))
				try { Acceptar("prog");} catch (SyntacticError e1){} //no generara error 
		}
		
		LL_INST();
		
		try {
			Acceptar("fiprog");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
			consumir(new ArrayList<String>(Arrays.asList("fiprog", "eof")));
			if (lookAhead.getTipus().equals("fiprog"))
				try { Acceptar("fiprog");} catch (SyntacticError e1){} //no generara error 
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
System.out.println("Fora DECL");
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
System.out.println("Fora DECL_CONST_VAR");
				return;
	
			case "var":
				try {
					DECL_VAR();
				} catch (SyntacticError e) {
					Error.escriuError(24, "", alex.getLiniaActual(), "");
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "funcio", "prog", "eof")));
				}
				DECL_CONST_VAR();
System.out.println("Fora DECL_CONST_VAR");
				return;
	
			default: 
System.out.println("Fora DECL_CONST_VAR");
				return;
		}
	}


	private void DECL_CONST() throws SyntacticError{
		
System.out.println("Dins DECL_CONST");
		
		Acceptar("const");
		Acceptar("identificador");
		Acceptar("igual");
		EXPRESIO();
		Acceptar("punt_i_coma");
System.out.println("Fora DECL_CONST");
		return;
		
	}


	private void DECL_VAR() throws SyntacticError{

System.out.println("Dins DECL_VAR");
	
		Acceptar("var");
		Acceptar("identificador");
		Acceptar("dos_punts");
		TIPUS();
		Acceptar("punt_i_coma");
System.out.println("Fora DECL_VAR");
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
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "func", "fifunc", "prog", "eof")));					
				}
				
				DECL_CONST_VAR();
				
				try {
					Acceptar("func");
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					consumir(new ArrayList<String>(Arrays.asList("func", "prog","identificador", "escriure", "llegir", "cicle", "mentre", "si", "percada", "retornar", "fifunc", "eof")));
					if (lookAhead.getTipus().equals("func"))
						try { Acceptar("func");} catch (SyntacticError e1){} //no generara error 
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
System.out.println("Fora DECL_FUNC");
				return;
			
			default:
System.out.println("Fora DECL_FUNC");
				return;

		}

	}
	
	
	private void LL_PARAM() throws SyntacticError {
		
System.out.println("Dins LL_PARAM");
		switch (lookAhead.getTipus()) {
		
			case "perref":
			case "perval":
				LL_PARAM1();
System.out.println("Fora LL_PARAM");
				return;
			
			default:
System.out.println("Fora LL_PARAM");
				return;
		
		}
		
	}
	
	
	private void LL_PARAM1() throws SyntacticError {
		
System.out.println("Dins LL_PARAM1");
		
		PER(); //no hauria de treure error
		Acceptar("identificador");
		Acceptar("dos_punts");
		TIPUS();
		LL_PARAM11();
System.out.println("Fora LL_PARAM1");
		return;
		
	}
	
	private void PER() throws SyntacticError {
		
System.out.println("Dins PER");
		switch (lookAhead.getTipus()) {
		
			case "perref":
				Acceptar("perref");
System.out.println("Fora PER");
				return;
				
			case "perval":
				Acceptar("perval");
System.out.println("Fora PER");
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
System.out.println("Fora LL_PARAM11");
				return;
					
			default:
System.out.println("Fora LL_PARAM11");
				return;
		
		}
		
	}
	
	private void TIPUS() throws SyntacticError {
		
System.out.println("Dins TIPUS");
		switch (lookAhead.getTipus()) {
			
			case "tipus_simple": 
				Acceptar("tipus_simple");
System.out.println("Fora TIPUS");
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
System.out.println("Fora TIPUS");
				return;
				
			default: throw new SyntacticError("tipus_simple, vector");
				
		}
	}
	
	
	private void EXPRESIO() {
		
System.out.println("Dins EXPRESIO");
		EXPRESIO_SIMPLE();
		EXPRESIO1();
System.out.println("Fora EXPRESIO");
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
System.out.println("Fora EXPRESIO1");
				return;
				
			default: 
System.out.println("Fora EXPRESIO1");
				return;
		
		}
	}
	
	
	private void EXPRESIO_SIMPLE() {
		
System.out.println("Dins EXPRESIO_SIMPLE");
		try {
			OP_INICI_EXP(); //mai donara error
		} catch (SyntacticError e) { }
		TERME();
		EXPRESIO_SIMPLE1();
System.out.println("Fora EXPRESIO_SIMPLE");
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
System.out.println("Fora EXPRESIO_SIMPLE1");
				return;
				
			default:
System.out.println("Fora EXPRESIO_SIMPLE1");
				return;
		
		}
		
	}
	
	private void TERME() {
		
System.out.println("Dins TERME");
		try {
			FACTOR();
		} catch (SyntacticError e) {
			Error.escriuError(28, "", alex.getLiniaActual(), "");
			//TODO firsts de terme1
			consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
					"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
					"fer", "llavors","eof")));
		}
		TERME1();
System.out.println("Fora TERME");
		return;
		
	}
	
	private void TERME1() {
		
System.out.println("Dins TERME1");
		switch (lookAhead.getTipus()) {
		
			case "multiplicacio":
			case "divisio":
			case "and":
				try {
					OP_TERME(); //mai donara error
					FACTOR();
				} catch (SyntacticError e) {
					Error.escriuError(28, "", alex.getLiniaActual(), "");
					//TODO first de terme1
					consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
							"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
							"fer", "llavors","eof"))); 
				}
				
				TERME1();
System.out.println("Fora TERME1");
				return;
				
			default: 
System.out.println("Fora TERME1");
				return;
			
		}
		
	}
	
	
	private void OP_INICI_EXP() throws SyntacticError {
		
System.out.println("Dins OP_INICI_EXP");
		switch(lookAhead.getTipus()) {
		
			case "suma":
				Acceptar("suma");
System.out.println("Fora OP_INICI_EXP");
				return;
				
			case "resta":
				Acceptar("resta");
System.out.println("Fora OP_INICI_EXP");
				return;
				
			case "not":
				Acceptar("not");
System.out.println("Fora OP_INICI_EXP");
				return;
				
			default:
System.out.println("Fora OP_INICI_EXP");
				return;
		
		}
		
		
	}
		
	private void OP_EXP () throws SyntacticError {
		
System.out.println("Dins OP_EXP");
		switch (lookAhead.getTipus()) {
		
			case "suma":
				Acceptar("suma");
System.out.println("Fora OP_EXP");
				return;
							
			case "resta":
				Acceptar("resta");
System.out.println("Fora OP_EXP");
				return;
							
			case "or":
				Acceptar("or");
System.out.println("Fora OP_EXP");
				return;
				
			default: throw new SyntacticError("+, -, or");
				
		}
	}
	
	private void OP_TERME () throws SyntacticError {
		
System.out.println("Dins OP_TERME");
		switch (lookAhead.getTipus()) {
		
			case "multiplicacio":
				Acceptar("multiplicacio");
System.out.println("Fora OP_TERME");
				return;
				
			case "divisio":
				Acceptar("divisio");
System.out.println("Fora OP_TERME");
				return;
				
			case "and":
				Acceptar("and");
System.out.println("Fora OP_TERME");
				return;	
				
			default: throw new SyntacticError("*, /, and");
								
		}
		
	}
	
	private void FACTOR () throws SyntacticError {
		
System.out.println("Dins FACTOR");
		switch (lookAhead.getTipus()) {
		
			case "ct_enter":
				Acceptar("ct_enter"); //no tirara error
System.out.println("Fora FACTOR");
				return;
				
			case "ct_logica":
				Acceptar("ct_logica"); //no tirara error
System.out.println("Fora FACTOR");
				return;
				
			case "ct_cadena":
				Acceptar("ct_cadena"); //no tirara error
System.out.println("Fora FACTOR");
				return;
				
			case "identificador":
				Acceptar("identificador"); //no tirara errror
				FACTOR1();
System.out.println("Fora FACTOR");
				return;	
				
			case "parentesi_obert":
				Acceptar("parentesi_obert"); //no tirara errror
				EXPRESIO();
				Acceptar("parentesi_tancat"); //pot tirar error
System.out.println("Fora FACTOR");
				return;
				
			default: throw new SyntacticError("ct_enter, ct_logica, ct_cadena, identificador, (");
								
		}
	}
	
	private void FACTOR1 () {
		
System.out.println("Dins FACTOR1");
		switch (lookAhead.getTipus()) {
		
			case "parentesi_obert":
				try {
					Acceptar("parentesi_obert"); //no tirara error
					LL_EXPRESIO(); //tira error (falta ,)
					Acceptar("parentesi_tancat"); // pot tirar error
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//TODO follows factor1
					consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
							"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
							"fer", "llavors","eof")));
					if (lookAhead.getTipus().equals("parentesi_tancat"))
						try { Acceptar("parentesi_tancat");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora FACTOR1");
				return;
				
			case "claudator_obert":
				try {
					Acceptar("claudator_obert"); //no tirara error
					EXPRESIO();
					Acceptar("claudator_tancat"); // pot tirar error
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//TODO completar amb follows factor1
					consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
							"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
							"fer", "llavors","eof")));
					if (lookAhead.getTipus().equals("claudator_tancat"))
						try { Acceptar("claudator_tancat");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora FACTOR1");
				return;
			
			default:
System.out.println("Fora FACTOR");
				return;
								
		}
	}
	
	
	private void LL_EXPRESIO() throws SyntacticError {
		
System.out.println("Dins LL_EXPRESIO");
		EXPRESIO();
		LL_EXPRESIO1();
System.out.println("Fora LL_EXPRESIO");
		return;
		
	}
	
	private void LL_EXPRESIO1 () throws SyntacticError{
		
System.out.println("Dins LL_EXPRESIO1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_EXPRESIO();
System.out.println("Fora LL_EXPRESIO1");
				return;
			
			default: 
System.out.println("Fora LL_EXPRESIO1");
				return;
								
		}
	}
	
	private void LL_VAR () throws SyntacticError {
		
System.out.println("Dins LL_VAR");
		VAR();
		LL_VAR1();
System.out.println("Fora LL_VAR");
		return;
	}
	
	private void LL_VAR1 () throws SyntacticError {
		
System.out.println("Dins LL_VAR1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_VAR();
System.out.println("Fora LL_VAR1");
				return;
			
			default:
System.out.println("Fora LL_VAR1");
				return;
								
		}
	}
	
	private void VAR () throws SyntacticError {
		
System.out.println("Dins VAR");
		Acceptar("identificador");
		VAR1();
System.out.println("Fora VAR");
		return;
		
	}
	
	private void VAR1 () throws SyntacticError {
		
System.out.println("Dins VAR1");
		switch (lookAhead.getTipus()) {
		
			case "claudator_obert":
				Acceptar("claudator_obert");
				EXPRESIO();
				Acceptar("claudator_tancat");
System.out.println("Fora VAR1");
				return;						
									
			default:
System.out.println("Fora VAR1");
				return;
								
		}
	}
	
	private void LL_INST () {
		
System.out.println("Dins LL_INST");
		try {
			INSTRUCCIO(); //pot tirar error (switch)
			Acceptar("punt_i_coma");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
			//first ll_inst1
			consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre", "si",
					"retornar", "percada", "fifunc", "fiprog", "punt_i_coma", "fins", "fimentre", "per", "sino", "fisi","eof")));
			if (lookAhead.getTipus().equals("punt_i_coma"))
				try { Acceptar("punt_i_coma");} catch (SyntacticError e1){} //no generara error 
		}
		LL_INST1();
System.out.println("Fora LL_INST");
		return;
		
	}
	
	private void LL_INST1 () {
		
System.out.println("Dins LL_INST1");
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
					
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//first ll_inst1
					consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre", "si",
							"retornar", "percada", "fifunc", "fiprog", "punt_i_coma", "fins", "fimentre", "per", "sino", "fisi","eof")));
					if (lookAhead.getTipus().equals("punt_i_coma"))
						try { Acceptar("punt_i_coma");} catch (SyntacticError e1){} //no generara error 
				}
				LL_INST1();	
System.out.println("Fora LL_INST1");
				return;
				
			default:
System.out.println("Fora LL_INST1");
				return;
								
		}
	}
	
	private void INSTRUCCIO () throws SyntacticError {
		
System.out.println("Dins INSTRUCCIO");
		switch (lookAhead.getTipus()) {
		
			case "identificador":
				try {
					VAR();//pot tirar error
					Acceptar("igual");
					INSTRUCCIO1();
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","eof")));
				}
System.out.println("Fora INSTRUCCIO");
				return;
				
			case "escriure":
				Acceptar("escriure"); // mai donara error
				try {
					Acceptar("parentesi_obert"); // (
					LL_EXP_ESCRIURE(); 	 
					Acceptar("parentesi_tancat"); // )
				} catch (SyntacticError e) {
					Error.escriuError(27, "escriure", alex.getLiniaActual(), "");
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","parentesi_tancat", "eof")));
					if (lookAhead.getTipus().equals("parentesi_tancat"))
						try { Acceptar("parentesi_tancat");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora INSTRUCCIO");
				return;		
				
			case "llegir":
				Acceptar("llegir"); // mai donara error
				try {
					Acceptar("parentesi_obert"); // (
					LL_VAR(); 	 
					Acceptar("parentesi_tancat"); // )
				} catch (SyntacticError e) {
					Error.escriuError(27, "llegir", alex.getLiniaActual(), "");
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","parentesi_tancat", "eof")));
					if (lookAhead.getTipus().equals("parentesi_tancat"))
						try { Acceptar("parentesi_tancat");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora INSTRUCCIO");
				return;
				
			case "cicle":
				Acceptar("cicle"); // mai donara error
				LL_INST();
				try {
					Acceptar("fins"); // fins 	 
				} catch (SyntacticError e) {
					Error.escriuError(27, "cicle", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//first expresio
					consumir(new ArrayList<String>(Arrays.asList("suma", "resta", "not", "ct_entera", "ct_logica", "ct_cadena",
							"identificador", "parentesi_obert","fins", "eof")));
					if (lookAhead.getTipus().equals("fins"))
						try { Acceptar("fins");} catch (SyntacticError e1){} //no generara error 
				}
				EXPRESIO();
System.out.println("Fora INSTRUCCIO");
				return;
				
			case "mentre":
				Acceptar("mentre"); // mai donara error
				EXPRESIO();
				try {
					Acceptar("fer");
				} catch (SyntacticError e) {
					Error.escriuError(27, "mentre", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//firsts ll_inst
					consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre",
							"si", "retorna", "percada", "fer", "eof")));
					if (lookAhead.getTipus().equals("fer"))
						try { Acceptar("fer");} catch (SyntacticError e1){} //no generara error 
				}
				LL_INST();
				try {	
					Acceptar("fimentre");	
				} catch (SyntacticError e) {
					Error.escriuError(27, "mentre", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","fimentre", "eof")));
					if (lookAhead.getTipus().equals("fimentre"))
						try { Acceptar("fimentre");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora INSTRUCCIO");
				return;
				
			case "si":
				Acceptar("si"); // mai donara error
				EXPRESIO();
				try {
					Acceptar("llavors");
				} catch (SyntacticError e) {
					Error.escriuError(27, "si", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//fists ll_inst
					consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre",
							"si", "retornar", "percada", "llavors", "eof")));
					if (lookAhead.getTipus().equals("llavors"))
						try { Acceptar("llavors");} catch (SyntacticError e1){} //no generara error 
				}
				LL_INST();
				SINO();
				try {
					Acceptar("fisi"); // fisi	 
				} catch (SyntacticError e) { 
					Error.escriuError(27, "si", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","fisi", "eof")));
					if (lookAhead.getTipus().equals("fisi"))
						try { Acceptar("fisi");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora INSTRUCCIO");
				return;
				
			case "retornar":
				Acceptar("retornar"); // mai donara error
				EXPRESIO();
System.out.println("Fora INSTRUCCIO");
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
					//firsts ll_inst
					consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre",
							"si", "retorna", "percada", "fer", "eof")));
					if (lookAhead.getTipus().equals("fer"))
						try { Acceptar("fer");} catch (SyntacticError e1){} //no generara error 
				}
				LL_INST();
				
				try {
					Acceptar("fiper"); // fiper	
				} catch (SyntacticError e) {
					Error.escriuError(27, "percada", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma", "fiper", "eof")));
					if (lookAhead.getTipus().equals("fiper"))
						try { Acceptar("fiper");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora INSTRUCCIO");
				return;
									
			default: throw new SyntacticError("identificador, escriure, llegir, cicle, mentre, si, percada, retornar");
								
		}
	}

	
	private void INSTRUCCIO1 () throws SyntacticError {
		
System.out.println("Dins INSTRUCCIO1");
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
System.out.println("Fora INSTRUCCIO1");
				return;
		
			
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
					//follows instruccio1
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma", "eof")));
					if (lookAhead.getTipus().equals("punt_i_coma"))
						try { Acceptar("punt_i_coma");} catch (SyntacticError e1){} //no generara error 
				}
System.out.println("Fora INSTRUCCIO1");
				return;
							
			default: throw new SyntacticError("si, +, -, not, ct_enter, ct_logica, ct_cadena, identificador, (");
									
		}
	}
	
	private void LL_EXP_ESCRIURE () throws SyntacticError {
		
System.out.println("Dins LL_EXP_ESCRIURE");
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
System.out.println("Fora LL_EXP_ESCRIURE");
				return;
				
			default: throw new SyntacticError("+, -, not, ct_enter, ct_logica, ct_cadena, identificador, (");
									
		}
	}
	
	private void LL_EXP_ESCRIURE1 () {
		
System.out.println("Dins LL_EXP_ESCRIURE1");
		switch (lookAhead.getTipus()) {
		
			case "coma":
				try {
					Acceptar("coma"); //mai tirara error
				} catch (SyntacticError e) { }
				EXPRESIO();
System.out.println("Fora LL_EXP_ESCRIURE1");
				return;
							
			default:
System.out.println("Fora LL_EXP_ESCRIURE1");
				return;
								
		}
	}
	
	private void SINO () {
		
System.out.println("Dins SINO");
		switch (lookAhead.getTipus()) {
		
			case "sino":
				try {
					Acceptar("sino"); //mai tirara error
				} catch (SyntacticError e) { }
				LL_INST();
System.out.println("Fora SINO");				
				return;
				
			default:
System.out.println("Fora SINO");
				return;
								
		}
	}
}
