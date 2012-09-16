package javac.absyn;

import javac.env.*;
import javac.type.*;

public class GlobalVisitor implements NodeVisitor {
	public Env env;
	
	public GlobalVisitor(Env e) {
		env = e;
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
		// TODO Auto-generated method stub
		if (!env.addEntry(functionDef.head.functionName, 
				 new FuncEntry(functionDef.head.functionName))) {
			System.err.println(functionDef.pos.toString() + "Error: duplicated declaration.");
			System.exit(1);
		}
	}

	@Override
	public void visit(FunctionHead functionHead) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ParameterList parameterList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PrototypeDecl prototypeDecl) {
		// TODO Auto-generated method stub
		if (!env.addEntry(prototypeDecl.head.functionName, 
			new FuncEntry(prototypeDecl.head.functionName))) {
			System.err.println(prototypeDecl.pos.toString() + "Error: duplicated declaration.");
			System.exit(1);
		}
	}

	@Override
	public void visit(RecordDef recordDef) {
		// TODO Auto-generated method stub
		RECORD type = new RECORD(recordDef.name);
		if (!env.addEntry(recordDef.name, new TypeEntry(type))) {
			System.err.println(recordDef.pos.toString() + "Error: duplicated declaration.");
			System.exit(1);
		}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VariableDeclList variableDeclList) {
		// TODO Auto-generated method stub

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
