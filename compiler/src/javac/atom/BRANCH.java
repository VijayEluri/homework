package javac.atom;
import java.util.Set;

import javac.quad.*;
public class BRANCH extends Atom {
	public String type;
	public Temp src;
	public boolean m_src;
	public Label label;
	public BRANCH(String s, Temp t, Label l) {
		type = s;
		src = t; label = l;
	}
	@Override
	public String toString() {
		return type + " " + src.toString() + ", " + label.toString();
	}
	@Override
	public void handle_liveness(Set<Temp> alive) {
		m_src = alive.contains(src);
		alive.add(src);
	}
	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		if (m_src) nextUse.add(src);
		else nextUse.remove(src);
	}
	@Override
	public void touch(Set<Temp> alive) {
		//alive.add(src);
		//if (!src.isTemp) alive.add(src);
	}

}
