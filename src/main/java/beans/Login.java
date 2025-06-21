package beans;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import model.DataManager;


public class Login implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
   
    public String login() {
        DataManager dm = new DataManager();
        boolean isValid = dm.validateLogin(this.email, this.password);

        if (isValid) {
            return "first.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Incorrect Email or Password"));
            return null;
        }
    }

}