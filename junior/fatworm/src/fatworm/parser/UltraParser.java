/**
 * 
 */
package fatworm.parser;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.*;

import fatworm.db.ColumnType;
import fatworm.db.Schema;
import fatworm.expr.*;
import fatworm.stmt.Action;
import fatworm.stmt.CreateDB;
import fatworm.stmt.CreateIndex;
import fatworm.stmt.CreateTable;
import fatworm.stmt.CreateUniqueIndex;
import fatworm.stmt.Delete;
import fatworm.stmt.DropDB;
import fatworm.stmt.DropIndex;
import fatworm.stmt.DropTable;
import fatworm.stmt.InsertSubquery;
import fatworm.stmt.InsertValues;
import fatworm.stmt.Select;
import fatworm.stmt.Update;
import fatworm.stmt.UseDB;
import fatworm.value.Int;
import fatworm.value.Time;
import fatworm.value.Value;
import fatworm.value.FloatNumber;
import fatworm.value.Varchar;

/**
 * @author MRain
 *
 */
public final class UltraParser {
	
	public static Object parse(String sql) throws Exception {
		ANTLRStringStream in = new ANTLRStringStream(sql);
		FatwormLexer lexer = new FatwormLexer(in);
		FatwormParser parser = new FatwormParser(new CommonTokenStream(lexer));
		CommonTree tree = (CommonTree)parser.statement().tree;
		if (tree.getType() == FatwormParser.SELECT ||
			tree.getType() == FatwormParser.SELECT_DISTINCT)
			return translateExpr(tree);
		return translate(tree);
	}
	
	public static Action translate(CommonTree tree) throws Exception {
		int type = tree.getType();
		switch (type) {
			case FatwormParser.CREATE_DATABASE:
				return new CreateDB(tree.getChild(0).getText().toUpperCase());
			
			case FatwormParser.DROP_DATABASE:
				return new DropDB(tree.getChild(0).getText().toUpperCase());
				
			case FatwormParser.USE_DATABASE:
				return new UseDB(tree.getChild(0).getText().toUpperCase());
				
			case FatwormParser.CREATE_TABLE: {
				String name = tree.getChild(0).getText().toUpperCase();
				String primary = "";
				
				Schema schema = new Schema();
				List<String> columns = new ArrayList<String>();
				for (int i = 1; i < tree.getChildCount(); ++ i) {
					CommonTree subtree = (CommonTree) tree.getChild(i);
					if (subtree.getType() == FatwormParser.CREATE_DEFINITION) {
						String cname = subtree.getChild(0).getText().toUpperCase();
						ColumnType col = (ColumnType) getColumnType(subtree);
						if (schema.hasColumn(cname)) {
							throw new Exception("Duplicated key definition");
						}
						schema.put(cname, col);
						columns.add(cname);
					} else {
						primary = subtree.getChild(0).getText().toUpperCase();
					}
				}
				if (primary != "") {
					if (!schema.hasColumn(primary)) {
						throw new Exception("undefined primary key");
					}
				}
				return new CreateTable(name, schema, columns, primary);
			}
				
			
				
			case FatwormParser.DROP_TABLE: {
				List<String> tables = new ArrayList<String>(tree.getChildCount());
				for (int i = 0; i < tree.getChildCount(); ++ i)
					tables.add(tree.getChild(i).getText().toUpperCase());
				return new DropTable(tables);
			}
			
			case FatwormParser.INSERT_VALUES: {
				String table = tree.getChild(0).getText().toUpperCase();
				CommonTree subtree = (CommonTree) tree.getChild(1);
				List<Expr> values = new ArrayList<Expr>(subtree.getChildCount());
				for (int i = 0; i < subtree.getChildCount(); ++ i)
					values.add((Expr)translateExpr((CommonTree) subtree.getChild(i)));
				return new InsertValues(table, null, values);
			}
			
			case FatwormParser.INSERT_COLUMNS: {
				String table = tree.getChild(0).getText().toUpperCase();
				int count = tree.getChildCount();
				
				List<String> columns = new ArrayList<String>(count - 2);
				for (int i = 1; i < count - 1; ++ i) {
					CommonTree col = (CommonTree) tree.getChild(i);
					if (col.getChildCount() == 0)
						columns.add(tree.getChild(i).getText().toUpperCase());
					else {
						String tableName = col.getChild(0).getText().toUpperCase();
						String colName = col.getChild(1).getText().toUpperCase();
						if (tableName != table) {
							throw new RuntimeException("Invalid insert argument");
						}
						columns.add(colName);
					}
				}
				CommonTree subtree = (CommonTree) tree.getChild(2);
				List<Expr> values = new ArrayList<Expr>(subtree.getChildCount());
				for (int i = 0; i < subtree.getChildCount(); ++ i)
					values.add((Expr)translateExpr((CommonTree) subtree.getChild(i)));
				
				//System.out.println(columns.size() + " " + values.size());
				if (columns.size() != values.size())
					throw new RuntimeException("Invalid insert argument: values do not match columns");
				return new InsertValues(table, columns, values);
			}
			
			case FatwormParser.INSERT_SUBQUERY: {
				String table = tree.getChild(0).getText().toUpperCase();
				return new InsertSubquery(table, (Select) translateExpr((CommonTree) tree.getChild(1)));
			}
			
			case FatwormParser.DELETE: {
				String table = tree.getChild(0).getText().toUpperCase();
				if (tree.getChildCount() > 1)
					return new Delete(table, (Expr) translateExpr((CommonTree) tree.getChild(1)));
				else {
					return new Delete(table, null);
				}
			}
			
			case FatwormParser.UPDATE: {
				String table = tree.getChild(0).getText().toUpperCase();
				List<String> columns = new ArrayList<String>();
				List<Object> values = new ArrayList<Object>();
				Expr where = null;
				for (int i = 1; i < tree.getChildCount(); ++ i) {
					CommonTree subtree = (CommonTree)tree.getChild(i);
					if (subtree.getType() == FatwormParser.UPDATE_PAIR) {
						columns.add(subtree.getChild(0).getText().toUpperCase());
						values.add(translateExpr((CommonTree) subtree.getChild(1)));
					} else where = (Expr)translateExpr(subtree);
				}
				return new Update(table, columns, values, where);
			}
			
			case FatwormParser.CREATE_INDEX: {
				return new CreateIndex(tree.getChild(1).getText(), tree.getChild(0).getText(),
								tree.getChild(2).getText());
			}
			
			case FatwormParser.CREATE_UNIQUE_INDEX: {
				return new CreateUniqueIndex(tree.getChild(1).getText(), tree.getChild(0).getText(),
						tree.getChild(2).getText());
			}
			
			case FatwormParser.DROP_INDEX: {
				return new DropIndex(tree.getChild(1).getText(), tree.getChild(0).getText());
			}
			
			default:
				return null;
		}
	}
	
