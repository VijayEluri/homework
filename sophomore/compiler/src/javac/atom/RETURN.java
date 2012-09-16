package javac.atom;

import java.util.Set;

import javac.quad.*;
public class RETURN extends Atom {
	public Oprand value;
	public RETURN(Oprand op) {
		value = op;
	}
	@Override
	public String toString() {
		return "ret " + value.toString();
	}
	@Override
	public void handle_liveness(Set<Temp> alive) {
		if (value instanceof TempOprand)
			alive.add(((TempOprand)value).temp);
	}
	@Override
	public void handle_nextUse(Set<Temp> nextUse) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void touch(Set<Temp> alive) {
		if (value instanceof TempOprand && !((TempOprand)value).temp.isTemp)
			alive.add(((TempOprand)value).temp);
	}

}
