package analitzadorSintactic;

import analitzadorLexicografic.Alex;
import main.Token;
import main.Error;

public class Asin2 {

	private Alex alex;
	private Token lookahead;


	private void accept(String token) {
		if (token.equals(lookahead.getTipus())) lookahead = alex.getToken();
		else {
			
			Error.escriuError(0, "", 0);
		}
	}

	public void P() {

		DECL();
		accept("prog");
		LL_INST();
		accept("fiprog");
		return;

	}


	private void DECL() {

		DECL_CONST_VAR();
		DECL_FUNC();
		return;

	}


	private void DECL_CONST_VAR() {

		switch (lookahead.getTipus()) {

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

		accept("const");
		accept("identificador");
		accept("igual");
		EXPRESIO();
		accept("punt_i_coma");
		return;

	}


	private void DECL_VAR() {

		accept("var");
		accept("identificador");
		accept("dos_punts");
		TIPUS();
		accept("punt_i_coma");
		return;

	}


	private void DECL_FUNC() {

		switch (lookahead.getTipus()) {
	
			case "funcio": 
				accept("funcio");
				accept("identificador");
				accept("parentesi_obert");
				LL_PARAM();
				accept("parentesi_tancat");
				accept("dos_punts");
				accept("tipus_simple");
				accept("punt_i_coma");
				DECL_CONST_VAR();
				accept("func");
				LL_INST();
				accept("fifunc");
				accept("punt_i_coma");
				DECL_FUNC();
				return;
			
			case "prog": return;
			
			default: Error.escriuError(0, "", 0);

		}

	}
	
	
	private void LL_PARAM() {
		
		switch (lookahead.getTipus()) {
		
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
		
		switch (lookahead.getTipus()) {
		
			case "perref":
				accept("perref");
				accept("identificador");
				accept("dos_punts");
				TIPUS();
				LL_PARAM11();
				return;
				
			case "perval":
				accept("perval");
				accept("identificador");
				accept("dos_punts");
				TIPUS();
				LL_PARAM11();
				return;
				
			default: Error.escriuError(0, "", 0);
		
		}
		
	}
	
	private void LL_PARAM11() {
		
		switch (lookahead.getTipus()) {
		
			case ",":
				accept("coma");
				LL_PARAM1();
				return;
					
			case ")": return;
			
			default: Error.escriuError(0, "", 0);
		
		}
		
	}
	
	private void TIPUS() {
		
		switch (lookahead.getTipus()) {
		
		case "sencer": 
			accept("tipus_simple");
			return;
		case "logic":
			accept("tipus_simple");
		
		
		}
		
		
	}

}
