package analitzadorSintactic;

import main.Error;
import main.Token;
import taulasimbols.Bloc;
import taulasimbols.ITipus;
import taulasimbols.TaulaSimbols;
import taulasimbols.TipusIndefinit;

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
	private Semantic semantic;
	
	//CONSTRUCTOR
	public Asin (String args, String name) {
		
		alex = new Alex(args);
		asem = new Asem();
		error = new Error(name);
		semantic = new Semantic();
		taulaSimbols = new TaulaSimbols();
		lookAhead = alex.getToken();
		alex.writeToken(lookAhead);
		
	}
	
	
	//ACCEPTAR UN TOKEN
	private void Acceptar (String token) throws SyntacticError {
		
		if (lookAhead.getTipus().equals(token)) {
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
		
		taulaSimbols.setBlocActual(0);
		taulaSimbols.inserirBloc(new Bloc());
		DECL();
		
		try {
			Acceptar("prog");
		} catch (SyntacticError e) {
			Error.escriuError(22, "", alex.getLiniaActual(), "");
			consumir(new ArrayList<String>(Arrays.asList("prog","identificador", "escriure", "llegir", "cicle", "mentre", "si", "percada", "retornar", "fiprog", "eof")));
			if (lookAhead.getTipus().equals("prog"))
				try { Acceptar("prog");} catch (SyntacticError e1){} //no generara error 
		}
		
		PROG();
		
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
	
	private void PROG() {
		LL_INST();
		
		try {
			Acceptar("fiprog");
		} catch (SyntacticError e) {
			Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
			consumir(new ArrayList<String>(Arrays.asList("fiprog","const", "funcio","var", "prog", "func", "fifunc", "sino", "fisi", "fimentre", "fins", "fiper", "eof")));
			if (lookAhead.getTipus().equals("fiprog"))
				try { Acceptar("fiprog");} catch (SyntacticError e1){} //no generara error 
			else {
				if (!lookAhead.getTipus().equals("eof")) {
					switch (lookAhead.getTipus()) {
					case "fifunc":
						try { Acceptar("fifunc"); } catch (SyntacticError e1) {} //mai donara error
						break;
					case "sino":
						try { Acceptar("sino"); } catch (SyntacticError e1) {} //mai donara error
						break;
					case "fisi":
						try { Acceptar("fisi"); } catch (SyntacticError e1) {} //mai donara error
						break;
					case "fimentre":
						try { Acceptar("fimentre"); } catch (SyntacticError e1) {} //mai donara error
						break;
					case "fins":
						try { Acceptar("fins"); } catch (SyntacticError e1) {} //mai donara error
						break;
					case "fiper":
						try { Acceptar("fiper"); } catch (SyntacticError e1) {} //mai donara error
						break;
					case "func":
						DECL_FUNC();
						//try { Acceptar("func"); } catch (SyntacticError e1) {} //mai donara error
						break;
					case "const":
						DECL();
						break;
					case "var":
						DECL();
						break;
					case "funcio":
						DECL_FUNC();
						break;
					}
					PROG();
				}
					
				
			}
		}
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
				try {
					DECL_CONST();
				} catch (SyntacticError e) {
					Error.escriuError(23, "", alex.getLiniaActual(), "");
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "funcio", "prog","identificador", "eof")));
				}
				DECL_CONST_VAR();
				return;
	
			case "var":
				try {
					DECL_VAR();
				} catch (SyntacticError e) {
					Error.escriuError(24, "", alex.getLiniaActual(), "");
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "funcio", "prog", "identificador", "eof")));
				}
				DECL_CONST_VAR();
				return;
	
			default: 
				return;
		}
	}


	private void DECL_CONST() throws SyntacticError{
				
		semantic.removeAll();
		Acceptar("const");
		if (lookAhead.getTipus().equals("identificador"))
			semantic.setValue("lexema", lookAhead.getLexema());
		Acceptar("identificador");
		Acceptar("igual");
		//semantic = EXPRESIO(semantic);
		EXPRESIO();
semantic.setValue("tipus", new TipusIndefinit("undefined", 0));
semantic.setValue("valor", "null");
semantic.setValue("esEstatic", true);
		asem.afegirConstant(semantic, taulaSimbols);
		Acceptar("punt_i_coma");
		return;
		
	}


	private void DECL_VAR() throws SyntacticError{

	
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
					consumir(new ArrayList<String>(Arrays.asList("const", "var", "identificador", "func", "fifunc", "prog", "eof")));					
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
				return;
			
			default:
				return;

		}

	}
	
	
	private void LL_PARAM() throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "perref":
			case "perval":
				LL_PARAM1();
				return;
			
			default:
				return;
		
		}
		
	}
	
	
	private void LL_PARAM1() throws SyntacticError {
		
		
		PER(); //no hauria de treure error
		Acceptar("identificador");
		Acceptar("dos_punts");
		TIPUS();
		LL_PARAM11();
		return;
		
	}
	
	private void PER() throws SyntacticError {
		
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
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_PARAM1();
				return;
					
			default:
				return;
		
		}
		
	}
	
	private void TIPUS() throws SyntacticError {
		
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
		
		EXPRESIO_SIMPLE();
		EXPRESIO1();
		return;
		
	}
	
	
	private void EXPRESIO1() {
		
		switch (lookAhead.getTipus()) {
		
			case "oper_rel": 
				try {
					Acceptar("oper_rel"); //mai donara error	
				} catch (SyntacticError e) {}
				EXPRESIO_SIMPLE();
				return;
				
			default: 
				return;
		
		}
	}
	
	
	private void EXPRESIO_SIMPLE() {
		
		try {
			OP_INICI_EXP(); //mai donara error
		} catch (SyntacticError e) { }
		TERME();
		EXPRESIO_SIMPLE1();
		return;
		
	}
	
	
	private void EXPRESIO_SIMPLE1() {
		
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
				
			default:
				return;
		
		}
		
	}
	
	private void TERME() {
		
		try {
			FACTOR();
		} catch (SyntacticError e) {
			Error.escriuError(28, "", alex.getLiniaActual(), "");
			// firsts de terme1
			consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
					"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
					"fer", "llavors","eof")));
		}
		TERME1();
		return;
		
	}
	
	private void TERME1() {
		
		switch (lookAhead.getTipus()) {
		
			case "multiplicacio":
			case "divisio":
			case "and":
				try {
					OP_TERME(); //mai donara error
					FACTOR();
				} catch (SyntacticError e) {
					Error.escriuError(28, "", alex.getLiniaActual(), "");
					//first de terme1
					consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
							"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
							"fer", "llavors","eof"))); 
				}
				
				TERME1();
				return;
				
			default: 
				return;
			
		}
		
	}
	
	
	private void OP_INICI_EXP() throws SyntacticError {
		
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
				
			default:
				return;
		
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
				
			default: throw new SyntacticError("+, -, or");
				
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
				
			default: throw new SyntacticError("*, /, and");
								
		}
		
	}
	
	private void FACTOR () throws SyntacticError {
		
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
		
		switch (lookAhead.getTipus()) {
		
			case "parentesi_obert":
				try {
					Acceptar("parentesi_obert"); //no tirara error
					LL_EXPRESIO(); //tira error (falta ,)
					Acceptar("parentesi_tancat"); // pot tirar error
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//follows factor1
					consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
							"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
							"fer", "llavors","eof")));
					if (lookAhead.getTipus().equals("parentesi_tancat"))
						try { Acceptar("parentesi_tancat");} catch (SyntacticError e1){} //no generara error 
				}
				return;
				
			case "claudator_obert":
				try {
					Acceptar("claudator_obert"); //no tirara error
					EXPRESIO();
					Acceptar("claudator_tancat"); // pot tirar error
				} catch (SyntacticError e) {
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//follows factor1
					consumir(new ArrayList<String>(Arrays.asList("multiplicacio", "divisio", "and", "suma", "resta",
							"or", "oper_rel", "punt_i_coma", "rang", "claudator_tancat", "parentesi_tancat", "coma",
							"fer", "llavors","eof")));
					if (lookAhead.getTipus().equals("claudator_tancat"))
						try { Acceptar("claudator_tancat");} catch (SyntacticError e1){} //no generara error 
				}
				return;
			
			default:
				return;
								
		}
	}
	
	
	private void LL_EXPRESIO() throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
			
			case "suma":
			case "resta":
			case "not":
			case "ct_entera":
			case "ct_logica":
			case "ct_cadena":
			case "identificador":
			case "parentesi_obert":
				EXPRESIO();
				LL_EXPRESIO1();
				return;
				
			default: return;
		
		}
		
	}
	
	private void LL_EXPRESIO1 () throws SyntacticError{
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_EXPRESIO();
				return;
			
			default: 
				return;
								
		}
	}
	
	private void LL_VAR () throws SyntacticError {
		
		VAR();
		LL_VAR1();
		return;
	}
	
	private void LL_VAR1 () throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				Acceptar("coma");
				LL_VAR();
				return;
			
			default:
				return;
								
		}
	}
	
	private void VAR () throws SyntacticError {
		
		Acceptar("identificador");
		VAR1();
		return;
		
	}
	
	private void VAR1 () throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "claudator_obert":
				Acceptar("claudator_obert");
				EXPRESIO();
				Acceptar("claudator_tancat");
				return;						
									
			default:
				return;
								
		}
	}
	
	private void LL_INST () {
		
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
				return;
				
			default:
				return;
								
		}
	}
	
	private void INSTRUCCIO () throws SyntacticError {
		
		switch (lookAhead.getTipus()) {
		
			case "identificador":
				try {
					VAR();//pot tirar error
					Acceptar("igual");
					INSTRUCCIO1();
				} catch (SyntacticError e) {
					Error.escriuError(27, "assignacio", alex.getLiniaActual(), "");
					Error.escriuError(21, "[" + lookAhead.getLexema() + "]", alex.getLiniaActual(), "[" + e.getMessage() + "]");
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("escriure", "llegir", "cicle", "mentre", "si",
							"retornar", "percada", "fifunc", "fiprog", "punt_i_coma", "fins", "fimentre", "per", "sino", "fisi","eof")));
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
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","parentesi_tancat", "fiprog", "eof")));
					if (lookAhead.getTipus().equals("parentesi_tancat"))
						try { Acceptar("parentesi_tancat");} catch (SyntacticError e1){} //no generara error 
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
					//follows instruccio
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","parentesi_tancat","fiprog", "eof")));
					if (lookAhead.getTipus().equals("parentesi_tancat"))
						try { Acceptar("parentesi_tancat");} catch (SyntacticError e1){} //no generara error 
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
					//first expresio
					consumir(new ArrayList<String>(Arrays.asList("suma", "resta", "not", "ct_entera", "ct_logica", "ct_cadena",
							"identificador", "parentesi_obert","fins", "eof", "fiprog")));
					if (lookAhead.getTipus().equals("fins"))
						try { Acceptar("fins");} catch (SyntacticError e1){} //no generara error 
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
					//firsts ll_inst
					consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre",
							"si", "retorna", "percada", "fer", "eof", "fiprog")));
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
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","fimentre", "eof", "fiprog")));
					if (lookAhead.getTipus().equals("fimentre"))
						try { Acceptar("fimentre");} catch (SyntacticError e1){} //no generara error 
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
					//fists ll_inst
					consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre",
							"si", "retornar", "percada", "llavors", "eof", "fiprog")));
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
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma","fisi", "eof", "fiprog")));
					if (lookAhead.getTipus().equals("fisi"))
						try { Acceptar("fisi");} catch (SyntacticError e1){} //no generara error 
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
					//firsts ll_inst
					consumir(new ArrayList<String>(Arrays.asList("identificador", "escriure", "llegir", "cicle", "mentre",
							"si", "retorna", "percada", "fer", "eof", "fiprog")));
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
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma", "fiper", "eof", "fiprog")));
					if (lookAhead.getTipus().equals("fiper"))
						try { Acceptar("fiper");} catch (SyntacticError e1){} //no generara error 
				}
				return;
									
			default: throw new SyntacticError("identificador, escriure, llegir, cicle, mentre, si, percada, retornar");
								
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
			case "parentesi_obert":
				EXPRESIO();
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
					consumir(new ArrayList<String>(Arrays.asList("punt_i_coma", "eof", "fiprog")));
				}
				return;
							
			default: throw new SyntacticError("si, +, -, not, ct_enter, ct_logica, ct_cadena, identificador, (");
									
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
				LL_EXP_ESCRIURE1();
				return;
				
			default: throw new SyntacticError("+, -, not, ct_enter, ct_logica, ct_cadena, identificador, (");
									
		}
	}
	
	private void LL_EXP_ESCRIURE1 () {
		
		switch (lookAhead.getTipus()) {
		
			case "coma":
				try {
					Acceptar("coma"); //mai tirara error
				} catch (SyntacticError e) { }
				EXPRESIO();
				return;
							
			default:
				return;
								
		}
	}
	
	private void SINO () {
		
		switch (lookAhead.getTipus()) {
		
			case "sino":
				try {
					Acceptar("sino"); //mai tirara error
				} catch (SyntacticError e) { }
				LL_INST();
				return;
				
			default:
				return;
								
		}
	}
}
