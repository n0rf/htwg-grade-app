package de.htwg.moc.htwg_grade_app.dos;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Degree.
 * 
 * @author Andreas
 * 
 */
public class Degree {
	private String m_number;
	private String m_name;
	private List<Grade> m_grades = new ArrayList<Grade>();

	public Degree(String number, String name) {
		this.m_number = number;
		this.m_name = name;
	}

	public String getNumber() {
		return m_number;
	}

	public void setNumber(String number) {
		this.m_number = number;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public List<Grade> getGrades() {
		return m_grades;
	}

	public void setGrades(List<Grade> grades) {
		this.m_grades = grades;
	}

	public void addGrade(Grade grade) {
		m_grades.add(grade);
	}

	public String toString() {
		return m_number + " " + m_name;
	}
}
