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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tasteofuganda.app.provider.recipe.RecipeColumns;

import java.util.ArrayList;

/**
 * Created by Timo on 12/24/14.
 */
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int RECIPE_LOADER = 0;
    private static final String SAVED_STATE_SELECTION_KEY = "selection";
    private static final String ARGS_SELECTED_ID_KEY = "selected_id";//for when fragment is called from notification
    private static final String ARGS_CATEGORY_ID_KEY = "category_id";
    private static final String ARGS_IS_SEARCH_KEY = "is_search";
    private static final String ARGS_QUERY_KEY = "query";
    private static final String TAG = RecipeFragment.class.getSimpleName();
    private SimpleCursorAdapter recipeAdapter;
    private final String[] COLUMNS = {RecipeColumns.RECIPE_NAME, RecipeColumns.DESCRIPTION, RecipeColumns.IMAGEURL};
    private final int[] VIEW_IDS = {R.id.recipe_title, R.id.recipe_description, R.id.recipe_image};
    private int mPosition;
    private ListView mListView;

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
        mListView = (ListView) rootView.findViewById(R.id.recipe_list);
        recipeAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            /** Binds the Cursor column defined by the specified index to the specified view */
            public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                view.setTag(1, cursor.getLong(cursor.getColumnIndex(RecipeColumns._ID)));
                if(view.getId() == R.id.recipe_image){
                    //((ImageView)view).setImageURI(Uri.parse("http://tasteofuganda.appspot.com/serve?blob-key=" + cursor.getString(columnIndex)));

                    Picasso.with(getActivity()).load(cursor.getString(columnIndex)).into((ImageView)view);
                    return true; //true because the data was bound to the view
                }
                return false;
            }
        });
        mListView.setAdapter(recipeAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                Cursor cursor = recipeAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity()).onItemSelected(cursor.getLong(0));
                }
            }
        });
        if(savedInstanceState!=null && savedInstanceState.containsKey(SAVED_STATE_SELECTION_KEY)){
            mPosition = savedInstanceState.getInt(SAVED_STATE_SELECTION_KEY);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(args!=null && args.containsKey(ARGS_IS_SEARCH_KEY) && args.containsKey(ARGS_QUERY_KEY)){
            if(args.getBoolean(ARGS_IS_SEARCH_KEY)){
                String[] selectionArgs  = new String[]{args.getString(ARGS_QUERY_KEY), args.getString(ARGS_QUERY_KEY), args.getString(ARGS_QUERY_KEY)};
                return new CursorLoader(getActivity(), RecipeColumns.CONTENT_URI,
                        RecipeColumns.FULL_PROJECTION,
                        RecipeColumns.DESCRIPTION+" like ?"+" or "+RecipeColumns.DIRECTIONS+" like ?"+" or "+RecipeColumns.RECIPE_NAME+" like ?",
                        selectionArgs,
                        null
                        );
            }
        }else{
            if(args != null && args.containsKey(ARGS_CATEGORY_ID_KEY) && args.getLong(ARGS_CATEGORY_ID_KEY)==0){
                return new CursorLoader(
                        getActivity(),
                        RecipeColumns.CONTENT_URI,
                        RecipeColumns.FULL_PROJECTION,
                        null,
                        null,
                        null
                );
            }else if(args != null && args.containsKey(ARGS_CATEGORY_ID_KEY)){
                String[] selectionArgs  = new String[]{String.valueOf(args.getLong(ARGS_CATEGORY_ID_KEY, 0))};
                return new CursorLoader(
                        getActivity(),
                        RecipeColumns.CONTENT_URI,
                        RecipeColumns.FULL_PROJECTION,
                        RecipeColumns.CATEGORYID+" = ?",
                        selectionArgs,
                        null
                );
            }
            /*for just in case category id hasn't yet been set*/
            else{
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

        if(getArguments().containsKey(ARGS_SELECTED_ID_KEY)){
            ArrayList<View> views = new ArrayList<>();
            mListView.reclaimViews(views);
            for(View v: views){
                if(v.getTag(1) == Long.valueOf(getArguments().getString(ARGS_SELECTED_ID_KEY))){
                    mPosition = mListView.getPositionForView(v);
                }
            }
        }
        mListView.setSelection(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void reloadRecipeFragmentFromArgs(Bundle args) {
        getLoaderManager().restartLoader(RECIPE_LOADER, args, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SAVED_STATE_SELECTION_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
