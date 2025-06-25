package beans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import model.DataManager;

public class ReservationManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataManager dm = new DataManager();
	private String roomType;
	private User userBean;
	private Login loginBean;
	private Reservation reservation;

	public String getRoomType() {
		roomType = dm.getRoomType(101);
		return roomType;
	}

	public void setRoomType(String s) {
		// Do nothing
	}

	public User getUserBean() {
		if (userBean == null) {
			userBean = new User();
		}
		return userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public Login getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(Login loginBean) {
		this.loginBean = loginBean;
	}

	public String displayUserInfo() {
		return "" + userBean.getName() + userBean.getEmail();
	}

	public String login() {
		DataManager dm = new DataManager();

		String email = getLoginBean().getEmail();
		String password = getLoginBean().getPassword();
		
		int UID = dm.validateLogin(email, password);

		if (UID >= 0) {

			getUserBean().setUid(UID);

			return "home.xhtml?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "*Login Failed*", "Incorrect Email or Password"));
			return null;
		}

	}

}