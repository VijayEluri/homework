package javac.absyn;

import javac.util.Position;

public class WhileStmt extends Stmt {
	
	public Expr cond;
	public Stmt body;

	public WhileStmt(Position pos, Expr cond, Stmt body) {
		super(pos);
		this.cond = cond;
		this.body = body;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		cond.accept(visitor);
		if (visitor instanceof FinalVisitor) ((FinalVisitor) visitor).loop_count ++;
		body.accept(visitor);
		if (visitor instanceof FinalVisitor) ((FinalVisitor) visitor).loop_count --;
		visitor.visit(this);
	}
}
