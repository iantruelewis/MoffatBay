package beans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.DataManager;

@ManagedBean(name = "contact")
@ViewScoped
public class Contact implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String message;

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // Handles form submissions
    public String submit() {
        DataManager dataManager = new DataManager();
        FacesContext context = FacesContext.getCurrentInstance();

        boolean success = dataManager.saveContactMessage(firstName, lastName, email, message);

        if (success) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Message sent successfully!", null));
            clearForm();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to send message. Please try again.", null));
        }

        return null; // Stays on same page
    }

    private void clearForm() {
        firstName = "";
        lastName = "";
        email = "";
        message = "";
    }
}
