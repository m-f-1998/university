package func.compiler;

import java.util.ArrayList;
import java.util.List;

interface ASTVisitor {
	void visit(Method prog) throws Exception;

	void visit(ReturnCmd cmd) throws Exception;

	void visit(WriteCmd cmd) throws Exception;

	void visit(PrintEndCmd cmd) throws Exception;

	void visit(InputCmd cmd) throws Exception;

	void visit(WhileCmd cmd) throws Exception;

	void visit(IfCmd cmd) throws Exception;

	void visit(AssignCmd cmd) throws Exception;

	void visit(NegExp e) throws Exception;

	void visit(IdentExp e) throws Exception;

	void visit(NumberExp e) throws Exception;

	void visit(OpExp e) throws Exception;

	void visit(NewMethod e) throws Exception;
}

interface ASTElement {
	void accept(ASTVisitor visitor) throws Exception;
}

abstract class Exp implements ASTElement {
}

class NegExp extends Exp implements ASTElement {
	Exp exp;

	public NegExp(Exp exp) {
		this.exp = exp;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class IdentExp extends Exp implements ASTElement {
	String v;

	public IdentExp(String v) {
		this.v = v;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class NumberExp extends Exp implements ASTElement {
	int n;

	public NumberExp(int n) {
		this.n = n;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class OpExp extends Exp implements ASTElement {
	String op;
	Exp left, right;

	public OpExp(String string, Exp left, Exp right) {
		this.op = string;
		this.left = left;
		this.right = right;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

abstract class Command implements ASTElement {
}

class PrintEndCmd extends Command implements ASTElement {
	Exp e;

	public PrintEndCmd(Exp e) {
		this.e = e;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class ReturnCmd extends Command implements ASTElement {
	Exp e;

	public ReturnCmd(Exp e) {
		this.e = e;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class AssignCmd extends Command implements ASTElement {
	Exp var, e;

	public AssignCmd(Exp title, Exp e) {
		this.var = title;
		this.e = e;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class IfCmd extends Command implements ASTElement {
	Exp cond;
	ArrayList<Command> cmd1, cmd2;
	Command end;

	public IfCmd(Exp cond, ArrayList<Command> cmd1, ArrayList<Command> cmd2, Command end) {
		this.cond = cond;
		this.cmd1 = cmd1;
		this.cmd2 = cmd2;
		this.end = end;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class WhileCmd extends Command implements ASTElement {
	Exp cond;
	ArrayList<Command> b;
	Command end;
	
	public WhileCmd(Exp conditional, ArrayList<Command> block, Command end) {
		this.cond = conditional;
		this.b = block;
		this.end = end;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class InputCmd extends Command implements ASTElement {
	String id;

	public InputCmd(String id) {
		this.id = id;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class WriteCmd extends Command implements ASTElement {
	Exp exp;

	public WriteCmd(Exp exp) {
		this.exp = exp;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class NewMethod extends Command implements ASTElement {
	Method m;

	public NewMethod(Method m) {
		this.m = m;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}

class Method implements ASTElement {
	IdentExp n;
	List<Exp> p, v;
	List<Command> c;

	public Method(IdentExp title, List<Exp> params, List<Exp> vars, List<Command> cmds) {
		this.n = title;
		this.p = params;
		this.v = vars;
		this.c = cmds;
	}

	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}
}
