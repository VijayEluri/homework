package javac.Main;


import javac.atom.*;
import java.util.*;
import javac.quad.*;
import javac.absyn.*;
import javac.symbol.*;
import javac.type.*;

public class Translator {
	public Label function_label;
	public int arg_size, local_size;
	public Map<Symbol, Temp> env;
	public FunctionDef def;
	public List<Atom> atoms;
	
	private List<Quad> code;
	private String name;
	//private Temp temporary;
	private Stack<Label> loop_exit, loop_cont;
	private List<Label> string_label;
	private List<String> string_literal;
	private Set<Temp> free_temporary;
	
	private Oprand toTemp(Oprand handle) {
		if (!(handle instanceof TempOprand)) {
			TempOprand t = new TempOprand(pickTemporary());
			code.add(new Move(t, handle));
			handle = t;
		}
		return handle;
	}
	
	private Temp asmTemp(Oprand handle) {
		if (handle instanceof TempOprand)
			return ((TempOprand)handle).temp;
		TempOprand t = new TempOprand(pickTemporary());
		Move quad = new Move(t, handle);
		asm(quad);
		return t.temp;
	}
	
	private Temp pickTemporary() {
		if (!free_temporary.isEmpty()) {
			Temp t = free_temporary.iterator().next();
			free_temporary.remove(t);
			return t;
		}
		return new Temp(true);
	}
	
	private void recycle(Temp temporary) {
		if (temporary.isTemp) 
			free_temporary.add(temporary);
	}
	
	private void recycle(Oprand op) {
		if (op instanceof TempOprand)
			recycle(((TempOprand)op).temp);
	}
	
	public Translator(FunctionDef functionDef) {
		code = new ArrayList<Quad>();
		name = functionDef.head.functionName.toString();
		env = new HashMap<Symbol, Temp>();
		free_temporary = new HashSet<Temp>();
		//loop_head = new Stack<Label>();
		loop_exit = new Stack<Label>();
		loop_cont = new Stack<Label>();
		string_label = new ArrayList<Label>();
		string_literal = new ArrayList<String>();
		function_label = new Label(name);
		atoms = new ArrayList<Atom>();
		arg_size = 4;// local_size = 0;
		Temp.count = 0;
		
		def = functionDef;
		
		//temporary = pickTemporary();
		for (ParameterDecl param : functionDef.head.parameterList.parameterDeclarations)
			env.put(param.name, new Temp(false));
		for (VariableDecl vardecl : functionDef.vardec.variableDeclarations) {
			for (Symbol sym : vardecl.ids.ids) {
				env.put(sym, new Temp(false));
			}
		}
		
		//code.add(new LabelQuad(function_label));
		
		for (Stmt stmt : functionDef.stmts.statements) {
			//System.out.println(stmt.toString());
			translate(stmt);
		}
		local_size = Temp.count;
		
		for (int i = 0; i < code.size(); ++ i) {
			Quad quad = code.get(i);
			if (quad instanceof BinOp) asm((BinOp)quad);
			if (quad instanceof Branch) asm((Branch)quad);
			if (quad instanceof Call) asm((Call)quad);
			if (quad instanceof Jump) {
				if (i < code.size() - 1) {
					Quad tmp = code.get(i + 1);
					if (tmp instanceof LabelQuad
					 && ((LabelQuad)tmp).label.equals(((Jump)quad).label))
						continue;
				}
				asm((Jump)quad);
			}
			if (quad instanceof LabelQuad) asm((LabelQuad)quad);
			if (quad instanceof Move) asm((Move)quad);
			if (quad instanceof Return) asm((Return)quad);
			if (quad instanceof SinOp) asm((SinOp)quad);
		}
		

	}
	
	public void translate(Stmt stmt) {
		if (stmt instanceof BreakStmt) translate((BreakStmt)stmt);
		if (stmt instanceof CompoundStmt) translate((CompoundStmt)stmt);
		if (stmt instanceof ContinueStmt) translate((ContinueStmt)stmt);
		if (stmt instanceof ExprStmt) translate((ExprStmt)stmt);
		if (stmt instanceof ForStmt) translate((ForStmt)stmt);
		if (stmt instanceof IfStmt) translate((IfStmt)stmt);
		if (stmt instanceof ReturnStmt) translate((ReturnStmt)stmt);
		if (stmt instanceof WhileStmt) translate((WhileStmt)stmt);
	}
	
	public void translate(BreakStmt stmt) {
		code.add(new Jump(loop_exit.peek()));
	}
	
	public void translate(CompoundStmt stmt) {
		for (Stmt st : stmt.stmts.statements)
			translate(st);
	}
	
	public void translate(ContinueStmt stmt) {
		code.add(new Jump(loop_cont.peek()));
	}
	
	public void translate(ExprStmt stmt) {
		translate(stmt.expr);
	}
	
