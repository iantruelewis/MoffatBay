package beans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import model.DataManager;

public class ReservationManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataManager dm = new DataManager();
	private String roomType;
	private User userBean;
	private Login loginBean;
	private Register registerBean;
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

	public Register getRegisterBean() {
		return registerBean;
	}

	public void setRegisterBean(Register registerBean) {
		this.registerBean = registerBean;
	}

	public String login() {
		DataManager dm = new DataManager();

		// Get form input to login
		String email = getLoginBean().getEmail();
		String password = getLoginBean().getPassword();
		
		User user = dm.validateLogin(email, password);

		
		if (user != null) {
			setUser(user);


			return "home.xhtml?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "*Login Failed*", "Incorrect Email or Password"));
			return null;
		}
	}
	
	public String displayUserInfo() {
		return "delete me when you can";
	}
	
	public String register() {

		// Register User in the database - return the user retrieved from the database
		DataManager dm = new DataManager();
		User user = dm.registerUser(getRegisterBean());

		// Set the user bean after login
		if (user != null) {
			setUser(user);
		}

		// Forward to reservation.xhtml
		return "reservation";
	}
	
	// Sets the Managed User Bean based on a provided User object.
	private void setUser(User user) {
		getUserBean().setUid(user.getUid());
		getUserBean().setName(user.getName());
		getUserBean().setEmail(user.getEmail());
		getUserBean().setPhone(user.getPhone());
		getUserBean().setComments(user.getComments());
		getUserBean().setGoogleId(user.getGoogleId());
		getUserBean().setInitial(String.valueOf(user.getName().charAt(0)).toUpperCase());
	}
	
}