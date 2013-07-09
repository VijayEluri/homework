package fatworm.driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fatworm.db.ColumnType;
import fatworm.db.Schema;
import fatworm.db.Table;
import fatworm.db.Transaction;
import fatworm.parser.UltraParser;
import fatworm.scan.DeleteScan;
import fatworm.scan.Scan;
import fatworm.scan.UpdateScan;
import fatworm.stmt.*;
import fatworm.value.*;
import fatworm.expr.ColumnRef;
import fatworm.expr.Expr;
import fatworm.expr.Item;
import fatworm.expr.Subquery;

public class fwStatement implements Statement {
	
	Transaction tx;
	Scan scan;
	
	public fwStatement(Transaction tx) {
		this.tx = tx;
	}
	
	@Override
	public ResultSet getResultSet() throws SQLException {
		return new fwResultSet(scan);
	}
	
	@Override
	public boolean execute(String sql) throws SQLException {
		try {
			Action act = (Action) UltraParser.parse(sql);
			if (act instanceof Select) {
				// TODO
				scan = ((Select)act).scan(tx);
				//System.out.println(act + " " + scan);
				scan = scan.KillOrder(new ArrayList<String>(), new ArrayList<Boolean>());
				//System.out.println(scan.toString());
			} else if (act instanceof Update) {
				
				Update update = (Update) act;
				if (!tx.getCurrent().hasTable(update.table))
					return false;
				Table table = tx.getCurrent().get(update.table);
				List<UpdatePair> actions = new ArrayList<UpdatePair>();
				for (int i = 0; i < update.values.size(); ++ i) {
					String name = update.colname.get(i);
					ColumnType type = table.getSchema().getColumnType(name);
					Value v = type.getDefault();
					Object tmp = update.values.get(i);
					if (tmp instanceof Expr) {
						((Expr)tmp).fillDefault(v);
						actions.add(new UpdatePair(name, (Expr)tmp));
					} else {
						actions.add(new UpdatePair(name, new Subquery((Select)tmp, tx)));
					}
				}
				
				UpdateScan scan = new UpdateScan(tx, table.getName(), actions, update.filter);
				while(scan.next());
				
			} else if (act instanceof Delete) {
				Delete delete = (Delete) act;
				if (!tx.getCurrent().hasTable(delete.table)) {
					return false;
				}
				
				if (delete.filter == null) {
					tx.getCurrent().clearTable(delete.table);
				} else {
					DeleteScan scan = new DeleteScan(tx, delete.table, delete.filter);
					while (scan.next());
				}
				return true;
			} else if (act instanceof InsertValues) {
				InsertValues insert = (InsertValues) act;
				if (insert.columns != null) {
					HashMap<String, Expr> pool = new HashMap<String, Expr>();
					int index = 0;
					for (String name : insert.columns)
						pool.put(name, insert.values.get(index ++));
					Schema sch = tx.getCurrent().get(insert.table).getSchema();
					ArrayList<Value> values = new ArrayList<Value>();
					for (int i = 0; i < sch.getColumnCount(); ++ i) {
						String name = sch.getColumnName(i);
						ColumnType t = sch.getColumnType(i);
						if (pool.containsKey(name)) {
							Expr e = pool.get(name);
							if (e instanceof Item)
								values.add(Value.parse(e.toString(), t.getType()));
							else 
								values.add(pool.get(name).getValue());
						} else {
							values.add(t.getDefault());
						}
						//System.out.println(t.getDefault());
						if (values.get(values.size() - 1) instanceof Null && t.isNotNull()) {
							return false;
						}
					}
					tx.getCurrent().addRecord(insert.table, values);
					return true;
				} else {
					ArrayList<Value> values = new ArrayList<Value>();
					//System.out.println(tx.getCurrent());
					Schema sch = tx.getCurrent().get(insert.table).getSchema();
					int i = 0;
					for (Expr e : insert.values) {
						ColumnType t = sch.getColumnType(i ++);
						e.fillDefault(t.getDefault());
						if (e instanceof Item)
							values.add(Value.parse(e.toString(), t.getType()));
						else 
							values.add(e.getValue());
					}
					tx.getCurrent().addRecord(insert.table, values);
					return true;
				}
			} else if (act instanceof InsertSubquery) {
				InsertSubquery t = (InsertSubquery)act;
				Scan scan = t.subquery.scan(tx);
				int col = scan.getSchema().getColumnCount();
				List<List<Value>> all = new ArrayList<List<Value>>();
				while (scan.next()) {
					List<Value> p = new ArrayList<Value>();
					for (int i = 0; i < col; ++ i)
						p.add(scan.getValue(i));
					all.add(p);
				}
				for (List<Value> p : all) {
					tx.getCurrent().addRecord(t.table, p);
				}
			} else if (act instanceof CreateDB) {
				return tx.createDB(((CreateDB)act).name);
			} else if (act instanceof DropDB) {
				return tx.dropDB(((DropDB)act).name);
			} else if (act instanceof UseDB) {
				return tx.useDB(((UseDB)act).name);
			} else if (act instanceof CreateTable) {
				CreateTable t = (CreateTable) act;
				return tx.getCurrent().createTable(t.name, t.schema, t.columns, t.primary);
			} else if (act instanceof DropTable) {
				boolean ret = true;
				List<String> tables = ((DropTable)act).tables;
				for (String name : tables)
					if (!tx.getCurrent().hasTable(name)) {
						ret = false;
						break;
					}
				if (ret == false) return false;
				for (String name : tables)
					tx.getCurrent().dropTable(name);
				return true;
			} else if (act instanceof CreateIndex) {
				
			} else if (act instanceof DropIndex) {
				
			} else return false;
			// TODO Auto-generated method stub
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addBatch(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws SQLException {
		//tx.close();
		//TODO
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean execute(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet executeQuery(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeUpdate(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMoreResults(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCursorName(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaxFieldSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaxRows(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setQueryTimeout(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

}
