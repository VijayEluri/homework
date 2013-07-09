/**
 * 
 */
package fatworm.stmt;

import java.util.List;

import fatworm.db.Schema;

/**
 * @author mrain
 *
 */
public class CreateTable extends Action {
	public String name;
	public Schema schema;
	public List<String> columns;
	public String primary;
	
	public CreateTable(String name, Schema schema, List<String> columns, String primary) {
		this.name = name;
		this.schema = schema;
		this.columns = columns;
		this.primary = primary;
	}

}
