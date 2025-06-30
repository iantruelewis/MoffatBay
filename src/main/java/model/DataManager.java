package model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.TimeZone;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import beans.Register;
import beans.Reservation;
import beans.User;

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



	// Newsletter  Sign up - Save Email
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
	public User registerUser(Register user) {

		Connection conn = getConnection();

		String email = user.getEmail(); // store email for later retrieval

		// get the password and hash it with a random salt
		String passwordHash = hashPassword(user.getPassword());

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
				closeConnection(conn);
			}
		}

		return getUser(email);
	}

	// Get user from DB using email. Returns a user object on success. Empty user on
	// failure
	public User getUser(String email) {

		User user = new User();

		Connection conn = getConnection();

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
				closeConnection(conn);
			}
		}

		return user;
	}

	// Validate User login against the database. Returns a user object on success.
	// Null on failure
	public User validateLogin(String email, String password) {

		User user = null;

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
				closeConnection(conn);
			}
		}

		return user;
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

	// Get room availability to avoid over booking
	public HashMap<String, Integer> getRoomAvailability(Date checkin, Date checkout) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		Connection conn = getConnection();
		HashMap<String, Integer> rooms = new HashMap<String, Integer>();

		// set all room types to zero
		rooms.put("1king", 0);
		rooms.put("1queen", 0);
		rooms.put("2queen", 0);
		rooms.put("2full", 0);

		if (conn != null) {
			try {
				String sql = "WITH reservations AS (\n" + "SELECT \n"
						+ "    res.res_id, checkin, checkout, room.room_num, inv.type\n" + "FROM\n"
						+ "    reservation AS res\n" + "        INNER JOIN\n"
						+ "    room_reservation AS room ON res.res_id = room.res_id\n" + "        INNER JOIN\n"
						+ "    room_inventory AS inv ON inv.room_num = room.room_num\n" + "WHERE\n" + "checkout > ? \n"
						+ "        AND checkin < ? \n" + ")\n" + "SELECT\n" + "	inv.room_num, inv.type\n"
						+ "FROM room_inventory AS inv \n"
						+ "LEFT JOIN reservations ON inv.room_num=reservations.room_num \n"
						+ "WHERE reservations.res_id IS NULL;";

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, dateFormat.format(checkin));
				ps.setString(2, dateFormat.format(checkout));

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {

					String roomType = rs.getString("type");

					// Increment room values by one
					rooms.put(roomType, rooms.getOrDefault(roomType, 0) + 1);
				}
			}

			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}

		return rooms;
	}

	// Save a reservation in the database
	public String saveReservation(User user, Reservation reservation) {

		Connection conn = getConnection();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		if (conn != null) {

			try {
				// allow multiple transactions before committing
				conn.setAutoCommit(false);

				// convert java.util.Date to a string for SQL queries
				String checkin = dateFormat.format(reservation.getCheckinDate());
				String checkout = dateFormat.format(reservation.getCheckoutDate());

				// get available room numbers
				String sql = "WITH reservations AS (\n" + "SELECT \n"
						+ "    res.res_id, checkin, checkout, room.room_num, inv.type\n" + "FROM\n"
						+ "    reservation AS res\n" + "        INNER JOIN\n"
						+ "    room_reservation AS room ON res.res_id = room.res_id\n" + "        INNER JOIN\n"
						+ "    room_inventory AS inv ON inv.room_num = room.room_num\n" + "WHERE\n" + "checkout > ? \n"
						+ "        AND checkin < ? \n" + ")\n" + "SELECT\n" + "	inv.room_num, inv.type\n"
						+ "FROM room_inventory AS inv \n"
						+ "LEFT JOIN reservations ON inv.room_num=reservations.room_num \n"
						+ "WHERE reservations.res_id IS NULL\n" + "ORDER BY rand();";

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, checkin);
				ps.setString(2, checkout);

				ResultSet rs = ps.executeQuery();

				// store available rooms
				Deque<Integer> king1 = new ArrayDeque<Integer>();
				Deque<Integer> queen1 = new ArrayDeque<Integer>();
				Deque<Integer> queen2 = new ArrayDeque<Integer>();
				Deque<Integer> full2 = new ArrayDeque<Integer>();

				while (rs.next()) {

					String type = rs.getString("type");
					int room_num = rs.getInt("room_num");

					if (type.equals("1king")) {
						king1.add(room_num);
					} else if (type.equals("1queen")) {
						queen1.add(room_num);
					} else if (type.equals("2queen")) {
						queen2.add(room_num);
					} else if (type.equals("2full")) {
						full2.add(room_num);
					}
				}

				// Insert Reservation into reservation table
				sql = "INSERT INTO `reservation` (`uid`, `checkin`, `checkout`, `guest_count`) VALUES (?, ?, ?, ?);";

				ps = conn.prepareStatement(sql);
				ps.setInt(1, user.getUid());
				ps.setString(2, checkin);
				ps.setString(3, checkout);
				ps.setInt(4, reservation.getGuestCount());

				ps.executeUpdate();

				// Get reservation room type needed and count into a HashMap
				HashMap<String, Integer> roomsNeeded = new HashMap<String, Integer>();
				roomsNeeded.put("king1", reservation.getKing1());
				roomsNeeded.put("queen1", reservation.getQueen1());
				roomsNeeded.put("queen2", reservation.getQueen2());
				roomsNeeded.put("full2", reservation.getFull2());

				// For Each room Type add an available room to the reservation
				for (String roomType : roomsNeeded.keySet()) {

					// Add each room of each room type to the reservation table
					for (int i = 0; i < roomsNeeded.get(roomType); i++) {

						int roomNumber = 0;
						boolean available = true;

						if (roomType.equals("king1")) {
							if (!king1.isEmpty()) roomNumber = king1.pop();
							else available = false;
						} else if (roomType.equals("queen1")) {
							if (!queen1.isEmpty()) roomNumber = queen1.pop();
							else available = false;
						} else if (roomType.equals("queen2")) {
							if (!queen2.isEmpty()) roomNumber = queen2.pop();
							else available = false;
						} else if (roomType.equals("full2")) {
							if (!full2.isEmpty()) roomNumber = full2.pop();
							else available = false;
						}

						if (!available) {
							System.err.println("Not enough rooms available for type: " + roomType);
							conn.rollback(); // rollback any partial inserts
							return "reserror";
						}

						// Insert Room Reservation table
						sql = "INSERT INTO `room_reservation` SELECT res_id, ? from `reservation` where `uid` = ? AND `checkin` = ? AND `checkout` = ? AND `guest_count` = ?;";

						ps = conn.prepareStatement(sql);

						ps.setInt(1, roomNumber);
						ps.setInt(2, user.getUid());
						ps.setString(3, checkin);
						ps.setString(4, checkout);
						ps.setInt(5, reservation.getGuestCount());

						ps.executeUpdate();
					}
				}

				// commit changes
				conn.commit();

			} catch (SQLException e) {
				// Rollback changes on error
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				e.printStackTrace();

				// Go to reservation error page
				return "reserror";

			} finally {
				closeConnection(conn);
			}
		}

		// Go to reservation summary on success
		return "reservation-summary?faces-redirect=true";
	}

	// Get the most recent reservation for a user, including room counts
	public Reservation getLatestReservationForUser(int userId) {
	    Reservation reservation = null;

	    Connection conn = getConnection();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

	    if (conn != null) {
	        try {
	            // First, get the latest reservation record for the user
	            String sql = "SELECT * FROM reservation WHERE uid = ? ORDER BY res_id DESC LIMIT 1";
	            PreparedStatement ps = conn.prepareStatement(sql);
	            ps.setInt(1, userId);

	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {
	                reservation = new Reservation();
	                reservation.setCheckinDate(dateFormat.parse(rs.getString("checkin")));
	                reservation.setCheckoutDate(dateFormat.parse(rs.getString("checkout")));
	                reservation.setGuestCount(rs.getInt("guest_count"));

	                int resId = rs.getInt("res_id");

	                // Now get the count of each room type reserved for this reservation
	                sql = "SELECT inv.type, COUNT(*) as count FROM room_reservation rr "
	                    + "JOIN room_inventory inv ON rr.room_num = inv.room_num "
	                    + "WHERE rr.res_id = ? GROUP BY inv.type";

	                PreparedStatement psRooms = conn.prepareStatement(sql);
	                psRooms.setInt(1, resId);

	                ResultSet rsRooms = psRooms.executeQuery();

	                // Initialize counts to zero
	                reservation.setKing1(0);
	                reservation.setQueen1(0);
	                reservation.setQueen2(0);
	                reservation.setFull2(0);

	                while (rsRooms.next()) {
	                    String type = rsRooms.getString("type");
	                    int count = rsRooms.getInt("count");

	                    switch (type) {
	                        case "1king":
	                            reservation.setKing1(count);
	                            break;
	                        case "1queen":
	                            reservation.setQueen1(count);
	                            break;
	                        case "2queen":
	                            reservation.setQueen2(count);
	                            break;
	                        case "2full":
	                            reservation.setFull2(count);
	                            break;
	                    }
	                }

	                rsRooms.close();
	                psRooms.close();
	            }

	            rs.close();
	            ps.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            closeConnection(conn);
	        }
	    }

	    return reservation;
	}

}
