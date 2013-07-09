/**
 * 
 */
package fatworm.expr;

import java.util.List;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.scan.Scan;
import fatworm.value.Bool;
import fatworm.value.Null;
import fatworm.value.Value;

/**
 * @author mrain
 *
 */
public class BinaryOp extends Expr {
	public Expr left, right; 
	public String op;
	Value value;
	
	public BinaryOp(String op, Expr left, Expr right) {
		this.op = op;
		this.left = left;
		this.right = right;
		
		switch (op.charAt(0)) {
			case 'O': case 'A': case '=': case '<': case '>':
				this.type = java.sql.Types.BOOLEAN;
				break;
			
			case '+': case '-': case '*': case '%':
				if (left.type == java.sql.Types.FLOAT
					|| right.type == java.sql.Types.FLOAT)
					this.type = java.sql.Types.FLOAT;
				else
					this.type = java.sql.Types.INTEGER;
				break;
				
			case '/':
				this.type = java.sql.Types.FLOAT;
		}
	}

	@Override
	public void consumeFields(Schema schema) {
		left.consumeFields(schema);
		right.consumeFields(schema);
		switch (op.charAt(0)) {
			case 'O': case 'A': case '=': case '<': case '>':
				this.type = java.sql.Types.BOOLEAN;
				break;
			
			case '+': case '-': case '*': case '/': case '%':
				if (left.type == java.sql.Types.FLOAT
					|| right.type == java.sql.Types.FLOAT)
					this.type = java.sql.Types.FLOAT;
				else
					this.type = java.sql.Types.INTEGER;
				break;
		}
	}

	@Override
	public void scan(Transaction tx) {
		left.scan(tx);
		right.scan(tx);
	}

	@Override
	public Value getValue() {
		Value l = left.getValue(), r = right.getValue();

		//System.out.println(l.toString() + " " + op + " " + r.toString());
		if (l instanceof Null || r instanceof Null)
			return new Null();
		if (op.compareTo("OR") == 0) {
			return l.or(r);
		} else if (op.compareTo("AND") == 0) {
			return l.and(r);
		} else if (op.compareTo("+") == 0) {
			//System.out.println(left + " " + right);
			//System.out.println(l.toString() + " " + op + " " + r.toString());
			return l.add(r);
		} else if (op.compareTo("-") == 0) {
			return l.subtract(r);
		} else if (op.compareTo("*") == 0) {
			return l.multiply(r);
		} else if (op.compareTo("/") == 0) {
			return l.divide(r);
		} else if (op.compareTo("%") == 0) {
			return l.mod(r);
		} else {
			int result = l.compareTo(r);
			//System.out.println(l + " " + r + " " + result);
			if (op.compareTo("=") == 0) {
				return new Bool(result == 0);
			} else if (op.compareTo("<") == 0) {
				return new Bool(result == -1);
			} else if (op.compareTo(">") == 0) {
				return new Bool(result == 1);
			} else if (op.compareTo("<=") == 0) {
				return new Bool(result <= 0);
			} else if (op.compareTo(">=") == 0) {
				return new Bool(result >= 0);
			} else if (op.compareTo("<>") == 0) {
				return new Bool(result != 0);
			}
		}
		return null;
	}

	@Override
	public void consume(Scan scan) {
		left.consume(scan);
		right.consume(scan);
	}

	@Override
	public void fillDefault(Value v) {
		left.fillDefault(v);
		right.fillDefault(v);
	}
	
	@Override
	public String toString() {
		return left.toString() + op + right.toString();
	}

	@Override
	public void aggregate(Scan scan) {
		left.aggregate(scan);
		right.aggregate(scan);
	}

	@Override
	public boolean hasAggFn() {
		return left.hasAggFn() || right.hasAggFn();
	}

	@Override
	public void stablize() {
		left.stablize();
		right.stablize();
	}

	@Override
	public void extractAggFn(List<Expr> collector) {
		left.extractAggFn(collector);
		right.extractAggFn(collector);
	}

	@Override
	public void getFields(List<String> table) {
		left.getFields(table);
		right.getFields(table);
	}

	@Override
	public void initValue() {
		left.initValue();
		right.initValue();
	}
}
