/**
 * 
 */
package fatworm.scan;

import java.util.List;

import fatworm.db.Schema;
import fatworm.expr.Expr;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class ProductScan extends Scan {
	Scan src1, src2;

	public ProductScan(Scan src1, Scan src2) {
		this.src1 = src1;
		this.src2 = src2;
		this.schema = new Schema(src1.getSchema());
		schema.putAll(src2.getSchema());
		src1.next();
	}
	
	@Override
	public boolean next() {
		if (!src2.next()) {
			if (!src1.next()) return false;
			src2.beforeFirst();
			return src2.next();
		} else return true;
	}

	@Override
	public Value getValue(int index) {
		int l = src1.getSchema().getColumnCount();
		if (index < l)
			return src1.getValue(index);
		else return src2.getValue(index - l);
	}

	@Override
	public void beforeFirst() {
		src1.beforeFirst();
		src1.next();
		src2.beforeFirst();
	}

	@Override
	public String toString() {
		return "[" + src1.toString() + "] * [" + src2.toString() + "]";
	}

	@Override
	public Value getValue(String fld) {
		return getValue(schema.getIndex(fld));
	}

	@Override
	public void consume(Scan scan) {
		src1.consume(scan);
		src2.consume(scan);
	}

	@Override
	public Scan KillOrder(List<String> col, List<Boolean> asc) {
		return this;
	}

	@Override
	public Scan push() {
		src1 = src1.push();
		src2 = src2.push();
		return this;
	}
}
