/**
 * 
 */
package fatworm.stmt;

import java.util.ArrayList;
import java.util.List;

import fatworm.db.Schema;
import fatworm.db.Transaction;
import fatworm.expr.*;
import fatworm.scan.*;


/**
 * @author mrain
 *
 */
public class Select extends Action {
	public boolean distinct;
	public List<Expr> columns;
	public List<Object> from;
	public List<String> table_alias, column_alias;
	public Expr where, having;
	public String groupby;
	public List<String> order_colname;
	public List<Boolean> order_asc;
	
	Schema consumedSchema;
	
	public Select(List<Expr> columns, List<String> column_alias, List<Object> from, List<String> table_alias, 
			Expr where, String groupby, Expr having, List<String> order_colname, List<Boolean> order_asc) {
		this.columns = columns;
		this.column_alias = column_alias;
		this.from = from;
		this.table_alias = table_alias;
		this.where = where;
		this.having = having;
		this.groupby = groupby;
		this.order_colname = order_colname;
		this.order_asc = order_asc;
	}

	public Scan fromPart(Transaction tx) {
		ArrayList<Scan> s = new ArrayList<Scan>();
		int index = 0;
		for (Object obj : from) {
			String alias = table_alias.get(index ++);
			if (obj instanceof String) {
				if (alias == null)
					s.add(new TableScan((String)obj, tx));
				else s.add(new TableScan((String)obj, tx, alias));
			} else {
				Scan sub = ((Select)obj).scan(tx);
				//System.out.println(obj + " " + sub);
				if (alias != null)
					sub.getSchema().renameTable(alias);
				s.add(sub);
			}
		}
		if (s.size() == 1) return s.get(0);
		else {
			Scan ret = s.get(0);
			for (int i = 1; i < s.size(); ++ i)
				ret = new ProductScan(ret, s.get(i));
			return ret;
		}
	}
	
	public Scan wherePart(Transaction tx, Scan fst) {
		if (where != null) {
			if (consumedSchema == null) {
				where.consumeFields(fst.getSchema());
			} else {
				Schema tmp = new Schema(consumedSchema);
				tmp.putAll(fst.getSchema());
				//if (!(fst instanceof OrderScan))
				where.consumeFields(tmp);
			}
			//System.out.println(where);
			where.scan(tx);
			
			return new SelectScan(fst, where);
		} else return fst;
	}
	
	public Scan simpleScan(Transaction tx) {
		Scan ret = fromPart(tx);
		ret = wherePart(tx, ret);
		return ret;
	}
	
	public Scan scan(Transaction tx) {
		if (from.size() > 0) {
			//cleverPush();
			Scan ret = simpleScan(tx);
			
			if (having != null) {
				having.consumeFields(ret.getSchema());
			}
			
			if (columns.size() != 0) {
				Schema sch;
				if (consumedSchema == null)
					sch = ret.getSchema();
				else {
					sch = new Schema(consumedSchema);
					sch.putAll(ret.getSchema());
				}
				
				boolean AggFn = groupby.length() > 0;
				
				for (Expr expr : columns) {
					expr.consumeFields(sch);
					expr.scan(tx);
					AggFn |= expr.hasAggFn();
				}
				if (having != null) AggFn |= having.hasAggFn();
				
				if (!AggFn) {
					ret = new ProjectScan(ret, columns, column_alias);
				} else {
					List<Expr> funcs = new ArrayList<Expr>();
					if (having != null) having.extractAggFn(funcs);
					if (groupby.length() > 0) {
						List<String> col = new ArrayList<String>();
						List<Boolean> tmp = new ArrayList<Boolean>();
						col.add(groupby);
						tmp.add(true);
						ret = new OrderScan(ret, col, tmp);
						ret = new GroupScan(ret, groupby, columns, column_alias);
					} else {
						ret = new GroupScan(ret, "", columns, column_alias);
					}
					((GroupScan)ret).addMeta(funcs);
				}
			}
			
			if (having != null) {
				having.scan(tx);
				ret = new SelectScan(ret, having);
				((SelectScan)ret).takeHaving();
				having.consumeFields(ret.getSchema());
			}
			if (order_colname.size() > 0)
				ret = new OrderScan(ret, order_colname, order_asc);
			
			//System.out.println(columns);
			if (columns.size() > 0)
				ret = new EliminationScan(ret, columns, column_alias);
			if (distinct)
				ret = new DistinctScan(ret);
			return ret;
		} else
			return new ConstantScan(columns);
	}
	
	public void consume(Schema schema) {
		if (consumedSchema == null)
			this.consumedSchema = new Schema(schema);
		else
			consumedSchema.putAll(schema);
	}
	/**
	 * Evaluation order:
	 * From -> Where -> Group/ProjExpr -> Having -> Order -> Projection
	 * @throws Exception 
	 */
}
 