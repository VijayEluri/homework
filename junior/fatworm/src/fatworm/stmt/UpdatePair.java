package fatworm.stmt;

import fatworm.expr.Expr;

public class UpdatePair {
	
	public String name;
	public Expr expr;
	
	public UpdatePair(String name, Expr expr) {
		this.name = name.toUpperCase();
		this.expr = expr;
	}
}
