/**
 * 
 */
package fatworm.value;

import java.nio.ByteBuffer;

/**
 * TypeSpecifier: 3
 * @author mrain
 *
 */
public class Varchar extends Value {
	public String value;
	
	public Varchar(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "'" + value + "'";
	}

	@Override
	public int length() {
		return 5 + value.length();
	}

	@Override
	public byte[] toByteArray() {
		char[] tmp = value.toCharArray();
		ByteBuffer bb = ByteBuffer.allocate(length());
		bb.put((byte) 3);
		bb.putInt(value.length());
		for (int i = 0; i < tmp.length; ++ i)
			bb.put((byte) tmp[i]);
		return bb.array();
	}

	@Override
	public Int castInt() {
		return new Int(Integer.parseInt(value));
	}

	@Override
	public Bool castBool() {
		if (value.toUpperCase() == "FALSE") return new Bool(false);
		return new Bool(true);
	}

	@Override
	public Varchar castString() {
		return this;
	}

	@Override
	public FloatNumber castFloat() {
		return new FloatNumber(Float.parseFloat(value));
	}

	public Time castTime() {
		return new Time(value);
	}

	@Override
	public Value add(Value arg) {
		if (arg instanceof FloatNumber)
			return this.castFloat().add(arg);
		return this.castInt().add(arg);
	}

	@Override
	public Value subtract(Value arg) {
		if (arg instanceof FloatNumber)
			return this.castFloat().subtract(arg);
		return this.castInt().subtract(arg);
	}

	@Override
	public Value multiply(Value arg) {
		if (arg instanceof FloatNumber)
			return this.castFloat().multiply(arg);
		return this.castInt().multiply(arg);
	}

	@Override
	public Value divide(Value arg) {
		if (arg instanceof FloatNumber)
			return this.castFloat().divide(arg);
		return this.castInt().divide(arg);
	}

	@Override
	public Value mod(Value arg) {
		if (arg instanceof FloatNumber)
			return this.castFloat().mod(arg);
		return this.castInt().mod(arg);
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof Varchar)
			return value.compareTo(o.castString().value);
		return castFloat().compareTo(o.castFloat());
	}

	@Override
	public Object getObject() {
		return value;
	}

	@Override
	public int getType() {
		return java.sql.Types.VARCHAR;
	}

	@Override
	public int strictCompare(Value another) {
		if (another instanceof Null)
			return 1;
		if (!(another instanceof Varchar))
			throw new RuntimeException("Compare to different type");
		return value.compareTo(((Varchar)another).value);
	}

}
