package beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import model.DataManager;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "reservationManager")
@SessionScoped
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

    @PostConstruct
    public void init() {
        // System.out.println("Initializing ReservationManager and loading latest reservation...");
        loadLatestReservation();
    }

    public String login() {
        DataManager dm = new DataManager();

        String email = getLoginBean().getEmail();
        String password = getLoginBean().getPassword();

        User user = dm.validateLogin(email, password);

        if (user != null) {
            setUser(user);

            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            // System.out.println("Session user inside login(): " + session.getAttribute("user"));

            return "home.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "*Login Failed*", "Incorrect Email or Password"));
            return null;
        }
    }

    public String register() {
        DataManager dm = new DataManager();
        User user = dm.registerUser(getRegisterBean());

        if (user != null) {
            setUser(user);
        }

        return "reservation";
    }

    // Sets the Managed User Bean and stores it in session
    private void setUser(User user) {
        this.userBean = user;

        if (!(user instanceof Serializable)) {
            // System.out.println("WARNING: User is NOT Serializable! Session storage may fail.");
        }

        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        session.setAttribute("user", user);
        // System.out.println("User set in session: " + user.getEmail() + ", session id: " + session.getId());
    }

    public String saveReservation() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        // System.out.println("Session ID at saveReservation: " + (session != null ? session.getId() : "null"));
        // System.out.println("UserBean at saveReservation: " + (userBean != null ? userBean.getEmail() : "null"));

        if (userBean == null || userBean.getUid() == 0) {
            // System.out.println("UserBean is null or invalid, redirecting to login.");
            return "login";
        }

        if (!reservationBean.getCheckoutDate().after(reservationBean.getCheckinDate())) {
            return "reserror";
        }

        DataManager dm = new DataManager();
        String redirectLocation = dm.saveReservation(userBean, reservationBean);

        Reservation savedReservation = dm.getLatestReservationForUser(userBean.getUid());
        if (savedReservation != null) {
            setReservationBean(savedReservation);
        }

        /*
        System.out.println("==== Saved Reservation Debug ====");
        System.out.println("Check-in: " + reservationBean.getCheckinDate());
        System.out.println("Check-out: " + reservationBean.getCheckoutDate());
        System.out.println("Guests: " + reservationBean.getGuestCount());
        System.out.println("King1: " + reservationBean.getKing1());
        System.out.println("Queen1: " + reservationBean.getQueen1());
        System.out.println("Queen2: " + reservationBean.getQueen2());
        System.out.println("Full2: " + reservationBean.getFull2());
        System.out.println("User: " + userBean.getName() + ", Email: " + userBean.getEmail());
        */

        return redirectLocation;
    }

    public void loadLatestReservation() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

        if (session != null) {
            // System.out.println("Session ID at loadLatestReservation: " + session.getId());
            User sessionUser = (User) session.getAttribute("user");
            // System.out.println("Session user at loadLatestReservation: " + (sessionUser == null ? "null" : sessionUser.getEmail()));

            if (sessionUser != null && sessionUser.getUid() > 0) {
                this.setUser(sessionUser);
                DataManager dm = new DataManager();
                Reservation latest = dm.getLatestReservationForUser(sessionUser.getUid());
                // System.out.println("Latest reservation found? " + (latest == null ? "No" : "Yes"));
                if (latest != null) {
                    setReservationBean(latest);
                }
            } else {
                // System.out.println("User is null or not logged in. Can't fetch reservation.");
            }
        } else {
            // System.out.println("No session found.");
        }
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
}