	public void translate(ForStmt stmt) {
		Label head = new Label(), cont = new Label(), exit = new Label();
		//Label chk = new Label();
		loop_cont.push(cont);
		loop_exit.push(exit);
		
		translate(stmt.init);
		translate_revert(stmt.cond, head, exit);
		code.add(new LabelQuad(head));
		translate(stmt.body);
		code.add(new LabelQuad(cont));
		translate(stmt.step);
		translate(stmt.cond, head, exit);
		code.add(new LabelQuad(exit));
		
		loop_cont.pop();
		loop_exit.pop();
	}
	
	public void translate(IfStmt stmt) {
		if (stmt.elsePart != null) {
			Label elsePart = new Label(), thenPart = new Label(), exit = new Label();
			
			translate_revert(stmt.cond, thenPart, elsePart);
			code.add(new LabelQuad(thenPart));
			translate(stmt.thenPart);
			code.add(new Jump(exit));
			code.add(new LabelQuad(elsePart));
			translate(stmt.elsePart);
			code.add(new LabelQuad(exit));
		} else {
			Label thenPart = new Label(), exit = new Label();
			
			translate_revert(stmt.cond, thenPart, exit);
			code.add(new LabelQuad(thenPart));
			translate(stmt.thenPart);
			code.add(new LabelQuad(exit));
		}
	}
	
	public void translate(ReturnStmt stmt) {
		Oprand val = translate(stmt.expr);
		code.add(new Return(val));
//		recycle(val);
	}
	
	public void translate(WhileStmt stmt) {
		Label cont = new Label(), exit = new Label();
		Label tmp = new Label();
		loop_cont.push(cont);
		loop_exit.push(exit);
		
		
		translate_revert(stmt.cond, tmp, exit);
		code.add(new LabelQuad(tmp));
		translate(stmt.body);
		code.add(new LabelQuad(cont));
		translate(stmt.cond, tmp, exit);
		code.add(new LabelQuad(exit));
		
		loop_cont.pop();
		loop_exit.pop();
	}
	
	public Oprand translate(Expr expr) {
		if (expr instanceof BinaryExpr) return translate((BinaryExpr)expr);
		if (expr instanceof CharLiteral) return translate((CharLiteral)expr);
		if (expr instanceof FieldPostfix) return translate((FieldPostfix)expr);
		if (expr instanceof FunctionCall) return translate((FunctionCall)expr);
		if (expr instanceof Id) return translate((Id)expr);
		if (expr instanceof IntLiteral) return translate((IntLiteral)expr);
		if (expr instanceof NewArray) return translate((NewArray)expr);
		if (expr instanceof NewRecord) return translate((NewRecord)expr);
		if (expr instanceof Null) return translate((Null)expr);
		if (expr instanceof StringLiteral) return translate((StringLiteral)expr);
		if (expr instanceof SubscriptPostfix) return translate((SubscriptPostfix)expr);
		if (expr instanceof UnaryExpr) return translate((UnaryExpr)expr);
		Oprand ret = new TempOprand(pickTemporary());
		recycle(ret);
		return ret;
	}
	
