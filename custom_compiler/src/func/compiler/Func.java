package func.compiler;

import java.io.BufferedWriter;
import java.io.FileWriter;

class Func { // NO_UCD (unused code)

	public static void main(String[] args) throws Exception{
		String fname = "myprogram.flex";
		Parser p = new Parser(fname);
		p = new Parser(fname);
		Method prog = p.Method();
		// Pretty printing of program
		Compiler c = new Compiler();
		ASTPrinter ast = new ASTPrinter();
		System.out.println();
		System.out.println();
		prog.accept(ast);
		prog.accept(c);
		FileWriter fw = new FileWriter("/Users/matthew-mac/eclipse-workspace/F29LP-Compiler/prog.asm");
		BufferedWriter bf = new BufferedWriter(fw);
		bf.write(c.code.toString());
		bf.close();
		System.out.println(c.code);
		
	}
}
