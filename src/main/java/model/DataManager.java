package model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import beans.Register;

public class DataManager {

	private final int passKeyLength = 512;
	private final int passIterations = 100000;
	private final String hashAlg = "PBKDF2WithHmacSHA256";

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
			} catch (SQLException e) {
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

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}
		return success;
	}

	// Save a new user to the database
	public String registerUser(Register user) {

		Connection conn = getConnection();

		String success = "Failed";

		// get the password and hash it with a random salt
		String passwordHash = hashPassword(user.getPassword());

		if (conn != null) {
			try {

				String sql = "INSERT INTO `user` (`name`, `email`, `phone`, `password`) VALUES (?, ?, ?, ?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, user.getName());
				ps.setString(2, user.getEmail());
				ps.setString(3, user.getPhone());
				ps.setString(4, passwordHash);

				ps.executeUpdate();

				success = "Success";

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
				closeConnection(conn);
			}
		}
		return success;
	}

	public boolean validateLogin(String email, String password) {
		boolean valid = false;
		Connection conn = getConnection();

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
					String hashedInput = hashPassword(password, salt);

					// true if the hashes match.
					valid = hashedInput.equals(storedHash);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}

		return valid;
	}

	// Return Hex String salt + password string. - separator
	private String hashPassword(String password) {

		byte[] salt = new byte[16];

		// generate random salt
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, passIterations, passKeyLength);

		try {

			SecretKeyFactory factory = SecretKeyFactory.getInstance(hashAlg);

			byte[] hash = factory.generateSecret(spec).getEncoded();

			// return hex string with salt and hashed password
			return ("" + byteToHex(salt) + "-" + byteToHex(hash));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return null;
	}

	// Return Hex String salt + password string. - separator
	private String hashPassword(String password, String salt) {

		// convert salt to byte array
		byte[] byteSalt = hexToByte(salt);

		KeySpec spec = new PBEKeySpec(password.toCharArray(), byteSalt, passIterations, passKeyLength);

		// hash the password with supplied salt
		try {

			SecretKeyFactory factory = SecretKeyFactory.getInstance(hashAlg);

			byte[] hash = factory.generateSecret(spec).getEncoded();

			// return hex string with salt and hashed password
			return ("" + salt + "-" + byteToHex(hash));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		// if failed, return null
		return null;
	}

	// convert byte array to hex string
	private String byteToHex(byte[] byteArray) {
		StringBuilder hex = new StringBuilder();

		// Iterating through each byte in the array
		for (byte i : byteArray) {
			hex.append(String.format("%02X", i));
		}

		return hex.toString();
	}

	// convert hex string to byte array Geeks for Geeks
	// https://www.geeksforgeeks.org/java/java-program-to-convert-hex-string-to-byte-array/
	private byte[] hexToByte(String hexString) {

		// Initializing the byte array
		byte[] byteArray = new byte[hexString.length() / 2];

		// Iterate over each character
		for (int i = 0; i < byteArray.length; i++) {
			int index = i * 2;

			// Using parseInt() method of Integer class convert to byte
			int val = Integer.parseInt(hexString.substring(index, index + 2), 16);
			byteArray[i] = (byte) val;
		}

		return byteArray;
	}
}
