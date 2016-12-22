package com.bluggee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBObject {

	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	
	
	public static Connection getDBConnection(DbConnection db) throws SQLException {

		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		dbConnection = DriverManager.getConnection(db.getConnectionString(), db.getUser(),
				db.getPassword());
		return dbConnection;

	}
}
