package local.watt.mzansitravel.Activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import local.watt.mzansitravel.Data.DataProviderContract;
import local.watt.mzansitravel.Interfaces.OnReviewSelected;
import local.watt.mzansitravel.Interfaces.OnTaskCompleted;
import local.watt.mzansitravel.Models.Details;
import local.watt.mzansitravel.Models.MzansiPlaces;
import local.watt.mzansitravel.R;
import local.watt.mzansitravel.Tasks.LoadPlacesDetails;

public class PlaceDetailActivity extends AppCompatActivity implements OnTaskCompleted<List<Details>>,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnReviewSelected,
        LoaderManager.LoaderCallbacks<Cursor>,
        PopupMenu.OnMenuItemClickListener {
    private static final String TAG = PlaceDetailActivity.class.getSimpleName();

    private static String mPlaceName;
    private static Double mRating;
    private static String mPlaceID;
    private static String mPlacePhoneNumber;
    private static String mPlaceWebURL;
    private static String mPlaceAddress;
    private static String mPlaceVicinity;
    private static Double mLatitude;
    private static Double mLongtitude;
    private static String mPlaceType;

    @BindView(R.id.poster_full) ImageView mPlacePhoto;
    @BindView(R.id.location_name) TextView mLocationName;
    @BindView(R.id.ratingOverall) RatingBar mPlaceRating;
    @BindView(R.id.address) TextView mAddress;
    @BindView(R.id.call) LinearLayout mCallPlace;
    @BindView(R.id.website) LinearLayout mVisitWebsite;
    @BindView(R.id.reviews) LinearLayout mReviews;
    @BindView(R.id.save) LinearLayout mSavePlace;

    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;

    private GoogleApiClient mGoogleApiClient;

    private Uri mUri;
    private Cursor mCursor;

    public static final int MAIN_ACTIVITY = 1001;
    public static final int SAVED_PLACES_ACTIVITY = 1002;
    private static int mCallingActivity;

    private MzansiPlaces mMzansiPlaces;

    String[] PLACES_SUMMARY_PROJECTION = {
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

    private String mDBPlaceID;

    private static boolean IS_PLACE_IN_DATABASE = false;
    private String mSnackBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Set info depending on how we're called
         */
        mMzansiPlaces = getIntent().getParcelableExtra(getString(R.string.place_key_text));
        mCallingActivity = getIntent().getIntExtra(getString(R.string.calling_activity_text), 0);

        switch (mCallingActivity) {
            case MAIN_ACTIVITY:
                Log.d(TAG, "Called by MainActivity");

                mPlaceID = mMzansiPlaces.getId();
                mPlaceName = mMzansiPlaces.getName();
                mRating = mMzansiPlaces.getRating();
                mPlaceVicinity = mMzansiPlaces.getVicinity();

                if (savedInstanceState == null) {
                    startLoader(); // Checks if place is already saved.
                    fetchPlaceDetails(mPlaceID);
                    updatePhotoImageView();
                    updateUI();
                } else {

                    mPlaceID = savedInstanceState.getString(getString(R.string.place_id_text));
                    mPlaceName = savedInstanceState.getString(getString(R.string.place_name_text));
                    mRating = savedInstanceState.getDouble(getString(R.string.place_rating_text));
                    mPlaceVicinity = savedInstanceState.getString(getString(R.string.place_vicinity_text));

                    Log.d(TAG, "savedInstanceState.getBoolean: " + savedInstanceState.getBoolean("SavePlaceDisabled"));

                    if(savedInstanceState.getBoolean(getString(R.string.save_place_disabled_text))) {
                       mSavePlace.setVisibility(View.GONE);
                    }

                    fetchPlaceDetails(mPlaceID);
                    updatePhotoImageView();
                    updateUI();
                }

                break;
            case SAVED_PLACES_ACTIVITY:
                Log.d(TAG, "Called by SavedPlacesActivity");

                mPlaceID = getIntent().getStringExtra(getString(R.string.place_id_text));
                mPlaceName = getIntent().getStringExtra(getString(R.string.place_name_text));
                mRating = Double.valueOf(getIntent().getStringExtra(getString(R.string.place_rating_text)));
                mPlacePhoneNumber = getIntent().getStringExtra(getString(R.string.place_number_text));
                mPlaceWebURL = getIntent().getStringExtra(getString(R.string.place_website_text));
                mPlaceAddress = getIntent().getStringExtra(getString(R.string.place_address_text));
                mPlaceVicinity = getIntent().getStringExtra(getString(R.string.place_vicinity_text));

                String coordinates = getIntent().getStringExtra(getString(R.string.place_coordinates_text));
                String[] coordinatesArr = coordinates.split(",");

                mLatitude = Double.valueOf(coordinatesArr[0]);
                mLongtitude = Double.valueOf(coordinatesArr[1]);

                updatePhotoImageView();
                updateUI();

                mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mSupportMapFragment.getMapAsync(this);
        }

        mCallPlace.setOnClickListener(new View.OnClickListener() {
            Intent call = new Intent(Intent.ACTION_DIAL);

            @Override
            public void onClick(View v) {
                if (mPlacePhoneNumber != null) {
                    call.setData(Uri.parse("tel:" + mPlacePhoneNumber));
                    startActivity(call);
                } else {
                    Snackbar.make(
                            v,
                            R.string.no_phn_number_for_place_text,
                            Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snackbar_action_text), null)
                            .show();
                }
            }
        });

        mVisitWebsite.setOnClickListener(new View.OnClickListener() {
            Intent browse = new Intent(Intent.ACTION_VIEW);

            @Override
            public void onClick(View v) {
                if (mPlaceWebURL != null) {
                    browse.setData(Uri.parse(mPlaceWebURL));
                    startActivity(browse);
                } else {
                    Snackbar.make(
                            v,
                            R.string.no_website_for_place_text,
                            Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snackbar_action_text), null)
                            .show();
                }
            }
        });

        mReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReviewSelected(mPlaceID);
            }
        });

        mSavePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCallingActivity) {
                    case MAIN_ACTIVITY:

                        Log.d(TAG, "Saving place: " + mPlaceName);

                        if (!IS_PLACE_IN_DATABASE) {
                            showPopup(v);
                        }

                        break;
                    case SAVED_PLACES_ACTIVITY:
                        Snackbar.make(
                                v,
                                R.string.place_has_already_been_saved_text,
                                Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.snackbar_action_text), null)
                                .show();
                        break;
                }
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.save_actions);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_hotels:
                mPlaceType = getString(R.string.hotel_place_type_text);
                addPlaceToDB(mPlaceType);
                return true;
            case R.id.action_restaurant:
                mPlaceType = getString(R.string.restaurant_place_type_text);
                addPlaceToDB(mPlaceType);
                return true;
            case R.id.action_places:
                mPlaceType = getString(R.string.point_of_interest_place_type_text);
                addPlaceToDB(mPlaceType);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState()");

        outState.putBoolean(getString(R.string.save_place_disabled_text), true);
        outState.putString(getString(R.string.place_id_text), mPlaceID);
        outState.putString(getString(R.string.place_name_text), mPlaceName);
        outState.putDouble(getString(R.string.place_rating_text), mRating);
        outState.putString(getString(R.string.place_vicinity_text), mPlaceVicinity);
    }

    private void updatePhotoImageView() {
        /*
         * Update Place Photo ImageView
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        getPhotosTask();
    }

    private void updateUI() {

        mLocationName.setText(mPlaceName);

        switch (mCallingActivity) {
            case SAVED_PLACES_ACTIVITY:
                mAddress.setText(mPlaceAddress);
                mSavePlace.setVisibility(View.GONE);
                break;
        }

        if (mRating == 0.0) {
            mPlaceRating.setVisibility(View.GONE);
        } else {
            mPlaceRating.setRating(Float.parseFloat(String.valueOf(mRating)));
        }
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        // Reset IS_PLACE_IN_DATABASE boolean
        IS_PLACE_IN_DATABASE = false;

        super.onStop();
    }

    @Override
    protected void onResume() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        super.onResume();
    }

    private void fetchPlaceDetails(String placeId) {
        new LoadPlacesDetails(this).execute(placeId);
    }

    private void getPhotosTask() {
        new PhotoTask() {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Display photo that has been loaded
                    mPlacePhoto.setImageBitmap(attributedPhoto.bitmap);

                    // Display the attribution as HTML content if set
                    if (attributedPhoto.attribution == null) {
                        // Hide the Attribution TextView
                        Log.d(TAG, "Attribution is null");
                    } else {
                        // Show the attribution TextView and set its text
                        Log.d(TAG, "Attribution is not null");
                    }
                }
            }
        }.execute(mPlaceID);
    }

    @Override
    public void onTaskCompleted(List<Details> result) {

        for (int i = 0; i < result.size(); i++) {
            mPlaceName = result.get(i).getName();
            mPlacePhoneNumber = result.get(i).getInternational_phone_number();
            mPlaceWebURL = result.get(i).getWebsite();
            mPlaceAddress = result.get(i).getFormatted_address();
            mLatitude = result.get(i).getLatitude();
            mLongtitude = result.get(i).getLongitude();
            mPlaceType = result.get(i).getType();
        }

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        mAddress.setText(mPlaceAddress);
    }

    @Override
    public void onError() {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mGoogleMap = googleMap;

        this.mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        this.mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

        LatLng latLng = new LatLng(mLatitude, mLongtitude);

        mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));

        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        this.mGoogleMap.animateCamera(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onReviewSelected(String placeId) {
        Intent review = new Intent(getApplicationContext(), ReviewActivity.class);
        review.putExtra(getString(R.string.place_id_text), placeId);
        startActivity(review);
    }

    private void startLoader() {
        Log.d(TAG, "startLoader()");

        // Prepare a Loader
        //Bundle bundle = new Bundle();
        //bundle.putString(getString(R.string.place_id_text), mPlaceID);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(TAG, "onCreateLoader()");

        String[] selectionArgs = {mPlaceID};

        return new CursorLoader(
                this,
                DataProviderContract.PLACE_TABLE_CONTENTURI,
                PLACES_SUMMARY_PROJECTION,
                DataProviderContract.PLACE_ID_COLUMN + "=?",
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished()");

        Log.d(TAG, "COUNT: " + data.getCount());

        if (data.getCount() > 0) { // Not a new database or database not empty

            for (int i = 0; i < data.getCount(); i++) {

                if(data.moveToNext()) {
                    mDBPlaceID = data.getString(data.getColumnIndexOrThrow(DataProviderContract.PLACE_ID_COLUMN));
                }

                if (mPlaceID.equals(mDBPlaceID)) {
                    Log.d(TAG, "A match has been found: " + mPlaceID + " == " + mDBPlaceID);
                    IS_PLACE_IN_DATABASE = true;
                    Log.d(TAG, "Place is in database: " + IS_PLACE_IN_DATABASE);
                    mSavePlace.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "mPlaceID: " + mPlaceID + " !=> " + mDBPlaceID);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
    }

    private void addPlaceToDB(String placeType) {

        Log.d(TAG, "addPlaceToDB()");

        ContentValues values = new ContentValues();

        values.put(DataProviderContract.PLACE_ID_COLUMN, mPlaceID);
        values.put(DataProviderContract.PLACE_NAME_COLUMN, mPlaceName);
        values.put(DataProviderContract.PLACE_RATING_COLUMN, mRating);
        values.put(DataProviderContract.PLACE_PHONE_NUMBER_COLUMN, mPlacePhoneNumber);
        values.put(DataProviderContract.PLACE_WEBSITE_COLUMN, mPlaceWebURL);
        values.put(DataProviderContract.PLACE_ADDRESS_COLUMN, mPlaceAddress);
        values.put(DataProviderContract.PLACE_VICINITY_COLUMN, mPlaceVicinity);
        values.put(DataProviderContract.PLACE_COORDINATES_COLUMN, mLatitude + "," + mLongtitude);
        values.put(DataProviderContract.PLACE_TYPE_COLUMN, placeType);

        mUri = getContentResolver().insert(DataProviderContract.PLACE_TABLE_CONTENTURI, values);

        switch (placeType) {
            case "hotel":
                mSnackBarText = getString(R.string.place_saved_to_hotels_text);
                break;
            case "restaurant":
                mSnackBarText = getString(R.string.place_saved_to_restaurants_text);
                break;
            case "point_of_interest":
                mSnackBarText = getString(R.string.place_saved_to_interesting_places_text);
        }

        Snackbar.make(
                findViewById(R.id.activityPlaceDetail),
                mSnackBarText,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_text, null)
                .show();

        mSavePlace.setVisibility(View.GONE);
    }

    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        public PhotoTask() {
        }

        @Override
        protected AttributedPhoto doInBackground(String... params) {

            if (params.length != 1) {
                return null;
            }

            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();

                    // Load a scaled bitmap for this photo
                    Bitmap image = photo.getPhoto(mGoogleApiClient).await().getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        class AttributedPhoto {
            public final CharSequence attribution;
            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }
}
