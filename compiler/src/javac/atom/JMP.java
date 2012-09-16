package javac.atom;
import java.util.Set;

import javac.quad.*;
public class JMP extends Atom {
	public String type;
	public Label label;
	public JMP(String s, Label l) {
		type = s;
		label = l;
	}
	@Override
	public String toString() {
		return type + " " + label.toString();
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
