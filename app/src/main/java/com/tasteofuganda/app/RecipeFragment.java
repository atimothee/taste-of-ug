package com.tasteofuganda.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tasteofuganda.app.provider.recipe.RecipeColumns;

/**
 * Created by Timo on 12/24/14.
 */
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int RECIPE_LOADER = 0;
    private SimpleCursorAdapter recipeAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(RECIPE_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        recipeAdapter = new SimpleCursorAdapter(getActivity(),R.layout.list_item_recipe, null,new String[]{}, new int[]{}, 0);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecipeColumns.CONTENT_URI,
                RecipeColumns.FULL_PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            recipeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
