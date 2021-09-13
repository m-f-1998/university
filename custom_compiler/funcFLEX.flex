package func.compiler;

import java.io.*;

%%

%class funcFLEX

%{
  enum FuncFLEXTokens{
    ASSIGN, BEGIN, COMMA, DIVIDE,
    ELSE, ENDIF, ENDMETHOD, ENDWHILE, EOF, EQ,
    ID, IF, INT, LBRA, LESS, LESSEQ, METHOD, MINUS,
    NEQ, PLUS, RBRA, READ, RETURN, SEMI,
    THEN, TIMES, VARS, WHILE, WRITE
  }

%}

DIGIT = [0-9]
IDENT = [a-zA-Z][A-Za-z0-9]*

%%

"DIVIDE"  { return FuncFLEXTokens.DIVIDE;}
"divide"  { return FuncFLEXTokens.DIVIDE;}

"EQ"  { return FuncFLEXTokens.EQ;}
"eq"  { return FuncFLEXTokens.EQ;}

"LESS"  { return FuncFLEXTokens.LESS;}
"less"  { return FuncFLEXTokens.LESS;}

"LESSEQ"  { return FuncFLEXTokens.LESSEQ;}
"lessEq"  { return FuncFLEXTokens.LESSEQ;}
"lesseq"  { return FuncFLEXTokens.LESSEQ;}

"NEQ"  { return FuncFLEXTokens.NEQ;}
"nEq"  { return FuncFLEXTokens.NEQ;}
"neq"  { return FuncFLEXTokens.LESSEQ;}

"PLUS"  {return FuncFLEXTokens.PLUS;}
"plus"  {return FuncFLEXTokens.PLUS;}

"TIMES"  {return FuncFLEXTokens.TIMES;}
"times"  {return FuncFLEXTokens.TIMES;}

"MINUS"  { return FuncFLEXTokens.MINUS;}
"minus"  { return FuncFLEXTokens.MINUS;}

"IF"  { return FuncFLEXTokens.IF;}
"if"  { return FuncFLEXTokens.IF;}

"METHOD"  { return FuncFLEXTokens.METHOD;}
"method"  { return FuncFLEXTokens.METHOD;}

"READ"  { return FuncFLEXTokens.READ;}
"read"  { return FuncFLEXTokens.READ;}

"RETURN"  { return FuncFLEXTokens.RETURN;}
"return"  { return FuncFLEXTokens.RETURN;}

"THEN"  { return FuncFLEXTokens.THEN;}
"then"  { return FuncFLEXTokens.THEN;}

"VARS"  { return FuncFLEXTokens.VARS;}
"vars"  { return FuncFLEXTokens.VARS;}

"WHILE"  { return FuncFLEXTokens.WHILE;}
"while"  { return FuncFLEXTokens.WHILE;}

"WRITE"  { return FuncFLEXTokens.WRITE;}
"write"  { return FuncFLEXTokens.WRITE;}

"BEGIN"  { return FuncFLEXTokens.BEGIN;}
"begin"  { return FuncFLEXTokens.BEGIN;}

"ELSE"  { return FuncFLEXTokens.ELSE;}
"else"  { return FuncFLEXTokens.ELSE;}

"ENDIF"  { return FuncFLEXTokens.ENDIF;}
"endif"  { return FuncFLEXTokens.ENDIF;}

"ENDMETHOD"  { return FuncFLEXTokens.ENDMETHOD;}
"endmethod"  { return FuncFLEXTokens.ENDMETHOD;}

"ENDWHILE"  { return FuncFLEXTokens.ENDWHILE;}
"endwhile"  { return FuncFLEXTokens.ENDWHILE;}

{IDENT}  { return FuncFLEXTokens.ID;}
{DIGIT}+ { return FuncFLEXTokens.INT;}

"("  { return FuncFLEXTokens.LBRA;}
")"  { return FuncFLEXTokens.RBRA;}
";"  { return FuncFLEXTokens.SEMI;}
":="  { return FuncFLEXTokens.ASSIGN;}
","  { return FuncFLEXTokens.COMMA;}
<<EOF>>   { return FuncFLEXTokens.EOF;}

[\ \t\b\f\r\n]+ { /* eat whitespace */ }
.               { throw new Error("Unexpected character ["+yytext()+"]"); }
