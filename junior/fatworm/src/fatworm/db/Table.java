/**
 * 
 */
package fatworm.db;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MRain
 *
 */
public class Table {
	String name, primary;
	Schema schema;
	public int offset, empty;
	public List<String> columns;
	
	public Table(String name, Schema schema, List<String> columns, String primary, int offset) {
		this.name = name;
		this.schema = schema;
		this.columns = columns;
		this.primary = primary;
		this.offset = this.empty = offset;
	}
	
	/**
	 * String = name,primary,{sch.length,schema},offset,empty,{column}
	 */
	public String toString() {
		String ret = "";
		String sch = schema.toString();
		ret += name + "," + primary + ",{" + sch.length() + "," + sch + "},";
		ret += offset + "," + empty + ",{";
		for (String s : columns)
			ret += s + ",";
		ret += "}";
		return ret;
	}
	
	public Table(String meta) {
		//System.out.println(meta);
		// resolve name
		int p = meta.indexOf(',');
		this.name = meta.substring(0, p);
		meta = meta.substring(p + 1);

		//System.out.println(meta);
		// resolve primary
		p = meta.indexOf(',');
		this.primary = meta.substring(0, p);
		meta = meta.substring(p + 2);

		//System.out.println(meta);
		// resolve schema
		p = meta.indexOf(',');
		int l = Integer.parseInt(meta.substring(0, p));
		this.schema = new Schema(meta.substring(p + 1, p + l + 1));
		meta = meta.substring(p + l + 3);

		//System.out.println(meta);
		// resolve offset
		p = meta.indexOf(',');
		this.offset = Integer.parseInt(meta.substring(0, p));
		meta = meta.substring(p + 1);

		//System.out.println(meta);
		// resolve empty
		p = meta.indexOf(',');
		this.empty = Integer.parseInt(meta.substring(0, p));
		meta = meta.substring(p + 2);

		//System.out.println(meta);
		// resolve columns
		columns = new ArrayList<String>();
		p = meta.indexOf(',');
		while (p != -1) {
			columns.add(meta.substring(0, p));
			meta = meta.substring(p + 1);
			p = meta.indexOf(',');
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasPrimary() {
		return primary.length() > 0;
	}
	
	public Schema getSchema() {
		return schema;
	}
}
