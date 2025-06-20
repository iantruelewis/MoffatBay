package beans;

import java.io.Serializable;

public class Footer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String section1;
	private String section2;
	private String section3;
	private String section4;

	public Footer() {
		section1 = "Address";
		section2 = "Quick Links";
		section3 = "Contact";
		section4 = "Get Social";
	}

	public String getSection1() {
		return section1;
	}

	public String getSection2() {
		return section2;
	}

	public String getSection3() {
		return section3;
	}

	public String getSection4() {
		return section4;
	}
	
	public void setSection1(String section1) {
		this.section1 = section1;
	}

	public void setSection2(String section2) {
		this.section2 = section2;
	}

	public void setSection3(String section3) {
		this.section3 = section3;
	}

	public void setSection4(String section4) {
		this.section4 = section4;
	}

}