/**
 * 
 */
package fatworm.value;

/**
 * TypeSpecifier: 1
 * @author mrain
 *
 */
public class Bool extends Value {
	public boolean value;
	
	public Bool(boolean value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		if (value) return "TRUE";
		else return "FALSE";
	}

	@Override
	public int length() {
		return 2;
	}

	@Override
	public byte[] toByteArray() {
		byte[] ret = new byte[2];
		ret[1] = (byte) (value ? 1 : 0);
		ret[0] = 1;
		return ret;
	}

	@Override
	public Int castInt() {
		return new Int(value ? 1 : 0);
	}

	@Override
	public Bool castBool() {
		return this;
	}

	@Override
	public Varchar castString() {
		return new Varchar(toString());
	}

	@Override
	public FloatNumber castFloat() {
		return new FloatNumber(value ? 1 : 0);
	}

	public Time castTime() {
		return new Time(toString());
	}

	@Override
	public Value add(Value arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value subtract(Value arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value multiply(Value arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value divide(Value arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value mod(Value arg) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean takeAnd(Bool castBool) {
		return value & castBool.value;
	}

	public boolean takeOr(Bool castBool) {
		return value | castBool.value;
	}

	@Override
	public int compareTo(Value o) {
		return castFloat().compareTo(o.castFloat());
	}

	@Override
	public Object getObject() {
		return Boolean.valueOf(value);
	}

	@Override
	public int getType() {
		return java.sql.Types.BOOLEAN;
	}

	@Override
	public int strictCompare(Value another) {
		if (another instanceof Null)
			return 1;
		if (!(another instanceof Bool))
			throw new RuntimeException("Compare to different type");
		return Boolean.valueOf(value).compareTo(((Bool)another).value);
	}

	
}
