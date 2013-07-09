/**
 * 
 */
package fatworm.value;

import java.nio.ByteBuffer;
import java.sql.*;

/**
 * TypeSpecifier: 4
 * @author mrain
 *
 */
public class Time extends Value {
	public Timestamp value;
	
	public Time(String value) {
		this.value = Timestamp.valueOf(value);
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int length() {
		return 5 + value.toString().length();
	}

	@Override
	public byte[] toByteArray() {
		String t = value.toString();
		char[] tmp = t.toCharArray();
		ByteBuffer bb = ByteBuffer.allocate(5 + t.length());
		bb.put((byte) 4);
		bb.putInt(t.length());
		for (char c : tmp)
			bb.put((byte) c);
		return bb.array();
	}

	@Override
	public Int castInt() {
		return new Int(Integer.parseInt(value.toString()));
	}

	@Override
	public Bool castBool() {
		//if (value.toUpperCase() == "FALSE") return new Bool(false);
		return new Bool(true);
	}

	@Override
	public Varchar castString() {
		return new Varchar(value.toString());
	}

	@Override
	public FloatNumber castFloat() {
		return new FloatNumber(Float.parseFloat(value.toString()));
	}

	@Override
	public Time castTime() {
		return this;
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
		if (o instanceof Time)
			return value.compareTo(o.castTime().value);
		try {
			return value.compareTo(o.castTime().value);
		} catch (Exception e) {
			return castFloat().compareTo(o.castFloat());
		}
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public int getType() {
		return java.sql.Types.VARCHAR;
	}

	@Override
	public int strictCompare(Value another) {
		if (another instanceof Null)
			return 1;
		if (!(another instanceof Time))
			throw new RuntimeException("Compare to different type");
		return value.compareTo(((Time)another).value);
	}

}
