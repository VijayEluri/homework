/**
 * 
 */
package fatworm.stmt;

/**
 * @author mrain
 *
 */
public class CreateDB extends Action {
	public String name;
	
	public CreateDB(String name) {
		this.name = name.toUpperCase();
	}
}
