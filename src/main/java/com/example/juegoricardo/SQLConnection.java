package com.example.juegoricardo;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
	private static final String URL = "jdbc:mysql://localhost/ricardojuego?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	public static java.sql.Connection getConnection() throws SQLException {
		java.sql.Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

		if (conn == null) {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		}
		return conn;
	}
}
