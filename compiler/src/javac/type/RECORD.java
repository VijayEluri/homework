package javac.type;

import java.util.ArrayList;
import java.util.List;

import javac.symbol.Symbol;

public final class RECORD extends Type {

	public static final class RecordField {

		public Type type;
		public Symbol name;
		public int index;

		public RecordField(Type type, Symbol name, int index) {
			this.type = type;
			this.name = name;
			this.index = index;
		}
	}

	public List<RecordField> fields;
	public Symbol name;

	public RECORD(Symbol name) {
		fields = new ArrayList<RecordField>();
		this.name = name;
		this.size = 4;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RECORD) {
			if (this.name.equals(Symbol.valueOf("null"))
				|| ((RECORD)other).name.equals(Symbol.valueOf("null"))
				) {
				return true;
			} else return name.equals(((RECORD) other).name);
		}
		return false;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isRecord() {
		return true;
	}

}