	public void translate(Expr expr, Label trueLabel, Label falseLabel) {
		if (expr instanceof BinaryExpr) {
			BinaryExpr binaryExpr = (BinaryExpr)expr;
			if (binaryExpr.op == BinaryOp.EQ || binaryExpr.op == BinaryOp.GREATER
			 || binaryExpr.op == BinaryOp.GREATER_EQ || binaryExpr.op == BinaryOp.LESS
			 || binaryExpr.op == BinaryOp.LESS_EQ || binaryExpr.op == BinaryOp.NEQ) {
				BinaryOp op = binaryExpr.op;
				
				if (binaryExpr.l.ty.equals(STRING.getInstance())) {
					Temp l = ((TempOprand)toTemp(translate(binaryExpr.l))).temp;
					Temp r = ((TempOprand)toTemp(translate(binaryExpr.r))).temp;
					Temp t = pickTemporary();
					Temp[] params = new Temp[2];
					params[0] = l; params[1] = r;
					code.add(new Call(new Label("_strcmp"), params, t));
					if (op == BinaryOp.EQ)
						code.add(new Branch(trueLabel, new TempOprand(t), new Const(0), BinaryOp.EQ));
					else if (op == BinaryOp.NEQ)
						code.add(new Branch(trueLabel, new TempOprand(t), new Const(0), BinaryOp.NEQ));
					else if (op == BinaryOp.LESS)
						code.add(new Branch(trueLabel, new TempOprand(t), new Const(-1), BinaryOp.EQ));
					else if (op == BinaryOp.GREATER)
						code.add(new Branch(trueLabel, new TempOprand(t), new Const(1), BinaryOp.EQ));
					else if (op == BinaryOp.GREATER_EQ)
						code.add(new Branch(trueLabel, new TempOprand(t), new Const(-1), BinaryOp.NEQ));
					else if (op == BinaryOp.LESS_EQ)
						code.add(new Branch(trueLabel, new TempOprand(t), new Const(1), BinaryOp.NEQ));
					code.add(new Jump(trueLabel));
					//recycle(l); recycle(r); recycle(t);
				} else {
					Oprand l = translate(binaryExpr.l), r = translate(binaryExpr.r);
					code.add(new Branch(trueLabel, l, r, op));
					code.add(new Jump(falseLabel));
					//recycle(l); recycle(r); 
				}
			} else if (binaryExpr.op == BinaryOp.OR) {
				Label tmp = new Label();
				translate(binaryExpr.l, trueLabel, tmp);
				code.add(new LabelQuad(tmp));
				translate(binaryExpr.r, trueLabel, falseLabel);
			} else if (binaryExpr.op == BinaryOp.AND || binaryExpr.op == BinaryOp.MULTIPLY) {
				Label tmp = new Label();
				translate(binaryExpr.l, tmp, falseLabel);
				code.add(new LabelQuad(tmp));
				translate(binaryExpr.r, trueLabel, falseLabel);
			} else if (binaryExpr.op == BinaryOp.MINUS) {
				Oprand l = translate(binaryExpr.l), r = translate(binaryExpr.r);
				code.add(new Branch(trueLabel, l, r, BinaryOp.NEQ));
				code.add(new Jump(falseLabel));
			} else if (binaryExpr.op == BinaryOp.COMMA) {
				translate(binaryExpr.l);
				translate_revert(binaryExpr.r, trueLabel, falseLabel);
			} else {
				Oprand cond = toTemp(translate(expr));
				code.add(new Branch(falseLabel, cond, new Const(0), BinaryOp.EQ));
				code.add(new Jump(trueLabel));
				//recycle(cond); 
			}
		} else if (expr instanceof UnaryExpr) {
			UnaryOp op = ((UnaryExpr)expr).op;
			if (op == UnaryOp.NOT) translate_revert(((UnaryExpr)expr).expr, falseLabel, trueLabel);
			else translate(((UnaryExpr)expr).expr, trueLabel, falseLabel);
		} else if (expr instanceof IntLiteral) {
			int k = ((IntLiteral)expr).i;
			if (k == 0) code.add(new Jump(falseLabel));
			else code.add(new Jump(trueLabel));
		} else {
			Oprand cond = toTemp(translate(expr));
			code.add(new Branch(trueLabel, cond, new Const(0), BinaryOp.NEQ));
			code.add(new Jump(falseLabel));
			//recycle(cond);
		}
	}
	
