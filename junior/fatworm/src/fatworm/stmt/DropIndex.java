/**
 * 
 */
package fatworm.stmt;

/**
 * @author mrain
 *
 */
public class DropIndex extends Action {
	String table, index;
	
	public DropIndex(String table, String index) {
		this.table = table;
		this.index = index;
	}

}
