package com.github.programmerr47.imageviewer.representation.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * @author Michael Spitsin
 * @since 2015-12-04
 */
public class RecentSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = RecentSuggestionsProvider.class.getName();
    public static final int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
