package javac.env;

import javac.type.*;
import javac.symbol.*;

public class VariableEntry extends Entry {
	public Type type;
	public Symbol name;
	
	public VariableEntry(Type t, Symbol n) {
		type = t; name = n;
	}
}
