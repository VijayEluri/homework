/**
 * 
 */
package fatworm.stmt;

import java.util.List;

/**
 * @author mrain
 *
 */
public class DropTable extends Action {
	public List<String> tables;
	
	public DropTable(List<String> tables) {
		this.tables = tables;
	}

}
