package javac.atom;
import java.util.Set;

import javac.quad.*;
public class LA extends Atom {
	public Temp dst;
	public boolean m_dst;
	public Label src;
	
	public LA(Temp t, Label s) {
		dst = t;
		src = s;
	}

	@Override
	public String toString() {
		return "la " + dst.toString() + ", " + src.toString();
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
