package model;

/*
 * This static class handles the newsletter sign-ups and contact messages.
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
import java.sql.SQLException;

public class MessagingPeer {
	
	// Newsletter  Sign up - Save Email
	public static boolean newsletterSignup(String email) {

		Connection conn = DataManager.getConnection();

		boolean success = false;

		if (conn != null) {
			try {
				String sql = "INSERT INTO `newsletter` (`email`, `active`) VALUES (?, b'1')";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, email);

				ps.executeUpdate();

				success = true;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DataManager.closeConnection(conn);
			}
		}
		return success;
	}
	
	
    // Save contact message to database
    public static boolean saveContactMessage(String firstName, String lastName, String email, String message) {
        String sql = "INSERT INTO contact_messages (first_name, last_name, email, message) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, message);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}