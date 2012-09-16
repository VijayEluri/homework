package javac.env;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javac.symbol.Symbol;

public class Env {
	public HashMap <Symbol, Entry> records;
	
	public Env() {
		records = new HashMap<Symbol, Entry>();
	}
	
	public Env(Env v) {
		records = new HashMap<Symbol, Entry>();
		Iterator iter = v.records.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			Symbol key = (Symbol) entry.getKey();
			Entry ent = (Entry) entry.getValue();
			records.put(key, ent);
		}
	}
	
	public boolean addEntry(Symbol sym, Entry entry) {
		if (records.containsKey(sym)) return false;
		records.put(sym, entry);
		return true;
	}
	
	public boolean contains(Symbol sym) {
		return records.containsKey(sym);
	}
	
	public Entry getEntry(Symbol sym) {
		return records.get(sym);
	}
	
	public void clear() {
		records.clear();
	}
	
	public void printEnv() {
		Iterator iter = records.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			Symbol key = (Symbol) entry.getKey();
			Entry ent = (Entry) entry.getValue();
			if (ent instanceof TypeEntry) System.out.print("Record\t\t");
			else if (ent instanceof FuncEntry) System.out.print("Function\t");
			else System.out.print("Variable\t");
			System.out.println(key.toString());
		}
	}
}
