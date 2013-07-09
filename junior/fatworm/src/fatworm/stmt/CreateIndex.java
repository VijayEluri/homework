/**
 * 
 */
package fatworm.stmt;


/**
 * @author mrain
 *
 */
public class CreateIndex extends Action {
	String index, table, column;
	
	public CreateIndex(String table, String index, String column) {
		this.index = index;
		this.table = table;
		this.column = column;
	}
}
