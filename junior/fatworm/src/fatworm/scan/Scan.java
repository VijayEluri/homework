/**
 * 
 */
package fatworm.scan;

import java.util.List;

import fatworm.db.Schema;
import fatworm.expr.Expr;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public abstract class Scan {
	
	protected Schema schema;
	
	public Schema getSchema() {
		return schema;
	}
	
	public abstract boolean next();
	public abstract void beforeFirst();
	public abstract Value getValue(int index);
	public abstract Value getValue(String fld);
	
	public abstract String toString();

	public abstract void consume(Scan scan);
	
	public abstract Scan push();
	public abstract Scan KillOrder(List<String> col, List<Boolean> asc);
}
