package javac.atom;
import java.util.Set;

import javac.quad.*;
public class SARI extends Atom {
	public String type;
	public Temp dst, src;
	public boolean m_dst, m_src;
	public SARI(String s, Temp t, Temp t1) {
		type = s; dst = t;
		src = t1;
	}
	@Override
	public String toString() {
		return type + " " + dst + ", " + src;
	}
	@Override
	public void handle_liveness(Set<Temp> alive) {
		m_dst = alive.contains(dst);
		m_src = alive.contains(src);
		if (alive.contains(dst)) alive.remove(dst);
		alive.add(src);
	}

	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		if (m_src) nextUse.add(src);
		else nextUse.remove(src);
		if (m_dst) nextUse.add(dst);
		else nextUse.remove(dst);
	}
	@Override
	public void touch(Set<Temp> alive) {
		if (!dst.isTemp) alive.add(dst);
		//if (!src.isTemp) alive.add(src);
	}
}
