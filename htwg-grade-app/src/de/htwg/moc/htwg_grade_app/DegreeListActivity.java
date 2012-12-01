package de.htwg.moc.htwg_grade_app;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import de.htwg.moc.htwg_grade_app.qis.DegreeContent;
import de.htwg.moc.htwg_noten_app.dos.GradesFilter;

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
public class DegreeListActivity extends FragmentActivity implements
		OnMenuItemClickListener, DegreeListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean m_twoPane;

	private Builder m_builder;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_degree_list);

		// check settings and restore preferences:
		SharedPreferences s1 = getSharedPreferences(
				SettingsActivity.KEY_PREF_USER_SETTINGS, MODE_PRIVATE);
		String u = s1.getString(SettingsActivity.KEY_PREF_USERNAME, "");
		String pw = s1.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
		// SharedPreferences settings =
		// PreferenceManager.getDefaultSharedPreferences(this);
		// String user = settings.getString(SettingsActivity.KEY_PREF_USERNAME,
		// "");
		// String password =
		// settings.getString(SettingsActivity.KEY_PREF_PASSWORD, "");

		if ("".equals(u) || "".equals(pw)) {
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
		} else {
			// AsyncTask<String, String, Boolean> task =
			DegreeContent.loadData(DegreeListActivity.this, u, pw);

			m_builder = new AlertDialog.Builder(this);
			dialog = m_builder.create();
			dialog.setMessage(getText(R.string.refreh_loading));
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		if (findViewById(R.id.degree_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			m_twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.

			// Only in two-pane mode, touched items should be marked as actived!
			((DegreeListFragment) getSupportFragmentManager().findFragmentById(
					R.id.degree_list)).setActivateOnItemClick(true);

			// Get the intent, verify the action and get the query
			handleIntent(getIntent());
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String examText = intent.getStringExtra(SearchManager.QUERY);

			GradesListFragment fragment = ((GradesListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.degree_detail_container));
			if (null != fragment) {
				fragment.setTextFilter(examText);
				// fragment.updateGradeList(examText);
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
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.degree_detail_container, fragment).commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent gradesListIntent = new Intent(this, GradesListActivity.class);
			gradesListIntent.putExtra(GradesListFragment.ARG_DEGREE_NUMBER,
					number);
			startActivity(gradesListIntent);
		}

		// if (m_twoPane) {
		// // if twp-pane mode is actived:
		// // add or replace details fragment of selected number by transaction
		// Bundle arguments = new Bundle();
		// arguments.putString(DegreeDetailFragment.ARG_DEGREE_NUMBER, number);
		// DegreeDetailFragment fragment = new DegreeDetailFragment();
		// fragment.setArguments(arguments);
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.grade_detail_container, fragment).commit();
		//
		// } else {
		// // single-pane: start detail activity for the selected number
		// Intent detailIntent = new Intent(this, DegreeDetailActivity.class);
		// detailIntent.putExtra(DegreeDetailFragment.ARG_DEGREE_NUMBER,
		// number);
		// startActivity(detailIntent);
		// }
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
		// if (m_twoPane) {
		// GradesListFragment g =
		// ((GradesListFragment)getSupportFragmentManager().findFragmentById(R.layout.fragment_grades_list));
		// g.onOptionsItemSelected(item);

		// Intent gradesListIntent = new Intent(this,
		// GradesListActivity.class);
		// gradesListIntent.putExtra("menuSelection", item.getItemId());
		// //gradesListIntent.addFlags(Intent.FLAG_ACTIVITY_);
		// startActivity(gradesListIntent);
		Intent intent;

		switch (item.getItemId()) {
		case R.id.grades_menu_item_search:
			onSearchRequested();
			return true;
		case R.id.grades_menu_item_filter:
			showPopup(findViewById(R.id.grades_menu_item_filter));
			return true;
		case R.id.degree_menu_item_refresh:
			refresh();
			return true;
		case R.id.grades_menu_item_settings:
		case R.id.degree_menu_item_settings:
			intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);

		// }
		// Handle item selection
		// switch (item.getItemId()) {
		// case R.id.degree_menu_item_refresh:
		// // TODO: refresh the activity by checking for new degrees
		// return true;
		// case R.id.degree_menu_item_settings:
		// Intent intent = new Intent(this, SettingsActivity.class);
		// this.startActivity(intent);
		// return true;
		// default:
		// return super.onOptionsItemSelected(item);
		// }
	}

	private void refresh() {
		if (!DegreeContent.isRequesting) {
			SharedPreferences settings = getSharedPreferences(
					SettingsActivity.KEY_PREF_USER_SETTINGS, MODE_PRIVATE);
			// SharedPreferences settings =
			// PreferenceManager.getDefaultSharedPreferences(this);
			String u = settings.getString(SettingsActivity.KEY_PREF_USERNAME,
					"");
			String pw = settings.getString(SettingsActivity.KEY_PREF_PASSWORD,
					"");
			DegreeContent.loadData(this, u, pw);

			Builder builder = new AlertDialog.Builder(this);
			dialog = builder.create();
			// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage(getText(R.string.refreh_loading));
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

			// refresh degree list view and request new grades and degrees from
			// QIS
			// ((DegreeListFragment)
			// getSupportFragmentManager().findFragmentById(
			// R.id.degree_list)).refreshListView();
			//
			// if (m_twoPane) {
			// // refresh grades list view and without requesting new grades
			// // from
			// // QIS
			// GradesListFragment fragment = ((GradesListFragment)
			// getSupportFragmentManager()
			// .findFragmentById(R.id.degree_detail_container));
			// if (null != fragment) {
			// fragment.refreshListView(false);
			// }
			// }
		}
	}

	/**
	 * Method shows the popup menu with settings and other items.
	 */
	public void showPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		popup.setOnMenuItemClickListener(this);
		inflater.inflate(R.menu.popup_menu, popup.getMenu());
		MenuItem item;
		if (DegreeContent.FILTER_MENU_SELECTION == 0) {
			item = popup.getMenu().findItem(R.id.popup_filter_menu_item_all);
		} else {
			item = popup.getMenu()
					.findItem(DegreeContent.FILTER_MENU_SELECTION);
		}
		item.setChecked(true);
		popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		item.setChecked(true);
		DegreeContent.FILTER_MENU_SELECTION = item.getItemId();

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
		default:
			return false;
		}
		DegreeContent.FILTER = filter;

		((GradesListFragment) getSupportFragmentManager().findFragmentById(
				R.id.degree_detail_container)).updateGradeList(filter);

		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.degree_detail_container, new
		// GradesListFragment()).commit();

		// GradesListFragment g = ((GradesListFragment)
		// getSupportFragmentManager()
		// .findFragmentById(R.layout.fragment_grades_list));
		// g.onOptionsItemSelected(item);

		// m_fragment.updateGradeList(filter);
		// Intent gradesListIntent = new Intent(this, GradesListFragment.class);
		// gradesListIntent.putExtra("menuSelection", item.getItemId());
		// gradesListIntent.addFlags(Intent.FLAG_ACTIVITY_);
		// startActivity(gradesListIntent);
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
		// dialog.setMessage(message);
	}

	public void finishedLoadProcedure(boolean result) {
		DegreeContent.isRequesting = false;
		dialog.dismiss();
		if (null == m_builder) {
			m_builder = new AlertDialog.Builder(this);
		}
		dialog = m_builder.create();
		if (result) {
			dialog.setMessage(getText(R.string.refreh_success));
		} else {
			dialog.setMessage(getText(R.string.refreh_failed));
		}
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						DegreeListFragment degreeFragment = ((DegreeListFragment) getSupportFragmentManager()
								.findFragmentById(R.id.degree_list));
						if (null != degreeFragment) {
							degreeFragment.refreshListView();
						}

						if (m_twoPane) {
							// refresh grades list view and without requesting
							// new grades from QIS
							GradesListFragment gradesFragment = ((GradesListFragment) getSupportFragmentManager()
									.findFragmentById(
											R.id.degree_detail_container));
							if (null != gradesFragment) {
								gradesFragment.refreshListView(false);
							}
						}

						DegreeListFragment fragment = ((DegreeListFragment) getSupportFragmentManager()
								.findFragmentById(R.id.degree_list));
						if (null != fragment) {
							fragment.refreshListView();
						}
					}
				});
		dialog.show();
	}
}
