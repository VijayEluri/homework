package fatworm.scan;

import java.util.List;

import fatworm.db.ColumnType;
import fatworm.db.Schema;
import fatworm.expr.*;
import fatworm.value.Value;

public class ProjectScan extends Scan {
	Scan src;
	List<Expr> expr;
	int cnt;
	
	public ProjectScan(Scan src, List<Expr> expr, List<String> column_alias) {
		this.src = src;
		this.expr = expr;
		
		this.schema = new Schema(src.getSchema());
		cnt = src.getSchema().getColumnCount();
		

		
		int index = 0;
		for (Expr e : expr) {
			String alias = column_alias.get(index ++);
			if (alias == null) {
				schema.put(e.toString(), new ColumnType(e.type));
			} else schema.put(alias, new ColumnType(e.type));
		}
		
	}
	
	@Override
	public boolean next() {
		if (!src.next()) return false;
		for (Expr e : expr)
			e.consume(src);
		return true;
	}
	@Override
	public void beforeFirst() {
		src.beforeFirst();
	}
	@Override
	public Value getValue(int index) {
		//System.out.println(index);
		if (index < cnt)
			return src.getValue(index);
		else {
			Expr e = expr.get(index - cnt);
			//System.out.println(index + " " + e + " " + e.getClass());
			return e.getValue();
		}
	}

	@Override
	public String toString() {
		return "rows <- [" + src.toString() + "]";
	}

	@Override
	public Value getValue(String fld) {
		try {
			return getValue(schema.getIndex(fld));
		} catch (Exception e) {
			return getValue(schema.getIndex(fld.substring(fld.indexOf(".") + 1)));
		}
	}

	@Override
	public void consume(Scan scan) {
		src.consume(scan);
		for (Expr e : expr)
			e.consume(scan);
	}
	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
		return this;
	}

	@Override
	public Scan push() {
		src = src.push();
		return this;
	}

}
