package beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import model.DataManager;

public class Reservation implements Serializable {

	private static final long serialVersionUID = 1L;

	private int res_id = 0;
	private Date checkinDate;
	private Date checkoutDate;
	private int guestCount = 1;
	private int king1 = 0;
	private int queen1 = 0;
	private int queen2 = 0;
	private int full2 = 0;
	private String comment;
	private HashMap<String, Integer> roomAvailability = new HashMap<String, Integer>();
	private String roomType;
	private String ownerName;
	


	public Reservation() {
		checkinDate = new Date();
		checkoutDate = new Date(new Date().getTime() + 86400000);
		updateRoomAvailability();
	}

	public int getRes_id() {
		return res_id;
	}

	public void setRes_id(int res_id) {
		this.res_id = res_id;
	}

	public Date getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(Date checkinDate) {
		
		// initialize date if null
		if (checkinDate == null) {
			this.checkinDate = new Date();
		}
		
		// set date 
		else {
			this.checkinDate = checkinDate;
			updateRoomAvailability();
		}

	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		
		// initialize date if null
		if (checkoutDate == null) {
			this.checkoutDate = new Date(new Date().getTime() + 86400000);
		}
		
		// set date 
		else {
			this.checkoutDate = checkoutDate;
			updateRoomAvailability();
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
		
		int maxRooms = roomAvailability.getOrDefault("1king", 0);
		
		if (king1 <= 0) {
			this.king1 = 0;
		}
		else if (king1 >= maxRooms) {
			this.king1 = maxRooms;
		}
		else {
			this.king1 = king1;
		}
	}

	public int getQueen1() {
		return queen1;
	}

	public void setQueen1(int queen1) {
		
		int maxRooms = roomAvailability.getOrDefault("1queen", 0);
		
		if (queen1 <= 0) {
			this.queen1 = 0;
		}
		else if (queen1 >= maxRooms) {
			this.queen1 = maxRooms;
		}
		else {
			this.queen1 = queen1;
		}
	}

	public int getQueen2() {
		return queen2;
	}

	public void setQueen2(int queen2) {
		
		int maxRooms = roomAvailability.getOrDefault("2queen", 0);
		
		if (queen2 <= 0) {
			this.queen2 = 0;
		}
		else if (queen2 >= maxRooms) {
			this.queen2 = maxRooms;
		}
		else {
			this.queen2 = queen2;
		}
	}

	public int getFull2() {
		return full2;
	}

	public void setFull2(int full2) {
		
		int maxRooms = roomAvailability.getOrDefault("2full", 0);
		
		if (full2 <= 0) {
			this.full2 = 0;
		}
		else if (full2 >= maxRooms) {
			this.full2 = maxRooms;
		}
		else {
			this.full2 = full2;
		}
	}

	public int getRoomCount() {
		return king1 + queen1 + queen2 + full2;
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

		// update room reservation list to not exceed available rooms
		setKing1(king1);
		setQueen1(queen1);
		setQueen2(queen2);
		setFull2(full2);

		return "reservation";
	}

	public String upFull2() {
		if (full2 < roomAvailability.get("2full")) {
			full2++;
		}
		return null;
	}

	public String dnFull2() {
		if (full2 > 0) {
			full2--;
		}
		return null;
	}


	public String upQueen1() {
		if (queen1 < roomAvailability.get("1queen")) {
			queen1++;
		}
		return null;
	}

	public String dnQueen1() {
		if (queen1 > 0) {
			queen1--;
		}
		return null;
	}
	
	public String upKing1() {
		if (king1 < roomAvailability.get("1king")) {
			king1++;
		}
		return null;
	}

	public String dnKing1() {
		if (king1 > 0) {
			king1--;
		}
		return null;
	}


	public String upQueen2() {
		if (queen2 < roomAvailability.get("2queen")) {
			queen2++;
		}
		return null;
	}

	public String dnQueen2() {
		if (queen2 > 0) {
			queen2--;
		}
		return null;
	}
	
	public String upGuest() {
		if (guestCount < 99) {
			guestCount++;
			}
		return null;
	}

	public String dnGuest() {
		if (guestCount > 1) {
			guestCount--;
		}
		return null;
	}
	

	public String getRoomType() {
    return roomType;
	}

	public void setRoomType(String roomType) {
	this.roomType = roomType;
	}
	
	public String getReadableRoomType() {
	    if (roomType == null) return "";
	    return switch (roomType) {
	        case "1king" -> "King (1 bed)";
	        case "1queen" -> "Queen (1 bed)";
	        case "2queen" -> "Queen (2 beds)";
	        case "2full" -> "Full (2 beds)";
	        default -> roomType;
	    };
	}
	

	// reset the reservation bean after a reservation has been completed
	public String resetBean() {
		res_id = 0;
		guestCount = 1;
		king1 = 0;
		queen1 = 0;
		queen2 = 0;
		full2 = 0;
		return "home";
	}


	
}