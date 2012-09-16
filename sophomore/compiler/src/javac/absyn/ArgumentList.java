package javac.absyn;

import javac.util.Position;
import java.util.LinkedList;
import java.util.List;

public class ArgumentList extends Node {
	public List<Expr> arguments = new LinkedList<Expr>();
	
	public ArgumentList(Position pos) {
		super(pos);
	}
	
	public ArgumentList() {
	}
	
	public void add(Expr expr) {
		arguments.add(expr);
	}
	
	@Override
	public void accept(NodeVisitor visitor) {
		for (Expr expr : arguments)
			expr.accept(visitor);
		visitor.visit(this);
	}

}
