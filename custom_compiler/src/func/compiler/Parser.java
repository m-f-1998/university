package func.compiler;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
class Parser {

	private funcFLEX l;
	private funcFLEX.FuncFLEXTokens symb;

	Parser(final String fname) throws Exception {
		l = new funcFLEX(new InputStreamReader(new FileInputStream(fname), "Cp1252"));
		lex();
	}

	private void advance(final funcFLEX.FuncFLEXTokens tok, final String err) throws Exception {
		if (symb == tok)
			symb = l.yylex();
		else
			throw new Exception("Parser error : expected " + tok + " but is " + symb + " \n" + err);
	}

	private void lex() throws Exception {
		symb = l.yylex();
	}

	private String getStr() {
		return l.yytext();
	}

	private int getInt() {
		return Integer.parseInt(getStr());
	}

	private List<funcFLEX.FuncFLEXTokens> currentBlocks = new ArrayList<funcFLEX.FuncFLEXTokens>();

	Method Method() throws Exception {
		currentBlocks.add(symb);
		advance(funcFLEX.FuncFLEXTokens.METHOD, "METHOD MUST BE FOUND IN METHOD BLOCK");
		IdentExp methodTitle = new IdentExp(getStr());
		advance(funcFLEX.FuncFLEXTokens.ID, "METHOD TITLE MUST BE FOUND IN METHOD BLOCK");
		List<Exp> params = Param(), vars = null;
		if (symb == funcFLEX.FuncFLEXTokens.VARS) vars = Var();
		advance(funcFLEX.FuncFLEXTokens.BEGIN, "BEGIN MUST BE FOUND IN METHOD BLOCK");
		return new Method(methodTitle, params, vars, Commands());
	}

	private List<Command> Commands() throws Exception {
		final List<Command> cmds = new ArrayList<Command>();
		if (symb == funcFLEX.FuncFLEXTokens.SEMI) {
			lex();
		}
		cmds.add(Command());
		if (symb == funcFLEX.FuncFLEXTokens.EOF)
			advance(funcFLEX.FuncFLEXTokens.EOF, "END OF FILE EXPECTED");
		else
			cmds.addAll(Commands());
		return cmds;
	}

	private Command Command() throws Exception {
		if (symb == funcFLEX.FuncFLEXTokens.ID)
			return AssignCommand();
		else if (symb == funcFLEX.FuncFLEXTokens.METHOD) {
			currentBlocks.add(symb);
			return new NewMethod(Method()); // New Method
		} else if ((symb == funcFLEX.FuncFLEXTokens.ENDMETHOD && currentBlocks.get(currentBlocks.size() - 1) == funcFLEX.FuncFLEXTokens.METHOD)) {
			final IdentExp e = new IdentExp(getStr());
			advance(symb, "END OF SECTION EXPECTED");
			lex();
			currentBlocks.remove(currentBlocks.size() - 1);
			return new PrintEndCmd(e);
		} else if (symb == funcFLEX.FuncFLEXTokens.IF) {
			currentBlocks.add(symb);
			lex();
			return IfCommand();
		} else if (symb == funcFLEX.FuncFLEXTokens.WHILE) {
			currentBlocks.add(symb);
			lex();
			return WhileCommand();
		} else if (symb == funcFLEX.FuncFLEXTokens.WRITE) {
			lex();
			return new WriteCmd(Exp());
		} else if (symb == funcFLEX.FuncFLEXTokens.RETURN) {
			lex();
			return ReturnCommand();
		} else if (symb == funcFLEX.FuncFLEXTokens.READ) {
			lex();
			return InputCommand();
		}
		throw new Exception("Parser error: not Command: " + symb);
	}

	private Command ReturnCommand() throws Exception {
		final Exp e = Exp();
		lex();
		return new ReturnCmd(e);
	}

	private Command AssignCommand() throws Exception {
		final Exp title = Exp();
		advance(funcFLEX.FuncFLEXTokens.ASSIGN, "ASSIGN MUST BE FOUND IN ASSIGN BLOCK");
		final Exp rTitle = Exp();
		lex();
		return new AssignCmd(title, rTitle);
	}

	private Command IfCommand() throws Exception {
		final Exp cond = CondExp();
		advance(funcFLEX.FuncFLEXTokens.THEN, "THEN MUST BE FOUND IF ASSIGN BLOCK");
		ArrayList<Command> block = new ArrayList<Command>();
		final Command i = Command();
		if (symb == funcFLEX.FuncFLEXTokens.SEMI)
			lex();
		block.add(i);
		while (symb != funcFLEX.FuncFLEXTokens.ENDIF && symb != funcFLEX.FuncFLEXTokens.ELSE) {
			final Command newCmd = Command();
			if (symb == funcFLEX.FuncFLEXTokens.SEMI)
				lex();
			block.add(newCmd);
		}
		if (symb == funcFLEX.FuncFLEXTokens.ELSE) {
			ArrayList<Command> block2 = new ArrayList<Command>();
			lex();
			final Command i2 = Command();
			if (symb == funcFLEX.FuncFLEXTokens.SEMI)
				lex();
			block2.add(i2);
			while (symb != funcFLEX.FuncFLEXTokens.ENDIF) {
				final Command newCmd2 = Command();
				if (symb == funcFLEX.FuncFLEXTokens.SEMI)
					lex();
				block2.add(newCmd2);
			}
			final IdentExp e = new IdentExp(getStr());
			advance(symb, "END OF SECTION EXPECTED");
			lex();
			currentBlocks.remove(currentBlocks.size() - 1);
			Command end = new PrintEndCmd(e);
			return new IfCmd(cond, block, block2, end);
		} 
		final IdentExp e = new IdentExp(getStr());
		advance(symb, "END OF SECTION EXPECTED");
		currentBlocks.remove(currentBlocks.size() - 1);
		Command end = new PrintEndCmd(e);
		return new IfCmd(cond, block, null, end);
	}