	public static Object translateExpr(CommonTree tree) throws Exception {
		int type = tree.getType();
		switch (type) {
			case FatwormParser.SELECT:
			case FatwormParser.SELECT_DISTINCT: {
				int i = 0;
				
				List<Expr> proj = new ArrayList<Expr>();
				List<Object> from = new ArrayList<Object>();
				List<String> column_alias = new ArrayList<String>();
				List<String> table_alias = new ArrayList<String>();
				List<String> order = new ArrayList<String>();
				List<Boolean> order_asc = new ArrayList<Boolean>();
				Expr where = null, having = null;
				String groupby = "";
				for (i = 0; i < tree.getChildCount(); ++ i) {
					CommonTree subtree = (CommonTree) tree.getChild(i);
					if (subtree.getType() == FatwormParser.FROM) {
						for (int j = 0; j < subtree.getChildCount(); ++ j) {
							CommonTree tableTree = (CommonTree)subtree.getChild(j);
							String alias = null;
							if (tableTree.getType() == FatwormParser.AS) {
								alias = tableTree.getChild(1).getText().toUpperCase();
								tableTree = (CommonTree) tableTree.getChild(0);
							}
							if (tableTree.getChildCount() > 0)
								from.add(translateExpr(tableTree));
							else from.add(tableTree.getText().toUpperCase());
							table_alias.add(alias);
						}
					} else if (subtree.getType() == FatwormParser.WHERE) {
						where = (Expr)translateExpr((CommonTree)subtree.getChild(0));
					} else if (subtree.getType() == FatwormParser.GROUP) {
						//System.out.println(subtree.toStringTree());
						CommonTree t = (CommonTree) subtree.getChild(0);
						if (t.getChildCount() == 0)
							groupby = t.getText().toUpperCase();
						else groupby = t.getChild(0).getText().toUpperCase() + "." + t.getChild(1).getText().toUpperCase();
						//groupby = subtree.getChild(0).getText();
					} else if (subtree.getType() == FatwormParser.HAVING) {
						having = (Expr)translateExpr((CommonTree)subtree.getChild(0));
					} else if (subtree.getType() == FatwormParser.ORDER) {
						for (int j = 0; j < subtree.getChildCount(); ++ j) {
							CommonTree orderTree = (CommonTree)subtree.getChild(j);
							if (orderTree.getType() == FatwormParser.ASC) {
								String order_by_name = "";
								CommonTree nameTree = (CommonTree) orderTree.getChild(0);
								if (nameTree.getChildCount() == 0)
									order_by_name = nameTree.getText().toUpperCase();
								else order_by_name = nameTree.getChild(0).getText().toUpperCase() + "." + nameTree.getChild(1).getText().toUpperCase();
								order.add(order_by_name);
								order_asc.add(true);
							} else if (orderTree.getType() == FatwormParser.DESC) {
								String order_by_name = "";
								CommonTree nameTree = (CommonTree) orderTree.getChild(0);
								if (nameTree.getChildCount() == 0)
									order_by_name = nameTree.getText().toUpperCase();
								else order_by_name = nameTree.getChild(0).getText().toUpperCase() + "." + nameTree.getChild(1).getText().toUpperCase();									
								order.add(order_by_name);
								order_asc.add(false);
							} else {
								String order_by_name = "";
								if (orderTree.getChildCount() == 0)
									order_by_name = orderTree.getText().toUpperCase();
								else order_by_name = orderTree.getChild(0).getText().toUpperCase() + "." + orderTree.getChild(1).getText().toUpperCase();										
								order.add(order_by_name);
								order_asc.add(true);
							}
						}
					} else {
						if (subtree.getType() == FatwormParser.AS) {
							column_alias.add(subtree.getChild(1).getText().toUpperCase());
							subtree = (CommonTree) subtree.getChild(0);
						} else column_alias.add(null);
						if (subtree.getChildCount() == 0) {
							if (subtree.getText().charAt(0) != '*')
								proj.add((Expr)translateExpr(subtree));
						} else
							proj.add((Expr)translateExpr(subtree));
					}
				}
				Select ret = new Select(proj, column_alias, from, table_alias, where, groupby,
								having, order, order_asc);
				ret.distinct = (type == FatwormParser.SELECT_DISTINCT);
				return ret;
			}
			
			case FatwormParser.HAVING:
			case FatwormParser.WHERE:
				return translateExpr((CommonTree) tree.getChild(0));
				
			case FatwormParser.OR:
				return new BinaryOp("OR", (Expr)translateExpr((CommonTree)tree.getChild(0)), 
								(Expr)translateExpr((CommonTree)tree.getChild(1)));
			
			case FatwormParser.AND:
				return new BinaryOp("AND", (Expr)translateExpr((CommonTree)tree.getChild(0)), 
						(Expr)translateExpr((CommonTree)tree.getChild(1)));
			
			case FatwormParser.MAX:
				return new UnaryOp("MAX", (Expr)translateExpr((CommonTree)tree.getChild(0)));
			
			case FatwormParser.MIN:
				return new UnaryOp("MIN", (Expr)translateExpr((CommonTree)tree.getChild(0)));
				
			case FatwormParser.COUNT:
				return new UnaryOp("COUNT", (Expr)translateExpr((CommonTree)tree.getChild(0)));
					
			case FatwormParser.SUM:
				return new UnaryOp("SUM", (Expr)translateExpr((CommonTree)tree.getChild(0)));
					
			case FatwormParser.AVG:
				return new UnaryOp("AVG", (Expr)translateExpr((CommonTree)tree.getChild(0)));
					
			case FatwormParser.EXISTS:
				return new Exists((Select)translateExpr((CommonTree)tree.getChild(0)));
				
			case FatwormParser.NOT_EXISTS:
				return new NotExists((Select)translateExpr((CommonTree)tree.getChild(0)));
				
			case FatwormParser.ANY:
			case FatwormParser.ALL: {
				Expr left = (Expr)translateExpr((CommonTree)tree.getChild(0));
				Select right = (Select)translateExpr((CommonTree)tree.getChild(2));
				String op = tree.getChild(1).getText();
				String quantifier = tree.getText().toUpperCase();
				return new Quantifier(quantifier, op, left, right);
			}

			case FatwormParser.IN: {
				Expr left = (Expr)translateExpr((CommonTree)tree.getChild(0));
				Select right = (Select)translateExpr((CommonTree)tree.getChild(1));
				String op = "=";
				String quantifier = "ANY";
				return new Quantifier(quantifier, op, left, right);
			}
			
			case FatwormParser.NULL:
				return new Item("NULL");
			
			case FatwormParser.TRUE:
				return new Item("TRUE");
				
			case FatwormParser.FALSE:
				return new Item("FALSE");
				
			//case FatwormParser.DEFAULT:
			//	return new ColumnRef("");
				
			default: {
				if (tree.getChildCount() == 0) {
					String text = tree.getText();
					String value = "0123456789\'";
					if (value.contains(text.subSequence(0, 1)) || 
						value.toUpperCase().compareTo("DEFAULT") == 0)
						return new Item(text);
					else return new ColumnRef(text.toUpperCase());
				} else if (tree.getChildCount() == 1) {
					String text = tree.getText();
					Expr arg = (Expr)translateExpr((CommonTree)tree.getChild(0));
					return new UnaryOp(text, arg);
				} else {
					// tree has two childs
					//System.out.println(tree.toStringTree());
					//if (tree.getChildCount() == 1) throw new Exception("wtf");
					String text = tree.getText();
					if (text.compareTo(".") == 0) {
						return new ColumnRef(tree.getChild(1).getText().toUpperCase(), tree.getChild(0).getText().toUpperCase());
					}
					Expr arg0 = (Expr)translateExpr((CommonTree)tree.getChild(0));
					Expr arg1 = (Expr)translateExpr((CommonTree)tree.getChild(1));
					return new BinaryOp(text, arg0, arg1);
				}
			}
		}
	}
	
