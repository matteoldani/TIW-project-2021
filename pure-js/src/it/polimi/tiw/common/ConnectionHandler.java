package it.polimi.tiw.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class ConnectionHandler {

	public static Connection getConnection(ServletContext context) throws UnavailableException {
		Connection connection = null;
		try {
			//takes params from context (web.xml)
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			//try to connect
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			return null; //throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			return null; //throw new UnavailableException("Couldn't get db connection");
		}
		//return the connection that is the same for every servlet
		return connection;
	}

	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}
