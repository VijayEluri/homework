/**
 * 
 */
package fatworm.driver;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author mrain
 *
 */
public class Driver implements java.sql.Driver {

	static {
		try {
			java.sql.DriverManager.registerDriver(new Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO
	}
	
	public Driver() {
		// TODO
	}
	
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return false;
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		// TODO Auto-generated method stub
		url = url.substring(14);
		return new fwConnection(url);
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}
}
