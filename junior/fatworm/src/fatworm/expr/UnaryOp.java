/**
 * 
 */
package fatworm.expr;

import java.util.List;

import fatworm.db.ColumnType;
import fatworm.db.Schema;
import fatworm.value.FloatNumber;
import fatworm.value.Int;
import fatworm.value.Null;
import fatworm.db.Transaction;
import fatworm.scan.Scan;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class UnaryOp extends Expr {
	Expr arg;
	String op;
	Value value;
	int cnt = 1;
	boolean stablize = false;
	
	public UnaryOp(String op, Expr arg) {
		this.op = op;
		this.arg = arg;
		
		if (op.compareTo("-") == 0) {
			if (arg.type == java.sql.Types.INTEGER)
				this.type = arg.type;
			else this.type = java.sql.Types.FLOAT;
		} else {
			if (op.compareTo("COUNT") == 0)
				this.type = java.sql.Types.INTEGER;
			else if ((op.compareTo("MAX") == 0 || op.compareTo("MIN") == 0) && arg.type == java.sql.Types.INTEGER)
				this.type = arg.type;
			else 
				this.type = java.sql.Types.FLOAT;
		}
	}

	@Override
	public void consumeFields(Schema schema) {
		if (stablize) return;
		arg.consumeFields(schema);
		if (op.compareTo("-") == 0) {
			if (arg.type == java.sql.Types.INTEGER)
				this.type = arg.type;
			else this.type = java.sql.Types.FLOAT;
		} else {
			if (op.compareTo("COUNT") == 0)
				this.type = java.sql.Types.INTEGER;
			else if ((op.compareTo("MAX") == 0 || op.compareTo("MIN") == 0) && arg.type == java.sql.Types.INTEGER)
				this.type = arg.type;
			else 
				this.type = java.sql.Types.FLOAT;
		}
	}

	@Override
	public void scan(Transaction tx) {
		arg.scan(tx);
	}

	@Override
	public Value getValue() {
		if (stablize) {
			return value;
		} else {
			if (op.compareTo("-") == 0) {
				Value a = arg.getValue();
				if (a instanceof FloatNumber) {
					return new FloatNumber(-a.castFloat().value);
				} else {
					return new Int(-a.castInt().value);
				}
			} else {
				//System.out.println(this + " ooop " + value);
				if (op.compareTo("AVG") == 0 && cnt > 0)
					return new FloatNumber(value.castFloat().value / cnt);
				else if (op.compareTo("COUNT") == 0)
					return new Int(cnt);
				else return value;
			}
		}
	}

	@Override
	public void consume(Scan scan) {
		if (stablize) {
			//System.out.println(toString() + " " + scan.getSchema());
			if (scan.getSchema().hasColumn(toString()))
				value = scan.getValue(toString());
		} else {
			arg.consume(scan);
			//value = new ColumnType(this.type).parse(arg.getValue());
			//value = ;
			value = arg.getValue();
			//value = Value.parse(arg);
			if (op != "COUNT")
				value = new ColumnType(this.type).parse(value);
			cnt = 1;
		}
	}

	@Override
	public void fillDefault(Value v) {
		arg.fillDefault(v);
	}
	
	@Override
	public String toString() {
		if (op.compareTo("-") == 0)
			return op + arg.toString();
		else return op + "(" + arg.toString() + ")";
	}

	@Override
	public void aggregate(Scan scan) {
		if (op.compareTo("-") == 0) {
			arg.aggregate(scan);
		} else {
			arg.consume(scan);
			
			Value v = arg.getValue();
			//System.out.println(this + " " + value + " " + v);
			if (v instanceof Null)
				return;
			if (op.compareTo("COUNT") == 0) {
				++ cnt;
			} else if (op.compareTo("MAX") == 0) {
				//Value v = arg.getValue();
				if (v instanceof Int)
					((Int)value).value = Math.max(((Int)value).value, ((Int)v).value);
				else 
					((FloatNumber)value).value = Math.max(((FloatNumber)value).value, ((FloatNumber)v).value);
				//now.value = Math.max(v.value, now.value);
			} else if (op.compareTo("MIN") == 0) {
				//Value v = arg.getValue();
				if (v instanceof Int)
					((Int)value).value = Math.min(((Int)value).value, ((Int)v).value);
				else 
					((FloatNumber)value).value = Math.min(((FloatNumber)value).value, ((FloatNumber)v).value);
			} else if (op.compareTo("SUM") == 0) {
				value = value.add(v).castFloat();
			} else {
				//Float v = arg.getValue().castFloat();
				FloatNumber now = value.castFloat();
				if (op.compareTo("AVG") == 0) {
					++ cnt;
					now.value += v.castFloat().value;
				}
				this.value = now;
			}
			//System.out.println(this + " " + value + " " + v);
		}
	}

	@Override
	public boolean hasAggFn() {
		return op.compareTo("-") != 0;
	}

	@Override
	public void stablize() {
		this.stablize = true;
	}

	@Override
	public void extractAggFn(List<Expr> collector) {
		if (op.compareTo("-") == 0)
			arg.extractAggFn(collector);
		else
			collector.add(new UnaryOp(op, new ColumnRef(((ColumnRef)arg).name, ((ColumnRef)arg).table)));
	}

	@Override
	public void getFields(List<String> table) {
		if (op.compareTo("-") == 0)
			arg.getFields(table);
	}

	@Override
	public void initValue() {
		cnt = 0;
		value = new Null();
	}

	
}
