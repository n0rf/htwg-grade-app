package de.htwg.moc.htwg_noten_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * An activity representing a list of Grades. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link GradeDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link DegreeListFragment} and the item details (if present) is a
 * {@link GradeDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link DegreeListFragment.Callbacks} interface to listen for item selections.
 */
public class DegreeListActivity extends FragmentActivity implements
		DegreeListFragment.Callbacks {// OnMenuItemClickListener {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean m_twoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_degree_list);

		// check if detail container is present.
		// container is only present if in large-screen layout
		if (findViewById(R.id.grade_detail_container) != null) {
			// set activity to be in two-pane mode
			m_twoPane = true;

			// Only in two-pane mode, touched items should be marked as actived!
			((DegreeListFragment) getSupportFragmentManager().findFragmentById(
					R.id.grade_list)).setActivateOnItemClick(true);
		}
	}

	/**
	 * Callback method from {@link DegreeListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String number) {
		if (m_twoPane) {
			// if twp-pane mode is actived:
			// add or replace details fragment of selected number by transaction
			Bundle arguments = new Bundle();
			arguments.putString(GradeDetailFragment.ARG_DEGREE_NUMBER, number);
			GradeDetailFragment fragment = new GradeDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.grade_detail_container, fragment).commit();

		} else {
			// single-pane: start detail activity for the selected number
			Intent detailIntent = new Intent(this, GradeDetailActivity.class);
			detailIntent
					.putExtra(GradeDetailFragment.ARG_DEGREE_NUMBER, number);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.degree_menu_item_refresh:
			// TODO: refresh the activity by checking for new degrees
			return true;
		case R.id.degree_menu_item_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
