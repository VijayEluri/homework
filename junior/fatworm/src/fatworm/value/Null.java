/**
 * 
 */
package fatworm.value;

/**
 * TypeSpecifier: 10
 * @author mrain
 *
 */
public class Null extends Value {

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Value o) {
		return -2;
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#length()
	 */
	@Override
	public int length() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#toByteArray()
	 */
	@Override
	public byte[] toByteArray() {
		byte[] data = new byte[1];
		data[0] = 10;
		return data;
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#castInt()
	 */
	@Override
	public Int castInt() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#castBool()
	 */
	@Override
	public Bool castBool() {
		return new Bool(false);
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#castString()
	 */
	@Override
	public Varchar castString() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#castFloat()
	 */
	@Override
	public FloatNumber castFloat() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#castTime()
	 */
	@Override
	public Time castTime() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fatworm.value.Value#add(fatworm.value.Value)
	 */
	@Override
	public Value add(Value arg) {
		return this;
	}

	@Override
	public Value subtract(Value arg) {
		return this;
	}

	@Override
	public Value multiply(Value arg) {
		return this;
	}

	@Override
	public Value divide(Value arg) {
		return this;
	}

	@Override
	public Value mod(Value arg) {
		return this;
	}

	@Override
	public Object getObject() {
		return this;
	}
	
	public String toString() {
		return "NULL";
	}

	@Override
	public int getType() {
		return java.sql.Types.INTEGER;
	}

	@Override
	public int strictCompare(Value another) {
		if (another instanceof Null) return 0;
		return -1;
	}
}
