package fatworm.scan;

import java.util.ArrayList;
import java.util.List;

import fatworm.db.ColumnType;
import fatworm.db.Schema;
import fatworm.expr.Expr;
import fatworm.value.Value;

public class ConstantScan extends Scan {
	
	List<Expr> exprs;
	boolean beforeFirst;
	
	public ConstantScan(List<Expr> exprs) {
		beforeFirst = true;
		
		this.exprs = new ArrayList<Expr>(exprs);
		this.schema = new Schema();
		for (Expr e : exprs) {
			schema.put(e.toString(), new ColumnType(e.type));
		}
		
	}
	@Override
	public boolean next() {
		if (beforeFirst) {
			beforeFirst = false;
			return true;
		} else 
			return false;
	}

	@Override
	public void beforeFirst() {
		beforeFirst = true;
	}

	@Override
	public Value getValue(int index) {
		return exprs.get(index).getValue();
	}

	@Override
	public Value getValue(String fld) {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public void consume(Scan scan) {
		for (Expr e : exprs)
			e.consume(scan);
	}
	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
		return this;
	}
	@Override
	public Scan push() {
		return this;
	}

}
