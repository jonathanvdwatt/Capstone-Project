package local.watt.mzansitravel.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import local.watt.mzansitravel.Interfaces.OnTaskCompleted;
import local.watt.mzansitravel.Models.MzansiPlaces;
import local.watt.mzansitravel.Parsers.ParsePlacesData;
import local.watt.mzansitravel.Utils.SearchPlaces;

/**
 * Created by f4720431 on 2016/09/18.
 */
public class LoadPlaces extends AsyncTask<ArrayAdapter<MzansiPlaces>, Double, List<MzansiPlaces>> {
    private static final String TAG = LoadPlaces.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private double mLatitude;
    private double mLongitude;
    private Context mContext;
    private OnTaskCompleted<List<MzansiPlaces>> delegate;
    private String mPlaceListJSON;

    private String PLACE_TYPES = "airport|amusement_park|aquarium|art_gallery|bakery|bar|bowling_alley|cafe|campground|casino|clothing_store|department_store|gym|hindu_temple|zoo|transit_station|train_station|synagogue|store|stadium|spa|shopping_mall|shoe_store|restaurant|park|night_club|museum|movie_theater|mosque|meal_takeaway|lodging|liqour_store|library|hospital";

    public LoadPlaces(OnTaskCompleted<List<MzansiPlaces>> delegate, double latitude, double longitude, Context context) {
        this.delegate = delegate;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mContext = context;
    }

    /*
     * Show ProgressDialog before starting
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Mzansi Places..."));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /*
     * Get places JSON
     */
    @Override
    protected List<MzansiPlaces> doInBackground(ArrayAdapter... params) {

        double radius = 10000;

        if (params.length == 0) {
            return null;
        }

        try {
            mPlaceListJSON = new SearchPlaces().getPlaces(mLatitude, mLongitude, radius, PLACE_TYPES);

            List<MzansiPlaces> mzansiPlaces = ParsePlacesData.getPlacesListFromJSON(mPlaceListJSON);

            return mzansiPlaces;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MzansiPlaces> result) {
        Log.d(TAG, "########## onPostExecute()");

        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            this.delegate.onError();
            return;
        }
        this.delegate.onTaskCompleted(result);
    }
}