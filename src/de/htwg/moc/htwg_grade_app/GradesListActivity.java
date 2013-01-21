package de.htwg.moc.htwg_grade_app;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu.OnMenuItemClickListener;

/**
 * An activity representing a single Degree detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link DegreeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link GradesListFragment}.
 */
public class GradesListActivity extends FragmentActivity implements OnMenuItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_grade_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();

			arguments.putString(GradesListFragment.ARG_DEGREE_NUMBER,
					getIntent().getStringExtra(GradesListFragment.ARG_DEGREE_NUMBER));
			GradesListFragment fragment = new GradesListFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.grade_detail_container, fragment).commit();

			// Get the intent, verify the action and get the query
			handleIntent(getIntent());
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

			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.grade_detail_container);
			if (fragment instanceof GradesListFragment) {
				((GradesListFragment) fragment).updateGradeList(examText);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.grades_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Fragment fragment = null;
		
		switch (item.getItemId()) {
		case android.R.id.home:
			// this ID represents the Home or Up button
			NavUtils.navigateUpTo(this, new Intent(this, DegreeListActivity.class));
			return true;
		case R.id.grades_menu_item_search:
			onSearchRequested();
			return true;
		case R.id.grades_menu_item_filter:
			fragment = getSupportFragmentManager().findFragmentById(R.id.grade_detail_container);
			if (fragment != null && fragment instanceof GradesListFragment) {
				((GradesListFragment) fragment).showPopup(this, findViewById(R.id.grades_menu_item_filter));
			}
			return true;
		case R.id.grades_menu_item_refresh:
			// refresh grades list view and request new grades from QIS
			fragment = getSupportFragmentManager().findFragmentById(R.id.grade_detail_container);
			if (fragment != null && fragment instanceof GradesListFragment) {
				((GradesListFragment) fragment).refreshListView(true);
			}
			return true;
		case R.id.grades_menu_item_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.grade_detail_container);
		if (fragment != null && fragment instanceof GradesListFragment) {
			((GradesListFragment) fragment).filterMenuItemClicked(item);
		}
		return true;
	}

	public void showGradeDetails(String degreeNumber, String examText) {
		Bundle arguments = new Bundle();
		arguments.putString(GradeDetailsFragment.ARG_DEGREE_NUMBER, degreeNumber);
		arguments.putString(GradeDetailsFragment.ARG_GRADE_KEY, examText);
		GradeDetailsFragment fragment = new GradeDetailsFragment();
		fragment.setArguments(arguments);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.grade_detail_container,
				fragment);
		ft.addToBackStack("gradesList");
		ft.commit();
	}
}
