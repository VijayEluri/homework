/**
 * 
 */
package fatworm.stmt;

/**
 * @author MRain
 *
 */
public class InsertSubquery extends Action {
	public String table;
	public Select subquery;
	
	public InsertSubquery(String table, Select subquery) {
		this.table = table.toUpperCase();
		this.subquery = subquery;
	}
}
