/**
 * 
 */
package fatworm.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author MRain
 *
 */
public class Transaction {
	Map<String, Database> pool;
	Database current;
	String dir;
	
	public Transaction(String dir, String meta) throws FileNotFoundException {
		this.pool = new HashMap<String, Database>();
		this.dir = dir;
		
		if (meta.length() == 0) return;
		int p = meta.indexOf(',');
		String cur_name = meta.substring(0, p);
		meta = meta.substring(p + 2);
		p = meta.indexOf(':');
		while (p != -1) {
			String name = meta.substring(0, p);
			meta = meta.substring(p + 2);
			p = meta.indexOf(',');
			int l = Integer.parseInt(meta.substring(0, p));
			pool.put(name, new Database(meta.substring(p + 1, p + l + 1), dir, 0));
			meta = meta.substring(p + l + 2);
			p = meta.indexOf(':');
		}
		if (cur_name.length() > 0)
			current = pool.get(cur_name);
		else current = null;
	}
	
	public boolean createDB(String name) throws FileNotFoundException {
		if (pool.containsKey(name)) return false;
		//System.out.println(name);
		pool.put(name, new Database(name, dir));
		return true;
	}
	
	public boolean dropDB(String name) {
		if (!pool.containsKey(name)) return false;
		Database db = pool.get(name);
		db.destroy();
		pool.remove(name);
		return true;
	}
	
	public boolean useDB(String name) {
		if (!pool.containsKey(name)) return false;
		//System.out.println(pool);
		/*for (Entry<String, Database> entry : pool.entrySet())
			System.out.println(entry.getKey() + " " + entry.getValue());*/
		current = pool.get(name);
		return true;
	}
	
	public boolean createTable(String name, Schema schema, List<String> columns, String primary) {
		if (current != null)
			return current.createTable(name, schema, columns, primary);
		else
			return false;
	}
	
	public boolean dropTable(String name) {
		if (current != null)
			return current.dropTable(name);
		else
			return false;
	}
	
	public Database getCurrent() {
		return current;
	}

	public void close() {
		String metaString = toString();
		try {
			PrintWriter pw = new PrintWriter(dir + File.separator + "meta");
			pw.println(metaString);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for (Database db : pool.values())
			db.close();
	}
	
	/**
	 * current,{dbname:{db.length,database},*}
	 */
	public String toString() {
		String curname = "";
		if (current != null) curname = current.name;
		String ret = curname + ",{";
		for (Entry<String, Database> entry : pool.entrySet()) {
			String v = entry.getValue().toString();
			ret += entry.getKey() + ":{" + v.length() + "," + v + "}"; 
		}
		ret += "}";
		//System.out.println(ret);
		return ret;
	}
}
