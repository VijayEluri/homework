/**
 * 
 */
package fatworm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author mrain
 *
 */
public class Schema {
	ArrayList<ColumnType> columns;
	ArrayList<String> names;
	Map<String, Integer> map;

	public Schema() {
		columns = new ArrayList<ColumnType>();
		names = new ArrayList<String>();
		map = new HashMap<String, Integer>();
	}
	
	public Schema(Schema schema) {
		columns = new ArrayList<ColumnType>();
		names = new ArrayList<String>();
		map = new HashMap<String, Integer>();
		columns.addAll(schema.columns);
		names.addAll(schema.names);
		map.putAll(schema.map);
	}
	
	public Schema(String name, Schema schema) {
		columns = new ArrayList<ColumnType>();
		names = new ArrayList<String>();
		map = new HashMap<String, Integer>();
		columns.addAll(schema.columns);
		for (String fld : schema.names)
			names.add(name + "." + fld);
		for (Entry<String, Integer> entry : schema.map.entrySet())
			map.put(name + "." + entry.getKey(), entry.getValue());
	}
	
	public String toString() {
		String a = "";
		for (int i = 0; i < names.size(); ++ i) {
			a += names.get(i);
			String t = columns.get(i).toString();
			a += ":{" + t.length() + "," + t + "}"; 
		}
		return a;
	}
	
	public Schema(String meta) {
		columns = new ArrayList<ColumnType>();
		names = new ArrayList<String>();
		map = new HashMap<String, Integer>();
		int p = meta.indexOf(':');
		while (p != -1) {
			String name = meta.substring(0, p);
			meta = meta.substring(p + 2);
			p = meta.indexOf(',');
			int l = Integer.parseInt(meta.substring(0, p));
			String colmeta = meta.substring(p + 1, p + l + 1);
			put(name, new ColumnType(colmeta));
			//System.out.println(name);
			meta = meta.substring(p + l + 2);
			p = meta.indexOf(':');
		}
	}
	
	public boolean hasColumn(String name) {
		return map.containsKey(name);
	}
	
	public ColumnType getColumnType(int id) {
		return columns.get(id);
	}
	
	public ColumnType getColumnType(String fld) {
		return columns.get(map.get(fld));
	}
	
	public String getColumnName(int index) {
		return names.get(index);
	}
	
	public void put(String name, ColumnType type) {
		map.put(name, columns.size());
		columns.add(type);
		names.add(name);
	}
	
	public int getIndex(String fld) {
		if (!map.containsKey(fld))
			return map.get(fld.substring(fld.indexOf(".") + 1));
		return map.get(fld);
	}
	
	public void putAll(Schema schema) {
		for (int i = 0; i < schema.names.size(); ++ i) {
			put(schema.names.get(i), schema.columns.get(i));
		}
	}
	
	public Collection<String> getColumnNames() {
		return names;
	}

	public int getColumnCount() {
		return columns.size();
	}

	public void renameTable(String alias) {
		ArrayList<String> oldnames = names;
		ArrayList<ColumnType> oldcolumns = columns;

		columns = new ArrayList<ColumnType>();
		names = new ArrayList<String>();
		map.clear();
		
		for (int i = 0; i < oldnames.size(); ++ i) {
			String name = oldnames.get(i);
			if (name.contains(".")) {
				name = alias + "." + name.substring(name.indexOf('.') + 1);
				put(name, oldcolumns.get(i));
			} else put(alias + "." + name, oldcolumns.get(i));
		}
	}
	
	public String getTable(String fld) {
		String table = "";
		for (String col : names) {
			int ind = col.indexOf('.');
			if (ind == -1) continue;
			String t = col.substring(0, ind);
			if (col.substring(ind + 1).compareTo(fld) == 0) {
				table = t;
			//	break;
			}
		}
		return table;
	}
}
