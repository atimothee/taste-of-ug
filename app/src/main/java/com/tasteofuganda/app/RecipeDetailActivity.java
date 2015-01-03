package com.tasteofuganda.app;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Timo on 12/30/14.
 */
public class RecipeDetailActivity extends ActionBarActivity{

    private static final String DETAIL_ID_KEY = "id";
    private static final String COLOR_KEY = "color";
    private Long mId;
    private String mColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(getIntent().hasExtra(DETAIL_ID_KEY)){
            mId = getIntent().getLongExtra(DETAIL_ID_KEY, 0);
        }
        if(getIntent().hasExtra(COLOR_KEY)){
            mColor = getIntent().getStringExtra(COLOR_KEY);
        }
        if(savedInstanceState != null && mId == 0 && savedInstanceState.containsKey(DETAIL_ID_KEY)){
            mId = savedInstanceState.getLong(DETAIL_ID_KEY);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(COLOR_KEY)){
            mColor = savedInstanceState.getString(COLOR_KEY);
        }
        if(mColor!=null && !mColor.isEmpty()){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mColor)));
        }
        Bundle args = new Bundle();
        args.putLong(DETAIL_ID_KEY, mId);
        RecipeDetailFragment detailFragment = new RecipeDetailFragment();
        detailFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, detailFragment)
                    .commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DETAIL_ID_KEY, mId);
        outState.putString(COLOR_KEY, mColor);
    }
}
