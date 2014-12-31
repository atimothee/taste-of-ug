package com.tasteofuganda.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tasteofuganda.app.provider.recipe.RecipeColumns;

/**
 * Created by Timo on 12/30/14.
 */
public class RecipeDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public RecipeDetailFragment(){
        setHasOptionsMenu(true);
    }

    private static final int DETAIL_LOADER = 1;
    private static final String RECIPE_SHARE_HASHTAG = "#TasteOfUgApp";
    private static final String TAG = RecipeDetailFragment.class.getSimpleName();
    private static final  String IMAGE_BASE_URI = "http://tasteofuganda.appspot.com/serve?blob-key=";
    private Long mId;
    private String mShareString;
    private Cursor mCursor;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mDirectionsView;
    private TextView mIngredientsView;
    private ImageView mImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            //mShareString = intnt.getLongExtra("id", 0);
            //mShareString = mCursor.getString(1);
        }
        View rootView = inflater.inflate(R.layout.detail_fragment, null);
        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_description);
        mIngredientsView = (TextView) rootView.findViewById(R.id.detail_ingredients);
        mDirectionsView = (TextView) rootView.findViewById(R.id.detail_directions);
        mImageView = (ImageView) rootView.findViewById(R.id.detail_image);


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putString();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle args = getArguments();
        if(args!=null && args.containsKey("id") && mId!=null){
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this );
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = {String.valueOf(args.getLong("id"))};
        return new CursorLoader(getActivity(), RecipeColumns.CONTENT_URI, RecipeColumns.FULL_PROJECTION, "_id = ?", selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            String name = data.getString(1);
            mTitleView.setText(name);
            String description = data.getString(2);
            mDescriptionView.setText(description);
            String ingredients = data.getString(3);
            ingredients = "Ingredients:\n"+ingredients;
            String directions = data.getString(4);
            directions = "Directions:\n"+directions;
            mDirectionsView.setText(directions);
            String imageKey = data.getString(6);
            Picasso.with(getActivity()).load(IMAGE_BASE_URI+imageKey).into(mImageView);
            mShareString = "I just checked out "+name+" recipe "+RECIPE_SHARE_HASHTAG;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            mId = savedInstanceState.getLong("id");
        }
        Bundle args = getArguments();
        if(args!=null && args.containsKey("id")){
            getLoaderManager().initLoader(DETAIL_LOADER, args, this);
        }
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareString+RECIPE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }else {
            Log.d(TAG, "Share action provider is null?");
        }

    }
}
