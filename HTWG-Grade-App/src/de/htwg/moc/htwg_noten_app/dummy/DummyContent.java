package de.htwg.moc.htwg_noten_app.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htwg.moc.htwg_noten_app.dos.Degree;
import de.htwg.moc.htwg_noten_app.dos.Grade;
import de.htwg.moc.htwg_noten_app.dos.GradesFilter;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	public static GradesFilter FILTER = GradesFilter.ALL;
	
	/** List of dummy degrees */
	public static Map<String, Degree> DEGREES = new HashMap<String, Degree>();
	
	public static List<Degree> DEGREE_LIST = new ArrayList<Degree>();

	static {
		// some sample degrees and grades
		Degree bachelor = new Degree("84", "Bachelor");
		bachelor.addGrade(new Grade("SEB", "Mathematik 1", "WS08/09", 1.7, false));
		bachelor.addGrade(new Grade("SEB", "Mathematik 1 (S)", "WS08/09"));
		bachelor.addGrade(new Grade("SEB", "Digitaltechnik", "WS08/09", 1.7, false));
		bachelor.addGrade(new Grade("SEB", "Digitaltechnik (S)", "WS08/09"));
		bachelor.addGrade(new Grade("SEB", "Algorithmen und Theoretische Informatik", "WS09/10", 2.2, true));
		bachelor.addGrade(new Grade("SEB", "Wahlpflichtmodul", "WS11/12", 1.7, true));
		DEGREES.put(String.valueOf(bachelor.getNumber()), bachelor);
		DEGREE_LIST.add(bachelor);
		
		Degree master = new Degree("90", "Master");
		master.addGrade(new Grade("MSI", "Theoretische Informatik", "SS12", 2.1, true));
		master.addGrade(new Grade("MSI", "Komplexitätstheorie", "SS12", 2.3, false));
		master.addGrade(new Grade("MSI", "Algorithmentechnik", "SS12", 2.0, false));
		DEGREES.put(String.valueOf(master.getNumber()), master);
		DEGREE_LIST.add(master);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
//	public static class DummyItem {
//		public String id;
//		public String content;
//
//		public DummyItem(String id, String content) {
//			this.id = id;
//			this.content = content;
//		}
//
//		@Override
//		public String toString() {
//			return content;
//		}
//	}
}
