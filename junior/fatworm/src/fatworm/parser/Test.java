package fatworm.parser;


import java.nio.ByteBuffer;
import java.sql.*;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println(java.sql.Timestamp.valueOf("1"));
		try {
			Class.forName("fatworm.driver.Driver");
			java.sql.Connection conn = java.sql.DriverManager.getConnection(
									"jdbc:fatworm://home/mrain/db");
			Statement stmt = conn.createStatement();
			/*System.out.println(stmt.execute("DROP DATABASE fatworm"));
			System.out.println(stmt.execute("CREATE DATABASE fatworm"));
			System.out.println(stmt.execute("USE fatworm"));
			System.out.println(stmt.execute("SELECT * FROM fatworm"));*/
			System.out.println(stmt.execute("CREATE DATABASE test"));
			System.out.println(stmt.execute("USE test"));
			System.out.println(stmt.execute("CREATE TABLE a ( a int, b timestamp )"));
			System.out.println(stmt.execute("DELETE FROM a"));
			System.out.println(stmt.execute("INSERT INTO a VALUES (1, NULL)"));
			System.out.println(stmt.execute("INSERT INTO a VALUES (2, NULL)"));
			
			//System.out.println(stmt.execute("INSERT INTO a (a) VALUES (1)"));
			//System.out.println(stmt.execute("USE bank"));
			//System.out.println(stmt.execute("INSERT INTO ATOM VALUES ('d1_3', 1, 'c', '22', 0)"));
			int cnt, count;
			ResultSet rs;
		/*	System.out.println(stmt.execute("select * from (select a1.a + a2.a * 10 + a3.a * 100" +
					" + a4.a * 1000 + a5.a * 10000 + a6.a * 100000 + a7.a * 1000000" +
					" + a8.a * 10000000 as t from a as a1, a as a2, a as a3, a as a4, " +
					"a as a5, a as a6, a as a7, a as a8 order by t) as tab where t < 10 order by t"));
			*/
			//System.out.println(stmt.execute("SELECT branch_name FROM account group by branch_name having avg(balance)>=all(select avg(balance) from account group by branch_name)"));
			System.out.println(stmt.execute("SELECT max(a)+min(a) from a"));
			//System.out.println(stmt.execute("SELECT * from a"));
			rs = stmt.getResultSet();
			cnt = rs.getMetaData().getColumnCount();
			count = 0;
			while (rs.next()) {
				for (int i = 0; i < cnt; ++ i)
					System.out.print(rs.getObject(i + 1).toString() + " ");
				System.out.println();
				++ count;
			}
			System.out.println(count);
			conn.close();
		} catch (SQLException e) {
			System.err.println("SQL Exception");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Class Not Found");
			e.printStackTrace();
		}
	}
}
