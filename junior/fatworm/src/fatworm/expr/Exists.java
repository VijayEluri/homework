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
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class Exists extends Expr {
	Select expr;
	Scan scan;
	Value value;	
	
	public Exists(Select expr) {
		this.expr = expr;
		expr.order_asc.clear();
		expr.order_colname.clear();
		this.type = java.sql.Types.BOOLEAN;
	}

	@Override
	public void consumeFields(Schema schema) {
		//expr.consume(schema);
	}

	@Override
	public void scan(Transaction tx) {
		scan = expr.scan(tx);
	}

	@Override
	public Value getValue() {
		scan.beforeFirst();
		return new Bool(scan.next());
	}

	@Override
	public void consume(Scan scan) {
		this.scan.consume(scan);
	}

	@Override
	public void fillDefault(Value v) {
	}

	@Override
	public void aggregate(Scan src) {
		// TODO;
	}

	@Override
	public boolean hasAggFn() {
		return false;
	}

	@Override
	public void stablize() {
		
	}

	@Override
	public void extractAggFn(List<Expr> collector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFields(List<String> table) {
		
	}

	@Override
	public void initValue() {
		
	}

}
