package javac.atom;
import java.util.Set;

import javac.quad.*;
public class LabelAtom extends Atom {
	public Label label;
	public LabelAtom(Label l) {
		label = l;
	}
	@Override
	public String toString() {
		return label.toString() + ":";
	}
	@Override
	public void handle_liveness(Set<Temp> alive) {
		
	}
	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void touch(Set<Temp> alive) {
		// TODO Auto-generated method stub
		
	}

}
