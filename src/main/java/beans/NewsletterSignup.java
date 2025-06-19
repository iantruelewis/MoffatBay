package beans;

import java.io.Serializable;
import model.DataManager;

public class NewsletterSignup implements Serializable {

	private static final long serialVersionUID = 1L;

	String email;
	String message;

	public NewsletterSignup() {
		message = "Join Our Newsletter";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;

		DataManager dm = new DataManager();
		boolean saveEmail = dm.newsletterSignup(email);

		if (saveEmail) {
			this.message = "" + email + " has been set.";
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}