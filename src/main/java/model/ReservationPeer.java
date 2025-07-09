package model;

/*
 * This class handles reservation data.
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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import beans.Reservation;
import beans.User;

public class ReservationPeer {

	// Get room availability to avoid over booking
	public static HashMap<String, Integer> getRoomAvailability(Date checkin, Date checkout) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		Connection conn = DataManager.getConnection();
		HashMap<String, Integer> rooms = new HashMap<String, Integer>();

		// set all room types to zero
		rooms.put("1king", 0);
		rooms.put("1queen", 0);
		rooms.put("2queen", 0);
		rooms.put("2full", 0);

		if (conn != null) {
			try {
				String sql = """
							WITH reservations AS (
							SELECT
							    res.res_id, checkin, checkout, room.room_num, inv.type
							FROM
							    reservation AS res
							        INNER JOIN
							    room_reservation AS room ON res.res_id = room.res_id
							        INNER JOIN
							    room_inventory AS inv ON inv.room_num = room.room_num
							WHERE
							checkout > ?
							        AND checkin < ?
							)
							SELECT
								inv.room_num, inv.type
							FROM room_inventory AS inv
							LEFT JOIN reservations ON inv.room_num=reservations.room_num
							WHERE reservations.res_id IS NULL;
						""";

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
				DataManager.closeConnection(conn);
			}
		}

		return rooms;
	}

	// Save a reservation in the database
	public static String saveReservation(User user, Reservation reservation) {

		Connection conn = DataManager.getConnection();

		// Set date format UTC yyyy-mm-dd for database
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		// temporary storage of reservation id
		// It will be saved to the reservation bean on successful completion of all sql
		// scripts
		int res_id = 0;

		if (conn != null) {

			try {
				// allow multiple transactions before committing
				conn.setAutoCommit(false);

				// convert java.util.Date to a string for SQL queries
				String checkin = dateFormat.format(reservation.getCheckinDate());
				String checkout = dateFormat.format(reservation.getCheckoutDate());

				// get available room numbers
				String sql = """
							WITH reservations AS (
							SELECT
							    res.res_id, checkin, checkout, room.room_num, inv.type
							FROM
							    reservation AS res
							        INNER JOIN
							    room_reservation AS room ON res.res_id = room.res_id
							        INNER JOIN
							    room_inventory AS inv ON inv.room_num = room.room_num
							WHERE
								checkout > ?
							        AND checkin < ?
							)
							SELECT
								inv.room_num, inv.type
							FROM room_inventory AS inv
							LEFT JOIN reservations ON inv.room_num=reservations.room_num
							WHERE reservations.res_id IS NULL
							ORDER BY rand();
						""";

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

				ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, user.getUid());
				ps.setString(2, checkin);
				ps.setString(3, checkout);
				ps.setInt(4, reservation.getGuestCount());

				ps.executeUpdate();

				// Get reservation ID for later use
				ResultSet keys = ps.getGeneratedKeys();
				if (keys.next()) {
					res_id = keys.getInt(1);
				}

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

						// If a room is booked that isn't available it will cause an exception.
						try {

//						// get room number
							if (roomType.equals("king1")) {
								roomNumber = king1.pop();
							} else if (roomType.equals("queen1")) {
								roomNumber = queen1.pop();
							} else if (roomType.equals("queen2")) {
								roomNumber = queen2.pop();
							} else if (roomType.equals("full2")) {
								roomNumber = full2.pop();
							}
						} catch (Exception e) {
							e.printStackTrace();

							// Forward to an error on failure
							return "reserror";
						}

						// Insert Room Reservation table
						sql = "INSERT INTO `moffat_bay_lodge`.`room_reservation` (`res_id`, `room_num`) VALUES (?, ?);";

						ps = conn.prepareStatement(sql);

						ps.setInt(1, res_id);
						ps.setInt(2, roomNumber);

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
				DataManager.closeConnection(conn);
			}
		}

		// store res_id in bean upon success
		reservation.setRes_id(res_id);

		return "reservation-summary";
	}
	
	public static List<ResHelper> findReservationById(int resId) {
		List<ResHelper> list = new ArrayList<>();
		try (Connection conn = DataManager.getConnection()) {
			String sql = """
					    SELECT
					        r.res_id,
					        r.checkin,
					        r.checkout,
					        r.guest_count,
					        u.name AS owner_name,
					        SUM(CASE WHEN inv.type = '1king' THEN 1 ELSE 0 END) AS king1,
					        SUM(CASE WHEN inv.type = '1queen' THEN 1 ELSE 0 END) AS queen1,
					        SUM(CASE WHEN inv.type = '2queen' THEN 1 ELSE 0 END) AS queen2,
					        SUM(CASE WHEN inv.type = '2full' THEN 1 ELSE 0 END) AS full2
					    FROM reservation r
					    JOIN user u ON u.uid = r.uid
					    JOIN room_reservation rr ON r.res_id = rr.res_id
					    JOIN room_inventory inv ON rr.room_num = inv.room_num
					    WHERE r.res_id = ?
					    GROUP BY r.res_id, r.checkin, r.checkout, r.guest_count, u.name
					""";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, resId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ResHelper r = new ResHelper();
				r.setRes_id(rs.getInt("res_id"));
				r.setCheckinDate(rs.getDate("checkin"));
				r.setCheckoutDate(rs.getDate("checkout"));
				r.setGuestCount(rs.getInt("guest_count"));
				r.setOwnerName(rs.getString("owner_name"));
				r.setKing1(rs.getInt("king1"));
				r.setQueen1(rs.getInt("queen1"));
				r.setQueen2(rs.getInt("queen2"));
				r.setFull2(rs.getInt("full2"));
				list.add(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<ResHelper> findReservationsByEmail(String email) {
		List<ResHelper> list = new ArrayList<>();
		try (Connection conn = DataManager.getConnection()) {
			String sql = """
					    SELECT
					        r.res_id,
					        r.checkin,
					        r.checkout,
					        r.guest_count,
					        u.name AS owner_name,
					        SUM(CASE WHEN inv.type = '1king' THEN 1 ELSE 0 END) AS king1,
					        SUM(CASE WHEN inv.type = '1queen' THEN 1 ELSE 0 END) AS queen1,
					        SUM(CASE WHEN inv.type = '2queen' THEN 1 ELSE 0 END) AS queen2,
					        SUM(CASE WHEN inv.type = '2full' THEN 1 ELSE 0 END) AS full2
					    FROM reservation r
					    JOIN user u ON u.uid = r.uid
					    JOIN room_reservation rr ON r.res_id = rr.res_id
					    JOIN room_inventory inv ON rr.room_num = inv.room_num
					    WHERE u.email = ?
					    GROUP BY r.res_id, r.checkin, r.checkout, r.guest_count, u.name
					""";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ResHelper r = new ResHelper();
				r.setRes_id(rs.getInt("res_id"));
				r.setCheckinDate(rs.getDate("checkin"));
				r.setCheckoutDate(rs.getDate("checkout"));
				r.setGuestCount(rs.getInt("guest_count"));
				r.setOwnerName(rs.getString("owner_name"));
				r.setKing1(rs.getInt("king1"));
				r.setQueen1(rs.getInt("queen1"));
				r.setQueen2(rs.getInt("queen2"));
				r.setFull2(rs.getInt("full2"));
				list.add(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
}