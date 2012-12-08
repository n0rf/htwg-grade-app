package de.htwg.moc.htwg_grade_app;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu.OnMenuItemClickListener;
import de.htwg.moc.htwg_grade_app.qis.DegreeContent;
import de.htwg.moc.htwg_grade_app.searchable.SuggestionProvider;

/**
 * An activity representing a list of Grades. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link GradesListActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link DegreeListFragment} and the item details (if present) is a
 * {@link GradesListFragment}.
 * <p>
 * This activity also implements the required
 * {@link DegreeListFragment.Callbacks} interface to listen for item selections.
 */
public class DegreeListActivity extends FragmentActivity implements OnMenuItemClickListener,
		DegreeListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean m_twoPane;

//	private Builder m_builder;
//	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_degree_list);

		refreshDegreeList();

		if (findViewById(R.id.degree_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			m_twoPane = true;

			// Only in two-pane mode, touched items should be marked as actived!
			((DegreeListFragment) getSupportFragmentManager().findFragmentById(R.id.degree_list))
					.setActivateOnItemClick(true);

			// Get the intent, verify the action and get the query
			handleIntent(getIntent());
		}
	}

	private void refreshDegreeList() {
		if (!DegreeContent.isRequesting) {
			// check settings and refresh view with new data:
			SharedPreferences settings = getSharedPreferences(SettingsActivity.KEY_PREF_USER_SETTINGS, MODE_PRIVATE);
			// SharedPreferences settings =
			// PreferenceManager.getDefaultSharedPreferences(this);
			String user = settings.getString(SettingsActivity.KEY_PREF_USERNAME, "");
			String password = settings.getString(SettingsActivity.KEY_PREF_PASSWORD, "");

			if ("".equals(user) || "".equals(password)) {
				Intent intent = new Intent(this, SettingsActivity.class);
				this.startActivity(intent);
			} else {
				//AsyncTask<String, String, Boolean> task =
				DegreeContent.loadData(DegreeListActivity.this, user, password);
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

			GradesListFragment fragment = ((GradesListFragment) getSupportFragmentManager().findFragmentById(
					R.id.degree_detail_container));
			if (null != fragment) {
				// fragment.setTextFilter(examText);
				fragment.updateGradeList(examText);
			}
		}
	}

	/**
	 * Callback method from {@link DegreeListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String number) {
		if (m_twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(GradesListFragment.ARG_DEGREE_NUMBER, number);
			GradesListFragment fragment = new GradesListFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.degree_detail_container, fragment).commit();
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
			GradesListFragment fragment = ((GradesListFragment) getSupportFragmentManager().findFragmentById(
					R.id.degree_detail_container));
			if (null != fragment) {
				fragment.showPopup(this, findViewById(R.id.grades_menu_item_filter));
			}
			return true;
		case R.id.degree_menu_item_refresh:
			refreshDegreeList();
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
		// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		// if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
	}

	public void showLoadProcedureInformation(String message) {
		// ProgressDialog dialog = new ProgressDialog(this);
		// dialog.setMessage(message);
		// dialog.show();
		//dialog.setMessage(message);
	}

	public void finishedLoadProcedure(boolean result) {
//		DegreeContent.isRequesting = false;
//		dialog.dismiss();
//		if (null == m_builder) {
//			m_builder = new AlertDialog.Builder(this);
//		}
//		dialog = m_builder.create();
//		if (result) {
//			// dialog.setMessage(getText(R.string.refreh_success));
//			refreshView();
//		} else {
//			dialog.setMessage(getText(R.string.refreh_failed));
//			dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					refreshView();
//				}
//			});
//			dialog.show();
//		}
		//refreshView();
	}

	public void refreshView() {
		DegreeListFragment degreeFragment = ((DegreeListFragment) getSupportFragmentManager().findFragmentById(
				R.id.degree_list));
		if (null != degreeFragment) {
			degreeFragment.refreshListView();
		}

		if (m_twoPane) {
			// refresh grades list view and without requesting
			// new grades from QIS
			GradesListFragment gradesFragment = ((GradesListFragment) getSupportFragmentManager().findFragmentById(
					R.id.degree_detail_container));
			if (null != gradesFragment) {
				gradesFragment.refreshListView(false);
			}
		}
	}
}
