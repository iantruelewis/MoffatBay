package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import beans.Register;

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
			} catch (SQLException e) {

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
	
	// Newsletter - Save Email
	public boolean newsletterSignup(String email) {
		
		Connection conn = getConnection();
		
		boolean success = false;
		

		if (conn != null) {
			try {
				String sql = "INSERT INTO `newsletter` (`email`, `active`) VALUES (?, b'1')";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, email);      
				
				ps.executeUpdate();
				
				success = true;

			}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}		
		return success;
	}
	
	// Register User
	public String registerUser(Register user) {
		
		Connection conn = getConnection();
		
		String success = "Failed";
		

		if (conn != null) {
			try {

				String sql = "INSERT INTO `user` (`name`, `email`, `phone`, `password`) VALUES (?, ?, ?, ?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, user.getName());  
				ps.setString(2, user.getEmail());    
				ps.setString(3, user.getPhone()); 
				ps.setString(4, user.getPassword());
				
				ps.executeUpdate();
				
				success = "Success";

			}
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}		
		return success;
	}
}
