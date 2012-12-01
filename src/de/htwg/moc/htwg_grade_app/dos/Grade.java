package de.htwg.moc.htwg_grade_app.dos;

public class Grade {
	private String m_program;
	private String m_examNumber;
	private String m_examText;
	private String m_semester;
	private double m_ects;
	private double m_grade;
	private boolean m_graded;
	private boolean m_certOnly;
	private boolean m_modul;
	private String m_state;
	private String m_notes;
	private int m_attempt;

	// default constructor
	public Grade() {
		m_graded = false;
		m_certOnly = true;
		m_modul = false;
	}

	public Grade(String program, String examText, String semester) {
		this.m_program = program;
		this.m_examText = examText;
		this.m_semester = semester;
		this.m_graded = false;
		m_certOnly = true;
		m_modul = false;
	}

	public Grade(String program, String examText, String semester,
			double grade, boolean modul) {
		this.m_program = program;
		this.m_examText = examText;
		this.m_semester = semester;
		this.m_grade = grade;
		this.m_graded = true;
		this.m_certOnly = false;
		this.m_modul = modul;
	}

	public String getProgram() {
		return m_program;
	}

	public void setProgram(String program) {
		this.m_program = program;
	}

	public String getExamNumber() {
		return m_examNumber;
	}

	public void setExamNumber(String examNumber) {
		this.m_examNumber = examNumber;
	}

	public String getExamText() {
		return m_examText;
	}

	public void setExamText(String examText) {
		this.m_examText = examText;
	}

	public String getSemester() {
		return m_semester;
	}

	public void setSemester(String semester) {
		this.m_semester = semester;
	}

	public double getEcts() {
		return m_ects;
	}

	public void setEcts(double ects) {
		this.m_ects = ects;
	}

	public double getGrade() {
		return m_grade;
	}

	public void setGrade(double grade) {
		this.m_grade = grade;
	}

	public boolean isGraded() {
		return m_graded;
	}

	public void setGraded(boolean graded) {
		this.m_graded = graded;
	}

	public boolean isCertOnly() {
		return m_certOnly;
	}

	public void setCertOnly(boolean certOnly) {
		this.m_certOnly = certOnly;
	}

	public boolean isModul() {
		return m_modul;
	}

	public void setModul(boolean modul) {
		this.m_modul = modul;
	}

	public String getState() {
		return m_state;
	}

	public void setState(String state) {
		this.m_state = state;
	}

	public String getNotes() {
		return m_notes;
	}

	public void setNotes(String notes) {
		this.m_notes = notes;
	}

	public int getAttempt() {
		return m_attempt;
	}

	public void setAttempt(int attempt) {
		this.m_attempt = attempt;
	}
	
	public String toString() {
		return m_examText + m_grade;
	}
}
