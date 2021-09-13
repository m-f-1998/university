package func.compiler;

class ASTPrinter implements ASTVisitor {

	private int indent;

	private void printIndent(int indentChange) {
		indent = indent + indentChange;
		for (int i = 0; i < indent; i++)
			System.out.print(" ");
	}

	public ASTPrinter() {
		indent = 0;
	}

	@Override
	public void visit(Method prog) throws Exception {
		System.out.printf("METHOD %s%s", prog.n.v, prog.p.isEmpty() ? "()" : "(");
		for (Exp e : prog.p) {
			e.accept(this);
			System.out.print(e != prog.p.get(prog.p.size() - 1) ? ", " : ")");
		}
		if (prog.v != null) {
			if (!prog.v.isEmpty())
				System.out.print(" VARS ");
			for (Exp e : prog.v) {
				e.accept(this);
				if (e != prog.v.get(prog.v.size() - 1))
					System.out.print(", ");
			}
		}
		System.out.print("\n");
		System.out.print("BEGIN\n");
		indent = indent + 2;
		for (Command d : prog.c)
			if (d != null)
				d.accept(this);
	}

	@Override
	public void visit(WriteCmd cmd) throws Exception {
		printIndent(0);
		System.out.print("WRITE ");
		cmd.exp.accept(this);
		System.out.print(";\n");
	}

	@Override
	public void visit(PrintEndCmd cmd) throws Exception {
		printIndent(-2);
		cmd.e.accept(this);
		System.out.println(";");
	}

	@Override
	public void visit(ReturnCmd cmd) throws Exception {
		printIndent(0);
		System.out.print("RETURN ");
		cmd.e.accept(this);
		System.out.println(";");
	}

	@Override
	public void visit(InputCmd cmd) throws Exception {
		printIndent(0);
		System.out.println("INPUT " + cmd.id);
	}

	@Override
	public void visit(WhileCmd cmd) throws Exception {
		printIndent(0);
		System.out.print("WHILE ");
		cmd.cond.accept(this);
		System.out.println();
		printIndent(0);
		System.out.println("BEGIN");
		indent = indent + 2;
		for (Command c : cmd.b) {
			c.accept(this);
		}
		cmd.end.accept(this);
	}

	@Override
	public void visit(IfCmd cmd) throws Exception {
		printIndent(0);
		System.out.print("IF ");
		cmd.cond.accept(this);
		System.out.println(" THEN");
		indent = indent + 2;
		for (Command c : cmd.cmd1) {
			c.accept(this);
		}
		if (cmd.cmd2 != null) {
			printIndent(-2);
			System.out.println("ELSE");
			indent = indent + 2;
			for (Command c : cmd.cmd2) {
				c.accept(this);
			}
		}
		cmd.end.accept(this);
	}

	@Override
	public void visit(AssignCmd cmd) throws Exception {
		printIndent(0);
		cmd.var.accept(this);
		System.out.print(" := ");
		cmd.e.accept(this);
		System.out.println(";");
	}

	@Override
	public void visit(NegExp e) throws Exception {
		System.out.print("-");
		e.exp.accept(this);
	}

	@Override
	public void visit(IdentExp e) throws Exception {
		System.out.print(e.v);
	}

	@Override
	public void visit(NumberExp e) throws Exception {
		System.out.print(e.n);
	}

	@Override
	public void visit(OpExp e) throws Exception {
		System.out.printf("%s(", e.op);
		if (e.left != null)
			e.left.accept(this);
		if (e.right != null) {
			System.out.print(",");
			e.right.accept(this);
		}
		System.out.print(")");
	}

	@Override
	public void visit(NewMethod newCmd) throws Exception {
		indent = 0;
		System.out.println();
		newCmd.m.accept(this);
	}

}