	public void translate_revert(Expr expr, Label trueLabel, Label falseLabel) {
		if (expr instanceof BinaryExpr) {
			BinaryExpr binaryExpr = (BinaryExpr)expr;
			if (binaryExpr.op == BinaryOp.EQ || binaryExpr.op == BinaryOp.GREATER
			 || binaryExpr.op == BinaryOp.GREATER_EQ || binaryExpr.op == BinaryOp.LESS
			 || binaryExpr.op == BinaryOp.LESS_EQ || binaryExpr.op == BinaryOp.NEQ) {
				BinaryOp op = BinaryOp.NEQ;
				if (binaryExpr.op == BinaryOp.EQ) op = BinaryOp.NEQ;
				else if (binaryExpr.op == BinaryOp.NEQ) op = BinaryOp.EQ;
				else if (binaryExpr.op == BinaryOp.LESS) op = BinaryOp.GREATER_EQ;
				else if (binaryExpr.op == BinaryOp.GREATER) op = BinaryOp.LESS_EQ;
				else if (binaryExpr.op == BinaryOp.LESS_EQ) op = BinaryOp.GREATER;
				else if (binaryExpr.op == BinaryOp.GREATER_EQ) op = BinaryOp.LESS;
				
				if (binaryExpr.l.ty.equals(STRING.getInstance())) {
					Temp l = ((TempOprand)toTemp(translate(binaryExpr.l))).temp;
					Temp r = ((TempOprand)toTemp(translate(binaryExpr.r))).temp;
					Temp t = pickTemporary();
					Temp[] params = new Temp[2];
					params[0] = l; params[1] = r;
					code.add(new Call(new Label("_strcmp"), params, t));
					if (op == BinaryOp.EQ)
						code.add(new Branch(falseLabel, new TempOprand(t), new Const(0), BinaryOp.EQ));
					else if (op == BinaryOp.NEQ)
						code.add(new Branch(falseLabel, new TempOprand(t), new Const(0), BinaryOp.NEQ));
					else if (op == BinaryOp.LESS)
						code.add(new Branch(falseLabel, new TempOprand(t), new Const(-1), BinaryOp.EQ));
					else if (op == BinaryOp.GREATER)
						code.add(new Branch(falseLabel, new TempOprand(t), new Const(1), BinaryOp.EQ));
					else if (op == BinaryOp.GREATER_EQ)
						code.add(new Branch(falseLabel, new TempOprand(t), new Const(-1), BinaryOp.NEQ));
					else if (op == BinaryOp.LESS_EQ)
						code.add(new Branch(falseLabel, new TempOprand(t), new Const(1), BinaryOp.NEQ));
					code.add(new Jump(trueLabel));
					//recycle(l); recycle(r); recycle(t);
				} else {
					Oprand l = translate(binaryExpr.l), r = translate(binaryExpr.r);
					code.add(new Branch(falseLabel, l, r, op));
					code.add(new Jump(trueLabel));
					//recycle(l); recycle(r); 
				}
			} else if (binaryExpr.op == BinaryOp.OR) {
				Label tmp = new Label();
				translate_revert(binaryExpr.l, trueLabel, tmp);
				code.add(new LabelQuad(tmp));
				translate_revert(binaryExpr.r, trueLabel, falseLabel);
			} else if (binaryExpr.op == BinaryOp.AND || binaryExpr.op == BinaryOp.MULTIPLY) {
				Label tmp = new Label();
				translate_revert(binaryExpr.l, tmp, falseLabel);
				code.add(new LabelQuad(tmp));
				translate_revert(binaryExpr.r, trueLabel, falseLabel);
			} else if (binaryExpr.op == BinaryOp.MINUS) {
				Oprand l = translate(binaryExpr.l), r = translate(binaryExpr.r);
				code.add(new Branch(falseLabel, l, r, BinaryOp.EQ));
				code.add(new Jump(trueLabel));
			} else if (binaryExpr.op == BinaryOp.COMMA) {
				translate(binaryExpr.l);
				translate_revert(binaryExpr.r, trueLabel, falseLabel);
			} else {
				Oprand cond = toTemp(translate(expr));
				code.add(new Branch(falseLabel, cond, new Const(0), BinaryOp.EQ));
				code.add(new Jump(trueLabel));
				//recycle(cond);
			}
		} else if (expr instanceof UnaryExpr) {
			UnaryOp op = ((UnaryExpr)expr).op;
			if (op == UnaryOp.NOT) translate(((UnaryExpr)expr).expr, falseLabel, trueLabel);
			else translate_revert(((UnaryExpr)expr).expr, trueLabel, falseLabel);
		} else if (expr instanceof IntLiteral) {
			int k = ((IntLiteral)expr).i;
			if (k == 0) code.add(new Jump(falseLabel));
			else code.add(new Jump(trueLabel));
		} else {
			Oprand cond = toTemp(translate(expr));
			code.add(new Branch(trueLabel, cond, new Const(0), BinaryOp.NEQ));
			code.add(new Jump(falseLabel));
			//recycle(cond);
		}
	}
	
