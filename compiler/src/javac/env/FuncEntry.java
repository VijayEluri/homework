package javac.env;

import javac.symbol.*;
import javac.type.*;
import java.util.*;

public class FuncEntry extends Entry {
	
	public static class ParameterField {
		
		public Type type;
		public Symbol name;
		
		public ParameterField(Type t, Symbol n) {
			type = t;
			name = n;
		}
	}
	
	public Symbol name;
	public List<ParameterField> parameter;
	public Type ty;
	
	public FuncEntry(Symbol n) {
		name = n;
	}
}
