package de.htwg.moc.htwg_grade_app;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {
	
	public static final String KEY_PREF_USER_SETTINGS = "pref_user_settings";

	public static final String KEY_PREF_USERNAME = "pref_username";
	
	public static final String KEY_PREF_PASSWORD = "pref_password";
	
	OnSharedPreferenceChangeListener listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setupActionBar();

//		SharedPreferences prefs = this.getSharedPreferences("settings", 0);
//	    listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//			public void onSharedPreferenceChanged(SharedPreferences prefs,
//					String key) {
//				int flag = 1;
//			}
//		};
//
//		prefs.registerOnSharedPreferenceChangeListener(listener);
		
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UserPasswordFragment())
                .commit();
	}

	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class UserPasswordFragment extends PreferenceFragment { // implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			getPreferenceManager().setSharedPreferencesName(SettingsActivity.KEY_PREF_USER_SETTINGS);
			
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.pref_user_settings);
		}

//		@Override
//		public void onSharedPreferenceChanged(
//				SharedPreferences sharedPreferences, String key) {
//			// TODO Auto-generated method stub
//			int a = 0;
//		}
	}
}
