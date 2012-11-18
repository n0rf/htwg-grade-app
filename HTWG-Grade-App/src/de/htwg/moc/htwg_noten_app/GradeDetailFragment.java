package de.htwg.moc.htwg_noten_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import de.htwg.moc.htwg_noten_app.adapter.GradeListAdapter;
import de.htwg.moc.htwg_noten_app.dos.Degree;
import de.htwg.moc.htwg_noten_app.dos.Grade;
import de.htwg.moc.htwg_noten_app.dos.GradesFilter;
import de.htwg.moc.htwg_noten_app.dummy.DummyContent;

/**
 * A fragment representing a single Grade detail screen. This fragment is either
 * contained in a {@link DegreeListActivity} in two-pane mode (on tablets) or a
 * {@link GradeDetailActivity} on handsets.
 */
public class GradeDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_DEGREE_NUMBER = "degree_number";

	private String m_selectedDegree = "";

	private View m_rootView = null;

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Degree m_degree = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GradeDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_DEGREE_NUMBER)) {
			// Load the dummy content specified by the fragment
			// arguments.
			// TODO In a real-world scenario, use a Loader
			// to load content from a content provider.

			m_selectedDegree = getArguments().getString(ARG_DEGREE_NUMBER);

			m_degree = DummyContent.DEGREES.get(m_selectedDegree);
			getActivity().setTitle(
					getString(R.string.title_degree_detail,
							m_degree.getNumber()));

			// Create our own version of the grade list adapter
			// List<Grade> grades = m_degree.getGrades();
			// ListAdapter adapter = new GradeListAdapter(this, grades,
			// android.R.layout.simple_list_item_2, new String[] {
			// Car.KEY_MODEL, Car.KEY_MAKE }, new int[] {
			// android.R.id.text1, android.R.id.text2 });
			// this.setListAdapter(adapter);

		}
	}

	public void updateGradeList(GradesFilter filter) {
		DummyContent.FILTER = filter;
		if (!m_selectedDegree.equals("") && null != m_degree
				&& null != m_rootView) {
			ListView lv = ((ListView) m_rootView.findViewById(R.id.grades_list));

			// create the grid item mapping
			// from: key of the items in the map
			String[] from = new String[] { "examText", "grade" };
			// to: id of the column where to store
			int[] to = new int[] { R.id.grade_item_examText,
					R.id.grade_item_grade };

			// prepare the list of all records
			List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
			HashMap<String, String> map;
			List<Grade> grades = new ArrayList<Grade>();
			for (Grade grade : m_degree.getGrades()) {
				if (filter == GradesFilter.ALL
						|| (filter == GradesFilter.MODULES && grade.isModul())
						|| (filter == GradesFilter.CERTIFICATES && grade
								.isCertOnly())
						|| (filter == GradesFilter.GRADED && grade.isGraded())) {
					map = new HashMap<String, String>();
					map.put("examText", grade.getExamText());
					if (grade.getGrade() == 0.0) {
						map.put("grade", "");
					} else {
						map.put("grade", String.valueOf(grade.getGrade()));
					}

					fillMaps.add(map);
					grades.add(grade);
				}
			}

			// fill in the grid_item layout
			SimpleAdapter adapter = new GradeListAdapter(getActivity(),
					fillMaps, grades, R.layout.grade_list_item,
					from, to);
			lv.setAdapter(adapter);

			// ArrayAdapter<Grade> adapter = new
			// ArrayAdapter<Grade>(getActivity(), R.layout.grade_item,
			// m_degree.getGrades());
			// lv.setAdapter(adapter);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		m_rootView = inflater.inflate(R.layout.fragment_grade_detail,
				container, false);

		// Show the degree details
		updateGradeList(DummyContent.FILTER);

		return m_rootView;
	}
}
