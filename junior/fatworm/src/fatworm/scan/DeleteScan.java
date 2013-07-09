package fatworm.scan;

import java.io.IOException;
import java.util.List;

import fatworm.db.Transaction;
import fatworm.expr.Expr;
import fatworm.value.Value;

public class DeleteScan extends Scan {
	Transaction tx;
	Expr expr;
	TableScan src;
	
	public DeleteScan(Transaction tx, String table, Expr expr) {
		this.tx = tx;
		this.expr = expr;
		src = new TableScan(table, tx);
		expr.consumeFields(src.getSchema());
	}
	
	@Override
	public boolean next() {
		if (!src.next()) return false;
		expr.consume(src);
		try {
			while (expr.getValue().castBool().value) {
				if (!src.delete()) return false;
				expr.consume(src);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void beforeFirst() {
	}

	@Override
	public Value getValue(int index) {
		return null;
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
