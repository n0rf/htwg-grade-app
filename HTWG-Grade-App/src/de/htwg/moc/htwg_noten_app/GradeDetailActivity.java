package de.htwg.moc.htwg_noten_app;

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
import de.htwg.moc.htwg_noten_app.dos.GradesFilter;
import de.htwg.moc.htwg_noten_app.dummy.DummyContent;

/**
 * An activity representing a single Grade detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link DegreeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link GradeDetailFragment}.
 */
public class GradeDetailActivity extends FragmentActivity implements
		OnMenuItemClickListener {

	private GradeDetailFragment m_fragment;
	
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
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(
					GradeDetailFragment.ARG_DEGREE_NUMBER,
					getIntent().getStringExtra(
							GradeDetailFragment.ARG_DEGREE_NUMBER));
			m_fragment = new GradeDetailFragment();
			m_fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.grade_detail_container, m_fragment).commit();
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

			// Handle item selection
		case R.id.grades_menu_item_search:
			// TODO: show keyboard, read input and search available grades!
			return true;
		case R.id.grades_menu_item_filter:
			showPopup(findViewById(R.id.grades_menu_item_filter));
			return true;
		case R.id.grades_menu_item_refresh:
			// TODO: refresh grades
			return true;
		case R.id.grades_menu_item_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
		MenuItem i = popup.getMenu().findItem(DummyContent.FILTER_MENU_SELECTION);
		i.setChecked(true);
		popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		
		item.setChecked(true);
		DummyContent.FILTER_MENU_SELECTION = item.getItemId();
		
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
