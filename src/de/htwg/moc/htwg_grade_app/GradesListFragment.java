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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleAdapter;
import de.htwg.moc.htwg_grade_app.adapter.GradeListAdapter;
import de.htwg.moc.htwg_grade_app.dos.Degree;
import de.htwg.moc.htwg_grade_app.dos.Grade;
import de.htwg.moc.htwg_grade_app.dos.GradesFilter;
import de.htwg.moc.htwg_grade_app.qis.Content;

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

	private ArrayList<Grade> m_filteredGrades = new ArrayList<Grade>();

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
			
			setHasOptionsMenu(true);

			if (Content.DEGREES.containsKey(m_selectedDegree)) {
				// if a degree is set, get degree and set title of the view
				m_degree = Content.DEGREES.get(m_selectedDegree);
				getActivity().setTitle(getString(R.string.title_degree_detail, m_degree.getNumber()));
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		m_rootView = inflater.inflate(R.layout.fragment_grades_list, container, false);

		final ListView lv = (ListView) m_rootView.findViewById(R.id.grades_list);
		if (null != lv) {
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					String degreeNumber = m_degree.getNumber();
					String examKey = m_filteredGrades.get(position).getExamText() + m_filteredGrades.get(position).getExamNumber();
					
					if (getActivity() instanceof DegreeListActivity) {
						((DegreeListActivity) getActivity()).showGradeDetails(degreeNumber, examKey);
					
					} else {
						((GradesListActivity) getActivity()).showGradeDetails(degreeNumber, examKey);
					}
				}
			});
		}

		// show the filtered degree details
		if (Content.EXAM_TEXT_FILTER.equals("")) {
			updateGradeList(Content.FILTER);
		} else {
			updateGradeList(Content.EXAM_TEXT_FILTER);
		}

		return m_rootView;
	}
	
	public void updateGradeList(GradesFilter filter) {
		Content.FILTER = filter;
		if (!m_selectedDegree.equals("") && null != m_degree && null != m_rootView) {
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
				to = new int[] { R.id.grade_item_examText, R.id.grade_item_ects, R.id.grade_item_grade };
			} else {
				from = new String[] { "examText", "grade" };
				// to: id of the column where to store
				to = new int[] { R.id.grade_item_examText, R.id.grade_item_grade };
			}

			// prepare the list of all records
			m_filteredGrades.clear();
			List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
			HashMap<String, String> map;
			List<Grade> grades = new ArrayList<Grade>();
			for (Grade grade : m_degree.getGrades().values()) {
				if (filter == GradesFilter.ALL || (filter == GradesFilter.MODULES && grade.isModul())
						|| (filter == GradesFilter.CERTIFICATES && grade.isCertOnly())
						|| (filter == GradesFilter.GRADED && grade.isGraded())) {
					m_filteredGrades.add(grade);
					map = new HashMap<String, String>();
					map.put("examText", grade.getExamText());
					if (grade.isCertOnly()) {
						map.put("grade", grade.getState());
					} else {
						map.put("grade", grade.getGrade());
					}

					if (twoPane) {
						map.put("ects", String.valueOf(grade.getEcts()));
					}

					fillMaps.add(map);
					grades.add(grade);
				}
			}

			// fill in the grid_item layout
			SimpleAdapter adapter = new GradeListAdapter(getActivity(), fillMaps, grades, R.layout.grade_list_item,
					from, to);
			adapter.notifyDataSetChanged();
			lv.setAdapter(adapter);
			lv.invalidateViews();
		}
	}

	public void updateGradeList(String examText) {
		Content.FILTER = GradesFilter.ALL;
		Content.EXAM_TEXT_FILTER = examText;

		if (!m_selectedDegree.equals("") && null != m_degree && null != m_rootView) {
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
				to = new int[] { R.id.grade_item_examText, R.id.grade_item_ects, R.id.grade_item_grade };
			} else {
				from = new String[] { "examText", "grade" };
				// to: id of the column where to store
				to = new int[] { R.id.grade_item_examText, R.id.grade_item_grade };
			}

			// prepare the list of all records
			m_filteredGrades.clear();
			List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
			HashMap<String, String> map;
			List<Grade> grades = new ArrayList<Grade>();
			Locale locale = getResources().getConfiguration().locale;
			for (Grade grade : m_degree.getGrades().values()) {
				if (grade.getExamText().toLowerCase(locale).contains(examText.toLowerCase(locale))) {
					m_filteredGrades.add(grade);
					map = new HashMap<String, String>();
					map.put("examText", grade.getExamText());
					if (grade.isCertOnly()) {
						map.put("grade", grade.getState());
					} else {
						map.put("grade", grade.getGrade());
					}

					if (twoPane) {
						map.put("ects", String.valueOf(grade.getEcts()));
					}

					fillMaps.add(map);
					grades.add(grade);
				}
			}

			// fill in the grid_item layout
			SimpleAdapter adapter = new GradeListAdapter(getActivity(), fillMaps, grades, R.layout.grade_list_item,
					from, to);
			lv.setAdapter(adapter);
		}
	}

	public void refreshListView(boolean requestNewGrades) {
		if ("" != m_selectedDegree) {
			if (requestNewGrades) {
				// starting intend causes the grades fragment to be in back stack!
				Bundle bundle = new Bundle();
				bundle.putBoolean(DegreeListActivity.REFRESH_CARE_ABOUT_CURRENT_KEY, false);
				Intent intent = new Intent(getActivity(), DegreeListActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				// show the filtered degree details
				if ("" == Content.EXAM_TEXT_FILTER) {
					updateGradeList(Content.FILTER);
				} else {
					updateGradeList(Content.EXAM_TEXT_FILTER);
				}
			}
		}
	}

	/**
	 * Method shows the popup menu with settings and other items.
	 */
	public void showPopup(Activity context, View v) {
		PopupMenu popup = new PopupMenu(context, v);
		MenuInflater inflater = popup.getMenuInflater();
		popup.setOnMenuItemClickListener((OnMenuItemClickListener) context);
		MenuItem item;
		inflater.inflate(R.menu.popup_menu, popup.getMenu());
		if (!Content.EXAM_TEXT_FILTER.equals("")) {
			String title = getString(R.string.popup_filter_menu_item_text, Content.EXAM_TEXT_FILTER);
			item = popup.getMenu().findItem(R.id.popup_filter_menu_item_textfilter);
			item.setTitle(title);
		} else {
			// inflater.inflate(R.menu.popup_menu, popup.getMenu());
			popup.getMenu().findItem(R.id.popup_filter_menu_item_textfilter).setVisible(false);
			if (Content.FILTER_MENU_SELECTION == 0) {
				item = popup.getMenu().findItem(R.id.popup_filter_menu_item_all);
			} else {
				item = popup.getMenu().findItem(Content.FILTER_MENU_SELECTION);
			}
		}

		item.setChecked(true);
		popup.show();
	}

	public void filterMenuItemClicked(MenuItem item) {
		item.setChecked(true);
		Content.FILTER_MENU_SELECTION = item.getItemId();

		GradesFilter filter = GradesFilter.ALL;
		switch (item.getItemId()) {
		case R.id.popup_filter_menu_item_all:
			filter = GradesFilter.ALL;
			break;
		case R.id.popup_filter_menu_item_grades:
			filter = GradesFilter.GRADED;
			break;
		case R.id.popup_filter_menu_item_certificates:
			filter = GradesFilter.CERTIFICATES;
			break;
		case R.id.popup_filter_menu_item_modules:
			filter = GradesFilter.MODULES;
			break;
		}
		Content.FILTER = filter;
		Content.EXAM_TEXT_FILTER = "";

		updateGradeList(filter);
	}
}
