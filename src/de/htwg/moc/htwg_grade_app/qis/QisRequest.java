package de.htwg.moc.htwg_grade_app.qis;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import de.htwg.moc.htwg_grade_app.DegreeListActivity;
import de.htwg.moc.htwg_grade_app.R;
import de.htwg.moc.htwg_grade_app.dos.Degree;
import de.htwg.moc.htwg_grade_app.dos.Grade;

public class QisRequest extends AsyncTask<String, Integer, Boolean> {

	private Context appContext;

	/** Activity to be notified about the execution status. */
	private DegreeListActivity activity;

	private Builder m_builder;
	private ProgressDialog m_progressDialog;

	private String m_latestMessage = "";

	/**
	 * Default constructor.
	 */
	public QisRequest(Context context) {
		this.activity = (DegreeListActivity) context;
		this.appContext = context;
		m_builder = new AlertDialog.Builder(activity);
	}

	@Override
	protected Boolean doInBackground(String... strings) {
		if (strings.length == 2) {
			if (!"".equals(strings[0]) && !"".equals(strings[1])) {
				String user = strings[0];
				String password = strings[1];

				QisClient client = new QisClient(user, password);

				if (!client.fetchData()) {
					return false;
				}

				DegreeContent.DEGREES.clear();
				DegreeContent.DEGREE_LIST.clear();

				List<Degree> degrees = client.getDegrees();
				for (Degree degree : degrees) {
					DegreeContent.DEGREES.put(degree.getNumber(), degree);
					DegreeContent.DEGREE_LIST.add(degree);
				}
				return true;
			}
		}
		publishProgress(R.string.load_no_login);
		return false;
	}

	@Override
	protected void onProgressUpdate(Integer... progressInfo) {
		super.onProgressUpdate(progressInfo);
		try {
			m_latestMessage = appContext.getString(progressInfo[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		m_progressDialog.setMessage(m_latestMessage);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		m_progressDialog = ProgressDialog.show(appContext, appContext.getString(R.string.refreh_loading), "...");
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		DegreeContent.isRequesting = false;
		m_progressDialog.dismiss();
		if (result) {
			activity.refreshView();
		} else {
			AlertDialog dialog = m_builder.create();
			dialog.setMessage(m_latestMessage);
			dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					activity.refreshView();
				}
			});
			dialog.show();
		}
	}

	public class QisClient {

		private String m_baseUrl = "https://qisserver.htwg-konstanz.de/qisserver/";
		private String m_user, m_password;
		private Proxy m_proxy;
		private String m_jsessionid = "";
		private List<Degree> m_degrees = new ArrayList<Degree>();

		public QisClient(String user, String password) {
			this(user, password, Proxy.NO_PROXY);
		}

		public QisClient(String user, String password, Proxy proxy) {
			m_user = user;
			m_password = password;
			m_proxy = proxy;
			HttpsURLConnection.setFollowRedirects(false);
		}

		public List<Degree> getDegrees() {
			return m_degrees;
		}

		private String getGradesLink() {

			String link = null;
			String html = getHtml("rds?state=change&type=1&moduleParameter=studyPOSMenu&nextdir=change&next=menu.vm&"
					+ "subdir=applications&xml=menu&purge=y&navigationPosition=functions%2CstudyPOSMenu&breadcrumb="
					+ "studyPOSMenu&topitem=functions&subitem=studyPOSMenu");
			if (html.equals("")) {
				publishProgress(R.string.load_lookup_of_link_failed);
				return link;
			}

			Matcher m = Pattern.compile("(rds.+?)\"  title=\"\" class=\"auflistung\">Notenspiegel</a>").matcher(html);
			if (m.find()) {
				link = m.group(1);
				link = link.replaceAll("&amp;", "&");
			}

			return link;
		}

		public boolean addDegrees(String link, List<Degree> degrees) {
			String html = getHtml(link);
			if (html.equals("")) {
				publishProgress(R.string.load_lookup_of_link_failed);
				return false;
			}

			Matcher matcher = Pattern.compile("(rds.+?)\" title=\"Leistungen f(.+?)r Abschluss (.+?) anzeigen\">")
					.matcher(html);
			while (matcher.find()) {
				String degLink = matcher.group(1);
				String degName = matcher.group(3);
				String[] name = degName.split(" ");
				if (name.length == 2) {
					Degree degree = new Degree(name[0], name[1]);

					degLink = degLink.replaceAll("&amp;", "&");

					String degHtml = getHtml(degLink);
					if (degLink.equals("")) {
						publishProgress(R.string.load_lookup_of_link_failed);
						return false;
					}

					Matcher degM = Pattern.compile("<tr>\\s+(<.+?>)\\s+</tr>", Pattern.MULTILINE | Pattern.DOTALL)
							.matcher(degHtml);
					while (degM.find()) {
						Grade grade = getGradeFromRow(degM.group(1));
						if (grade != null)
							degree.addGrade(grade);
					}
					degrees.add(degree);
				}
			}
			return true;
		}

		public boolean fetchData() {
			publishProgress(R.string.load_try_login);
			if (!doLogin()) {
				publishProgress(R.string.load_login_failed);
				return false;
			}

			publishProgress(R.string.load_lookup_link);
			String link = getGradesLink();
			if (null == link) {
				publishProgress(R.string.load_lookup_link_failed);
				return false;
			}

			m_degrees.clear();
			publishProgress(R.string.load_add_degrees);
			if (!addDegrees(link, m_degrees)) {
				return false;
			}

			if (m_degrees.size() == 0) {
				publishProgress(R.string.load_no_degrees_found);
				return false;
			} else {
				publishProgress(R.string.load_degrees_add_success);
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
									+ "<td.+?>(.+?)</td> <td.+?>(.+?)</td> <td.+?>(.+?)</td>").matcher(htmlRow);
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
				grade.setGraded(true);
				grade.setGrade(g.replace(',', '.'));
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
			String body = "username=" + m_user + "&password=" + m_password + "&submit=Anmeldung";
			try {
				URL url = new URL(m_baseUrl + "rds?state=user&type=1&category=auth.login&startpage=portal.vm");
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection(m_proxy);
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setRequestProperty("Content-length", String.valueOf(body.length()));
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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

			} catch (MalformedURLException e) {
				publishProgress(R.string.load_internal_error);
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
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection(m_proxy);
				con.setRequestProperty("Cookie", "JSESSIONID=" + m_jsessionid);
				InputStream is = con.getInputStream();
				Scanner scannerOuter = new Scanner(is, "UTF-8");
				Scanner scannerInner = scannerOuter.useDelimiter("\\A");
				html = scannerInner.hasNext() ? scannerInner.next() : "";
				scannerInner.close();
				scannerOuter.close();
				is.close();

			} catch (Exception e) {
				return "";
			}

			if (!html.contains("DOCTYPE HTML PUBLIC") || !html.contains("</html>")) {
				html = "";
			}
			return html;
		}
	}
}
