/**
 * 
 */
package fatworm.stmt;


/**
 * @author mrain
 *
 */
public class DropDB extends Action {
	public String name;
	
	public DropDB(String name) {
		this.name = name.toUpperCase();
	}
}
