package local.watt.mzansitravel.Activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import local.watt.mzansitravel.Adapters.ListViewAdapter;
import local.watt.mzansitravel.Data.DataProviderContract;
import local.watt.mzansitravel.R;

public class SavedPlacesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SavedPlacesActivity.class.getSimpleName();

    @BindView(R.id.name) TextView mName;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.noData) TextView mNodata;
    @BindView(R.id.savedPlacesListView) ListView mListView;

    private String mMode;

    private Cursor mCursor;
    private ListViewAdapter mAdapter;

    /*
     * Loader stuff
     */
    private String[] PLACES_SUMMARY_PROJECTION = {
            DataProviderContract.ROW_ID,
            DataProviderContract.PLACE_ID_COLUMN,
            DataProviderContract.PLACE_NAME_COLUMN,
            DataProviderContract.PLACE_RATING_COLUMN,
            DataProviderContract.PLACE_PHONE_NUMBER_COLUMN,
            DataProviderContract.PLACE_WEBSITE_COLUMN,
            DataProviderContract.PLACE_ADDRESS_COLUMN,
            DataProviderContract.PLACE_VICINITY_COLUMN,
            DataProviderContract.PLACE_COORDINATES_COLUMN,
            DataProviderContract.PLACE_TYPE_COLUMN
    };

    private String[] mSelectionArgs;
    private String mSelectionArgumentsHotels[] = {"hotel"};
    private String mSelectionArgumentsFood[] = {"restaurant"};
    private String mSelectionArgumentsPlaces[] = {"point_of_interest"};

    // Identifies a particular Loader being used in this component
    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_places);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNodata.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mMode = getIntent().getStringExtra(getString(R.string.mode_key_text));

        mAdapter = new ListViewAdapter(this, mCursor);
        mListView.setAdapter(mAdapter);

        final Intent intent = new Intent(this, PlaceDetailActivity.class);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) mAdapter.getItem(position);

                intent.putExtra(getString(R.string.calling_activity_text), PlaceDetailActivity.SAVED_PLACES_ACTIVITY);
                intent.putExtra(getString(R.string.place_id_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_ID_COLUMN)));
                intent.putExtra(getString(R.string.place_name_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_NAME_COLUMN)));
                intent.putExtra(getString(R.string.place_rating_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_RATING_COLUMN)));
                intent.putExtra(getString(R.string.place_number_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_PHONE_NUMBER_COLUMN)));
                intent.putExtra(getString(R.string.place_website_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_WEBSITE_COLUMN)));
                intent.putExtra(getString(R.string.place_address_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_ADDRESS_COLUMN)));
                intent.putExtra(getString(R.string.place_vicinity_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_VICINITY_COLUMN)));
                intent.putExtra(getString(R.string.place_coordinates_text), cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_COORDINATES_COLUMN)));

                startActivity(intent);
            }
        });

        Bundle selection = new Bundle();

        switch (mMode) {
            case "hotel":
                Log.d(TAG, "Loading hotels");
                mName.setText(R.string.saved_hotels_text);

                selection.putString(getString(R.string.saved_places_mode_bundle_key_text), getString(R.string.hotel_place_type_text));
                loadSavedPlaces(selection);

                break;

            case "restaurant":
                Log.d(TAG, "Loading restaurants");
                mName.setText(R.string.saved_restaurants_text);

                selection.putString(getString(R.string.saved_places_mode_bundle_key_text), getString(R.string.restaurant_place_type_text));
                loadSavedPlaces(selection);

                break;

            case "point_of_interest":
                Log.d(TAG, "Loading points of interest");
                mName.setText(R.string.interesting_places_text);

                selection.putString(getString(R.string.saved_places_mode_bundle_key_text), getString(R.string.point_of_interest_place_type_text));
                loadSavedPlaces(selection);
        }
    }

    private void loadSavedPlaces(Bundle bundle) {
        // Prepare the loader.
        getLoaderManager().initLoader(URL_LOADER, bundle, this);
    }

    // Start the query
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader()");

        String bundle = args.getString(getString(R.string.saved_places_mode_bundle_key_text));

        switch (bundle) {
            case "hotel":
                mSelectionArgs = mSelectionArgumentsHotels;
                break;
            case "restaurant":
                mSelectionArgs = mSelectionArgumentsFood;
                break;
            case "point_of_interest":
                mSelectionArgs = mSelectionArgumentsPlaces;
                break;
        }

        return new CursorLoader(
                this,
                DataProviderContract.PLACE_TABLE_CONTENTURI,
                PLACES_SUMMARY_PROJECTION,
                DataProviderContract.PLACE_TYPE_COLUMN + "=?",
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished()");

        if (data.getCount() > 0) {

            for (int i=0; i<data.getCount(); i++) {

                if (data.moveToNext()) {
                    Log.d(TAG, "Place Type: " + data.getString(data.getColumnIndexOrThrow(DataProviderContract.PLACE_TYPE_COLUMN)));
                }
            }
        }

        if (data != null && data.getCount() > 0) {
            mProgressBar.setVisibility(View.GONE);
            mAdapter.swapCursor(data);
        }
        else {
            mProgressBar.setVisibility(View.GONE);
            mNodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        mAdapter.swapCursor(null);
    }
}
