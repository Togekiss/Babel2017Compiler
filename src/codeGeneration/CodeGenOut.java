package codeGeneration;

import java.io.IOException;
import java.io.PrintWriter;

public class CodeGenOut {
	private PrintWriter writer;
	private final int MAXREGISTERS = 18;
	private int nLabel;
	private boolean[] registerList;
	private String[] registerNames = {"t0", "t1", "t2", "t3", "t4", "t5", 
	"t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "t8", "t9"};
	
	public CodeGenOut (String nomFitxer) {
		try {
			nLabel = 0;
			registerList = new boolean[MAXREGISTERS];	
			nomFitxer += ".asm";
			writer = new PrintWriter(nomFitxer, "UTF-8");
			writer.println("	.text");
			writer.println("	.align	2");
			writer.println("	.globl	main");
			writer.println("	.data");
			writer.println("error:");
			writer.println("	.asciiz \"[ERR_GC_1] Index de vector fora de limits\"");
			writer.println("	.text");
			writer.println("main:");
			writer.println("	move $fp, $sp");
		} catch (IOException e) {
			System.out.println("El fitxer " + nomFitxer + " no existeix.");
			System.exit(2);
		}
	}
	
	public void gc (String toWrite) { writer.println("	" + toWrite); }
	
	public String demanarEtiqueta () { return "E" + (++nLabel); }
	
	public void gcEtiqueta (String toWrite) { writer.println(toWrite); }
	
	public int getRegistre () {
//System.out.println("asking for a reg");
		for (int i = 0; i < MAXREGISTERS; i++) { 
			if (registerList[i] == false) {
				registerList[i] = true;
//System.out.println("got reg number " + i);
				return i;
			}
		}
		return -1;
	}
	
	public String getNomRegistre (int i) { return registerNames[i]; }
	
	public void freeRegistre (int i) { registerList[i] = false; }
	
	public void tancaFitxer () { writer.close(); }
}
