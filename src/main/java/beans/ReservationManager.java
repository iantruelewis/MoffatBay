package beans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


import model.DataManager;

public class ReservationManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private User userBean;
	private Login loginBean;
	private Register registerBean;
	private Reservation reservationBean;

	public User getUserBean() {
		if (userBean == null) {
			userBean = new User();
		}
		return userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
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

	public Reservation getReservationBean() {
		if (reservationBean == null) {
			reservationBean = new Reservation();
		}
		return reservationBean;
	}

	public void setReservationBean(Reservation reservationBean) {
		this.reservationBean = reservationBean;
	}

	public String login() {
		DataManager dm = new DataManager();

		// Get form input to login
		String email = getLoginBean().getEmail();
		String password = getLoginBean().getPassword();

		User user = dm.validateLogin(email, password);

		if (user != null) {
			setUser(user);

			return reservationBean.getForwardTo();
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "*Login Failed*", "Incorrect Email or Password"));
			return null;
		}
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
		return getReservationBean().getForwardTo();
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

	public String saveReservation() {
		
		// login if not logged in
		if (userBean.getUid() == 0) {
			return "login";
		}
		
		if (!reservationBean.getCheckoutDate().after(reservationBean.getCheckinDate())) {
			return "reserror";
		}
		
		DataManager dm = new DataManager();
		String redirectLocation = dm.saveReservation(userBean, reservationBean);
		
		// redirect based on result of saving the reservation
		return redirectLocation;
	}
	
	public String updateBean() {
		return "reservation";
	}
	
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "login?faces-redirect=true";
    }
    
    public String checkLogin(String forwardTo) {
    	getReservationBean().setForwardTo(forwardTo);
    	
    	
    	if (getUserBean().getUid() <= 0) {
        	getReservationBean().setForwardTo(forwardTo);
    		return "login";
    	}
    	else {
        	getReservationBean().setForwardTo("reservation");
    	}
    	
    	return null;
    }

}