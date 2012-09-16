package javac.atom;

import java.util.*;
import javac.quad.*;

abstract public class Atom {
	abstract public String toString();
	abstract public void handle_liveness(Set<Temp> alive);
	abstract public void handle_nextUse(Set<Temp> nextUse);
	abstract public void touch(Set<Temp> alive);
}
