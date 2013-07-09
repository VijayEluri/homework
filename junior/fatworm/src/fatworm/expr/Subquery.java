package fatworm.expr;

import java.util.List;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.scan.Scan;
import fatworm.stmt.Select;
import fatworm.value.Null;
import fatworm.value.Value;

public class Subquery extends Expr {
	Scan scan;
	Value v;
	
	public Subquery(Select src, Transaction tx) {
		this.scan = src.scan(tx);
		if (scan.next())
			v = scan.getValue(0);
		else v = new Null();
	}

	@Override
	public Value getValue() {
		return v;
	}

	@Override
	public void fillDefault(Value v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void aggregate(Scan scan) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasAggFn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stablize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void extractAggFn(List<Expr> collector) {
		// TODO Auto-generated method stub

	}

	@Override
	public void consumeFields(Schema schema) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scan(Transaction tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void consume(Scan scan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getFields(List<String> table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initValue() {
		// TODO Auto-generated method stub
		
	}

}
