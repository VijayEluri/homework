package javac.atom;

import java.util.Set;

import javac.quad.Temp;

public class LI extends Atom {
	public Temp dst;
	public boolean m_dst;
	public int imm;
	
	public LI(Temp t, int i) {
		dst = t;
		imm = i;
	}
	
	@Override
	public String toString() {
		return "li " + dst.toString() + ", " + imm;
	}

	@Override
	public void handle_liveness(Set<Temp> alive) {
		m_dst = alive.contains(dst);
		if (alive.contains(dst)) alive.remove(dst);
	}

	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		if (m_dst) nextUse.add(dst);
		else nextUse.remove(dst);
	}

	@Override
	public void touch(Set<Temp> alive) {
		if (!dst.isTemp) alive.add(dst);
	}

}