	public static ColumnType getColumnType(CommonTree tree) throws Exception {
		CommonTree typespec = (CommonTree) tree.getChild(1);
		String typeName = typespec.getText().toUpperCase();
		ColumnType ret;
		if (typeName.compareTo("INT") == 0)
			ret = new ColumnType(Types.INTEGER);
		else if (typeName.compareTo("FLOAT") == 0)
			ret = new ColumnType(Types.FLOAT);
		else if (typeName.compareTo("CHAR") == 0)
			ret = new ColumnType(Types.CHAR);
		else if (typeName.compareTo("DATETIME") == 0)
			ret = new ColumnType(Types.DATE);
		else if (typeName.compareTo("BOOLEAN") == 0)
			ret = new ColumnType(Types.BOOLEAN);
		else if (typeName.compareTo("DECIMAL") == 0)
			ret = new ColumnType(Types.DECIMAL);
		else if (typeName.compareTo("TIMESTAMP") == 0)
			ret = new ColumnType(Types.TIMESTAMP);
		else if (typeName.compareTo("VARCHAR") == 0)
			ret = new ColumnType(Types.VARCHAR);
		else return null;
		
		// Resolve args
		
		if (typespec.getChildCount() >= 1) {
			String arg0 = typespec.getChild(0).getText();
			ret.setArg0(Integer.parseInt(arg0));
		}
		
		if (typespec.getChildCount() >= 2) {
			String arg1 = typespec.getChild(1).getText();
			ret.setArg1(Integer.parseInt(arg1));
		}
		
		// Take Suffix
		for (int i = 2; i < tree.getChildCount(); ++ i) {
			CommonTree subtree = (CommonTree) tree.getChild(i);
			if (subtree.getType() == FatwormParser.AUTO_INCREMENT)
				ret.setAutoIncrement(true);
			if (subtree.getType() == FatwormParser.NULL)
				ret.setNotNull(true);
			if (subtree.getType() == FatwormParser.DEFAULT)
				ret.setDefault((Value)resolve(subtree.getChild(0).getText(), ret));
		}
		return ret;
	}
	
	public static Value resolve(String value, ColumnType type) throws Exception {
		// TODO: to Value
		switch (type.getType()) {
			case java.sql.Types.INTEGER:
				return new Int(Integer.parseInt(value));

			case java.sql.Types.DECIMAL:
			case java.sql.Types.FLOAT:
				return new FloatNumber(Float.parseFloat(value));
				
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:
				/*if (value.length() > type.getArg0())
					value = value.substring(0, type.getArg0());
				return new Varchar(value);*/
				return Value.parse(value);

			case java.sql.Types.DATE:	
			case java.sql.Types.TIMESTAMP:
				return new Time(value);
			
			//	return java.sql.Date.valueOf(value);
				
			//	return Double.parseDouble(value);
		}
		return null;
	}
}
