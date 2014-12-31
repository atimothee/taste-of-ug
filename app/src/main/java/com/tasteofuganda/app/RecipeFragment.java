package com.tasteofuganda.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;
import com.tasteofuganda.app.provider.recipe.RecipeColumns;

/**
 * Created by Timo on 12/24/14.
 */
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int RECIPE_LOADER = 0;
    private static final String IMAGE_BASE_URI = "http://tasteofuganda.appspot.com/serve?blob-key=";
    private static final String TAG = RecipeFragment.class.getSimpleName();
    private SimpleCursorAdapter recipeAdapter;
    private final String[] COLUMNS = {RecipeColumns.RECIPE_NAME, RecipeColumns.DESCRIPTION, RecipeColumns.IMAGEKEY};
    private final int[] VIEW_IDS = {R.id.recipe_title, R.id.recipe_description, R.id.recipe_image};

    public interface Callback{
        public void onItemSelected(Long id);
    }

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
                    //((ImageView)view).setImageURI(Uri.parse("http://tasteofuganda.appspot.com/serve?blob-key=" + cursor.getString(columnIndex)));

                    Picasso.with(getActivity()).load(IMAGE_BASE_URI + cursor.getString(columnIndex)).into((ImageView)view);
                    return true; //true because the data was bound to the view
                }
                return false;
            }
        });
        listView.setAdapter(recipeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = recipeAdapter.getCursor();
                if(cursor!=null && cursor.moveToPosition(position)){
                    ((Callback) getActivity()).onItemSelected(cursor.getLong(0));
                    //Log.d(TAG, "clicked position "+position+" id "+cursor.getLong(0));
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(args!=null && args.containsKey("is_search") && args.containsKey("query")){
            if(args.getBoolean("is_search")){
                String[] selectionArgs  = new String[]{args.getString("query"), args.getString("query"), args.getString("query")};
                return new CursorLoader(getActivity(), RecipeColumns.CONTENT_URI,
                        RecipeColumns.FULL_PROJECTION,
                        RecipeColumns.DESCRIPTION+" like ?"+" or "+RecipeColumns.DIRECTIONS+" like ?"+" or "+RecipeColumns.RECIPE_NAME+" like ?",
                        selectionArgs,
                        null
                        );
            }
        }else{
            if(args != null && args.containsKey("category_id")){
                String[] selectionArgs  = new String[]{String.valueOf(args.getLong("category_id", 0))};
                return new CursorLoader(
                        getActivity(),
                        RecipeColumns.CONTENT_URI,
                        RecipeColumns.FULL_PROJECTION,
                        RecipeColumns.CATEGORYID+" = ?",
                        selectionArgs,
                        null
                );
            }else if(args != null && args.containsKey("category_id") && args.getString("category_id").equals("0")){
                return new CursorLoader(
                        getActivity(),
                        RecipeColumns.CONTENT_URI,
                        RecipeColumns.FULL_PROJECTION,
                        null,
                        null,
                        null
                );
            }
        }

    //find way to return empty cursor
    return new CursorLoader(getActivity(), RecipeColumns.CONTENT_URI, null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            recipeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void reloadRecipeFragmentFromArgs(Bundle args) {
        getLoaderManager().restartLoader(RECIPE_LOADER, args, this);
    }


}
