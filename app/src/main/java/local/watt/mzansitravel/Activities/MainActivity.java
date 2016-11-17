package local.watt.mzansitravel.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import local.watt.mzansitravel.Adapters.PlacesAdapter;
import local.watt.mzansitravel.Interfaces.OnPlaceSelected;
import local.watt.mzansitravel.Interfaces.OnTaskCompleted;
import local.watt.mzansitravel.Models.MzansiPlaces;
import local.watt.mzansitravel.R;
import local.watt.mzansitravel.Tasks.LoadPlaces;
import local.watt.mzansitravel.Utils.AlertDialogManager;
import local.watt.mzansitravel.Utils.ConnectivityChecker;
import local.watt.mzansitravel.Utils.MapUtils;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnTaskCompleted<List<MzansiPlaces>>, OnPlaceSelected, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * Points of interest listview stuff
     */
    @BindView(R.id.mzansiPlaces)
    ListView mPlaceListView;
    private Boolean isConnectedToInternet = false;
    private Boolean locationServicesEnabled = false;
    private ConnectivityChecker mConnectivityChecker;
    private AlertDialogManager mAlertDialogManager = new AlertDialogManager();
    private ArrayList<MzansiPlaces> mMzansiPlacesList;
    private PlacesAdapter mPlacesAdapter;

    /*
     * Navigation drawer stuff
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    //private Cursor mCursor;

    /*
     * Location stuff
     */
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double mLatitude;
    private double mLongitude;

    /* Google API and map Stuffs */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Authentication stuff
     */
    private boolean mLoggedIn;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ButterKnife.bind(this);

        Log.d(TAG, "onCreate()");

        /*
         * Check if we have an Internet connection and that Location Services are enabled
         */
        mConnectivityChecker = new ConnectivityChecker(MainActivity.this);
        isConnectedToInternet = mConnectivityChecker.isConnectedToInternet();
        locationServicesEnabled = mConnectivityChecker.locationServicesEnabled();

        if (isConnectedToInternet && locationServicesEnabled) {

            if (savedInstanceState != null) {
                mLoggedIn = savedInstanceState.getBoolean("LoggedIn");
            } else {
                mLoggedIn = false;
            }

            if (!mLoggedIn) {
                logUserIn();
            }

            /*
             * Check if we've logged in
             */
            mPref = getSharedPreferences(String.valueOf(R.string.authentication_shared_preference_key_text), 0);
            mLoggedIn = mPref.getBoolean(String.valueOf(R.string.authentication_shared_pref_editor_boolean_text), true);

            if (mLoggedIn) {

                /*
                 * Set up the navigation drawer
                 */
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                mDrawerLayout.setDrawerListener(toggle);
                toggle.syncState();

                mNavigationView.setNavigationItemSelectedListener(this);

                /*
                 * Create the Google API Client object
                 */
                try {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                 * Create the LocationRequest object
                 */
                try {
                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(100000)
                            .setFastestInterval(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                 * The fragment below provides an AutoComplete getPlaces tool for the user
                 */
                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                autocompleteFragment.setBoundsBias(MapUtils.getLatLngBounds(new LatLng(mLatitude, mLongitude)));
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about selected place
                        Log.d(TAG, "########## MzansiPlaces: " + place.getName());

                        Log.d(TAG, "Address: " + place.getAddress());
                        Log.d(TAG, "Number: " + place.getPhoneNumber());
                        if (!(place.getWebsiteUri() == null)) {
                            Log.d(TAG, "Website: " + place.getWebsiteUri());
                        }

                        Log.d(TAG, "Coordinates: " + place.getLatLng());
                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.d(TAG, "########## An error occurred: " + status);
                    }
                });

                /*
                 * Load places into the ListView
                 */
                mMzansiPlacesList = new ArrayList<>();
                mPlacesAdapter = new PlacesAdapter(getApplicationContext(), R.layout.places, mMzansiPlacesList);
                mPlaceListView.setAdapter(mPlacesAdapter);

                mPlaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        MzansiPlaces mzansiPlaces = mPlacesAdapter.getItem(position);
                        ((OnPlaceSelected) MainActivity.this).onPlaceSelected(mzansiPlaces);
                    }
                });
            }
        }
        else if (!isConnectedToInternet) {
            mAlertDialogManager.showAlertDialog(this, getString(R.string.no_internet_connectivity_text), getString(R.string.no_internet_connectivity_detail_text), false);
        }
        else if (!locationServicesEnabled) {
            mAlertDialogManager.showAlertDialog(this, getString(R.string.no_location_services_text), getString(R.string.no_location_services_detail_text), false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("LoggedIn", true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
     * Connect the GoogleApiClient when this activity starts
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Log.e(TAG, "mGoogleApiClient is null");
        }

    }

    /*
     * Invalidate the user's logged on status when the activity life cycle reaches its end
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");

        /*
         * Log the user out
         */
        logUserOut();

        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed()");
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*
     * Get the last known location
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged()");
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, "handleNewLocation()");
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

        mPlacesAdapter.clear();
        fetchPlaces(mLatitude, mLongitude);
    }

    private void logUserIn() {
        /*
         * Go to the Login activity
         */
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void logUserOut() {
        Log.d(TAG, "logUserOut()");

        if (mPref != null) {
            SharedPreferences.Editor editor = mPref.edit();
            editor.putBoolean(getString(R.string.authentication_shared_pref_editor_boolean_text), false);
            editor.commit();
        } else {
            Log.e(TAG, "mPref is null");
            mPref = getSharedPreferences(String.valueOf(R.string.authentication_shared_preference_key_text), 0);
            SharedPreferences.Editor editor = mPref.edit();
            editor.putBoolean(getString(R.string.authentication_shared_pref_editor_boolean_text), false);
            editor.commit();
        }
    }

    @Override
    public void onTaskCompleted(List<MzansiPlaces> result) {
        Log.d(TAG, "onTaskCompleted()");

        /*
         * Add items to the adapter
         */
        for (MzansiPlaces mzansiPlaces : result) {
            mPlacesAdapter.add(mzansiPlaces);
        }
    }

    @Override
    public void onError() {
        Log.d(TAG, "onError()");
    }

    private void fetchPlaces(double lat, double lng) {
        Log.d(TAG, "fetchPlaces()");
        mPlacesAdapter.clear();
        new LoadPlaces(this, lat, lng, this).execute(mPlacesAdapter);
    }

    @Override
    public void onPlaceSelected(MzansiPlaces mzansiPlaces) {
        Log.d(TAG, "onPlaceSelected()");
        Intent placeIntent = new Intent(this, PlaceDetailActivity.class);
        placeIntent.putExtra(getString(R.string.calling_activity_text), PlaceDetailActivity.MAIN_ACTIVITY);
        placeIntent.putExtra(getString(R.string.place_key_text), mzansiPlaces);
        startActivity(placeIntent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected()");
        // Handle navigation view item clicks here

        int id = item.getItemId();

        if (id == R.id.nav_hotels) {
            Intent i = new Intent(this, SavedPlacesActivity.class);
            i.putExtra(getString(R.string.mode_key_text), getString(R.string.hotel_place_type_text));
            startActivity(i);
        } else if (id == R.id.nav_restaurant) {
            Intent i = new Intent(this, SavedPlacesActivity.class);
            i.putExtra(getString(R.string.mode_key_text), getString(R.string.restaurant_place_type_text));
            startActivity(i);
        } else if (id == R.id.nav_places) {
            Intent i = new Intent(this, SavedPlacesActivity.class);
            i.putExtra(getString(R.string.mode_key_text), getString(R.string.point_of_interest_place_type_text));
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}