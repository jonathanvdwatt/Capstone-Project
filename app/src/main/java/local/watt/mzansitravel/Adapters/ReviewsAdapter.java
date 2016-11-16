package local.watt.mzansitravel.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import local.watt.mzansitravel.Models.MzansiPlacesDetails;
import local.watt.mzansitravel.Models.Reviews;
import local.watt.mzansitravel.R;

/**
 * Created by f4720431 on 2016/10/05.
 */
public class ReviewsAdapter extends ArrayAdapter<Reviews> {
    private static final String TAG = ReviewsAdapter.class.getSimpleName();

    private Context mContext;
    private int layoutResourceId;
    private List<Reviews> mReviews = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    private ImageView mImageView;
    private TextView mReviewerName;
    private TextView mReviewText;
    private RatingBar mRatingBar;

    public ReviewsAdapter(Context context, int layoutResourceId, List<Reviews> reviews) {
        super(context, layoutResourceId, reviews);

        Log.d(TAG, "ReviewsAdapter()");
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.mReviews = reviews;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mReviews.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Reviews getItem(int position) {
        return mReviews.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Reviews reviews = mReviews.get(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResourceId, parent, false);
            convertView.setTag(R.id.textView, convertView.findViewById(R.id.textView));
        }

        Log.d(TAG, "Photo URL: " + reviews.getProfile_photo_url());

        mImageView = (ImageView) convertView.findViewById(R.id.reviewerPhoto);
        Picasso.with(mContext)
                .load("http:" + reviews.getProfile_photo_url())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .fit()
                .into(mImageView);

        mReviewerName = (TextView) convertView.findViewById(R.id.reviewerName);
        mReviewerName.setText(reviews.getAuthor_name());

        mReviewText = (TextView) convertView.findViewById(R.id.reviewText);
        mReviewText.setText(reviews.getReview_text());

        mRatingBar = (RatingBar) convertView.findViewById(R.id.rating);
        mRatingBar.setRating(reviews.getRating());

        return convertView;
    }
}
