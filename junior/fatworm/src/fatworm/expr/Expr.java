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
 * @author mrain
 *
 */
public abstract class Expr extends Predicate {
	public abstract Value getValue();

	public abstract void fillDefault(Value v);
	//public abstract boolean hasTableField(String table);

	public abstract void aggregate(Scan scan);
	
	public abstract boolean hasAggFn();

	public abstract void stablize();

	public abstract void extractAggFn(List<Expr> collector);
	
	public abstract void getFields(List<String> table);

	public abstract void initValue();
}
