package de.htwg.moc.htwg_grade_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import de.htwg.moc.htwg_grade_app.adapter.GradeDetailsAdapter;
import de.htwg.moc.htwg_grade_app.dos.Degree;
import de.htwg.moc.htwg_grade_app.dos.Grade;
import de.htwg.moc.htwg_grade_app.qis.DegreeContent;

/**
 * A fragment representing details of a Grade. This fragment is either contained
 * in a {@link DegreeListActivity} in two-pane mode (on tablets) or a
 * {@link GradesListActivity} on handsets.
 */
public class GradeDetailsFragment extends Fragment {
	
	/**
	 * The fragment argument representing the degree number.
	 */
	public static final String ARG_DEGREE_NUMBER = "degree_number";
	
	/**
	 * The fragment argument representing the grade name.
	 */
	public static final String ARG_GRADE_KEY = "grade_key";
	
	private Grade m_grade;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GradeDetailsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		if (getArguments().containsKey(ARG_DEGREE_NUMBER) && getArguments().containsKey(ARG_GRADE_KEY)) {
			if (DegreeContent.DEGREES.containsKey(getArguments().getString(ARG_DEGREE_NUMBER))) {
				Degree degree = DegreeContent.DEGREES.get(getArguments().getString(ARG_DEGREE_NUMBER));
				if (degree.getGrades().containsKey(getArguments().getString(ARG_GRADE_KEY))) {
					m_grade = degree.getGrades().get(getArguments().getString(ARG_GRADE_KEY));
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_grade_details, container, false);
		
		ListView lv = (ListView) rootView.findViewById(R.id.details_list);

		// create the grid item mapping
		// from: key of the items in the map
		String[] from;
		int[] to;
		from = new String[] { "key", "value" };
		// to: id of the column where to store
		to = new int[] { R.id.grade_detail_key, R.id.grade_detail_value };

		// prepare the list of all details
		List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_program));
		map.put("value", m_grade.getProgram());
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_exam_number));
		map.put("value", m_grade.getExamNumber());
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_exam_text));
		map.put("value", m_grade.getExamText());
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_semester));
		map.put("value", m_grade.getSemester());
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_ects));
		map.put("value", String.valueOf(m_grade.getEcts()));
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_grade));
		map.put("value", m_grade.getGrade());
		fillMaps.add(map);
		
		String type = "";
		if (m_grade.isModul()) {
			type = (String) getText(R.string.grade_detail_type_modul);
		} else if (m_grade.isCertOnly()) {
			type = (String) getText(R.string.grade_detail_type_certificate);
		} else if (m_grade.isGraded()) {
			type = (String) getText(R.string.grade_detail_type_graded);
		}
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_type));
		map.put("value", type);
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_state));
		map.put("value", m_grade.getState());
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_notes));
		map.put("value", m_grade.getNotes());
		fillMaps.add(map);
		
		map = new HashMap<String, String>();
		map.put("key", getString(R.string.grade_detail_attempts));
		map.put("value", String.valueOf(m_grade.getAttempt()));
		fillMaps.add(map);

		// fill in the grid_item layout
		SimpleAdapter adapter = new GradeDetailsAdapter(getActivity(), fillMaps, R.layout.grade_detail_item,
				from, to);
		lv.setAdapter(adapter);

		((Button) rootView.findViewById(R.id.share_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT,
						getString(R.string.share_button_text, m_grade.getExamText(), m_grade.getGrade()));

				startActivity(Intent.createChooser(i,
						getString(R.string.share_button_text, m_grade.getExamText(), m_grade.getGrade())));
			}
		});
		
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}
}