	public Oprand translate(BinaryExpr binaryExpr) {
		if (binaryExpr.l.ty.equals(STRING.getInstance())
			&& binaryExpr.r.ty.equals(STRING.getInstance())
			&& (binaryExpr.op == BinaryOp.EQ || binaryExpr.op == BinaryOp.NEQ
			 || binaryExpr.op == BinaryOp.GREATER || binaryExpr.op == BinaryOp.GREATER_EQ
			 || binaryExpr.op == BinaryOp.LESS || binaryExpr.op == BinaryOp.LESS_EQ)) {
			Temp l = ((TempOprand)toTemp(translate(binaryExpr.l))).temp;
			Temp r = ((TempOprand)toTemp(translate(binaryExpr.r))).temp;
			TempOprand t = new TempOprand(pickTemporary());
			Temp[] params = new Temp[2];
			params[0] = l; params[1] = r;
			code.add(new Call(new Label("_strcmp"), params, t.temp));
			if (binaryExpr.op == BinaryOp.EQ)
				code.add(new BinOp(t, t, new Const(0), BinaryOp.EQ));
			else if (binaryExpr.op == BinaryOp.NEQ)
				code.add(new BinOp(t, t, new Const(0), BinaryOp.NEQ));
			else if (binaryExpr.op == BinaryOp.LESS)
				code.add(new BinOp(t, t, new Const(-1), BinaryOp.EQ));
			else if (binaryExpr.op == BinaryOp.GREATER)
				code.add(new BinOp(t, t, new Const(1), BinaryOp.EQ));
			else if (binaryExpr.op == BinaryOp.GREATER_EQ)
				code.add(new BinOp(t, t, new Const(-1), BinaryOp.NEQ));
			else if (binaryExpr.op == BinaryOp.LESS_EQ)
				code.add(new BinOp(t, t, new Const(1), BinaryOp.NEQ));
			//recycle(l); recycle(r);
			return t;
			//code.add(new Jump(trueLabel));
		}
		Oprand l = translate(binaryExpr.l), r = translate(binaryExpr.r);
		if (l instanceof Const && r instanceof Const) {
			int a = ((Const)l).value, b = ((Const)r).value;
			Const ret = new Const(0);
			if (binaryExpr.op == BinaryOp.AND) ret.value = a & b;
			else if (binaryExpr.op == BinaryOp.COMMA) ret.value = b;
			else if (binaryExpr.op == BinaryOp.DIVIDE) ret.value = a / b;
			else if (binaryExpr.op == BinaryOp.EQ) ret.value = (a == b) ? 1 : 0;
			else if (binaryExpr.op == BinaryOp.GREATER) ret.value = (a > b) ? 1 : 0;
			else if (binaryExpr.op == BinaryOp.GREATER_EQ) ret.value = (a >= b) ? 1 : 0;
			else if (binaryExpr.op == BinaryOp.LESS) ret.value = (a < b) ? 1 : 0;
			else if (binaryExpr.op == BinaryOp.LESS_EQ) ret.value = a <= b ? 1 : 0;
			else if (binaryExpr.op == BinaryOp.MINUS) ret.value = a - b;
			else if (binaryExpr.op == BinaryOp.MODULO) ret.value = a % b;
			else if (binaryExpr.op == BinaryOp.MULTIPLY) ret.value = a * b;
			else if (binaryExpr.op == BinaryOp.NEQ) ret.value = a != b ? 1 : 0;
			else if (binaryExpr.op == BinaryOp.OR) ret.value = a | b;
			else if (binaryExpr.op == BinaryOp.PLUS) ret.value = a + b;
			return ret;
		}
		if (binaryExpr.op == BinaryOp.ASSIGN) {
			code.add(new Move(l, r));
			recycle(r);
			return l;
		} else if (binaryExpr.l.ty.equals(STRING.getInstance()) || 
				   binaryExpr.r.ty.equals(STRING.getInstance())) {
			l = toTemp(l); r = toTemp(r);
			Temp tl = ((TempOprand)l).temp;
			Temp tr = ((TempOprand)r).temp;
			if (binaryExpr.l.ty.equals(INT.getInstance())) {
				Temp t = pickTemporary();
				Temp params[] = new Temp[1];
				params[0] = tl;
				code.add(new Call(new Label("intToString"), params, t));
				recycle(tl); tl = t;
			}
			if (binaryExpr.r.ty.equals(INT.getInstance())) {
				Temp t = pickTemporary();
				Temp params[] = new Temp[1];
				params[0] = tr;
				code.add(new Call(new Label("intToString"), params, t));
				recycle(tr); tr = t;
			}
			if (binaryExpr.l.ty.equals(CHAR.getInstance())) {
				Temp t = pickTemporary();
				Temp params[] = new Temp[1];
				params[0] = tl;
				code.add(new Call(new Label("charToString"), params, t));
				recycle(tl); tl = t;
			}
			if (binaryExpr.r.ty.equals(CHAR.getInstance())) {
				Temp t = pickTemporary();
				Temp params[] = new Temp[1];
				params[0] = tr;
				code.add(new Call(new Label("charToString"), params, t));
				recycle(tr); tr = t;
			}
			Temp result;
			if (tl.isTemp) result = tl;
			else if (tr.isTemp) result = tr;
			else result = pickTemporary();
			
			Temp params[] = new Temp[2];
			params[0] = tl; params[1] = tr;
			code.add(new Call(new Label("_strcat"), params, result));
			if (!result.equal(tl)) recycle(tl); 
			if (!result.equal(tr)) recycle(tr);
			return new TempOprand(result);
		} else if (binaryExpr.op == BinaryOp.COMMA) {
			recycle(l);
			return r;
		} else {
			Oprand result;
			if (l instanceof TempOprand && ((TempOprand)l).temp.isTemp) result = l;
			else if (r instanceof TempOprand && ((TempOprand)r).temp.isTemp) result = r;
			else {
				result = new TempOprand(pickTemporary());
			}
			code.add(new BinOp(result, l, r, binaryExpr.op));
			if (l instanceof TempOprand && !((TempOprand)result).temp.equal(((TempOprand)l).temp)) recycle(l);
			if (r instanceof TempOprand && !((TempOprand)result).temp.equal(((TempOprand)r).temp)) recycle(r);
			//recycle(r);
			return result;
		}
	}
	
	public Oprand translate(CharLiteral charLiteral) {
		return new Const((int)charLiteral.c);
	}
	
	public Oprand translate(FieldPostfix fieldPostfix) {
		Oprand s = toTemp(translate(fieldPostfix.expr));
		if (fieldPostfix.expr.ty.isArray()) {
			return new Mem(((TempOprand)s).temp, 0);
		} else if (fieldPostfix.expr.ty.equals(STRING.getInstance())) {
			Temp ret = pickTemporary();
			//local_size = local_size + 1;
			Temp[] params = new Temp[1];
			params[0] = ((TempOprand)s).temp;
			code.add(new Call(new Label("_strlen"), params, ret));
			recycle(s);
			return new TempOprand(ret);
		}
		RECORD ty = (RECORD)fieldPostfix.expr.ty;
		int index = 0, size = 0;
		for (RECORD.RecordField item : ty.fields)
			if (item.name.equals(fieldPostfix.field)) {
				index = item.index;
				break;
			}
		for (RECORD.RecordField item : ty.fields)
			if (item.index < index)
				size += item.type.size;
		return new Mem(((TempOprand)s).temp, size);
	}
	
