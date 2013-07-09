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
public class Update extends Action {
	public String table;
	public List<String> colname;
	public List<Object> values;
	public Expr filter;
	
	public Update(String table, List<String> colname, List<Object> values, Expr filter) {
		this.table = table.toUpperCase();
		this.colname = colname;
		this.values = values;
		this.filter = filter;
	}
}
