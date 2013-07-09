/**
 * 
 */
package fatworm.scan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fatworm.db.ColumnType;
import fatworm.db.Schema;
import fatworm.expr.Expr;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class GroupScan extends Scan {
	Scan src;
	String group;
	List<Expr> exprs;
	Value[] ret;
	boolean first = true;
	
	public GroupScan(Scan src, String group, List<Expr> exprs, List<String> alias) {
		//this.schema = src.getSchema();
		this.src = src;
		this.group = group;
		this.exprs = new ArrayList<Expr>(exprs);
		
		if (group.length() > 0 && !group.contains(".")) {
			String table = "";
			Collection<String> collection = src.schema.getColumnNames();
			for (String col : collection) {
				int ind = col.indexOf('.');
				String t = col.substring(0, ind);
				if (col.substring(ind + 1).compareTo(group) == 0) {
					/*if (table.length() != 0)
						throw new RuntimeException("Column name not found");*/
					table = t;
				}
			}
			if (table.length() == 0)
				throw new RuntimeException("Column name not found");
			this.group = table + "." + group;
			//System.out.println(group);
		}
		
		this.schema = new Schema();
		int index = 0;
		for (Expr e : exprs) {
			String s = alias.get(index ++);
			if (s == null) {
				schema.put(e.toString(), new ColumnType(e.type));
			} else schema.put(s, new ColumnType(e.type));
		}
	}

	@Override
	public boolean next() {
		if (!src.next()) {
			if (!first || group.length() > 0)
				return false;
			else {
				first = false;
				for (Expr e : exprs)
					e.initValue();
				return true;
			}
		}
		first = false;
		if (group.length() > 0) {
			Value v = src.getValue(group);
			for (Expr e : exprs)
				e.consume(src);
			while (src.next()) {
				Value n = src.getValue(group);
				if (v.strictCompare(n) != 0) {
					((OrderScan)src).prev();
					break;
				}
				for (Expr e : exprs)
					e.aggregate(src);
			}
		} else {
			for (Expr e : exprs)
				e.consume(src);
			while (src.next()) {
				for (Expr e : exprs) {
					//System.out.println(e.getValue());
					e.aggregate(src);
					//System.out.println(e.getValue());
				}
			}
		}
		return true;
	}

	@Override
	public Value getValue(String fld) {
		return getValue(schema.getIndex(fld));
	}

	@Override
	public void beforeFirst() {
		first = true;
		src.beforeFirst();
	}

	@Override
	public String toString() {
		return "Group[" + src.toString() + "]";
	}

	@Override
	public Value getValue(int index) {
		return exprs.get(index).getValue();
	}

	@Override
	public void consume(Scan scan) {
		src.consume(scan);
	}

	public void addMeta(List<Expr> funcs) {
		// TODO Auto-generated method stub
		exprs.addAll(funcs);
		for (Expr e : funcs)
			if (!schema.hasColumn(e.toString()))
				schema.put(e.toString(), new ColumnType(e.type));
		
	}

	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
	//	src = src.KillOrder(col, asc);
		return this;
	}

	@Override
	public Scan push() {
		src = src.push();
		return this;
	}


}
