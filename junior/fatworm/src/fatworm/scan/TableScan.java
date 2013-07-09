/**
 * 
 */
package fatworm.scan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fatworm.db.ColumnType;
import fatworm.db.Schema;
import fatworm.db.Table;
import fatworm.db.Transaction;
import fatworm.expr.Expr;
import fatworm.stmt.UpdatePair;
import fatworm.storage.RecordFile;
import fatworm.storage.RecordPage;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class TableScan extends Scan {

	Table table;
	RecordFile file;
	int start;
	RecordPage current;
	List<Value> values;
	
	public TableScan(String table, Transaction tx) {
		this.table = tx.getCurrent().get(table);
		this.file = tx.getCurrent().file;
		this.start = this.table.offset;
		this.current = null;
		this.schema = new Schema(table, this.table.getSchema());
	}
	
	public TableScan(String table, Transaction tx, String alias) {
		this.table = tx.getCurrent().get(table);
		this.file = tx.getCurrent().file;
		this.start = this.table.offset;
		this.current = null;
		this.schema = new Schema(alias, this.table.getSchema());
	}
	
	@Override
	public boolean next() {
		if (current != null && current.length() == 12) return false;
		try {
			if (current == null)
				current = file.fetchPage(start, schema);
			else
				current = file.fetchPage(current.getNext(), schema);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (current.length() == 12) return false;
		values = current.resolve();
		//System.out.println(values);
		return true;
	}

	@Override
	public Value getValue(int index) {
		//System.out.println("!" + values);
		return values.get(index);
	}

	@Override
	public void beforeFirst() {
		current = null;
	}

	@Override
	public String toString() {
		return table.getName();
	}

	@Override
	public Value getValue(String fld) {
		return values.get(schema.getIndex(fld));
	}
	
	public boolean delete() throws IOException {
		if (current == null || current.length() == 12) return false;
		
		int pos = current.getPosition();
		boolean ret = next();
		
		file.removePage(pos, table);
		
		return ret;
	}

	public void update(List<UpdatePair> actions) throws IOException {
		int oldlen = current.length();
		for (UpdatePair pair : actions) {
			String name = table.getName() + "." + pair.name;
			Expr expr = pair.expr;
			expr.consume(this);
			ColumnType type = schema.getColumnType(name);
			values.set(schema.getIndex(name), type.parse(expr.getValue()));
		}
		//System.out.println(values);
		current.acquire(values);
		int newlen = current.length();
		if (oldlen >= newlen) {
			current.commit();
		} else {
			if (table.offset == current.position)
				table.offset = file.used;
			current.position = file.used;
			file.used += current.length();
			current.commit();
			file.fixPage(current.position);
		}
		
	}

	@Override
	public void consume(Scan scan) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
		return this;
	}

	@Override
	public Scan push() {
		return this;
	}

}
