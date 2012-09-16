package javac.absyn;

import java.util.*;

import javac.env.*;
import javac.type.*;
import javac.symbol.*;

public class FinalVisitor implements NodeVisitor {

	public int loop_count;
	private Env global_env, local_env;
	private FuncEntry cur_func;
	public boolean inRecord;
	
	public FinalVisitor(Env env) {
		loop_count = 0;
		inRecord = false;
		global_env = env;
		local_env = new Env();
	}
	
	@Override
	public void visit(ArrayType arrayType) {
		//System.out.println(arrayType.elemType.toType(local_env) == null);
		if (arrayType.elemType.toType(global_env) == null) {
			System.err.println("Error: Unknown type.");
			System.exit(1);
		} else {
			//System.out.println(arrayType.toType(global_env).toString());
		}
	}

	@Override
	public void visit(BinaryExpr binaryExpr) {
		Type lt = binaryExpr.l.ty, rt = binaryExpr.r.ty;
		if (binaryExpr.op == BinaryOp.MULTIPLY || binaryExpr.op == BinaryOp.DIVIDE
			|| binaryExpr.op == BinaryOp.MINUS || binaryExpr.op == BinaryOp.MODULO
			|| binaryExpr.op == BinaryOp.OR || binaryExpr.op == BinaryOp.AND) {
			if (lt instanceof INT && rt instanceof INT) {
				binaryExpr.ty = INT.getInstance();
				binaryExpr.lvalue = false;
			} else {
				System.err.println(binaryExpr.pos.toString() + "Expression error: two types must be the same.");
				System.exit(1);
			}
		} else if (binaryExpr.op == BinaryOp.PLUS) {
			if (lt instanceof ARRAY || rt instanceof ARRAY
				|| lt instanceof RECORD || rt instanceof RECORD) {
				System.err.println(binaryExpr.pos.toString() + "Expression error: '+' is not allowed between array or record.");
				System.exit(1);
			}
			if (lt instanceof STRING || rt instanceof STRING) {
				binaryExpr.ty = STRING.getInstance();
				binaryExpr.lvalue = false;
			} else binaryExpr.ty = INT.getInstance();
		} else if (binaryExpr.op == BinaryOp.COMMA) {
			binaryExpr.ty = rt;
			binaryExpr.lvalue = binaryExpr.r.lvalue;
		} else {
			if (!lt.equals(rt)) {
				System.err.println(binaryExpr.pos.toString() + "Error: invalid expression.");
				System.exit(1);
			}
			if (binaryExpr.op == BinaryOp.ASSIGN) {
				if (!binaryExpr.l.lvalue) {
					System.err.println(binaryExpr.pos.toString() + "Expression error: the left value can not be written.");
					System.exit(1);
				} else {
					binaryExpr.ty = lt;
					//binaryExpr.lvalue = binaryExpr.l.lvalue;
					binaryExpr.lvalue = false;
				}
			} else {
				if (binaryExpr.op != BinaryOp.EQ && binaryExpr.op != BinaryOp.NEQ) {
					if (lt.isArray() || lt.isRecord()) {
						System.err.println(binaryExpr.pos.toString() + "Expression error: these types can not be compared.");
						System.exit(1);
					} else {
						binaryExpr.ty = INT.getInstance();
						binaryExpr.lvalue = false;
					}
				} else {
					binaryExpr.ty = INT.getInstance();
					binaryExpr.lvalue = false;
				}
			}
		}
	}

	@Override
	public void visit(BreakStmt breakStmt) {
		if (loop_count == 0) {
			System.err.println(breakStmt.pos.toString() + "Error: break must be inside a loop.");
			System.exit(1);
		}
	}

	@Override
	public void visit(CharLiteral charLiteral) {
		charLiteral.ty = CHAR.getInstance();
		charLiteral.lvalue = false;
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
		if (loop_count == 0) {
			System.err.println("Error: continue must be inside a loop.");
			System.exit(1);
		}
	}

