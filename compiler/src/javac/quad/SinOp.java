package javac.quad;

import javac.absyn.UnaryOp;

public class SinOp extends Quad {
	
	public Oprand dst, src;
	public UnaryOp op;
	
	public SinOp(Oprand d, Oprand s, UnaryOp p) {
		dst = d;
		src = s;
		op = p;
	}
	
	@Override
	public String toString() {
		return dst.toString() + "<-" + trans(op) + src.toString();
	}
	
	static String trans(UnaryOp o) {
		switch (o) {
		case PLUS: return "+";
		case MINUS: return "-";
		case NOT: return "!";
		default: return "error";
		}
	}
}
