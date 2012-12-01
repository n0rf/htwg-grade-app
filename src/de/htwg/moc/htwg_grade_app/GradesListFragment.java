package de.htwg.moc.htwg_grade_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import de.htwg.moc.htwg_grade_app.adapter.GradeListAdapter;
import de.htwg.moc.htwg_grade_app.dos.Degree;
import de.htwg.moc.htwg_grade_app.dos.Grade;
import de.htwg.moc.htwg_grade_app.dos.GradesFilter;
import de.htwg.moc.htwg_grade_app.qis.DegreeContent;

/**
 * A fragment representing a single Degree detail screen. This fragment is
 * either contained in a {@link DegreeListActivity} in two-pane mode (on
 * tablets) or a {@link GradesListActivity} on handsets.
 */
public class GradesListFragment extends Fragment {
	/**
	 * The fragment argument representing the degree number that this fragment
	 * represents.
	 */
	public static final String ARG_DEGREE_NUMBER = "degree_number";

	private String m_selectedDegree = "";

	private View m_rootView = null;

	/** The degree and its grades this fragment is presenting. */
	private Degree m_degree = null;

	// private String m_examTextFilter = "";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GradesListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_DEGREE_NUMBER)) {

			// check for selected degree
			m_selectedDegree = getArguments().getString(ARG_DEGREE_NUMBER);

			if (DegreeContent.DEGREES.containsKey(m_selectedDegree)) {
				// if a degree is set, get degree and set title of the view
				m_degree = DegreeContent.DEGREES.get(m_selectedDegree);
				getActivity().setTitle(
						getString(R.string.title_degree_detail,
								m_degree.getNumber()));
			}
		}
	}

	public void updateGradeList(GradesFilter filter) {
		DegreeContent.FILTER = filter;
		if (!m_selectedDegree.equals("") && null != m_degree
				&& null != m_rootView) {
			ListView lv = ((ListView) m_rootView.findViewById(R.id.grades_list));
			Activity host = (Activity) m_rootView.getContext();
			boolean twoPane = false;
			if (host instanceof DegreeListActivity) {
				twoPane = true;
			} else if (host instanceof GradesListActivity) {
				twoPane = false;
			}

			// create the grid item mapping
			// from: key of the items in the map
			String[] from;
			int[] to;
			if (twoPane) {
				from = new String[] { "examText", "ects", "grade" };
				// to: id of the column where to store
				to = new int[] { R.id.grade_item_examText, R.id.grade_item_ects,
						R.id.grade_item_grade };
			} else {
				from = new String[] { "examText", "grade" };
				// to: id of the column where to store
				to = new int[] { R.id.grade_item_examText,
						R.id.grade_item_grade };
			}

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
					
					if (twoPane) {
						map.put("ects", String.valueOf(grade.getEcts()));
					}

					fillMaps.add(map);
					grades.add(grade);
				}
			}

			// fill in the grid_item layout
			SimpleAdapter adapter = new GradeListAdapter(getActivity(),
					fillMaps, grades, R.layout.grade_list_item, from, to);
			adapter.notifyDataSetChanged();
			lv.setAdapter(adapter);
			lv.invalidateViews();
		}
	}

	public void updateGradeList(String examText) {
		DegreeContent.FILTER = GradesFilter.ALL;
		// this.m_examTextFilter = examText;
		DegreeContent.EXAM_TEXT_FILTER = examText;

		if (!m_selectedDegree.equals("") && null != m_degree
				&& null != m_rootView) {
			ListView lv = (ListView) m_rootView.findViewById(R.id.grades_list);
			Activity host = (Activity) m_rootView.getContext();
			boolean twoPane = false;
			if (host instanceof DegreeListActivity) {
				twoPane = true;
			} else if (host instanceof GradesListActivity) {
				twoPane = false;
			}

			// create the grid item mapping
			// from: key of the items in the map
			String[] from;
			int[] to;
			if (twoPane) {
				from = new String[] { "examText", "ects", "grade" };
				// to: id of the column where to store
				to = new int[] { R.id.grade_item_examText, R.id.grade_item_ects,
						R.id.grade_item_grade };
			} else {
				from = new String[] { "examText", "grade" };
				// to: id of the column where to store
				to = new int[] { R.id.grade_item_examText,
						R.id.grade_item_grade };
			}

			// prepare the list of all records
			List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
			HashMap<String, String> map;
			List<Grade> grades = new ArrayList<Grade>();
			Locale locale = getResources().getConfiguration().locale;
			for (Grade grade : m_degree.getGrades()) {
				if (grade.getExamText().toLowerCase(locale)
						.contains(examText.toLowerCase(locale))) {
					map = new HashMap<String, String>();
					map.put("examText", grade.getExamText());
					if (grade.getGrade() == 0.0) {
						map.put("grade", "");
					} else {
						map.put("grade", String.valueOf(grade.getGrade()));
					}
					
					if (twoPane) {
						map.put("ects", String.valueOf(grade.getEcts()));
					}

					fillMaps.add(map);
					grades.add(grade);
				}
			}

			// fill in the grid_item layout
			SimpleAdapter adapter = new GradeListAdapter(getActivity(),
					fillMaps, grades, R.layout.grade_list_item, from, to);
			// adapter.notifyDataSetChanged();
			lv.setAdapter(adapter);
			// lv.invalidateViews();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		m_rootView = inflater.inflate(R.layout.fragment_grades_list, container,
				false);

		// Show the dummy content as text in a TextView.
		// if (mItem != null) {
		// ((TextView) rootView.findViewById(R.id.degree_detail))
		// .setText(mItem.content);
		// }

		// show the filtered degree details
		if ("" == DegreeContent.EXAM_TEXT_FILTER) {
			updateGradeList(DegreeContent.FILTER);
		} else {
			updateGradeList(DegreeContent.EXAM_TEXT_FILTER);
		}

		return m_rootView;
	}

	// public void setTextFilter(String examText) {
	// this.m_examTextFilter = examText;
	// updateGradeList(examText);
	// }

	public void refreshListView(boolean requestNewGrades) {
		if ("" != m_selectedDegree) {
			if (requestNewGrades) {
				Intent intent = new Intent(getActivity(),
						DegreeListActivity.class);
				startActivity(intent);

				// TODO: request grades and degrees on degree activity and
				// update fragment/activity
				// TODO: Single pane: if selected degree is still in list,
				// update view with this data!
				// TODO: Two pane: use degree list activity for update process
				// TODO: in all cases: checked grade still there?
				// m_degree = DegreeContent.DEGREES.get(m_selectedDegree);
			} else {

				// show the filtered degree details
				if ("" == DegreeContent.EXAM_TEXT_FILTER) {
					updateGradeList(DegreeContent.FILTER);
				} else {
					updateGradeList(DegreeContent.EXAM_TEXT_FILTER);
				}
			}
		}
	}
}
