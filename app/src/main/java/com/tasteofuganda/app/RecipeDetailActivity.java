package com.tasteofuganda.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Timo on 12/30/14.
 */
public class RecipeDetailActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, new RecipeDetailFragment())
                    .commit();
        }
    }
}
