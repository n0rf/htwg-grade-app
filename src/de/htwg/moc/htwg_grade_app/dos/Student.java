package de.htwg.moc.htwg_grade_app.dos;

/**
 * Class representing a Student.
 */
public class Student {
	private String m_name = "Michael Maier";
	private String m_birthDateAndPlace = "1.1.1980 in Konstanz";
	private String m_number = "123456";
	private String m_address = "Musterstraﬂe 667, 78467 Konstanz";

	public Student() {
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public String getBirthDateAndPlace() {
		return m_birthDateAndPlace;
	}

	public void setBirthDateAndPlace(String birthDateAndPlace) {
		this.m_birthDateAndPlace = birthDateAndPlace;
	}

	public String getNumber() {
		return m_number;
	}

	public void setNumber(String number) {
		this.m_number = number;
	}

	public String getAddress() {
		return m_address;
	}

	public void setAddress(String address) {
		this.m_address = address;
	}

	@Override
	public String toString() {
		return "[" + m_name + "; " + m_birthDateAndPlace + "; " + m_number + "; " + m_address + "]";
	}
}
