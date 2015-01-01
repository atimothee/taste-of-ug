package com.tasteofuganda.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Timo on 12/30/14.
 */
public class RecipeDetailActivity extends ActionBarActivity{
    private Long id;
    private static final String DETAIL_ID_KEY = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(getIntent().hasExtra(DETAIL_ID_KEY)){
            id = getIntent().getLongExtra(DETAIL_ID_KEY, 0);
        }
        if(savedInstanceState != null && id == 0){
            id = savedInstanceState.getLong(DETAIL_ID_KEY);
        }
        Bundle args = new Bundle();
        args.putLong(DETAIL_ID_KEY, id);
        RecipeDetailFragment detailFragment = new RecipeDetailFragment();
        detailFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, detailFragment)
                    .commit();

    }
}
