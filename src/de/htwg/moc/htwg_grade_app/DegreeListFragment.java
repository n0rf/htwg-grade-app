package de.htwg.moc.htwg_grade_app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.htwg.moc.htwg_grade_app.dos.Degree;
import de.htwg.moc.htwg_grade_app.qis.Content;

/**
 * A list fragment representing a list of Grades. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link GradesListFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class DegreeListFragment extends Fragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	public static final String STATE_ACTIVATED_POSITION = "activated_position";

	public static final String USER = "user";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks m_callbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int m_activatedPosition = ListView.INVALID_POSITION;

	private View m_rootView = null;

	private String m_userName = "";

	private int m_listChoiceMode;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(int position, String number);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int position, String number) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DegreeListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		m_rootView = inflater.inflate(R.layout.fragment_degree_list, container, false);

		final ListView lv = (ListView) m_rootView.findViewById(R.id.degree_list);
		lv.setChoiceMode(m_listChoiceMode);
		lv.setAdapter(new ArrayAdapter<Degree>(getActivity(), android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, Content.DEGREE_LIST));
		// possible addition: listview text size by setting an textview instead of text1
		if (null != lv) {
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String number = Content.DEGREE_LIST.get(position).getNumber();
					setActivatedPosition(position);
					m_callbacks.onItemSelected(position, number);
				}
			});
		}

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}

		if (getArguments().containsKey(STATE_ACTIVATED_POSITION)) {
			// check for selected degree
			setActivatedPosition(getArguments().getInt(STATE_ACTIVATED_POSITION));
		}

		if (getArguments().containsKey(USER)) {
			m_userName = getArguments().getString(USER);
		}
		if (!m_userName.equals("")) {
			refreshStudentDetails();
		}
		return m_rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		m_callbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		m_callbacks = sDummyCallbacks;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (m_activatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, m_activatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		if (null != m_rootView) {
			ListView lv = (ListView) m_rootView.findViewById(R.id.degree_list);
			if (null != lv) {
				lv.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
			}
		}
		m_listChoiceMode = activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE;
	}

	public void setActivatedPosition(int position) {
		if (null != m_rootView) {
			ListView lv = (ListView) m_rootView.findViewById(R.id.degree_list);
			if (position == ListView.INVALID_POSITION) {
				lv.setItemChecked(m_activatedPosition, false);
			} else {
				lv.setItemChecked(position, true);
			}
			m_activatedPosition = position;
		}
	}

	public void refreshListView() {
		ListView lv = (ListView) m_rootView.findViewById(R.id.degree_list);
		if (null != lv) {
			lv.setAdapter(new ArrayAdapter<Degree>(getActivity(), android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, Content.DEGREE_LIST));
			lv.setItemChecked(m_activatedPosition, true);
		}
	}

	public void refreshStudentDetails() {
		if (null != m_rootView && null != m_rootView.findViewById(R.id.logged_in_user_fullname)) {
			((TextView) m_rootView.findViewById(R.id.logged_in_user_fullname)).setText(Content.STUDENT.getName());
			((TextView) m_rootView.findViewById(R.id.logged_in_user_nummer)).setText(Content.STUDENT.getNumber());
			((TextView) m_rootView.findViewById(R.id.logged_in_user_birth)).setText(Content.STUDENT
					.getBirthDateAndPlace());
			((TextView) m_rootView.findViewById(R.id.logged_in_user_address)).setText(Content.STUDENT.getAddress());
		}
	}
}
