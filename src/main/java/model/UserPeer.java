package model;

/*
 * This Class handles the user model logic.
 * 
 * CSD 460 Team 3
 * 
 * Ian Lewis
 * Robert Minkler
 * Kevin Ramirez
 * 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import beans.Register;
import beans.User;

public class UserPeer {
	

	// Save a new user to the database
	public static User registerUser(Register user) {

		Connection conn = DataManager.getConnection();

		String email = user.getEmail(); // store email for later retrieval

		// get the password and hash it with a random salt
		String passwordHash = SecurityPeer.hashPassword(user.getPassword());

		if (conn != null) {
			try {

				String sql = "INSERT INTO `user` (`name`, `email`, `phone`, `password`) VALUES (?, ?, ?, ?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, user.getName());
				ps.setString(2, email);
				ps.setString(3, user.getPhone());
				ps.setString(4, passwordHash);

				ps.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();

				// Duplicate entry 1062 - send error that an account with that email address
				// already exists.
				if (e.getErrorCode() == 1062) {

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"An account with that email address already exists.",
									"Unable to create the user account."));

				} else {
					// Send other error.
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"ERROR. Unable to create account.", "Unable to create the user account."));
				}

			} finally {
				DataManager.closeConnection(conn);
			}
		}

		return getUser(email);
	}
	
	// Get user from DB using email. Returns a user object on success. Empty user on
	// failure
	public static User getUser(String email) {

		User user = new User();

		Connection conn = DataManager.getConnection();

		if (conn != null) {
			try {

				String sql = "SELECT * FROM user WHERE email = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, email);

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					user.setUid(rs.getInt("uid"));
					user.setName(rs.getString("name"));
					user.setEmail(rs.getString("email"));
					user.setPhone(rs.getString("phone"));
					user.setComments(rs.getString("comments"));
					user.setGoogleId(rs.getString("google_id"));
				}

			} catch (SQLException e) {
				e.printStackTrace();

			} finally {
				DataManager.closeConnection(conn);
			}
		}

		return user;
	}
	
	// Validate User login against the database. Returns a user object on success.
	// Null on failure
	public static User validateLogin(String email, String password) {

		User user = null;

		Connection conn = DataManager.getConnection();

		if (conn != null) {
			try {
				String sql = "SELECT * FROM user WHERE email = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, email);

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {

					// Get the stored password from the database
					String storedHash = rs.getString("password");

					// Get the Salt from stored password
					String salt = storedHash.split("-")[0];

					// Hash the input with stored salt
					String hashedInput = SecurityPeer.hashPassword(password, salt);

					// true if the hashes match.
					if (hashedInput.equals(storedHash)) {

						user = new User();

						// set the new user bean properties
						user.setUid(rs.getInt("uid"));
						user.setName(rs.getString("name"));
						user.setEmail(rs.getString("email"));
						user.setPhone(rs.getString("phone"));
						user.setComments(rs.getString("comments"));
						user.setGoogleId(rs.getString("google_id"));

					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DataManager.closeConnection(conn);
			}
		}

		return user;
	}

}