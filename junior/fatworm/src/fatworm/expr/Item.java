/**
 * 
 */
package fatworm.expr;

import java.util.List;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.scan.Scan;
import fatworm.value.Value;

/**
 * @author MRain
 *
 */
public class Item extends Expr {
	String value;
	
	public Item(String value) {
		this.value = value;
		this.type = Value.parse(value).getType();
	}

	@Override
	public void consumeFields(Schema schema) {
	}

	@Override
	public void scan(Transaction tx) {
	}

	@Override
	public Value getValue() {
		return Value.parse(value);
	}

	@Override
	public void consume(Scan scan) {
	}

	@Override
	public void fillDefault(Value v) {
	}
	
	@Override
	public String toString() {
		return value;
	}

	@Override
	public void aggregate(Scan scan) {
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
