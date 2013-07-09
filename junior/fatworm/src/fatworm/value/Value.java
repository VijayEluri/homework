/**
 * 
 */
package fatworm.value;

import java.math.*;
/**
 * @author mrain
 *
 */
public abstract class Value implements Comparable<Value> {
	
	public static Value parse(String value, int type) {
		if (value.toUpperCase().compareTo("NULL") == 0)
			return new Null();
		switch (type) {
			case java.sql.Types.INTEGER:
				return new Int(Integer.parseInt(value));
			case java.sql.Types.DECIMAL:
			case java.sql.Types.FLOAT:
				return new FloatNumber(Float.parseFloat(value));
			case java.sql.Types.DATE:
			case java.sql.Types.TIMESTAMP:
				return new Time(value.substring(1, value.length() - 1));
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:
				return new Varchar(value.substring(1, value.length() - 1));
			case java.sql.Types.BOOLEAN:
				return new Bool(value.toUpperCase().compareTo("FALSE") != 0);
		}
		System.out.println(value + " " + type);
		return null;
	}
	
	public static Value parse(String value) {
		if (value.startsWith("'")) {
			String v = value.substring(1, value.length() - 1);
			try {
				return new Time(v);
			} catch (Exception e) {
				return new Varchar(v);
			}
		} else if (value.toUpperCase().compareTo("TRUE") == 0)
			return new Bool(true);
		else if (value.toUpperCase().compareTo("FALSE") == 0)
			return new Bool(false);
		else if (value.contains("."))
			return new FloatNumber(Float.parseFloat(value));
		else if (value.compareTo("NULL") == 0)
			return new Null();
		else {
			try {
				return new Int(Integer.parseInt(value));
			} catch (Exception e) {
				return new FloatNumber(Float.parseFloat(value));
			}
		}
	}
	
	public abstract int length();
	public abstract byte[] toByteArray();
	public abstract Object getObject();
	
	public abstract int strictCompare(Value another);
	
	public abstract Int castInt();
	public abstract Bool castBool();
	public abstract Varchar castString();
	public abstract FloatNumber castFloat();
	public abstract Time castTime();
	
	public abstract Value add(Value arg);
	public abstract Value subtract(Value arg);
	public abstract Value multiply(Value arg);
	public abstract Value divide(Value arg);
	public abstract Value mod(Value arg); 
	
	public Value and(Value arg) {
		return new Bool(this.castBool().takeAnd(arg.castBool()));
	}
	
	public Value or(Value arg) {
		return new Bool(this.castBool().takeOr(arg.castBool()));
	}

	public abstract int getType();
}
