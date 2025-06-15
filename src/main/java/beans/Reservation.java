package beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

public class Reservation implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private HashSet<Integer> rooms = new HashSet<>();
	private Date checkinDate;
	private Date checkoutDate;
	private int guestCount;
	private String comment;
	
	public HashSet<Integer> getRooms() {
		return rooms;
	}
	
	public void addRoom(Integer roomNumber) {
		rooms.add(roomNumber);
	}
	
	public void remRoom(Integer roomNumber) {
		if (rooms.contains(roomNumber)) {
			rooms.remove(roomNumber);
		}
	}
	
	public void setCheckinDate(Date checkinDate) {
		this.checkinDate = checkinDate;
	}
	
	public Date getCheckinDate() {
		return checkinDate;
	}
	
	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	
	public Date getCheckoutDate() {
		return checkoutDate;
	}
	
	public int getGuestCount() {
		return guestCount;
	}
	
	public void setGuestCount(int guestCount) {
		this.guestCount = guestCount;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
}