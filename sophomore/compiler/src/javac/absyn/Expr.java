package javac.absyn;

import javac.type.Type;
import javac.util.Position;

public abstract class Expr extends Node {
	
	public Type ty;
	public boolean lvalue;

	public Expr(Position pos) {
		super(pos);
		lvalue = false;
	}
	public Expr() {
		lvalue = false;
	}
}