	public Oprand translate(FunctionCall functionCall) {
		Label func = new Label(functionCall.expr.toString());
		if (func.toString() == "printString") {
			if (functionCall.args.arguments.get(0) instanceof BinaryExpr) {
				BinaryExpr expr = (BinaryExpr)functionCall.args.arguments.get(0);
				if (expr.op == BinaryOp.PLUS && expr.l.ty.equals(STRING.getInstance())) {
					Oprand result;
					ArgumentList arg = new ArgumentList();
					ArgumentList arg2 = new ArgumentList();
					arg.add(expr.l);
					arg2.add(expr.r);
					FunctionCall func1 = new FunctionCall(functionCall.expr, arg);
					result = translate(func1);
					
					if (expr.r.ty.equals(STRING.getInstance())) {
						FunctionCall func2 = new FunctionCall(functionCall.expr, arg2);
						result = translate(func2);
					} else {
						if (expr.r.ty.equals(INT.getInstance())) {
							FunctionCall tmpf = new FunctionCall(new Id(Symbol.valueOf("intToString")), arg2);
							result = translate(tmpf);
							Temp params[] = new Temp[1];
							params[0] = ((TempOprand)toTemp(result)).temp;
							Temp ret = pickTemporary();
							code.add(new Call(func, params, ret));
							return new TempOprand(ret);
						} else {
							FunctionCall tmpf = new FunctionCall(new Id(Symbol.valueOf("charToString")), arg2);
							result = translate(tmpf);
							Temp params[] = new Temp[1];
							params[0] = ((TempOprand)toTemp(result)).temp;
							Temp ret = pickTemporary();
							code.add(new Call(func, params, ret));
							return new TempOprand(ret);
						}
					}
					return result;
				}
			}
		}
		
		
		int i = 0;
		Temp result = pickTemporary();
		
		Temp params[] = new Temp[functionCall.args.arguments.size()];
		for (Expr expr : functionCall.args.arguments) {
			Oprand oprand = toTemp(translate(expr));
			params[i ++] = (((TempOprand) oprand).temp);
			//recycle(oprand);
		}
		
		//local_size = local_size + 1;
		code.add(new Call(func, params, result));
		arg_size = params.length > arg_size ? params.length : arg_size;
		for (Temp t : params)
			if (!result.equal(t)) recycle(t);
		//System.out.println(result);
		//System.out.println(free_temporary.contains(result));
		return new TempOprand(result);
	}
	
	public Oprand translate(Id id) {
		return new TempOprand(env.get(id.sym));
	}
	
	public Oprand translate(IntLiteral intLiteral) {
		return new Const(intLiteral.i);
	}
	
	public Oprand translate(NewArray newArray) {
		Temp t = new Temp();
		int size = newArray.base_type.size;
		Oprand expr = translate(newArray.expr);
		Oprand length = expr;
		if (expr instanceof Const) {
			expr = toTemp(new Const(size * ((Const)expr).value + 4));
			//((TempOprand)expr).temp.isTemp = false;
		} else {
			if (expr instanceof TempOprand) {
				((TempOprand)expr).temp.isTemp = false;
				expr = new TempOprand(new Temp());
				//System.out.println("!" + new Move(expr, length));
				code.add(new Move(expr, length));
			} else { 
				expr = toTemp(expr);
			}
			if (size != 1) code.add(new BinOp(expr, expr, new Const(size), BinaryOp.MULTIPLY));
			code.add(new BinOp(expr, expr, new Const(4), BinaryOp.PLUS));
		}
		Temp[] params = new Temp[1];
		params[0] = ((TempOprand)expr).temp;
		//if (t == ((TempOprand)length).temp) t = pickTemporary();
		code.add(new Call(new Label("_malloc"), params, t));
		code.add(new Move(new Mem(t, 0), length));
		//recycle(expr); recycle(length);
		return new TempOprand(t);
	}
	
	public Oprand translate(NewRecord newRecord) {
		RECORD ty = (RECORD)newRecord.ty;
		int size = 0;
		for (RECORD.RecordField item : ty.fields)
			size = size + item.type.size;
		TempOprand t = new TempOprand(pickTemporary());
		//local_size = local_size + 1;
		code.add(new Move(t, new Const(size)));
		Temp[] params = new Temp[1];
		params[0] = t.temp;
		Temp ret = pickTemporary();
		//local_size = local_size + 1;
		code.add(new Call(new Label("_malloc"), params, ret));
		//recycle(t);
		return new TempOprand(ret);
	}
	
