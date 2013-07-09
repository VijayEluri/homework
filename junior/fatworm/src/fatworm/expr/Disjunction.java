/**
 * 
 */
package fatworm.expr;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.scan.Scan;

/**
 * @author mrain
 *
 */
public class Disjunction extends Predicate {
	Predicate left, right;
	
	public Disjunction(Predicate l, Predicate r) {
		this.left = l;
		this.right = r;
	}
	
	@Override
	public void consumeFields(Schema schema) {
		left.consumeFields(schema);
		right.consumeFields(schema);
	}

	@Override
	public void scan(Transaction tx) {
		left.scan(tx);
		right.scan(tx);
	}
	
	@Override
	public void consume(Scan scan) {
		left.consume(scan);
		right.consume(scan);
	}
}
