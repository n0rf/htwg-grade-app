package de.htwg.moc.htwg_grade_app.qis;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import de.htwg.moc.htwg_grade_app.dos.Degree;
import de.htwg.moc.htwg_grade_app.dos.Grade;

public class QisClient {

	private QisRequest m_requester;
	private String m_baseUrl = "https://qisserver.htwg-konstanz.de/qisserver/";
	private String m_user, m_password;
	private Proxy m_proxy;
	private String m_jsessionid = "";
	private List<Degree> m_degrees = new ArrayList<Degree>();

	public QisClient(QisRequest requester, String user, String password) {
		this(requester, user, password, Proxy.NO_PROXY);
	}

	public QisClient(QisRequest requester, String user, String password, Proxy proxy) {
		this.m_requester = requester;
		m_user = user;
		m_password = password;
		m_proxy = proxy;
		HttpsURLConnection.setFollowRedirects(false);
	}

	public List<Degree> getDegrees() {
		return m_degrees;
	}
	
	// private void dumpHtml(String html, String file) {
	// try {
	// BufferedWriter out = new BufferedWriter(new FileWriter(file));
	// out.write(html);
	// out.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	private String getGradesLink() {

		String html = getHtml("rds?state=change&type=1&moduleParameter=studyPOSMenu&nextdir=change&next=menu.vm&"
				+ "subdir=applications&xml=menu&purge=y&navigationPosition=functions%2CstudyPOSMenu&breadcrumb="
				+ "studyPOSMenu&topitem=functions&subitem=studyPOSMenu");

		// dumpHtml(html, "pruefungsverwaltung.html");

		String link = null;
		Matcher m = Pattern.compile(
				"(rds.+?)\"  title=\"\" class=\"auflistung\">Notenspiegel</a>")
				.matcher(html);
		if (m.find()) {
			link = m.group(1);
			link = link.replaceAll("&amp;", "&");
		}

