/**
 * 
 */
package fatworm.stmt;

import java.util.List;

import fatworm.expr.Expr;

/**
 * @author mrain
 *
 */
public class InsertValues extends Action {
	public String table;
	public List<String> columns;
	public List<Expr> values;
	
	public InsertValues(String table, List<String> columns, List<Expr> values) {
		this.table = table.toUpperCase();
		this.columns = columns;
		this.values = values;
	}
}
