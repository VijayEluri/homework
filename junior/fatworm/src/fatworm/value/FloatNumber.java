/**
 * 
 */
package fatworm.value;

import java.nio.ByteBuffer;

/**
 * TypeSpecifier: 2
 * @author mrain
 *
 */
public class FloatNumber extends Value {
	public float value;
	public FloatNumber(float d) {
		this.value = d;
	}
	@Override
	public String toString() {
		return new Float(value).toString();
	}
	
	@Override
	public int length() {
		return 5;
	}
	@Override
	public byte[] toByteArray() {
		ByteBuffer bb = ByteBuffer.allocate(5);
		bb.put((byte) 2);
		bb.putFloat(value);
		return bb.array();
	}
	@Override
	public Int castInt() {
		return new Int((int)value);
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
		return this;
	}
	@Override
	public Time castTime() {
		return new Time(toString());
	}
	@Override
	public Value add(Value arg) {
		return new FloatNumber(value + arg.castFloat().value);
	}
	@Override
	public Value subtract(Value arg) {
		return new FloatNumber(value - arg.castFloat().value);
	}
	@Override
	public Value multiply(Value arg) {
		return new FloatNumber(value * arg.castFloat().value);
	}
	@Override
	public Value divide(Value arg) {
		return new FloatNumber(value / arg.castFloat().value);
	}
	@Override
	public Value mod(Value arg) {
		return new FloatNumber(value % arg.castFloat().value);
	}

	@Override
	public int compareTo(Value o) {
		return Float.valueOf(value).compareTo(o.castFloat().value);
	}
	
	@Override
	public Object getObject() {
		return Float.valueOf(value);
	}
	@Override
	public int getType() {
		return java.sql.Types.FLOAT;
	}
	@Override
	public int strictCompare(Value another) {
		if (another instanceof Null)
			return 1;
		if (!(another instanceof FloatNumber))
			throw new RuntimeException("Compare to different type");
		return Float.valueOf(value).compareTo(((FloatNumber)another).value);
	}
}
