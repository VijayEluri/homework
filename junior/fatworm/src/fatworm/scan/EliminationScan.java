package fatworm.scan;

import java.util.ArrayList;
import java.util.List;

import fatworm.db.Schema;
import fatworm.expr.Expr;
import fatworm.value.Value;

public class EliminationScan extends Scan {
	Scan src;
	List<Expr> columns;
	List<Integer> indices;
	
	public EliminationScan(Scan src, List<Expr> columns, List<String> alias) {
		this.src = src;
		//if (columns.size() == 1 && columns.add(e))
		this.schema = new Schema();
		this.columns = columns;
		this.indices = new ArrayList<Integer>();

		for (int i = 0; i < columns.size(); ++ i) {
			String t = alias.get(i);
			Expr e = columns.get(i);
			if (t == null) t = e.toString();
			int id = src.getSchema().getIndex(t);
			indices.add(id);
			schema.put(t, src.getSchema().getColumnType(id));
		}
	}
	
	@Override
	public boolean next() {
		return src.next();
	}

	@Override
	public void beforeFirst() {
		src.beforeFirst();
	}

	@Override
	public Value getValue(int index) {
		int id = indices.get(index);
		return src.getValue(id);
	}

	@Override
	public Value getValue(String fld) {
		return src.getValue(fld);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Eliminate [" + src + "]";
	}

	@Override
	public void consume(Scan scan) {
		src.consume(scan);
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
