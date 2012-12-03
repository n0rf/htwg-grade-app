package de.htwg.moc.htwg_grade_app;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import de.htwg.moc.htwg_grade_app.dos.GradesFilter;
import de.htwg.moc.htwg_grade_app.qis.DegreeContent;

/**
 * An activity representing a single Degree detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link DegreeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link GradesListFragment}.
 */
public class GradesListActivity extends FragmentActivity implements
		OnMenuItemClickListener {

	private GradesListFragment m_fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_grade_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Bundle args = this.getIntent().getExtras();
		// if(args.containsKey("menuSelection")) {
		// String s = args.getString("menuSelection");
		// } else {
		//
		// }

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			// arguments.putString(
			// GradesListFragment.ARG_DEGREE_NUMBER,
			// getIntent().getStringExtra(
			// GradesListFragment.ARG_DEGREE_NUMBER));
			// GradesListFragment fragment = new GradesListFragment();
			// fragment.setArguments(arguments);
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.grade_detail_container, fragment).commit();

			arguments.putString(
					GradesListFragment.ARG_DEGREE_NUMBER,
					getIntent().getStringExtra(
							GradesListFragment.ARG_DEGREE_NUMBER));
			m_fragment = new GradesListFragment();
			m_fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.grade_detail_container, m_fragment).commit();
			
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

			GradesListFragment fragment = ((GradesListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.grade_detail_container));
			// fragment.setTextFilter(examText);
			fragment.updateGradeList(examText);
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
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this,
					DegreeListActivity.class));
			return true;
			// Handle item selection
		case R.id.grades_menu_item_search:
			onSearchRequested();
			return true;
		case R.id.grades_menu_item_filter:
			showPopup(findViewById(R.id.grades_menu_item_filter));
			return true;
		case R.id.grades_menu_item_refresh:
			refreshGradesListView();
			return true;
		case R.id.grades_menu_item_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshGradesListView() {
		// refresh grades list view and request new grades from QIS
		m_fragment.refreshListView(true);
	}

	/**
	 * Method shows the popup menu with settings and other items.
	 */
	public void showPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		popup.setOnMenuItemClickListener(this);
		MenuItem item;
		inflater.inflate(R.menu.popup_menu, popup.getMenu());
		if (!DegreeContent.EXAM_TEXT_FILTER.equals("")) {
			String title = getString(R.string.popup_filter_menu_item_text, DegreeContent.EXAM_TEXT_FILTER);
			item = popup.getMenu().findItem(R.id.popup_filter_menu_item_textfilter);
			item.setTitle(title);
		} else {
			//inflater.inflate(R.menu.popup_menu, popup.getMenu());
			popup.getMenu().findItem(R.id.popup_filter_menu_item_textfilter).setVisible(false);
			if (DegreeContent.FILTER_MENU_SELECTION == 0) {
				item = popup.getMenu().findItem(R.id.popup_filter_menu_item_all);
			} else {
				item = popup.getMenu().findItem(DegreeContent.FILTER_MENU_SELECTION);
			}
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
		m_fragment.updateGradeList(filter);
		return true;
	}
}
