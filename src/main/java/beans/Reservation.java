package beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import model.DataManager;

public class Reservation implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date checkinDate;
	private Date checkoutDate;
	private int guestCount = 1;
	private int king1 = 0;
	private int queen1 = 0;
	private int queen2 = 0;
	private int full2 = 0;
	private String comment;
	private HashMap<String, Integer> roomAvailability = new HashMap<String, Integer>();
	
	public Reservation() {
		checkinDate = new Date();
		checkoutDate = new Date();
	}

	public Date getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(Date checkinDate) {
		
		if (checkinDate != null) {
		updateRoomAvailability();
		this.checkinDate = checkinDate;
		} else {
			checkinDate = new Date();
		}
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		if (checkoutDate != null) {
			updateRoomAvailability();
			this.checkoutDate = checkoutDate;
		}
		else {
			this.checkoutDate = new Date();
		}
	}

	public int getGuestCount() {
		return guestCount;
	}

	public void setGuestCount(int guestCount) {
		if (guestCount > 0) {
		this.guestCount = guestCount;
		}
	}

	public int getKing1() {
		return king1;
	}

	public void setKing1(int king1) {
		if (king1 >= 0 && king1 <= roomAvailability.get("1king")) {
			this.king1 = king1;
		}
	}

	public int getQueen1() {
		return queen1;
	}

	public void setQueen1(int queen1) {
		if (queen1 >= 0 && queen1 <= roomAvailability.get("1queen")) {
			this.queen1 = queen1;
		}
	}

	public int getQueen2() {
		return queen2;
	}

	public void setQueen2(int queen2) {
		if (queen2 >= 0 && queen2 <= roomAvailability.get("2queen")) {
			this.queen2 = queen2;
		}
	}

	public int getFull2() {
		return full2;
	}

	public void setFull2(int full2) {
		if (full2 >= 0 && full2 <= roomAvailability.get("2full")) {
			this.full2 = full2;
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public HashMap<String, Integer> getRoomAvailability() {
		return roomAvailability;
	}

	public void setRoomAvailability(HashMap<String, Integer> roomAvailability) {
		this.roomAvailability = roomAvailability;
	}

	public String updateRoomAvailability() {

		DataManager dm = new DataManager();

		// get checkin and checkout from reservation bean
		// Then get availability from database
		roomAvailability = dm.getRoomAvailability(checkinDate, checkoutDate);

		return "reservation";
	}
}