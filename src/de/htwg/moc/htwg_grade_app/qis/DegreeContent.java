package de.htwg.moc.htwg_grade_app.qis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import de.htwg.moc.htwg_grade_app.DegreeListActivity;
import de.htwg.moc.htwg_grade_app.dos.Degree;
import de.htwg.moc.htwg_grade_app.dos.Grade;
import de.htwg.moc.htwg_grade_app.dos.GradesFilter;

/**
 * Helper class for providing degree and grade data as well as temporary used filter options.
 */
public class DegreeContent {

	public static int FILTER_MENU_SELECTION = 0;

	public static GradesFilter FILTER = GradesFilter.ALL;

	/** List of degrees */
	public static Map<String, Degree> DEGREES = new HashMap<String, Degree>();

	public static List<Degree> DEGREE_LIST = new ArrayList<Degree>();

	public static String EXAM_TEXT_FILTER = "";
	
	public static boolean isRequesting = false;

	// TODO: remove test data!
	static {
		// some sample degrees and grades
		//tmpRefresh();
	}
	
	public static void tmpRefresh() {
		DEGREES.clear();
		DEGREE_LIST.clear();
		
		Degree bachelor = new Degree("84", "Bachelor");
		bachelor.addGrade(new Grade("SEB", "Mathematik 1", "WS08/09", "1.7",
				false));
		bachelor.addGrade(new Grade("SEB", "Mathematik 1 (S)", "WS08/09"));
		bachelor.addGrade(new Grade("SEB", "Digitaltechnik", "WS08/09", "1.7",
				false));
		bachelor.addGrade(new Grade("SEB", "Digitaltechnik (S)", "WS08/09"));
		bachelor.addGrade(new Grade("SEB",
				"Algorithmen und Theoretische Informatik", "WS09/10", "2.2", true));
		bachelor.addGrade(new Grade("SEB", "Wahlpflichtmodul", "WS11/12", "1.7",
				true));
		DEGREES.put(String.valueOf(bachelor.getNumber()), bachelor);
		DEGREE_LIST.add(bachelor);

		Degree master = new Degree("90", "Master");
		master.addGrade(new Grade("MSI", "Theoretische Informatik", "SS12",
				"2.1", true));
		master.addGrade(new Grade("MSI", "Komplexitätstheorie", "SS12", "2.3",
				false));
		master.addGrade(new Grade("MSI", "Algorithmentechnik", "SS12", "2.0",
				false));
		DEGREES.put(String.valueOf(master.getNumber()), master);
		DEGREE_LIST.add(master);
	}

	public static AsyncTask<String, Integer, Boolean> loadData(DegreeListActivity activity, String user, String password) {
		isRequesting = true;
		 return new QisRequest(activity).execute(user, password);
	}
}
