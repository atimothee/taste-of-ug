package com.tasteofuganda.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Timo on 12/30/14.
 */
public class RecipeDetailActivity extends ActionBarActivity{
    //add nutrition facts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getSupportActionBar().set
        if(savedInstanceState == null){
            Long id = getIntent().getLongExtra("id", 0);
            Bundle args = new Bundle();
            args.putLong("id", id);
            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, detailFragment)
                    .commit();
        }
    }
}
