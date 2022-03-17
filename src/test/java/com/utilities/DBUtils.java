package com.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class DBUtils {

	static final String DB_URL = "jdbc:mysql://localhost/DBNAME";
	static final String USERNAME = "raj";
	static final String PASSWORD = "raj";
	static Connection con;
	
	public static final String EMP_DETAILS = "select * from emp";

	public static Connection createDbConnection()   {
		// Open a connection
		try  {
			if(con == null)
				con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return con;
	}


	public static String getString(String query) throws SQLException {

		createDbConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()) {
			return rs.getString("id");
		}
		return null;

	}

}
