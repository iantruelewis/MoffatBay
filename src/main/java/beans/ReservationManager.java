package beans;

import java.io.Serializable;

import model.DataManager;

public class ReservationManager implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private DataManager dm = new DataManager();
	private String roomType;

	public String getRoomType() {
		roomType = dm.getRoomType(101);
		return roomType;
	}

	public void setRoomType(String s) {
		// Do nothing
	}
}