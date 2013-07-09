/**
 * 
 */
package fatworm.expr;

import java.util.Collection;
import java.util.List;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.scan.Scan;
import fatworm.value.Null;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class ColumnRef extends Expr {
	public String name;
	public String table;
	String field;
	Value value;
	
	public ColumnRef(String name) {
		this.name = name;
		this.table = "";
		field = name;
		this.type = java.sql.Types.INTEGER;
	}
	
	public ColumnRef(String name, String table) {
		this.name = name;
		this.table = table;
		if (table.length() > 0)
			field = table + "." + name;
		else field = name;
		this.type = java.sql.Types.INTEGER;
	}

	@Override
	public void consumeFields(Schema schema) {
		if (table.length() == 0) {
			table = schema.getTable(name);
			if (table.length() > 0)
				field = table + "." + name;
		}
		//if (!schema.hasColumn(table + "." + name))
		//	throw new RuntimeException("Column name not found");
		//System.out.println(field);
		if (schema.hasColumn(field))
			this.type = schema.getColumnType(schema.getIndex(field)).getType();
	}

	@Override
	public void scan(Transaction tx) {
	}

	@Override
	public Value getValue() {
		return value;
	}

	@Override
	public void consume(Scan scan) {
		/*if (table.length() == 0)
			throw new RuntimeException("Column name not found");*/
		//System.out.println(field);
		if (scan.getSchema().hasColumn(field))
			value = scan.getValue(field);
	}

	@Override
	public void fillDefault(Value v) {
		if (name.toUpperCase().compareTo("DEFAULT") == 0) {
			//if (v == null) throw new RuntimeException("No default value this row");
			if (v == null) value = new Null();
			else value = v;
		}
	}
	
	@Override
	public String toString() {
		if (table.length() > 0)
			return table + "." + name;
		else return name;
	}

	@Override
	public void aggregate(Scan scan) {
	}

	@Override
	public boolean hasAggFn() {
		return false;
	}

	@Override
	public void stablize() {		
	}

	@Override
	public void extractAggFn(List<Expr> collector) {		
	}

	@Override
	public void getFields(List<String> table) {
		table.add(this.table);
	}

	@Override
	public void initValue() {
		value = new Null();
	}
}
