package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DataManager {

	/// Get and return database connection
	public Connection getConnection() {

		Connection conn = null;

		try {
			Context context = new InitialContext();

			if (context != null) {
				Context envContext = (Context) context.lookup("java:/comp/env");

				if (envContext != null) {
					DataSource ds = (DataSource) envContext.lookup("jdbc/mysql");

					if (ds != null) {
						conn = ds.getConnection();
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Could not connect to DB: " + e.getMessage());
			e.printStackTrace(System.out);
		}

		return conn;
	}

	/// Close the database connection
	public void closeConnection(Connection conn) {

		if (conn != null) {

			try {
				conn.close();
			} catch (SQLException _) {

			}
		}
	}

	// Test connection to get room type from DB
	public String getRoomType(int roomNumber) {
		
		String room = "failed to get room type";

		Connection conn = getConnection();

		if (conn != null) {
			try {
				Statement s = conn.createStatement();
				String sql = "SELECT type FROM room_inventory where room_num = " + roomNumber;
				
				ResultSet rs = s.executeQuery(sql);

				if (rs.next()) {
					room = rs.getString("type");
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}		
		return room;
	}
}
