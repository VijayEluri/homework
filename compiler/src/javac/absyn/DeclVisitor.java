package javac.absyn;

import javac.env.*;

import java.util.*;

import javac.symbol.Symbol;
import javac.type.*;

public class DeclVisitor implements NodeVisitor {

    public Env env;
    private List<RECORD.RecordField> variable_list;
    private List<FuncEntry.ParameterField> parameter_list;
    private HashSet<Symbol> table;

    public DeclVisitor(Env global_env) {
        env = global_env;
        table = new HashSet<Symbol>();
        parameter_list = new ArrayList<FuncEntry.ParameterField>();
        variable_list = new ArrayList<RECORD.RecordField>();
    }

	@Override
	public void visit(ArrayType arrayType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BinaryExpr binaryExpr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BreakStmt breakStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CharLiteral charLiteral) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CharType charType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CompoundStmt compoundStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ContinueStmt continueStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExprStmt exprStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FieldPostfix fieldPostfix) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ForStmt forStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FunctionCall functionCall) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FunctionDef functionDef) {
		variable_list.clear();
	}

	@Override
	public void visit(FunctionHead functionHead) {
		// TODO Auto-generated method stub
		Symbol name = functionHead.functionName;
		FuncEntry entry = (FuncEntry)env.getEntry(name);
		entry.parameter = parameter_list;
		entry.ty = functionHead.type.toType(env);
		parameter_list = new ArrayList<FuncEntry.ParameterField>();
		if (entry.ty == null) {
			System.err.println(functionHead.pos.toString() + "Error: invalid return type.");
			System.exit(1);
		}
	}

	@Override
	public void visit(Id id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IdList idList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IdType idType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IfStmt ifStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntLiteral intLiteral) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntType intType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NewArray newArray) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NewRecord newRecord) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Null n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ParameterDecl parameterDecl) {
		Type type = parameterDecl.type.toType(env);
		if (type == null) {
			System.err.println(parameterDecl.pos.toString() + "Error: unknown type.");
			System.exit(1);
		}
		if (table.contains(parameterDecl.name) || env.contains(parameterDecl.name)) {
			//env.printEnv();
			System.err.println(parameterDecl.pos.toString() + "Error: duplicated declaration.");
			System.exit(1);
		} else table.add(parameterDecl.name);
		parameter_list.add(new FuncEntry.ParameterField(type, parameterDecl.name));
	}

	@Override
	public void visit(ParameterList parameterList) {
		table.clear();
	}

	@Override
	public void visit(PrototypeDecl prototypeDecl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RecordDef recordDef) {
		// TODO Auto-generated method stub
		RECORD type = (RECORD)((TypeEntry)env.getEntry(recordDef.name)).type;
		type.fields = variable_list;
		variable_list = new ArrayList<RECORD.RecordField>();
	}

	@Override
	public void visit(ReturnStmt returnStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StmtList stmtList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StringLiteral stringLiteral) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StringType stringType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SubscriptPostfix subscriptPostfix) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TranslationUnit translationUnit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(UnaryExpr unaryExpr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VariableDecl variableDecl) {
		Type type = variableDecl.type.toType(env);
		if (type == null) {
			System.err.println(variableDecl.pos.toString() + "Error: invalid type.");
			System.exit(1);
		}
		for (Symbol id : variableDecl.ids.ids) {
			if (table.contains(id)) {
				System.err.println(variableDecl.pos.toString() + "Error: duplicated declaration.");
				System.exit(1);
			} else table.add(id);
			variable_list.add(new RECORD.RecordField(type, id, variable_list.size()));
		}
	}

	@Override
	public void visit(VariableDeclList variableDeclList) {
		table.clear();
	}

	@Override
	public void visit(WhileStmt whileStmt) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void visit(ArgumentList argumentList) {
		// TODO Auto-generated method stub

	}

}