	public Oprand translate(Null expr) {
		return new Const(0);
	}
	
	public Oprand translate(StringLiteral stringLiteral) {
		string_literal.add(stringLiteral.s);
		Label l = new Label();
		string_label.add(l);
		return new LabelAddress(l);
	}
	
	public Oprand translate(SubscriptPostfix subscriptPostfix) {
		Oprand s = toTemp(translate(subscriptPostfix.expr));
		Oprand t = translate(subscriptPostfix.subscript);
		int size = subscriptPostfix.ty.size;
		if (t instanceof Const) {
			int tmp = subscriptPostfix.expr.ty.equals(STRING.getInstance()) ? 0 : 4;
			Mem ret = new Mem(((TempOprand)s).temp, size * ((Const)t).value + tmp);
			ret.length = subscriptPostfix.expr.ty.equals(STRING.getInstance()) ? 1 : 4;
			//recycle(s);
			return ret;
		}
		TempOprand res = new TempOprand(new Temp());
		code.add(new BinOp(res, t, new Const(size), BinaryOp.MULTIPLY));
		if (!subscriptPostfix.expr.ty.equals(STRING.getInstance()))
			code.add(new BinOp(res, res, new Const(4), BinaryOp.PLUS));
		code.add(new BinOp(res, s, res, BinaryOp.PLUS));
		Oprand ret = new Mem(res.temp, 0);
		
		if (subscriptPostfix.expr.ty.equals(STRING.getInstance()))
			((Mem)ret).length = 1;
		
		//recycle(s);
		return ret;
	}
	
	public Oprand translate(UnaryExpr unaryExpr) {
		Oprand t = translate(unaryExpr.expr);
		if (t instanceof Const) {
			Const ret = (Const)t;
			if (unaryExpr.op == UnaryOp.NOT) ret.value = ret.value == 0 ? 1 : 0;
			else if (unaryExpr.op == UnaryOp.MINUS) ret.value = -ret.value;
			return ret;
		}
		TempOprand ret;
		if (!((TempOprand)t).temp.isTemp) ret = new TempOprand(pickTemporary());
		else ret = (TempOprand)t;
		if (unaryExpr.op != UnaryOp.PLUS) code.add(new SinOp(ret, t, unaryExpr.op));
		if (!ret.temp.equal(((TempOprand)t).temp)) recycle(t);
		return ret;
	}
	
	public String getName() { return name; }
	
	@Override
	public String toString() {
		String ret = "";
		for (Quad quad : code) {
			if (quad instanceof LabelQuad)
				ret = ret + quad.toString() + "\n";
			else ret = ret + "\t" + quad.toString() + "\n";
		}
		return ret;
	}
	
	public String DataSegment() {
		String ret = "";
		int index = 0;
		
		for (Label label : string_label) {
			ret += label.toString() + ":\t.asciiz\t";
			ret += "\"" + string_literal.get(index) + "\"\n";
			index ++;
		}
		return ret;
	}
	
	private void asm(BinOp quad) {
		String type = "";
		Temp dst = asmTemp(quad.dst);
		Temp l = asmTemp(quad.left);
		if (quad.op == BinaryOp.PLUS) type = "add";
		else if (quad.op == BinaryOp.AND) type = "and";
		else if (quad.op == BinaryOp.OR) type = "or";
		else if (quad.op == BinaryOp.LESS) type = "slt";
		else if (quad.op == BinaryOp.DIVIDE) type = "div";
		else if (quad.op == BinaryOp.EQ) type = "seq";
		else if (quad.op == BinaryOp.GREATER) type = "sgt";
		else if (quad.op == BinaryOp.GREATER_EQ) type = "sge";
		else if (quad.op == BinaryOp.LESS_EQ) type = "sle";
		else if (quad.op == BinaryOp.MINUS) type = "sub";
		else if (quad.op == BinaryOp.MODULO) type = "rem";
		else if (quad.op == BinaryOp.MULTIPLY) type = "mul";
		else if (quad.op == BinaryOp.NEQ) type = "sne";
		if (quad.right instanceof Const) {
			int r = ((Const)quad.right).value;
			if (type == "mul" || type == "div" || type == "rem") {
				for (int i = 0; i < 32; ++ i)
					if (r == (1 << i)) {
						if (type == "mul") type = "sll";
						if (type == "div") type = "srl";
						if (type == "rem") {
							type = "and";
							i = (1 << i) - 1;
						}
						atoms.add(new IARI(type, dst, l, i));
						if (!dst.equal(l)) recycle(l);
						return;
					}
			}
			atoms.add(new IARI(type, dst, l, r));
			if (!dst.equal(l)) recycle(l);
		} else {
			Temp r = asmTemp(quad.right);
			atoms.add(new ARI(type, dst, l, r));
			if (!dst.equal(l)) recycle(l);
			if (!dst.equal(r)) recycle(r);
		}
	}
	
