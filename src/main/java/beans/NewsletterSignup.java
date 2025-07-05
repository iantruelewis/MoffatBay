package beans;

import java.io.Serializable;
import model.DataManager;

public class NewsletterSignup implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String message;

	public NewsletterSignup() {
		message = "Join Our Newsletter";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		
		// preset boolean
		boolean saveEmail = false;
		
		// set Email
		this.email = email.trim().toLowerCase();
		
		// If not blank store it
		if (this.email != null && this.email != "") {

			// Save the email in the database
			DataManager dm = new DataManager();
			saveEmail = dm.newsletterSignup(email);
		}

		// on success deliver message
		if (saveEmail) {
			this.message = "Welcome! " + email;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}