package com.tasteofuganda.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Timo on 12/30/14.
 */
public class RecipeDetailFragment extends Fragment{

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putString();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = getActivity().getIntent();
        if(i !=null && i.hasExtra("id")){
           // getLoaderManager().restartLoader()
        }

    }
}