	@Override
	public void visit(ExprStmt exprStmt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FieldPostfix fieldPostfix) {
		Type lt = fieldPostfix.expr.ty;
		if (lt instanceof STRING || lt instanceof ARRAY) {
			if (fieldPostfix.field.equals(Symbol.valueOf("length"))) {
				fieldPostfix.ty = INT.getInstance();
				fieldPostfix.lvalue = false;
			} else {
				System.err.println(fieldPostfix.pos.toString() + "Error: string and array only have 'length'.");
				System.exit(1);
			}
		} else if (lt.isRecord()) {
			boolean found = false;
			for (RECORD.RecordField item : ((RECORD)lt).fields)
				if (item.name.equals(fieldPostfix.field)) {
					found = true;
					fieldPostfix.ty = item.type;
					fieldPostfix.lvalue = true;
					break;
				}
			if (!found) {
				System.err.println(fieldPostfix.pos.toString() + "Error: record error.");
				System.exit(1);
			}
		} else {
			System.err.println(fieldPostfix.pos.toString() + "Error: dot expression is only allowed in string, array and record.");
			System.exit(1);
		}
	}

	@Override
	public void visit(ForStmt forStmt) {
		if (!(forStmt.cond.ty instanceof INT)) {
			System.err.println(forStmt.pos.toString() + "Error: the condition must be int type.");
			System.exit(1);
		}
	}

	@Override
	public void visit(FunctionCall functionCall) {
		// TODO Auto-generated method stub
		Symbol funcname = Symbol.valueOf(functionCall.expr.toString());
		FuncEntry entry = (FuncEntry) global_env.getEntry(funcname);
		if (entry == null) {
			System.err.println(functionCall.pos.toString() + "Error: invaild function call.");
			System.exit(1);
		}
		if (functionCall.args.arguments.size() != entry.parameter.size()) {
			System.err.println(functionCall.pos.toString() + "Error: invaild function call.");
			System.exit(1);
		} else {
			int i = 0;
			for (Expr expr : functionCall.args.arguments) {
				if (!entry.parameter.get(i).type.equals(expr.ty)) {
					System.err.println(functionCall.pos.toString() + "Error: invaild function parameter.");
					System.exit(1);
				}
				++ i;
			}
			functionCall.ty = entry.ty;
		}
	}

	@Override
	public void visit(FunctionDef functionDef) {
		local_env.clear();
		cur_func = null;
	}

	@Override
	public void visit(FunctionHead functionHead) {
		List<ParameterDecl> list = functionHead.parameterList.parameterDeclarations;
		for (ParameterDecl decl : list) {
			Symbol name = decl.name;
			Type type = decl.type.toType(global_env);
			if (!local_env.addEntry(name, new VariableEntry(type, name))) {
				System.err.println(functionHead.pos.toString() + "Error: duplicated declaration.");
				System.exit(1);
			}
		}
		cur_func = (FuncEntry)global_env.getEntry(functionHead.functionName);
	}

	@Override
	public void visit(Id id) {
		boolean local = local_env.contains(id.sym);
		boolean global = global_env.contains(id.sym);
		if (!local && !global) {
			System.err.println(id.pos.toString() + "Error: variable must be declared first.");
			System.exit(1);
		} else {
			if (local) {
				id.ty = ((VariableEntry)local_env.getEntry(id.sym)).type;
				id.lvalue = true;
			}
		}
	}

	@Override
	public void visit(IdList idList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IdType idType) {
		if (idType.toType(global_env) == null) {
			System.err.println("Error: Undeclared type.");
			System.exit(1);
		}
	}

	@Override
	public void visit(IfStmt ifStmt) {
		if (!(ifStmt.cond.ty instanceof INT)) {
			System.err.println("Error: If condition must be int type.");
			System.exit(1);
		}
	}

	@Override
	public void visit(IntLiteral intLiteral) {
		intLiteral.ty = INT.getInstance();
		intLiteral.lvalue = false;
	}

