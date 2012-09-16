package javac.absyn;

import javac.symbol.Symbol;
import javac.util.Position;

public class RecordDef extends ExternalDecl {
	
	public Symbol name;
	public VariableDeclList fields;

	public RecordDef(Position pos, Symbol recordName, VariableDeclList fieldList) {
		super(pos);
		name = recordName;
		fields = fieldList;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		if (visitor instanceof FinalVisitor) ((FinalVisitor)visitor).inRecord = true;
		fields.accept(visitor);
		if (visitor instanceof FinalVisitor) ((FinalVisitor)visitor).inRecord = false;
		visitor.visit(this);
	}
}
