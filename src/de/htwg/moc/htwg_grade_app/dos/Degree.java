package de.htwg.moc.htwg_grade_app.dos;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class representing a Degree.
 */
public class Degree {
	private String m_number;
	private String m_name;
	private SortedMap<String, Grade> m_grades = new TreeMap<String, Grade>();

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

	public SortedMap<String, Grade> getGrades() {
		return m_grades;
	}

	public void setGrades(SortedMap<String, Grade> grades) {
		this.m_grades = grades;
	}

	public void addGrade(Grade grade) {
		m_grades.put(grade.getExamText() + grade.getExamNumber(), grade);
	}

	@Override
	public String toString() {
		return m_number + " " + m_name;
	}
}
