package javac.atom;
import java.util.Set;

import javac.quad.*;
public class COMP extends Atom {
	public String type;
	public Temp src1, src2;
	public boolean m_src1, m_src2;
	public Label label;
	public COMP(String s, Temp t1, Temp t2, Label l) {
		type = s; src1 = t1;
		src2 = t2; label = l;
	}
	@Override
	public String toString() {
		return type + " " + src1.toString() + ", " + src2.toString() + ", " + label.toString();
	}
	@Override
	public void handle_liveness(Set<Temp> alive) {
		m_src1 = alive.contains(src1);
		m_src2 = alive.contains(src2);
		alive.add(src1); alive.add(src2);
	}
	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		if (m_src1) nextUse.add(src1);
		else nextUse.remove(src1);
		if (m_src2) nextUse.add(src2);
		else nextUse.remove(src2);
	}
	@Override
	public void touch(Set<Temp> alive) {
		//if (!src1.isTemp) alive.add(src1);
		//if (!src2.isTemp) alive.add(src2);
	}

}
