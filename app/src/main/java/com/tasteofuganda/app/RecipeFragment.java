package com.tasteofuganda.app;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.tasteofuganda.app.provider.category.CategoryColumns;
import com.tasteofuganda.app.provider.recipe.RecipeColumns;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

/**
 * Created by Timo on 12/24/14.
 */
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int RECIPE_LOADER = 0;
    private static final String SAVED_STATE_SELECTION_KEY = "selection";
    private static final String ARGS_SAVED_STATE_SELECTED_ID_KEY = "selected_id";//for when fragment is called from notification
    private static final String ARGS_CATEGORY_ID_KEY = "category_id";
    private static final String ARGS_IS_SEARCH_KEY = "is_search";
    private static final String ARGS_QUERY_KEY = "query";
    private static final String TAG = RecipeFragment.class.getSimpleName();
    private SimpleCursorAdapter recipeAdapter;
    private final String[] COLUMNS = {RecipeColumns.RECIPE_NAME, RecipeColumns.DESCRIPTION, RecipeColumns.IMAGEURL, CategoryColumns.COLOR};
    private final int[] VIEW_IDS = {R.id.recipe_title, R.id.recipe_description, R.id.recipe_image, R.id.recipe_bg};
    private Integer mPosition;
    private ListView mListView;
    private Long mSelectedId;

    public interface Callback{
        public void onItemSelected(Long id, String color);
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
        recipeAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            /** Binds the Cursor column defined by the specified index to the specified view */
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(view.getId() == R.id.recipe_title) {
                    view.setTag(cursor.getLong(cursor.getColumnIndex(RecipeColumns._ID)));
                    ((TextView)view).setText(cursor.getString(cursor.getColumnIndex(RecipeColumns.RECIPE_NAME)));
                    return true;
                }
                else if (view.getId() == R.id.recipe_image) {
                    Picasso.with(getActivity()).load(cursor.getString(columnIndex)).into((ImageView) view);
                    return true;

                }
                else if (view.getId() == R.id.recipe_bg) {
                    view.setBackgroundColor(Color.parseColor(cursor.getString(cursor.getColumnIndex(CategoryColumns.COLOR))));
                    return true;
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
                    ((Callback) getActivity()).onItemSelected(cursor.getLong(cursor.getColumnIndex(RecipeColumns._ID)), cursor.getString(cursor.getColumnIndexOrThrow(CategoryColumns.COLOR)));
                }
            }
        });
        if(savedInstanceState!=null && savedInstanceState.containsKey(SAVED_STATE_SELECTION_KEY)){
            mPosition = savedInstanceState.getInt(SAVED_STATE_SELECTION_KEY);
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey(ARGS_SAVED_STATE_SELECTED_ID_KEY)){
            mSelectedId = savedInstanceState.getLong(ARGS_SAVED_STATE_SELECTED_ID_KEY);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(args!=null && args.containsKey(ARGS_IS_SEARCH_KEY) && args.containsKey(ARGS_QUERY_KEY)){
            if(args.getBoolean(ARGS_IS_SEARCH_KEY)){
                String[] selectionArgs  = new String[]{"%"+args.getString(ARGS_QUERY_KEY)+"%",
                        "%"+args.getString(ARGS_QUERY_KEY)+"%",
                        "%"+args.getString(ARGS_QUERY_KEY)+"%"
                };
                return new CursorLoader(getActivity(), RecipeColumns.CONTENT_URI,
                        ArrayUtils.addAll(RecipeColumns.FULL_PROJECTION, new String[]{CategoryColumns.COLOR}),
                        RecipeColumns.DESCRIPTION+" LIKE ? OR "+RecipeColumns.DIRECTIONS+" LIKE ?"+" OR "+RecipeColumns.RECIPE_NAME+" LIKE ?",
                        selectionArgs,
                        null
                        );
            }
        }else{
            if(args != null && args.containsKey(ARGS_CATEGORY_ID_KEY) && args.getLong(ARGS_CATEGORY_ID_KEY)==1){
                return new CursorLoader(
                        getActivity(),
                        RecipeColumns.CONTENT_URI,
                        ArrayUtils.addAll(RecipeColumns.FULL_PROJECTION, new String[]{CategoryColumns.COLOR}),
                        null,
                        null,
                        null
                );
            }else if(args != null && args.containsKey(ARGS_CATEGORY_ID_KEY)){
                String[] selectionArgs  = new String[]{String.valueOf(args.getLong(ARGS_CATEGORY_ID_KEY, 1))};
                return new CursorLoader(
                        getActivity(),
                        RecipeColumns.CONTENT_URI,
                        ArrayUtils.addAll(RecipeColumns.FULL_PROJECTION, new String[]{CategoryColumns.COLOR}),
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
                        ArrayUtils.addAll(RecipeColumns.FULL_PROJECTION, new String[]{CategoryColumns.COLOR}),
                        null,
                        null,
                        null
                );
            }
        }


    return new CursorLoader(getActivity(), RecipeColumns.CONTENT_URI, null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recipeAdapter.swapCursor(data);

        if(mSelectedId!=null){
            Log.d(TAG, "selected id is "+mSelectedId);
            ArrayList<View> views = new ArrayList<>();
            mListView.reclaimViews(views);
            Long tagValue;
            int count = 0;
            for(View v: views){
                tagValue = (Long)v.findViewById(R.id.recipe_title).getTag();
                Log.d(TAG, "views looped, tag "+tagValue+" "+v.findViewById(R.id.recipe_title).getTag().getClass().getName());
                if(mSelectedId.longValue() == tagValue.longValue()){
                    mPosition = count;
                    Log.d(TAG, "position for view is "+mPosition);
                    //mListView.setSelection(mPosition);
                    final int WHAT = 1;
                    Handler handler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if(msg.what == WHAT) {
                                                    mListView.performItemClick(
                            mListView.getAdapter().getView(mPosition, null, null),
                            mPosition,
                            mListView.getAdapter().getItemId(mPosition));
                            };
                        }
                    };
                    handler.sendEmptyMessage(WHAT);

                    Log.d(TAG, "list was clicked at position "+mPosition);
                    break;
                }
                count++;
            }
        }
        if(mPosition!=null) {
            mListView.setSelection(mPosition);
            Log.d(TAG, "selected position is "+mPosition);
        }
        Log.d(TAG, "Recipe fragment cursor finished loading");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void reloadRecipeFragmentFromArgs(Bundle args) {
        getLoaderManager().restartLoader(RECIPE_LOADER, args, this);
        if(args.containsKey(ARGS_SAVED_STATE_SELECTED_ID_KEY)) {
            mSelectedId = args.getLong(ARGS_SAVED_STATE_SELECTED_ID_KEY);
            Log.d(TAG, "mselectedId has been set with value "+mSelectedId+" on frag reload");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition !=null && mPosition!= ListView.INVALID_POSITION){
            outState.putInt(SAVED_STATE_SELECTION_KEY, mPosition);
        }
        if(mSelectedId != null){
            outState.putLong(ARGS_SAVED_STATE_SELECTED_ID_KEY, mSelectedId);
        }
        super.onSaveInstanceState(outState);
    }
}
