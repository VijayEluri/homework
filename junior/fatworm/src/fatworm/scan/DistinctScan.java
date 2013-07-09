package fatworm.scan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import fatworm.expr.Expr;
import fatworm.value.Value;

public class DistinctScan extends Scan {
	Scan src;
	TreeSet<List<Value>> pool;
	public DistinctScan(Scan src) {
		this.schema = src.getSchema();
		this.src = src;
		pool = new TreeSet<List<Value>>(new MyComparator());
	}
	
	@Override
	public boolean next() {
		int cnt = src.getSchema().getColumnCount();
		if (!src.next()) return false;
		List<Value> v = new ArrayList<Value>();
		for (int i = 0; i < cnt; ++ i)
			v.add(src.getValue(i));
		while (pool.contains(v)) {
			if (!src.next()) return false;
			v.clear();
			for (int i = 0; i < cnt; ++ i)
				v.add(src.getValue(i));
		}
		pool.add(v);
		return true;
	}

	@Override
	public void beforeFirst() {
		src.beforeFirst();
		pool.clear();
	}

	@Override
	public Value getValue(int index) {
		return src.getValue(index);
	}

	@Override
	public Value getValue(String fld) {
		return src.getValue(fld);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void consume(Scan scan) {
		this.src.consume(scan);
	}

	private class MyComparator implements Comparator<List<Value>> {
		@Override
		public int compare(List<Value> arg0, List<Value> arg1) {
			for (int i = 0; i < arg0.size(); ++ i) {
				int ret = arg0.get(i).strictCompare(arg1.get(i));
				if (ret != 0)
					return ret;
			}
			return 0;
		}
		
	}

	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
		src = src.KillOrder(col, asc);
		return this;
	}

	@Override
	public Scan push() {
		src = src.push();
		return this;
	}
}
