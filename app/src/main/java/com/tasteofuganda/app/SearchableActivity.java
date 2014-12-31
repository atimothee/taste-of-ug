package com.tasteofuganda.app;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Timo on 12/31/14.
 */
public class SearchableActivity extends ActionBarActivity implements RecipeFragment.Callback{

    private Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_browse_recipes);
        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, new RecipeDetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            RecipeFragment frag = (RecipeFragment) getSupportFragmentManager().findFragmentById(
                    R.id.fragment_recipe);
                Bundle args = new Bundle();
                args.putBoolean("is_search", true);
                args.putString("query", query);
                frag.reloadRecipeFragmentFromArgs(args);
            getSupportActionBar().setTitle("Results for '"+query+"'");
        }

    }

    @Override
    public void onItemSelected(Long id) {

    }
}
