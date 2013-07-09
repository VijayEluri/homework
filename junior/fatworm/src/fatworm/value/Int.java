/**
 * 
 */
package fatworm.value;

import java.nio.ByteBuffer;

/**
 * TypeSpecifier: 0
 * @author mrain
 *
 */
public class Int extends Value {
	public int value;
	public Int(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return Integer.valueOf(value).toString();
	}

	@Override
	public int length() {
		return 5;
	}

	@Override
	public byte[] toByteArray() {
		ByteBuffer bb = ByteBuffer.allocate(5);
		bb.put((byte) 0);
		bb.putInt(value);
		return bb.array();
	}

	@Override
	public Int castInt() {
		return this;
	}

	@Override
	public Bool castBool() {
		return new Bool(value != 0);
	}

	@Override
	public Varchar castString() {
		return new Varchar(toString());
	}

	@Override
	public FloatNumber castFloat() {
		return new FloatNumber(value);
	}

	@Override
	public Time castTime() {
		return new Time(toString());
	}

	@Override
	public Value add(Value arg) {
		if (arg instanceof FloatNumber) {
			return this.castFloat().add(arg);
		} else {
			return new Int(((Int)arg.castInt()).value + value);
		}
	}

	@Override
	public Value subtract(Value arg) {
		if (arg instanceof FloatNumber) {
			return this.castFloat().subtract(arg);
		} else {
			return new Int(value - ((Int)arg.castInt()).value);
		}
	}

	@Override
	public Value multiply(Value arg) {
		if (arg instanceof FloatNumber) {
			return this.castFloat().multiply(arg);
		} else {
			return new Int(value * ((Int)arg.castInt()).value);
		}
	}

	@Override
	public Value divide(Value arg) {
		//if (arg instanceof Float) {
			return this.castFloat().divide(arg);
		/*} else {
			return new Int(value / ((Int)arg.castInt()).value);
		}*/
	}

	@Override
	public Value mod(Value arg) {
		if (arg instanceof FloatNumber)
			return this.castFloat().mod(arg);
		else
			return new Int(value % ((Int)arg.castInt()).value);
	}


	@Override
	public int compareTo(Value o) {
		if (o instanceof FloatNumber)
			return castFloat().compareTo(o);
		return Integer.valueOf(value).compareTo(o.castInt().value);
	}

	@Override
	public Object getObject() {
		return Integer.valueOf(value);
	}

	@Override
	public int getType() {
		return java.sql.Types.INTEGER;
	}

	@Override
	public int strictCompare(Value another) {
		if (another instanceof Null)
			return 1;
		if (!(another instanceof Int))
			throw new RuntimeException("Compare to different type");
		return Integer.valueOf(value).compareTo(((Int)another).value);
	}

}
