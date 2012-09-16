package javac.atom;
import java.util.Set;

import javac.quad.*;
public class ARI extends Atom {
	public String type;
	public Temp dst, src1, src2;
	public boolean m_dst, m_src1, m_src2;
	public ARI(String s, Temp t, Temp t1, Temp t2) {
		type = s; dst = t;
		src1 = t1; src2 = t2;
	}
	@Override
	public String toString() {
		return type + " " + dst + ", " + src1 + ", " + src2;
	}
	@Override
	public void handle_liveness(Set<Temp> alive) {
		m_dst = alive.contains(dst);
		m_src1 = alive.contains(src1);
		m_src2 = alive.contains(src2);
		if (alive.contains(dst)) alive.remove(dst);
		alive.add(src1);
		alive.add(src2);
	}
	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		if (m_src1) nextUse.add(src1);
		else nextUse.remove(src1);
		if (m_src2) nextUse.add(src2);
		else nextUse.remove(src2);
		if (m_dst) nextUse.add(dst);
		else nextUse.remove(dst);
	}
	@Override
	public void touch(Set<Temp> alive) {
		if (!dst.isTemp) alive.add(dst);
		//if (!src1.isTemp) alive.add(src1);
		//if (!src2.isTemp) alive.add(src2);
	}

}
