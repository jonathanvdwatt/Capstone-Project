package local.watt.mzansitravel.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import local.watt.mzansitravel.Interfaces.OnTaskCompleted;
import local.watt.mzansitravel.Models.Details;
import local.watt.mzansitravel.Models.MzansiPlacesDetails;
import local.watt.mzansitravel.Models.Reviews;
import local.watt.mzansitravel.Parsers.ParsePlacesData;
import local.watt.mzansitravel.Utils.SearchPlaces;

/**
 * Created by f4720431 on 2016/09/22.
 */
public class LoadPlacesDetails extends AsyncTask<String, Void, List<Details>> {
    private static final String TAG = LoadPlacesDetails.class.getSimpleName();

    private OnTaskCompleted<List<Details>> delegate;
    private String placeId;
    private String mPlaceDetailsJSON;

    public LoadPlacesDetails(OnTaskCompleted<List<Details>> delegate) {
        this.delegate = delegate;
    }

    /*
     * Get places details JSON
     */
    @Override
    protected List<Details> doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");

        if (params.length == 0) {
            return null;
        }

        placeId = params[0];
        Log.d(TAG, "placeId: " + placeId);

        try {
            mPlaceDetailsJSON = new SearchPlaces().getPlaceDetails(placeId);

            /*
             * Get place details- (Create details json array)
             */
            List<Details> mzansiPlacesDetailsList = ParsePlacesData.getPlacesDetailsFromJSON(mPlaceDetailsJSON);

            return mzansiPlacesDetailsList;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Details> result) {
        Log.d(TAG, "########## onPostExecute()");

        if (result == null) {
            this.delegate.onError();
            return;
        }
        this.delegate.onTaskCompleted(result);
    }
}
