package de.htwg.moc.htwg_noten_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.htwg.moc.htwg_noten_app.dos.Degree;
import de.htwg.moc.htwg_noten_app.dos.Grade;
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

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Degree m_degree;

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
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			
			String selectedDegree = getArguments().getString(ARG_DEGREE_NUMBER);
			
			m_degree = DummyContent.DEGREES.get(selectedDegree);
			getActivity().setTitle(getString(R.string.title_degree_detail, m_degree.getNumber()));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_grade_detail,
				container, false);
		
		// Show the degree details
		if (m_degree != null) {
			ListView lv = ((ListView) rootView.findViewById(R.id.grades_list));
			ArrayAdapter<Grade> adapter = new ArrayAdapter<Grade>(getActivity(), R.layout.grade_item, m_degree.getGrades());
			lv.setAdapter(adapter);
		}

		return rootView;
	}
}