		return link;
	}

	public void addDegrees(String link, List<Degree> degrees) {
		String html = getHtml(link);
		// dumpHtml(html, "abschluesse.html");

		Matcher matcher = Pattern
				.compile(
						"(rds.+?)\" title=\"Leistungen f(.+?)r Abschluss (.+?) anzeigen\">")
				.matcher(html);
		while (matcher.find()) {
			String degLink = matcher.group(1);
			String degName = matcher.group(3);
			m_requester.onProgressUpdate("Found: " + degName);
			String[] name = degName.split(" ");
			if (name.length == 2) {
				Degree degree = new Degree(name[0], name[1]);

				degLink = degLink.replaceAll("&amp;", "&");
				m_requester.onProgressUpdate("Looking for Grades for: " + degName);
				String degHtml = getHtml(degLink);
				// dumpHtml(degHtml, "notenspiegel fuer "+ degName +".html");

				Matcher degM = Pattern.compile("<tr>\\s+(<.+?>)\\s+</tr>",
						Pattern.MULTILINE | Pattern.DOTALL).matcher(degHtml);
				while (degM.find()) {
					Grade grade = getGradeFromRow(degM.group(1));
					if (grade != null)
						degree.addGrade(grade);
				}
				m_requester.onProgressUpdate("Found " + degree.getGrades().size() + " Grades for Degree: " + degName);
				degrees.add(degree);
			}
		}
	}

	public boolean fetchData() {
		m_requester.onProgressUpdate("Login!");
		if (!doLogin()) {
			m_requester.onProgressUpdate("Login failed!");
			return false;
		}

		m_requester.onProgressUpdate("Look for 'notenspiegel' link.");
		String link = getGradesLink();
		if (link == null) {
			m_requester.onProgressUpdate("Could not find notenspiegel link!");
			return false;
		}

		m_degrees.clear();
		m_requester.onProgressUpdate("Add Degrees.");
		addDegrees(link, m_degrees);

		if (m_degrees.size() == 0) {
			m_requester.onProgressUpdate("Could not find any degrees!");
			return false;
		} else {
			m_requester.onProgressUpdate("Degrees and grades successfully added!");
			return true;
		}
	}

	private String removeBoldTagsAndTrim(String html) {
		html = html.replaceAll("<b>", "");
		html = html.replaceAll("</b>", "");
		return html.trim();
	}

	private String simplifyWhitespaces(String html) {
		html = html.replaceAll("\r\n", "");
		html = html.replaceAll("\n", "");
		return html.replaceAll("[ \t]+", " ");
	}

	private Grade getGradeFromRow(String htmlRow) {
		htmlRow = simplifyWhitespaces(htmlRow);

		Matcher matcher = Pattern
				.compile(
						"<td( class=\"qis_kontoOnTop| class=\"kursiv|).+?>(.+?)</td> "
								+ "<td.+?>(.+?)</td> <td.+?>(.+?)</td> <td.+?>(.+?)</td> <td.+?>(.+?)</td> <td.+?>(.+?)</td> "
								+ "<td.+?>(.+?)</td> <td.+?>(.+?)</td> <td.+?>(.+?)</td>")
				.matcher(htmlRow);
		if (!matcher.find())
			return null;

		String type = matcher.group(1);
		boolean isModul = false;
		type = type.replaceAll("class=\"", "").trim();
		if (type.equals("")) {
			type = "normal";
		} else if (type.equals("kursiv")) {
			type = "sg";
		} else if (type.equals("qis_kontoOnTop")) {
			type = "modul";
			isModul = true;
		}

		String[] matches = new String[9];
		for (int i = 0; i < matches.length; i++) {
			matches[i] = removeBoldTagsAndTrim(matcher.group(i + 2));
		}

		// new QisGrade(type, matches[0], matches[1], matches[2], matches[3],
		// matches[4], matches[5], matches[6], matches[7], matches[8]);

		// index 0 = programm (MSI, SEB, ...)
		// index 1 = exam number
		// index 2 = exam text
		// index 3 = semester
		// index 4 = ects
		// index 5 = grade
		// index 6 = passed/state
		// index 7 = notes
		// index 8 = attampt
		Grade grade = new Grade(matches[0], matches[2], matches[3]);
		if (matches[2].contains("(S)")) {
			grade.setCertOnly(true);
		} else {
			grade.setCertOnly(false);
		}
		grade.setExamNumber(matches[1]);
		String ects = matches[4];
		if (!ects.trim().equals("")) {
			try {
				grade.setEcts(Double.parseDouble(ects.replace(',', '.')));
			} catch (NumberFormatException e) {
				grade.setEcts(-1.0);
			}
		}
		String g = matches[5];
		if (g.trim().equals("") || g.trim().equals("0,0")) {
			grade.setGraded(false);
		} else {
			try {
				grade.setGraded(true);
				grade.setGrade(Double.parseDouble(g.replace(',', '.')));
			} catch (NumberFormatException e) {
				grade.setGrade(-1.0);
			}
		}
		grade.setState(matches[6]);
		grade.setNotes(matches[7]);
		String attempt = matches[8];
		if (!attempt.trim().equals("")) {
			try {
				grade.setAttempt(Integer.parseInt(matches[8]));
			} catch (NumberFormatException e) {
				grade.setAttempt(-1);
			}
		}
		grade.setModul(isModul);

		return grade;
	}

	private boolean doLogin() {
		String body = "username=" + m_user + "&password=" + m_password
				+ "&submit=Anmeldung";
		try {
			URL url = new URL(
					m_baseUrl
							+ "rds?state=user&type=1&category=auth.login&startpage=portal.vm");
			HttpsURLConnection con = (HttpsURLConnection) url
					.openConnection(m_proxy);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-length",
					String.valueOf(body.length()));
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			DataOutputStream output = new DataOutputStream(con.getOutputStream());
			output.writeBytes(body);

			if (con.getHeaderField("Location") == null) {
				return false;
			}

			String cookies = con.getHeaderField("Set-Cookie");
			Matcher m = Pattern.compile("JSESSIONID=(.+?);").matcher(cookies);

			if (m.find()) {
				m_jsessionid = m.group(1);
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private String getHtml(String subUrl) {
		String html = "";
		try {
			URL url = new URL(m_baseUrl + subUrl);
			HttpsURLConnection con = (HttpsURLConnection) url
					.openConnection(m_proxy);
			con.setRequestProperty("Cookie", "JSESSIONID=" + m_jsessionid);
			// BufferedReader br = new BufferedReader(new InputStreamReader(
			// con.getInputStream()));
			//
			// String tmp;
			// while ((tmp = br.readLine()) != null) {
			// html += tmp + "\n";
			// }
			//
			// br.close();
			InputStream is = con.getInputStream();
			Scanner scannerOuter = new Scanner(is, "UTF-8");
			Scanner scannerInner = scannerOuter.useDelimiter("\\A");
			html = scannerInner.hasNext() ? scannerInner.next() : "";
			scannerInner.close();
			scannerOuter.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return html;
	}
}
