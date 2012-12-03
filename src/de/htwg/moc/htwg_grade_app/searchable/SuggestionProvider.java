package de.htwg.moc.htwg_grade_app.searchable;

import android.content.SearchRecentSuggestionsProvider;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {

	public final static String AUTHORITY = "de.htwg.moc.htwg_grade_app.searchable.SuggestionProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public SuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}

}