	@Override
	public void visit(IntType intType) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(NewArray newArray) {
		Type type = newArray.type.toType(global_env);
		if (type == null) {
			System.err.println("Error: undeclared type.");
			System.exit(1);
		}
		if (!(newArray.expr.ty instanceof INT)) {
			System.err.println("Error: index must be an int type.");
			System.exit(1);
		}
		newArray.ty = new ARRAY(type);
		newArray.base_type = type;
		newArray.lvalue = false;
	}

	@Override
	public void visit(NewRecord newRecord) {
		Type type = newRecord.type.toType(global_env);
		if (type == null || !type.isRecord()) {
			System.err.println("Error: invalid declaration.");
			System.exit(1);
		}
		newRecord.ty = type;
		newRecord.lvalue = false;
	}

	@Override
	public void visit(Null n) {
		// TODO Auto-generated method stub
		n.ty = new RECORD(Symbol.valueOf("null"));
		n.lvalue = false;
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
		local_env.clear();
	}

	@Override
	public void visit(RecordDef recordDef) {
		local_env.clear();
	}

	@Override
	public void visit(ReturnStmt returnStmt) {
		if (!cur_func.ty.equals(returnStmt.expr.ty)) {
			System.err.println(returnStmt.pos.toString() + "Error: incorrect return type.");
			System.exit(1);
		}
	}

	@Override
	public void visit(StmtList stmtList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StringLiteral stringLiteral) {
		stringLiteral.ty = STRING.getInstance();
		stringLiteral.lvalue = false;
	}

	@Override
	public void visit(StringType stringType) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(SubscriptPostfix subscriptPostfix) {
		if (!subscriptPostfix.expr.ty.isArray() && !(subscriptPostfix.expr.ty instanceof STRING)) {
			System.err.println("Error: left hand side must be an array or a string.");
			System.exit(1);
		} else if (!subscriptPostfix.subscript.ty.equals(INT.getInstance())) {
			System.err.println("Error: index must be an int.");
			System.exit(1);
		} else {
			if (subscriptPostfix.expr.ty instanceof STRING) subscriptPostfix.ty = CHAR.getInstance();
			else subscriptPostfix.ty = ((ARRAY)subscriptPostfix.expr.ty).elementType;
			subscriptPostfix.lvalue = subscriptPostfix.expr.lvalue;
		}
	}

	@Override
	public void visit(TranslationUnit translationUnit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(UnaryExpr unaryExpr) {
		if (unaryExpr.expr.ty.equals(INT.getInstance()) == false) {
			System.err.println("Error: it must be int.");
			System.exit(1);
		} else {
			unaryExpr.ty = INT.getInstance();
			unaryExpr.lvalue = false;
		}
	}

	@Override
	public void visit(VariableDecl variableDecl) {
		if (inRecord) return;
		Type type = variableDecl.type.toType(global_env);
		if (type == null) {
			System.err.println(variableDecl.type.pos.toString() + "Error: undeclared type");
			System.exit(1);
		} else {
			for (Symbol sym : variableDecl.ids.ids) {
				if (global_env.contains(sym) && global_env.getEntry(sym) instanceof FuncEntry) {
					System.err.println(variableDecl.ids.pos.toString() + "Error: duplicated declaration.");
					System.exit(1);
				}
				if (!local_env.addEntry(sym, new VariableEntry(type, sym))) {
					System.err.println(variableDecl.ids.pos.toString() + "Error: duplicated declaration.");
					System.exit(1);
				}
			}
		}
	}

	@Override
	public void visit(VariableDeclList variableDeclList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WhileStmt whileStmt) {
		if (!(whileStmt.cond.ty instanceof INT)) {
			System.err.println(whileStmt.pos.toString() + "Error: the condition must be int type.");
			System.exit(1);
		}
	}

	@Override
	public void visit(ArgumentList argumentList) {
		// TODO Auto-generated method stub

	}
}