	private void asm(Branch quad) {
		//System.out.println(quad);
		String type = "";
		if (quad.op == BinaryOp.EQ) type = "beq";
		else if (quad.op == BinaryOp.NEQ) type = "bne";
		else if (quad.op == BinaryOp.LESS) type = "blt";
		else if (quad.op == BinaryOp.GREATER) type = "bgt";
		else if (quad.op == BinaryOp.LESS_EQ) type = "ble";
		else if (quad.op == BinaryOp.GREATER_EQ) type = "bge";
		
		//Temp s = ((TempOprand)toTemp(quad.left)).temp;
		Temp s = asmTemp(quad.left);
		if (quad.right instanceof Const && ((Const)quad.right).value == 0) {
			type = type + 'z';
			atoms.add(new BRANCH(type, s, quad.label));
			recycle(s);
		} else {
			//Temp t = ((TempOprand)toTemp(quad.right)).temp;
			Temp t = asmTemp(quad.right);
			atoms.add(new COMP(type, s, t, quad.label));
			recycle(s); recycle(t);
		}
	}
	
	private void asm(Call quad) {
		atoms.add(new CALL(quad.function, quad.params, quad.result));
		/*for (Temp t : quad.params)
			if (!quad.result.equal(t))
				recycle(t);*/
	}
	
	private void asm(Jump quad) {
		atoms.add(new JMP("j", quad.label));
	}
	
	private void asm(LabelQuad quad) {
		atoms.add(new LabelAtom(quad.label));
	}
	
	private void asm(Move quad) {
		if (quad.dst instanceof TempOprand) {
			Temp dst = ((TempOprand)quad.dst).temp;
			if (quad.src instanceof Const)
				atoms.add(new LI(dst, ((Const)quad.src).value));
			else if (quad.src instanceof TempOprand) {
				Temp src = ((TempOprand)quad.src).temp;
				if (atoms.size() == 0) {
					atoms.add(new SARI("move", dst, src));
				} else {
					Atom a = atoms.get(atoms.size() - 1);
					if (a instanceof ARI && ((ARI)a).dst.isTemp && ((ARI)a).dst.equal(src)) {
						((ARI)a).dst = dst;
					} else if (a instanceof IARI && ((IARI)a).dst.isTemp && ((IARI)a).dst.equal(src)) {
						((IARI)a).dst = dst;
					} else if (a instanceof SARI && ((SARI)a).dst.isTemp && ((SARI)a).dst.equal(src)) {
						((SARI)a).dst = dst;
					} else if (a instanceof CALL && ((CALL)a).result.isTemp && ((CALL)a).result.equal(src)) {
						((CALL)a).result = dst;
					} else {
						atoms.add(new SARI("move", dst, src));
					}
				}
			} else if (quad.src instanceof LabelAddress) {
				atoms.add(new LA(dst, ((LabelAddress)quad.src).label));
			} else if (quad.src instanceof Mem) {
				Mem src = (Mem)quad.src;
				//System.out.println(name);
				String type = src.length == 4 ? "lw" : "lb";
				//System.out.println(type);
				atoms.add(new MEM(type, dst, src.base, src.offset));
			}
		} else {
			Mem dst = (Mem)quad.dst;
			String type = dst.length == 4 ? "sw" : "sb";
			//System.out.println(type);
			if (quad.src instanceof TempOprand) {
				Temp src = ((TempOprand)quad.src).temp;
				//System.out.println(src.toString());
				//System.out.println(dst.base.toString());
				atoms.add(new MEM(type, src, dst.base, dst.offset));
			} else {
				Temp temporary = pickTemporary();
				if (quad.src instanceof Const)
					atoms.add(new LI(temporary, ((Const)quad.src).value));
				else if (quad.src instanceof LabelAddress) {
					atoms.add(new LA(temporary, ((LabelAddress)quad.src).label));
				} else if (quad.src instanceof Mem) {
					Mem src = (Mem)quad.src;
					String t = src.length == 4 ? "lw" : "lb";
					atoms.add(new MEM(t, temporary, src.base, src.offset));
				}
				atoms.add(new MEM(type, temporary, dst.base, dst.offset));
				recycle(temporary);
			}
		}
	}
	

	private void asm(Return quad) {
		if (!(quad.value instanceof TempOprand) && !(quad.value instanceof Const)) {
			TempOprand t = new TempOprand(asmTemp(quad.value));
			atoms.add(new RETURN(t));
			recycle(t);
		} else {
			atoms.add(new RETURN(quad.value));
		}
	}
	
	private void asm(SinOp quad) {
		String type = "neg";
		if (quad.op == UnaryOp.NOT) type = "not";
		else if (quad.op == UnaryOp.PLUS) return;
		atoms.add(new SARI(type, ((TempOprand)quad.dst).temp, ((TempOprand)quad.src).temp));
		//recycle(quad.src);
	}
	
}
