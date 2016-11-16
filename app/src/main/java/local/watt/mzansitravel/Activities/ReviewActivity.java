package local.watt.mzansitravel.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import local.watt.mzansitravel.Adapters.ReviewsAdapter;
import local.watt.mzansitravel.Models.Details;
import local.watt.mzansitravel.Models.Reviews;
import local.watt.mzansitravel.Parsers.ParsePlacesData;
import local.watt.mzansitravel.R;
import local.watt.mzansitravel.Utils.SearchPlaces;

public class ReviewActivity extends AppCompatActivity {
    private static final String TAG = ReviewActivity.class.getSimpleName();

    private static String mPlaceID;
    private ReviewsAdapter mReviewsAdapter;

    @BindView(R.id.placeReviews) ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlaceID = getIntent().getStringExtra(getString(R.string.place_id_text));
        getReviewsTask();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

        mReviewsAdapter.clear();
        mReviewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateUI(List<Reviews> reviewsList) {
        mReviewsAdapter = new ReviewsAdapter(this, R.layout.reviews, reviewsList);
        mListView.setAdapter(mReviewsAdapter);
    }

    private void getReviewsTask() {
        new ReviewsTask() {
            @Override
            protected void onPostExecute(List<Reviews> reviews) {
                updateUI(reviews);
            }
        }.execute(mPlaceID);
    }

    /*
     * AsyncTask for obtaining reviews
     */

    abstract class ReviewsTask extends AsyncTask<String, Void, List<Reviews>> {

        private String placeId;
        private String placeDetailsJSON;
        private List<Reviews> reviewsList = new ArrayList<Reviews>();

        public ReviewsTask() {
        }

        @Override
        protected List<Reviews> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            placeId = params[0];

            try {
                placeDetailsJSON = new SearchPlaces().getPlaceDetails(placeId);
                reviewsList = ParsePlacesData.getPlacesReviewsFromJSON(placeDetailsJSON);

                return reviewsList;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
