/**
 * 
 */
package fatworm.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fatworm.expr.Expr;
import fatworm.storage.RecordFile;
import fatworm.value.Value;

/**
 * @author MRain
 *
 */
public class Database {
	String name, dir;
	Map<String, Table> tables;
	public RecordFile file;
	
	public Database(String name, String dir) throws FileNotFoundException {
		this.name = name;
		tables = new HashMap<String, Table>();
		this.file = new RecordFile(dir + File.separator + name + ".db");
	}

	public boolean hasTable(String name) {
		return tables.containsKey(name);
	}
	
	public boolean createTable(String name, Schema schema, List<String> columns, String primary) {
		if (tables.containsKey(name)) return false;
		int offset = file.addEmptyPage();
		tables.put(name, new Table(name, schema, columns, primary, offset));
		return true;
	}
	
	public boolean dropTable(String name) {
		if (!tables.containsKey(name)) return false;
		tables.remove(name);
		return true;
	}
	
	public Table get(String name) {
		return tables.get(name);
	}

	public void clearTable(String table) throws IOException {
		Table t = get(table);
		file.mkEmptyPage(t.empty);
		t.offset = t.empty;
	}
	
	public void addRecord(String table, List<Value> list) throws IOException {
		if (!hasTable(table)) throw new RuntimeException("Invalid table name");
		List<Value> data = new ArrayList<Value>();
		Table t = get(table);
		Schema schema = t.getSchema();
		int index = 0, length = 0;
		for (Value v : list) {
			if (v == null) throw new RuntimeException("invalid expression");
			ColumnType type = schema.getColumnType(index);
			data.add(type.parse(v));
			length += v.length();
			++ index;
		}
		ByteBuffer bb = ByteBuffer.allocate(length);
		for (Value v : data)
			bb.put(v.toByteArray());
		int offset = file.addPage(t.empty, bb.array());
		if (t.offset == t.empty)
			t.offset = offset;
	}

	public void destroy() {
	}
	
	public void flushAll() {
		
	}
	
	/**
	 * name,filesize,{table_name:{table.length, table}, *}
	 */
	public String toString() {
		String ret = name + "," + file.length() + ",{";
		for (Entry<String, Table> entry : tables.entrySet()) {
			String v = entry.getValue().toString();
			ret += entry.getKey() + ":{" + v.length() + "," + v + "}"; 
		}
		ret += "}";
		return ret;
	}
	
	/**
	 * Construct database from meta string
	 * @param meta
	 * @param dir
	 * @param metaId unused
	 * @throws FileNotFoundException 
	 */
	public Database(String meta, String dir, int metaId) throws FileNotFoundException {
		tables = new HashMap<String, Table>();
		
		int p = meta.indexOf(',');
		this.name = meta.substring(0, p);
		meta = meta.substring(p + 1);
		p = meta.indexOf(',');
		int size = Integer.parseInt(meta.substring(0, p));
		meta = meta.substring(p + 2);
		p = meta.indexOf(':');
		while (p != -1) {
			String name = meta.substring(0, p);
			meta = meta.substring(p + 2);
			p = meta.indexOf(',');
			int l = Integer.parseInt(meta.substring(0, p));
			tables.put(name, new Table(meta.substring(p + 1, p + l + 1)));
			meta = meta.substring(p + l + 2);
			p = meta.indexOf(':');
		}
		
		this.file = new RecordFile(dir + File.separator + name + ".db", size);
	}

	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
