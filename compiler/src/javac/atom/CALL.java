package javac.atom;

import java.util.Set;

import javac.quad.Label;
import javac.quad.Temp;

public class CALL extends Atom {

	public CALL(Label name, Temp[] p, Temp r) {
		function = name;
		params = p;
		result = r;
	}
	public Label function;
	public Temp[] params;
	public boolean[] m_params;
	public boolean m_result;
	public Temp result;
	@Override
	public String toString() {
		String call = result.toString() + "<-" + function + "(";
		if (params != null)
			for (int i = 0; i < params.length; i++) {
				call += params[i].toString() + ',';
			}
		call += ")";
		return call;
	}
	@Override
	public void handle_liveness(Set<Temp> alive) {
		m_params = new boolean[params.length];
		m_result = alive.contains(result);
		int index = 0;
		for (Temp t : params)
			m_params[index ++] = alive.contains(t);
		if (alive.contains(result)) alive.remove(result);
		for (Temp t : params)
			alive.add(t);
	}
	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		if (m_result) nextUse.add(result);
		else nextUse.remove(result);
		int index = 0;
		for (Temp t : params) {
			if (m_params[index ++]) nextUse.add(t);
			else nextUse.remove(t);
		}
	}
	@Override
	public void touch(Set<Temp> alive) {
		if (!result.isTemp) alive.add(result);
		//for (Temp t : params)
		//	if (!t.isTemp) alive.add(t);
	}

}
