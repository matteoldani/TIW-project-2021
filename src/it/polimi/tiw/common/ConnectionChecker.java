package it.polimi.tiw.common;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionChecker {
	
	public static void checkConnection(Connection connection) throws SQLException {
		if(connection == null) {
			throw new SQLException();
		}
	}

}