	private Command WhileCommand() throws Exception {
		final Exp cond = CondExp();
		advance(funcFLEX.FuncFLEXTokens.BEGIN, "BEGIN MUST BE FOUND IN WHILE BLOCK");
		ArrayList<Command> block = new ArrayList<Command>();
		final Command cmd = Command();
		if (symb == funcFLEX.FuncFLEXTokens.SEMI)
			lex();
		block.add(cmd);
		while (symb != funcFLEX.FuncFLEXTokens.ENDWHILE) {
			final Command newCmd = Command();
			if (symb != funcFLEX.FuncFLEXTokens.ENDWHILE && symb == funcFLEX.FuncFLEXTokens.SEMI)
				lex();
			block.add(newCmd);
		}
		final IdentExp e = new IdentExp(getStr());
		advance(symb, "END OF SECTION EXPECTED");
		currentBlocks.remove(currentBlocks.size() - 1);
		Command end = new PrintEndCmd(e);
		return new WhileCmd(cond, block, end);
	}

	private Command InputCommand() throws Exception {
		final String name = getStr();
		advance(funcFLEX.FuncFLEXTokens.ID, "ID MUST BE FOUND IN READ BLOCK");
		lex();
		return new InputCmd(name);
	}

	private Exp CondExp() throws Exception {
		final funcFLEX.FuncFLEXTokens a = symb;
		final Set<funcFLEX.FuncFLEXTokens> valid = new HashSet<funcFLEX.FuncFLEXTokens>();
		valid.add(funcFLEX.FuncFLEXTokens.LESS);
		valid.add(funcFLEX.FuncFLEXTokens.LESSEQ);
		valid.add(funcFLEX.FuncFLEXTokens.EQ);
		valid.add(funcFLEX.FuncFLEXTokens.NEQ);
		if (valid.contains(symb)) {
			final String op = a.toString();
			lex();
			final List<Exp> params = Param();
			return new OpExp(op, params.get(0), params.get(1));
		}
		throw new Exception("Not valid operator");
	}

	private List<Exp> Var() throws Exception {
		final List<Exp> valid = new ArrayList<Exp>();
		advance(funcFLEX.FuncFLEXTokens.VARS, "VARS MUST BE FOUND VARIABLE REQUEST");
		valid.add(getParam());
		while (symb == funcFLEX.FuncFLEXTokens.COMMA) {
			lex();
			valid.add(getParam());
		}
		return valid;
	}

	private List<Exp> Param() throws Exception {
		final List<Exp> valid = new ArrayList<Exp>();
		advance(funcFLEX.FuncFLEXTokens.LBRA, "LBRA MUST BE FOUND PARAM REQUEST");
		if (symb == funcFLEX.FuncFLEXTokens.RBRA) {
			lex();
			return valid;
		} else {
			if (symb == funcFLEX.FuncFLEXTokens.ID | symb == funcFLEX.FuncFLEXTokens.INT) {
				valid.add(getParam());
				while (symb == funcFLEX.FuncFLEXTokens.COMMA) {
					lex();
					valid.add(getParam());
				}
				lex();
			} else {
				final String v = symb.toString();
				lex();
				valid.add(paramExp(v));
				lex();
			}
			return valid;
		}
	}

	private Exp getParam() throws Exception {
		if (symb == funcFLEX.FuncFLEXTokens.ID) {
			final String v = getStr();
			lex();
			return (symb == funcFLEX.FuncFLEXTokens.LBRA) ? paramExp(v) : new IdentExp(v);
		} else if (symb == funcFLEX.FuncFLEXTokens.INT) {
			final NumberExp id = new NumberExp(getInt());
			lex();
			return id;
		}
			throw new Exception("PARAMETER TYPE IS UNREOGNISED IN getParam()");
	}

	private OpExp paramExp(final String v) throws Exception {
		final List<Exp> params = Param();
		return params.isEmpty() ? new OpExp(v, null, null)
				: params.size() == 1 ? new OpExp(v, params.get(0), null) : new OpExp(v, params.get(0), params.get(1));
	}

	
	private Exp Exp() throws Exception {
		if (symb == funcFLEX.FuncFLEXTokens.PLUS) {
			final String s = getStr();
			lex();
			final List<Exp> params = Param();
			return new OpExp(s, params.get(0), params.get(1));
		}
		return Term();
	}

	private Exp Term() throws Exception {
		while (symb == funcFLEX.FuncFLEXTokens.TIMES || symb == funcFLEX.FuncFLEXTokens.DIVIDE) {
			final String v = getStr();
			lex();
			final List<Exp> params = Param();
			return new OpExp(v, params.get(0), params.get(1));
		}
		return Factor();
	}

	private Exp Factor() throws Exception {
		if (symb == funcFLEX.FuncFLEXTokens.MINUS) {
			final String v = getStr();
			lex();
			return (symb == funcFLEX.FuncFLEXTokens.LBRA) ? paramExp(v) : new NegExp(Base());
		}
		return Base();
	}

	private Exp Base() throws Exception {
		final funcFLEX.FuncFLEXTokens a = symb;
		final String v = getStr();
		lex();
		if (symb == funcFLEX.FuncFLEXTokens.LBRA) {
			return paramExp(v);
		}
		switch (a) {
		case INT:
			return new NumberExp(Integer.parseInt(v));
		case ID:
			return new IdentExp(v);
		default:
			throw new Exception("Parsing : base: int/ident/sym expected but " + symb + " found");
		}
	}

}
