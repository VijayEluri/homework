/**
 * 
 */
package fatworm.stmt;

/**
 * @author mrain
 *
 */
public class UseDB extends Action {
	public String name;
	
	public UseDB(String name) {
		this.name = name.toUpperCase();
	}
}
