/**
 * 
 */
package fatworm.db;

import fatworm.expr.Expr;
import fatworm.value.Bool;
import fatworm.value.FloatNumber;
import fatworm.value.Int;
import fatworm.value.Null;
import fatworm.value.Time;
import fatworm.value.Value;
import fatworm.value.Varchar;

/**
 * @author mrain
 *
 */
public class ColumnType {
	int type;
	int arg0, arg1;
	boolean notNull, autoInc;
	Value defaultValue;
	
	
	public ColumnType(int type) {
		this.type = type;
		notNull = autoInc = false;
		defaultValue = null;
	}
	
	public ColumnType(ColumnType col) {
		this.type = col.getType();
		this.arg0 = col.getArg0();
		this.arg1 = col.getArg1();
		this.defaultValue = col.getDefault();
		this.notNull = col.isNotNull();
		this.autoInc = col.isAutoIncrement();
	}

	/**
	 * 
	 * @param value
	 * @return if value can be cast into this type
	 */
	public boolean accept(Object value) {
		if (!(value instanceof Value)) return false;
		switch (type) {
			case java.sql.Types.INTEGER:
			case java.sql.Types.FLOAT:
				return true;
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:
				return true;
			case java.sql.Types.DECIMAL:
			case java.sql.Types.TIMESTAMP:
			case java.sql.Types.DATE:
			case java.sql.Types.BOOLEAN:
				return true;
		}
		return false;
	}
	
	public int compare(Object o1, Object o2) {
		// TODO
		return 0;
	}
	
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setArg0(int a) {
		this.arg0 = a;
	}
	
	public void setArg1(int a) {
		this.arg0 = a;
	}
	
	public int getArg0() {
		return this.arg0;
	}
	
	public int getArg1() {
		return this.arg1;
	}
	
	public void setNotNull(boolean mask) {
		notNull = mask;
	}
	
	public boolean isNotNull() {
		return notNull;
	}
	
	public void setAutoIncrement(boolean mask) {
		autoInc = mask;
	}
	
	public boolean isAutoIncrement() {
		return autoInc;
	}
	
	public int getNext() {
		return ++ arg0;
	}
	
	public boolean hasDefault() {
		return autoInc || (defaultValue != null);
	}
	
	public void setDefault(Value value) {
		defaultValue = value;
	}
	
	public Value getDefault() {
		if (autoInc) return new Int(getNext());
		else if (type == java.sql.Types.TIMESTAMP && defaultValue == null) {
			Time ret = new Time(new java.sql.Timestamp(new java.util.Date().getTime()).toString());
			//System.out.println(ret);
			return ret;
		} else if (defaultValue == null)
			return new Null();
		else return defaultValue;
	}
	
	public String toString() {
		String a = "";
		switch (type) {
			case java.sql.Types.INTEGER:
				a += "INT";
				break;
			case java.sql.Types.FLOAT:
				a += "FLOAT";
				break;
			case java.sql.Types.VARCHAR:
				a += "VARCHAR(" + arg0 + ")";
				break;
			case java.sql.Types.CHAR:
				a += "CHAR(" + arg0 + ")";
				break;
			case java.sql.Types.DECIMAL:
				a += "DECIMAL(" + arg0 + "," + arg1 + ")";
				break;
			case java.sql.Types.TIMESTAMP:
				a += "TIMESTAMP";
				break;
			case java.sql.Types.DATE:
				a += "DATETIME";
				break;
			case java.sql.Types.BOOLEAN:
				a += "BOOLEAN";
				break;
		}
		if (notNull) a += ",NOTNULL";
		if (autoInc) a += ",AUTOINC(" + arg0 + ")";
		if (defaultValue != null) {
			String s = defaultValue.toString();
			a += ",DEFAULT(" + s.length() + "," + s + ")";
		}
		a += "|";
		return a;
	}
	

