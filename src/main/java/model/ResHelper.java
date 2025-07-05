package model;

import java.io.Serializable;
import java.util.Date;

public class ResHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private int res_id = 0;
	private Date checkinDate;
	private Date checkoutDate;
	private int guestCount;
	private int king1 = 0;
	private int queen1 = 0;
	private int queen2 = 0;
	private int full2 = 0;
	private String comment;
	private String ownerName;

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
		this.checkinDate = checkinDate;
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	public int getGuestCount() {
		return guestCount;
	}

	public void setGuestCount(int guestCount) {
		this.guestCount = guestCount;
	}

	public int getKing1() {
		return king1;
	}

	public void setKing1(int king1) {
		this.king1 = king1;
	}

	public int getQueen1() {
		return queen1;
	}

	public void setQueen1(int queen1) {
		this.queen1 = queen1;
	}

	public int getQueen2() {
		return queen2;
	}

	public void setQueen2(int queen2) {
		this.queen2 = queen2;
	}

	public int getFull2() {
		return full2;
	}

	public void setFull2(int full2) {
		this.full2 = full2;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
}