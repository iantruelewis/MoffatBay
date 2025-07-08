package model;

/*
 * This class handles all back end access to the model.
 * 
 * CSD 460 Team 3
 * 
 * Ian Lewis
 * Robert Minkler
 * Kevin Ramirez
 * 
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import beans.Register;
import beans.Reservation;
import beans.User;

public class DataManager {

	/// Get and return database connection
	public static Connection getConnection() {

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
	public static void closeConnection(Connection conn) {

		if (conn != null) {

			try {
				conn.close();
			} catch (SQLException e) {

			}
		}
	}

	// Newsletter Sign up - Save Email
	public boolean newsletterSignup(String email) {
		return MessagingPeer.newsletterSignup(email);
	}

	// Save contact message to database
	public boolean saveContactMessage(String firstName, String lastName, String email, String message) {
		return MessagingPeer.saveContactMessage(firstName, lastName, email, message);
	}

	// Save a new user to the database
	public User registerUser(Register user) {
		return UserPeer.registerUser(user);
	}

	// Get user from DB using email. Returns a user object on success. Empty user on
	// failure
	public User getUser(String email) {
		return UserPeer.getUser(email);
	}

	// Validate User login against the database. Returns a user object on success.
	// Null on failure
	public User validateLogin(String email, String password) {
		return UserPeer.validateLogin(email, password);
	}

	// Get room availability to avoid over booking
	public HashMap<String, Integer> getRoomAvailability(Date checkin, Date checkout) {
		return ReservationPeer.getRoomAvailability(checkin, checkout);
	}

	// Save a reservation in the database
	public String saveReservation(User user, Reservation reservation) {
		return ReservationPeer.saveReservation(user, reservation);
	}

	public List<ResHelper> findReservationById(int resId) {
		return ReservationPeer.findReservationById(resId);
	}

	public List<ResHelper> findReservationsByEmail(String email) {
		return ReservationPeer.findReservationsByEmail(email);
	}

}
