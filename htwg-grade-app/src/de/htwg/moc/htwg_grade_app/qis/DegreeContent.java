package de.htwg.moc.htwg_grade_app.qis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import de.htwg.moc.htwg_grade_app.DegreeListActivity;
import de.htwg.moc.htwg_noten_app.dos.Degree;
import de.htwg.moc.htwg_noten_app.dos.GradesFilter;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DegreeContent {

	public static int FILTER_MENU_SELECTION = 0; // android.R.id.po

	public static GradesFilter FILTER = GradesFilter.ALL;

	/** List of dummy degrees */
	public static Map<String, Degree> DEGREES = new HashMap<String, Degree>();

	public static List<Degree> DEGREE_LIST = new ArrayList<Degree>();

	public static String EXAM_TEXT_FILTER = "";
	
	public static boolean isRequesting = false;

	static {
		// some sample degrees and grades
//		Degree bachelor = new Degree("84", "Bachelor");
//		bachelor.addGrade(new Grade("SEB", "Mathematik 1", "WS08/09", 1.7,
//				false));
//		bachelor.addGrade(new Grade("SEB", "Mathematik 1 (S)", "WS08/09"));
//		bachelor.addGrade(new Grade("SEB", "Digitaltechnik", "WS08/09", 1.7,
//				false));
//		bachelor.addGrade(new Grade("SEB", "Digitaltechnik (S)", "WS08/09"));
//		bachelor.addGrade(new Grade("SEB",
//				"Algorithmen und Theoretische Informatik", "WS09/10", 2.2, true));
//		bachelor.addGrade(new Grade("SEB", "Wahlpflichtmodul", "WS11/12", 1.7,
//				true));
//		DEGREES.put(String.valueOf(bachelor.getNumber()), bachelor);
//		DEGREE_LIST.add(bachelor);
//
//		Degree master = new Degree("90", "Master");
//		master.addGrade(new Grade("MSI", "Theoretische Informatik", "SS12",
//				2.1, true));
//		master.addGrade(new Grade("MSI", "Komplexitätstheorie", "SS12", 2.3,
//				false));
//		master.addGrade(new Grade("MSI", "Algorithmentechnik", "SS12", 2.0,
//				false));
//		DEGREES.put(String.valueOf(master.getNumber()), master);
//		DEGREE_LIST.add(master);
	}

	public static AsyncTask<String, String, Boolean> loadData(DegreeListActivity activity, String user, String password) {
		isRequesting = true;
		 return new QisRequest(activity).execute(user, password);
	}

	// /**
	// * An array of sample (dummy) items.
	// */
	// public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
	//
	// /**
	// * A map of sample (dummy) items, by ID.
	// */
	// public static Map<String, DummyItem> ITEM_MAP = new HashMap<String,
	// DummyItem>();
	//
	// static {
	// // Add 3 sample items.
	// addItem(new DummyItem("1", "Item 1"));
	// addItem(new DummyItem("2", "Item 2"));
	// addItem(new DummyItem("3", "Item 3"));
	// }
	//
	// private static void addItem(DummyItem item) {
	// ITEMS.add(item);
	// ITEM_MAP.put(item.id, item);
	// }
	//
	// /**
	// * A dummy item representing a piece of content.
	// */
	// public static class DummyItem {
	// public String id;
	// public String content;
	//
	// public DummyItem(String id, String content) {
	// this.id = id;
	// this.content = content;
	// }
	//
	// @Override
	// public String toString() {
	// return content;
	// }
	// }
}
