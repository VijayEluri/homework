package javac.absyn;

import javac.util.Position;

public class ForStmt extends Stmt {
	
	public Expr init, cond, step;
	public Stmt body;

	public ForStmt(Position pos, Expr init, Expr cond, Expr step, Stmt body) {
		super(pos);
		this.init = init;
		this.cond = cond;
		this.step = step;
		this.body = body;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		if (init != null) init.accept(visitor);
		if (cond != null) cond.accept(visitor);
		if (step != null) step.accept(visitor);
		if (visitor instanceof FinalVisitor) ((FinalVisitor) visitor).loop_count ++;
		body.accept(visitor);
		if (visitor instanceof FinalVisitor) ((FinalVisitor) visitor).loop_count --;
		visitor.visit(this);
	}
}
