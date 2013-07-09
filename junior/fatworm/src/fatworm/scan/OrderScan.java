/**
 * 
 */
package fatworm.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fatworm.db.Schema;
import fatworm.expr.Expr;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class OrderScan extends Scan {
	Scan src;
	MyComparator comp;
	List<List<Value>> pool;
	int index = -1;
	
	public OrderScan(Scan src, List<String> col, List<Boolean> asc) {
		this.schema = new Schema(src.getSchema());
		this.src = src;
		//System.out.println(src.getSchema());
		
		List<Integer> index = new ArrayList<Integer>();
		for (String s : col) {
			if (schema.hasColumn(s))
				index.add(schema.getIndex(s));
			else {
				int add = 0;
				boolean find = false;
				for (String t : schema.getColumnNames()) {
					int i = t.indexOf(".");
					//System.out.println(s + " " + t);
					if (i != -1 && s.compareTo(t.substring(i + 1)) == 0) {
						index.add(add);
						find = true;
						break;
					}
					++ add;
				}
				if (!find) 
					throw new RuntimeException("column name not found");
			}
		}
		
		comp = new MyComparator(index, asc);
		//System.out.println(this.schema);
	}
	
	@Override
	public boolean next() {
		if (pool == null) {
			pool = new ArrayList<List<Value>>();
			int cnt = src.getSchema().getColumnCount();
			while (src.next()) {
				List<Value> v = new ArrayList<Value>();
				for (int i = 0; i < cnt; ++ i)
					v.add(src.getValue(i));
				pool.add(v);
			}
			
			//System.out.println(src + " " + src.getSchema() + " " + pool);
			
			Collections.sort(pool, comp);
		}
		if (index < pool.size())
			++ index;
		return index < pool.size();
	}

	@Override
	public Value getValue(String fld) {
		return getValue(schema.getIndex(fld));
	}

	@Override
	public void beforeFirst() {
		index = -1;
	}

	@Override
	public String toString() {
		return "Order [" + src + "]";
	}

	@Override
	public Value getValue(int index) {
		return pool.get(this.index).get(index);
	}

	@Override
	public void consume(Scan scan) {
		src.consume(scan);
	}
	
	private class MyComparator implements Comparator<List<Value>> {
		public List<Integer> row;
		public List<Boolean> asc;
		public MyComparator(List<Integer> row, List<Boolean> asc) {
			this.row = row;
			this.asc = asc;
		}
		
		@Override
		public int compare(List<Value> arg0, List<Value> arg1) {
			for (int i = 0; i < row.size(); ++ i) {
				int index = row.get(i);
				boolean a = asc.get(i);
				int ret = arg0.get(index).strictCompare(arg1.get(index));
				if (ret != 0)
					return ret * (a ? 1 : -1);
			}
			return 0;
		}
		
	}

	public void prev() {
		-- index;
	}
	
	public void addFilter(Expr expr) {
		src = new SelectScan(src, expr);
	}
	
	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
		// TODO Auto-generated method stub
		//col.addAll(this.)
		int i = 0;
		while (i < comp.row.size()) {
			int pos = comp.row.get(i);
			String name = schema.getColumnName(pos);
			boolean a = comp.asc.get(i);
			
			//int pp = col.indexOf(name);
			int pp = -1, index = 0;
			for (String n : col) {
				if (n.compareTo(name) == 0) {
					pp = index;
					break;
				}
				if (n.substring(n.indexOf(".") + 1).compareTo(name) == 0) {
					pp = index;
					break;
				}
				++ index;
			}
			if (pp >= 0 && asc.get(pp) == a) {
				comp.row.remove(i);
				comp.asc.remove(i);
			} else {
				col.add(name);
				asc.add(a);
				++ i;
			}
		}
		//System.out.println(comp.row);
		//System.out.println(comp.asc);
		if (comp.row.size() > 0)
			src = src.KillOrder(col, asc);
		else return src.KillOrder(col, asc);
		return this;
	}

	@Override
	public Scan push() {
		src = src.push();
		return this;
	}

}
