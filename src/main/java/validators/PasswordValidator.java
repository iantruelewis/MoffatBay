package validators;

// Password Confirmation code modified from
// https://mkyong.com/jsf2/multi-components-validator-in-jsf-2-0/

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class PasswordValidator implements Validator {

	private int minLength = 10;
	final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
	final String NUMBERS = "1234567890";
	final String SPECIAL_CHAR = "@#$%^*_+-=!~<>,.";

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		String password = value.toString();

		UIInput uiInputConfirmPassword = (UIInput) component.getAttributes().get("passwordConfirm");
		String passwordConfirm = uiInputConfirmPassword.getSubmittedValue().toString();

		// Let required="true" do its job.
		if (password == null || password.isEmpty() || passwordConfirm == null || passwordConfirm.isEmpty()) {
			return;
		}

		// Confirm password and passwordConfirm entries match
		if (!password.equals(passwordConfirm)) {
			uiInputConfirmPassword.setValid(false);
			throw new ValidatorException(new FacesMessage("Password must match confirm password."));
		}

		// Enforce password length
		else if (password.length() < minLength) {
			uiInputConfirmPassword.setValid(false);
			throw new ValidatorException(new FacesMessage("Password must be at least 10 characters long."));
		}

		// Enforce password character type
		else if (!hasCharType(password, UPPERCASE_LETTERS)) {
			uiInputConfirmPassword.setValid(false);
			throw new ValidatorException(new FacesMessage("Password must must contain an uppercase character."));
		}
		
		else if (!hasCharType(password, LOWERCASE_LETTERS)) {
			uiInputConfirmPassword.setValid(false);
			throw new ValidatorException(new FacesMessage("Password must must contain a lowercase character."));
		}
		else if (!hasCharType(password, NUMBERS)) {
			uiInputConfirmPassword.setValid(false);
			throw new ValidatorException(new FacesMessage("Password must must contain a number."));
		}
		else if (!hasCharType(password, SPECIAL_CHAR)) {
			uiInputConfirmPassword.setValid(false);
			throw new ValidatorException(new FacesMessage("Password must must contain a special character " + SPECIAL_CHAR));
		}
	}

	// Helper Method to validate that the password contains the required character type.
	private boolean hasCharType(String password, String charType) {

		for (char ch : password.toCharArray()) {
			if (charType.indexOf(ch) >= 0) {
				return true;
			}
		}
		return false;
	}
	
}
