package fatworm.scan;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fatworm.db.Transaction;
import fatworm.expr.Expr;
import fatworm.stmt.UpdatePair;
import fatworm.value.Value;


public class UpdateScan extends Scan {
	Transaction tx;
	TableScan src;
	List<UpdatePair> actions;
	Expr expr;
	
	public UpdateScan(Transaction tx, String table, List<UpdatePair> actions, Expr expr) {
		this.tx = tx;
		this.expr = expr;
		this.actions = actions;
		src = new TableScan(table, tx);
		if (expr != null)
			expr.consumeFields(src.getSchema());
		

		for (UpdatePair pair : this.actions) {
			pair.expr.consumeFields(src.getSchema());
		}
	}
	
	@Override
	public boolean next() {
		if (!src.next()) return false;
		if (expr != null) {
			expr.consume(src);
			while (!expr.getValue().castBool().value) {
				if (!src.next()) return false;
				expr.consume(src);
			}
		}
		try {
			src.update(actions);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void beforeFirst() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Value getValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value getValue(String fld) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void consume(Scan scan) {
		// TODO Auto-generated method stub
		
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
