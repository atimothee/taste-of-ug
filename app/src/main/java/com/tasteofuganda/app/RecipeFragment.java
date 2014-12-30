package com.tasteofuganda.app;

import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;

import com.tasteofuganda.app.provider.recipe.RecipeColumns;

/**
 * Created by Timo on 12/24/14.
 */
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int RECIPE_LOADER = 0;
    private SimpleCursorAdapter recipeAdapter;
    private final String[] COLUMNS = {RecipeColumns.RECIPE_NAME, RecipeColumns.IMAGEKEY};
    private final int[] VIEW_IDS = {R.id.recipe_title, R.id.recipe_image};

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(RECIPE_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        recipeAdapter = new SimpleCursorAdapter(getActivity(),R.layout.list_item_recipe, null,COLUMNS, VIEW_IDS, 0);
        View rootView = inflater.inflate(R.layout.recipe_fragment, null);
        ListView listView = (ListView) rootView.findViewById(R.id.recipe_list);
        recipeAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            /** Binds the Cursor column defined by the specified index to the specified view */
            public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                if(view.getId() == R.id.recipe_image){
                    ((ImageView)view).setImageURI(Uri.parse("http://tasteofuganda.appspot.com/serve?blob-key=" + cursor.getString(columnIndex)));
                    return true; //true because the data was bound to the view
                }
                return false;
            }
        });
        listView.setAdapter(recipeAdapter);
        return rootView;
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
