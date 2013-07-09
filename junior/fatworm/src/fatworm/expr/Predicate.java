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
public abstract class Predicate {
	public abstract void consumeFields(Schema schema);
	public abstract void scan(Transaction tx);
	public abstract void consume(Scan scan);
	
	public int type;
}
