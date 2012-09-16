package javac.absyn;

import java.util.List;

import javac.util.Position;

public class FunctionCall extends Expr {
	
	public Expr expr;
	
	public ArgumentList args;
	
	//public List<Expr> params;

	public FunctionCall(Position pos, Expr expr, ArgumentList args) {
		super(pos);
		this.expr = expr;
		this.args = args;
	}
	
	public FunctionCall(Expr expr, ArgumentList args) {
		this.expr = expr;
		this.args = args;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		expr.accept(visitor);
		if (args != null) args.accept(visitor);
		visitor.visit(this);
	}
}
