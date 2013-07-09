/**
 * 
 */
package fatworm.scan;

import java.util.ArrayList;
import java.util.List;

import fatworm.db.Schema;
import fatworm.expr.BinaryOp;
import fatworm.expr.Expr;
import fatworm.value.Null;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class SelectScan extends Scan {
	Scan src;
	Expr expr;
	public boolean having = false;
	
	public SelectScan(Scan src, Expr expr) {
		this.src = src;
		this.expr = expr;
		this.schema = src.getSchema();
	}
	
	@Override
	public boolean next() {
		if (!src.next()) return false;
		expr.consume(src);
		Value ret = expr.getValue();
		while (ret instanceof Null || !ret.castBool().value) {
			if (!src.next()) return false;
			expr.consume(this);
			ret = expr.getValue();
		}
		return true;
	}

	@Override
	public Value getValue(int index) {
		return src.getValue(index);
	}

	@Override
	public void beforeFirst() {
		src.beforeFirst();
	}

	@Override
	public String toString() {
		return "Query[expr, " + src.toString() + "]";
	}

	@Override
	public Value getValue(String fld) {
		return src.getValue(fld);
	}

	@Override
	public void consume(Scan scan) {
		expr.consume(scan);
		src.consume(scan);
	}

	public void takeHaving() {
		expr.stablize();
	}

	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
		src = src.KillOrder(col, asc);
		return this;
	}

	private void extract(Expr expr, List<Expr> list) {
		if (expr instanceof BinaryOp && ((BinaryOp)expr).op.compareTo("AND") == 0) {
			extract(((BinaryOp)expr).left, list);
			extract(((BinaryOp)expr).right, list);
		} else list.add(expr);
	}
	
	private void extractScan(Scan scan, List<Scan> list) {
		if (src instanceof ProductScan) {
			extractScan(((ProductScan)scan).src1, list);
			extractScan(((ProductScan)scan).src2, list);
		} else list.add(scan);
	}
	
	@Override
	public Scan push() {
		/*List<Expr> exprs = new ArrayList<Expr>();
		List<Scan> scans = new ArrayList<Scan>();
		extract(expr, exprs);
		extractScan(src, scans);
		if (exprs.size() > 1 && scans.size() > 1) {
			while (exprs.size() > 0) {
				int i = 0;
				while (i < exprs.size())
			}
		}*/
		return this;
	}

}

