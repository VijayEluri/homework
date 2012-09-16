package javac.env;

import javac.type.*;
//import javac.absyn.RecordDef;

public class TypeEntry extends Entry {
	public Type type;
	//public RecordDef def;
	public TypeEntry(Type d) {
		type = d;
	}
	public static TypeEntry transferTypeEntry(Entry entry) {
		if (entry instanceof TypeEntry) return (TypeEntry)entry;
		else return null;
	}
}
