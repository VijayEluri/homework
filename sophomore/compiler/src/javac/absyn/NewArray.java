package javac.absyn;

import javac.type.Type;
import javac.util.Position;

public class NewArray extends Expr {
	
	public TypeSpecifier type;
	public Expr expr;
	public Type base_type;

	public NewArray(Position pos, TypeSpecifier type, Expr expr) {
		super(pos);
		this.type = type;
		this.expr = expr;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		type.accept(visitor);
		expr.accept(visitor);
		visitor.visit(this);
	}
}