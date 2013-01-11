package de.htwg.moc.htwg_grade_app;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu.OnMenuItemClickListener;
import de.htwg.moc.htwg_grade_app.qis.Content;
import de.htwg.moc.htwg_grade_app.searchable.SuggestionProvider;

/**
 * An activity representing a list of Degrees
 */
public class DegreeListActivity extends FragmentActivity implements OnMenuItemClickListener,
		DegreeListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean m_twoPane;

	public static final String REFRESH_CARE_ABOUT_CURRENT_KEY = "REFRESH_CARE_ABOUT_CURRENT_KEY";

	private String m_userName = "";

	private boolean m_refreshVisibleFragments = true;

	private int m_lastSelection = 0;
	public String m_lastSelectionKey = "";

	private String m_lastDegreeNumberForDetails = "";
	private String m_lastExamKeyForDetails = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean careAboutCurrent = true;
		if (getIntent().hasExtra(REFRESH_CARE_ABOUT_CURRENT_KEY)) {
			careAboutCurrent = getIntent().getExtras().getBoolean(REFRESH_CARE_ABOUT_CURRENT_KEY);
		}
		refreshDegreeList(careAboutCurrent);

		// Get the intent, verify the action and get the query
		handleIntent(getIntent());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();

		m_refreshVisibleFragments = true;

		if (Content.DEGREE_LIST.isEmpty()) {
			// show refresh fragment if no degrees are available
			useRefreshFragment();
		} else {
			// show degree list or two-fragment mode
			useDegreeListFragment();
		}
	}

	private void useDegreeListFragment() {
		setContentView(R.layout.activity_degree_list);

		if (findViewById(R.id.degree_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			m_twoPane = true;
		}

		if (m_refreshVisibleFragments) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();

			arguments.putInt(DegreeListFragment.STATE_ACTIVATED_POSITION, m_lastSelection);
			arguments.putString(DegreeListFragment.USER, m_userName);
			DegreeListFragment degreeFragment = new DegreeListFragment();
			degreeFragment.setArguments(arguments);
			getFragmentManager().beginTransaction().replace(R.id.degree_list_container, degreeFragment).commit();

			if (m_twoPane) {
				degreeFragment.setActivateOnItemClick(true);

				Fragment f = getSupportFragmentManager().findFragmentById(R.id.degree_detail_container);
				if (f == null || (null != f && !(f instanceof GradeDetailsFragment))) {
					// do not show Grades List Fragment if there was an other
					// fragment showing before!
					GradesListFragment gradesFragment = new GradesListFragment();

					arguments = new Bundle();
					if (!Content.DEGREE_LIST.isEmpty()) {
						if (m_lastSelectionKey.equals("")) {
							m_lastSelectionKey = Content.DEGREE_LIST.get(m_lastSelection).getNumber();
							degreeFragment.setActivatedPosition(m_lastSelection);
						}
						arguments.putString(GradesListFragment.ARG_DEGREE_NUMBER, m_lastSelectionKey);
						gradesFragment.setArguments(arguments);
					}
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.degree_detail_container, gradesFragment).commit();
				} else {
					// First pop back stack before loading the grade details
					// again. This is a necessary workaround for the chooser
					// activity in twopane mode.
					getSupportFragmentManager().popBackStack();
					showGradeDetails(m_lastDegreeNumberForDetails, m_lastExamKeyForDetails);
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		m_refreshVisibleFragments = false;
	}

	private void useRefreshFragment() {
		setContentView(R.layout.activity_refresh);

		RefreshFragment fragment = new RefreshFragment();
		getFragmentManager().beginTransaction().replace(R.id.refresh_container, fragment).commit();
	}

	private void refreshDegreeList(boolean careAboutCurrent) {
		// TODO: remove!
		// if (!careAboutCurrent) {
		// Content.tmpRefresh();
		// refreshView();
		// }
		if (!Content.isRequesting) {
			// check settings and refresh view with new data:
			SharedPreferences settings = getSharedPreferences(SettingsActivity.KEY_PREF_USER_SETTINGS, MODE_PRIVATE);
			m_userName = settings.getString(SettingsActivity.KEY_PREF_USERNAME, "");
			String password = settings.getString(SettingsActivity.KEY_PREF_PASSWORD, "");

			if ("".equals(m_userName) || "".equals(password)) {
				Intent intent = new Intent(this, SettingsActivity.class);
				this.startActivity(intent);
			} else {
				if (!careAboutCurrent || (careAboutCurrent && Content.DEGREE_LIST.isEmpty())) {
					Content.loadData(DegreeListActivity.this, m_userName, password);
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String examText = intent.getStringExtra(SearchManager.QUERY);

			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY,
					SuggestionProvider.MODE);
			suggestions.saveRecentQuery(examText, null);

			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.degree_detail_container);
			if (null != fragment && fragment instanceof GradesListFragment) {
				((GradesListFragment) fragment).updateGradeList(examText);
			}
		}
	}

	/**
	 * Callback method from {@link DegreeListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int position, String number) {
		m_lastSelectionKey = number;
		m_lastSelection = position;
		if (m_twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(GradesListFragment.ARG_DEGREE_NUMBER, number);
			GradesListFragment fragment = new GradesListFragment();
			fragment.setArguments(arguments);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(
					R.id.degree_detail_container, fragment);
			ft.commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent gradesListIntent = new Intent(this, GradesListActivity.class);
			gradesListIntent.putExtra(GradesListFragment.ARG_DEGREE_NUMBER, number);
			startActivity(gradesListIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (m_twoPane) {
			inflater.inflate(R.menu.menu_twopane, menu);
		} else {
			inflater.inflate(R.menu.main_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.grades_menu_item_search:
			onSearchRequested();
			return true;
		case R.id.grades_menu_item_filter:
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.degree_detail_container);
			if (null != fragment && fragment instanceof GradesListFragment) {
				((GradesListFragment) fragment).showPopup(this, findViewById(R.id.grades_menu_item_filter));
			}
			return true;
		case R.id.degree_menu_item_refresh:
			refreshDegreeList(false);
			return true;
		case R.id.grades_menu_clear_text_filter:
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY,
					SuggestionProvider.MODE);
			suggestions.clearHistory();
			return true;
		case R.id.grades_menu_item_settings:
		case R.id.degree_menu_item_settings:
			intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// inform grades fragment about the menu item click event
		((GradesListFragment) getSupportFragmentManager().findFragmentById(R.id.degree_detail_container))
				.filterMenuItemClicked(item);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {}
		// if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {}

		// possible addition in here:
		// show different layouts for landscape or portrait orientation
	}

	public void refreshView() {
		DegreeListFragment degreeFragment = (DegreeListFragment) getFragmentManager().findFragmentById(
				R.id.degree_list_container);
		if (null != degreeFragment && !Content.DEGREE_LIST.isEmpty()) {
			degreeFragment.refreshListView();
			degreeFragment.refreshStudentDetails();
		} else {
			if (!Content.DEGREE_LIST.isEmpty()) {
				useDegreeListFragment();
			} else {
				useRefreshFragment();
			}
		}

		if (m_twoPane) {
			// refresh grades list view and without requesting
			// new grades from QIS

			Fragment gradesFragment = getSupportFragmentManager().findFragmentById(R.id.degree_detail_container);
			if (null != gradesFragment && gradesFragment instanceof GradesListFragment) {
				((GradesListFragment) gradesFragment).refreshListView(false);
			}
		}
	}

	public void showGradeDetails(String degreeNumber, String examKey) {
		if (m_twoPane) {
			m_lastDegreeNumberForDetails = degreeNumber;
			m_lastExamKeyForDetails = examKey;

			Bundle arguments = new Bundle();
			arguments.putString(GradeDetailsFragment.ARG_DEGREE_NUMBER, degreeNumber);
			arguments.putString(GradeDetailsFragment.ARG_GRADE_KEY, examKey);
			GradeDetailsFragment fragment = new GradeDetailsFragment();
			fragment.setArguments(arguments);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.degree_detail_container, fragment);
			ft.addToBackStack("gradesList");
			ft.commit();
		}
	}

	public void refresh() {
		refreshDegreeList(false);
	}
}
