/**
 * 
 */
package fatworm.stmt;


/**
 * @author mrain
 *
 */
public class CreateUniqueIndex extends Action {
String index, table, column;
	
	public CreateUniqueIndex(String table, String index, String column) {
		this.index = index;
		this.table = table;
		this.column = column;
	}
}
