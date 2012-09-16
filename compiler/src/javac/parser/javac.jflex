/* JFlex example: part of Java language lexer specification */
//import java_cup.runtime.*;
package javac.parser;

/**
 * This class is a simple example lexer.
 */
%%
%class Yylex
%unicode
%line
%column
%cup
%implements Symbols
%{
    private StringBuffer string = new StringBuffer();
    private int commentsCount = 0;

    private void err(String message) {
        System.err.println(String.format("Scanning error in line %d, column %d: %s", yyline + 1, yycolumn + 1, message));
    }
    private java_cup.runtime.Symbol tok(int type) {
        return new java_cup.runtime.Symbol(type, yyline, yycolumn);
    }
    private java_cup.runtime.Symbol tok(int type, Object value) {
        return new java_cup.runtime.Symbol(type, yyline, yycolumn, value);
    }
%}
%eofval{
    {
        return tok(EOF, null);
    }
%eofval}
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
//Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

//TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
//EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
//DocumentationComment = "/**" {CommentContent} "*"+ "/"
//CommentContent       = ( [^*] | \*+ [^/*] )*

//Identifier = [:jletter:] [:jletterdigit:]*
Identifier = [_a-zA-Z][_a-zA-Z0-9]*
DecInteger = [0-9]+
//DecIntegerLiteral = 0 | [1-9][0-9]*
%state YYSTRING
%state YYCHAR
%state YYCOMMENT
%state YYLINECOMMENT

%%
 /* keywords */
<YYINITIAL> "native"             { return tok(NATIVE); }
<YYINITIAL> "record"             { return tok(RECORD); }
<YYINITIAL> "new"                { return tok(NEW); }
<YYINITIAL> "int"                { return tok(INT); }
<YYINITIAL> "string"             { return tok(STRING); }
<YYINITIAL> "char"               { return tok(CHAR); }
<YYINITIAL> "null"               { return tok(NULL); }
<YYINITIAL> "if"                 { return tok(IF); }
<YYINITIAL> "else"               { return tok(ELSE); }
<YYINITIAL> "while"              { return tok(WHILE); }
<YYINITIAL> "for"                { return tok(FOR); }
<YYINITIAL> "return"             { return tok(RETURN); }
<YYINITIAL> "break"              { return tok(BREAK); }
<YYINITIAL> "continue"           { return tok(CONTINUE); }
<YYINITIAL> {
    /* identifiers */ 
    {Identifier}                   { return tok(ID, yytext()); }

    /* literals */
    {DecInteger}                   { return tok(INTEGER, Integer.valueOf(yytext())); }
    \"                             { string.setLength(0); yybegin(YYSTRING); }
    \'                             { string.setLength(0); yybegin(YYCHAR); }
    "["{WhiteSpace}*"]"            { return tok(LRBRACKET); }
    ";"                            { return tok(SEMICOLON); }
    "["                            { return tok(LBRACKET); }
    "]"                            { return tok(RBRACKET); }
    "("                            { return tok(LPAREN); }
    ")"                            { return tok(RPAREN); }
    "{"                            { return tok(LBRACE); }
    "}"                            { return tok(RBRACE); }
    ","                            { return tok(COMMA); }


    /* operators */
    "="                            { return tok(ASSIGN); }
    "||"                           { return tok(OR); }
    "&&"                           { return tok(AND); }
    "=="                           { return tok(EQ); }
    "!="                           { return tok(NEQ); }
    "<"                            { return tok(LESS); }
    "<="                           { return tok(LESS_EQ); }
    ">"                            { return tok(GREATER); }
    ">="                           { return tok(GREATER_EQ); }
    "+"                            { return tok(PLUS); }
    "-"                            { return tok(MINUS); }
    "*"                            { return tok(MULTIPLY); }
    "/"                            { return tok(DIVIDE); }
    "%"                            { return tok(MODULO); }
    "!"                            { return tok(NOT); }
    "."                            { return tok(DOT); }

    /* comments */
    "//"                           { yybegin(YYLINECOMMENT); }
    "/*"                           { commentsCount = commentsCount + 1;
                                     yybegin(YYCOMMENT); }

    /* whitespace */
    {WhiteSpace}                   { /* ignore */ }
}
<YYSTRING> {
    \"                             { yybegin(YYINITIAL); 
                                   return tok(STRING_LITERAL, string.toString()); }
    [^\n\r\"\\]+                   { string.append( yytext() ); }
    \\t                            { string.append("\\t"); }
    \\n                            { string.append("\\n"); }

    \\r                            { string.append("\\r"); }
    \\\"                           { string.append("\\\""); }
    \\[0-9][0-9][0-9]              {
        int temporary = Integer.valueOf(yytext().substring(1));
        if (temporary > 255) err("Invalid number.");
        else string.append("\\" + temporary.toString());
    }
    \\\\                             { string.append("\\\\"); }
}
<YYCHAR> {
    \'                             {
        yybegin(YYINITIAL);
        if (string.length() != 1) err("Invalid character constant");
        else return tok(CHARACTER, string.charAt(0));
    }
    [^\n\r\'\\]+                   { string.append( yytext() ); }
    \\t                            { string.append('\t'); }
    \\n                            { string.append('\n'); }

    \\r                            { string.append('\r'); }
    \\\'                           { string.append('\''); }
    \\[0-9][0-9][0-9]              {
        int temporary = Integer.valueOf(yytext().substring(1));
        if (temporary > 255) err("Invalid number.");
        else string.append(temporary);
    }
    \\                             { string.append('\\'); }
}
<YYCOMMENT> {
    "/*"                           { commentsCount = commentsCount + 1; }
    "*/"                           { commentsCount = commentsCount - 1;
                                     if (commentsCount == 0) yybegin(YYINITIAL); }
    .|{LineTerminator}             { /*PASS COMMENT*/ }
}
<YYLINECOMMENT> {
    {LineTerminator}               { yybegin(YYINITIAL); }
    .                              { /*PASS LINE COMMENT*/ }
}
 /* error fallback */
.|\n                             { err("Illegal character <"+yytext()); System.exit(-1); }