	/**
	 * Construct type from metaString
	 * @param meta
	 */
	public ColumnType(String meta) {
		String tmp = "";
		int p = 1;
		char next = meta.charAt(0);
		while (p != meta.length() && next >= 'A' && next <= 'Z') {
			tmp += next;
			next = meta.charAt(p ++);
		}
		if (tmp.compareTo("INT") == 0)
			this.type = java.sql.Types.INTEGER;
		else if (tmp.compareTo("FLOAT") == 0)
			this.type = java.sql.Types.FLOAT;
		else if (tmp.compareTo("TIMESTAMP") == 0)
			this.type = java.sql.Types.TIMESTAMP;
		else if (tmp.compareTo("DATETIME") == 0)
			this.type = java.sql.Types.DATE;
		else if (tmp.compareTo("BOOLEAN") == 0)
			this.type = java.sql.Types.BOOLEAN;
		else if (tmp.compareTo("VARCHAR") == 0) {
			this.type = java.sql.Types.VARCHAR;
			tmp = "";
			next = meta.charAt(p ++);
			while (next >= '0' && next <= '9') {
				tmp += next;
				next = meta.charAt(p ++);
			}
			arg0 = Integer.valueOf(tmp);
			next = meta.charAt(p ++);
		} else if (tmp.compareTo("CHAR") == 0) {
			this.type = java.sql.Types.CHAR;
			tmp = "";
			next = meta.charAt(p ++);
			while (next >= '0' && next <= '9') {
				tmp += next;
				next = meta.charAt(p ++);
			}
			arg0 = Integer.valueOf(tmp);
			next = meta.charAt(p ++);
		} else if (tmp.compareTo("DECIMAL") == 0) {
			this.type = java.sql.Types.DECIMAL;
			next = meta.charAt(p ++);
			tmp = "";
			while (next >= '0' && next <= '9') {
				tmp += next;
				next = meta.charAt(p ++);
			}
			arg0 = Integer.valueOf(tmp);
			next = meta.charAt(p ++);
			tmp = "";
			while (next >= '0' && next <= '9') {
				tmp += next;
				next = meta.charAt(p ++);
			}
			arg1 = Integer.valueOf(tmp);
			next = meta.charAt(p ++);
		}
		
		while (next != '|') {
			tmp = "";
			next = meta.charAt(p ++);
			while (p != meta.length() && next >= 'A' && next <= 'Z') {
				tmp += next;
				next = meta.charAt(p ++);
			}
			if (tmp.compareTo("NOTNULL") == 0)
				this.notNull = true;
			else if (tmp.compareTo("AUTOINC") == 0) {
				autoInc = true;
				tmp = "";
				next = meta.charAt(p ++);
				while (next >= '0' && next <= '9') {
					tmp += next;
					next = meta.charAt(p ++);
				}
				arg0 = Integer.parseInt(tmp);
				next = meta.charAt(p ++);
			} else if (tmp.compareTo("DEFAULT") == 0) {
				tmp = "";
				next = meta.charAt(p ++);
				while (next >= '0' && next <= '9') {
					tmp += next;
					next = meta.charAt(p ++);
				}
				int length = Integer.parseInt(tmp);
				String val = "";
				for (int i = 0; i < length; ++ i)
					val += meta.charAt(p ++);
				defaultValue = Value.parse(val);
				next = meta.charAt(p ++);
			}
		}
	}

	public Value parse(Value v) {
		if (v instanceof Null)
			return v;
		switch (type) {
			case java.sql.Types.INTEGER:
				return new Int(v.castInt().value);
			case java.sql.Types.DECIMAL:
			case java.sql.Types.FLOAT:
				return new FloatNumber(v.castFloat().value);
			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
				return new Varchar(v.castString().value);
			case java.sql.Types.DATE:
			case java.sql.Types.TIMESTAMP:
				return new Time(v.castTime().toString());
			case java.sql.Types.BOOLEAN:
				return new Bool(v.castBool().value);
		}
		return null;
	}
}
