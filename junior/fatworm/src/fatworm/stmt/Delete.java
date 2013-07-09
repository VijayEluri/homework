/**
 * 
 */
package fatworm.stmt;

import fatworm.expr.Expr;

/**
 * @author mrain
 *
 */
public class Delete extends Action {
	public String table;
	public Expr filter;
	
	public Delete(String table, Expr filter) {
		this.table = table.toUpperCase();
		this.filter = filter;
	}
}
