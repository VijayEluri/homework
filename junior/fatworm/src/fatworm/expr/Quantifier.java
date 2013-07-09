/**
 * 
 */
package fatworm.expr;

import java.util.List;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.scan.Scan;
import fatworm.stmt.Select;
import fatworm.value.Bool;
import fatworm.value.Null;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class Quantifier extends Expr {
	String quantifier;
	String op;
	Expr left;
	Select right;
	Scan scan;
	
	public Quantifier(String quantifier, String op, Expr left, Select right) {
		this.quantifier = quantifier;
		this.op = op;
		this.left = left;
		this.right = right;
		
		this.type = java.sql.Types.BOOLEAN;
	}

	@Override
	public void consumeFields(Schema schema) {
		left.consumeFields(schema);
		right.consume(schema);
	}

	@Override
	public void scan(Transaction tx) {
		left.scan(tx);
		scan = right.scan(tx);
		if (scan.getSchema().getColumnCount() != 1)
			throw new RuntimeException("Oprand must contain one column");
	}

	@Override
	public Value getValue() {
		Value s = left.getValue();
		if (s instanceof Null)
			return s;
		scan.beforeFirst();
		while (scan.next()) {
			Value t = scan.getValue(0);
			int result = s.compareTo(t);
			boolean r = false;
			if (op.compareTo("=") == 0) {
				r = (result == 0);
			} else if (op.compareTo("<") == 0) {
				r = (result == -1);
			} else if (op.compareTo(">") == 0) {
				r = (result == 1);
			} else if (op.compareTo("<=") == 0) {
				r = (result <= 0);
			} else if (op.compareTo(">=") == 0) {
				r = (result >= 0);
			} else if (op.compareTo("<>") == 0) {
				r = (result != 0);
			}
			
			if (r && quantifier.compareTo("ANY") == 0)
				return new Bool(true);
			if (!r && quantifier.compareTo("ALL") == 0)
				return new Bool(false);
		}
		if (quantifier.compareTo("ANY") == 0 || quantifier.compareTo("IN") == 0)
			return new Bool(false);
		else return new Bool(true);
		//return null;
	}

	@Override
	public void consume(Scan scan) {
		left.consume(scan);
		this.scan.consume(scan);
		// TODO
	}

	@Override
	public void fillDefault(Value v) {
	}

	@Override
	public void aggregate(Scan scan) {
		
	}

	@Override
	public boolean hasAggFn() {
		return left.hasAggFn();
	}

	@Override
	public void stablize() {
		left.stablize();
	}

	@Override
	public void extractAggFn(List<Expr> collector) {
		left.extractAggFn(collector);
	}

	@Override
	public void getFields(List<String> table) {
		
	}

	@Override
	public void initValue() {
		// TODO Auto-generated method stub
		
	}

}
