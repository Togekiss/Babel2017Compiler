package main;

public class Token {
	
	private String tipus;
	private String lexema;
	
	
	public Token(String tipus, String lexema) {
		super();
		this.tipus = tipus;
		this.lexema = lexema;
	}
	
	
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getLexema() {
		return lexema;
	}
	public void setLexema(String lexema) {
		this.lexema = lexema;
	}
	
	public boolean esEOF() {
		return tipus.equals("eof");
	}
	
	public void pushLexema(char charActual) {
		lexema = lexema + charActual;
	}

	public void lexToLowerCase() {
		lexema = lexema.toLowerCase();
	}
	
	public void trunkLexema() {
		lexema = lexema.substring(0, 31);
	}
}
