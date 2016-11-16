package local.watt.mzansitravel.Parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import local.watt.mzansitravel.Models.Details;
import local.watt.mzansitravel.Models.MzansiPlaces;
import local.watt.mzansitravel.Models.MzansiPlacesDetails;
import local.watt.mzansitravel.Models.Reviews;

/**
 * Created by f4720431 on 2016/09/19.
 */
public class ParsePlacesData {
    public static final String TAG = ParsePlacesData.class.getSimpleName();

    public static List<MzansiPlaces> getPlacesListFromJSON(String placesJsonStr) throws JSONException {
        Log.d(TAG, "########## getPlacesListFromJSON()");

        final String results = "results";
        final String id = "place_id";
        final String name = "name";
        final String reference = "reference";
        final String icon = "icon";
        final String vicinity = "vicinity";
        final String rating = "rating";

        try {
            JSONObject placesJson = new JSONObject(placesJsonStr);
            JSONArray placesArray = placesJson.getJSONArray(results);

            List<MzansiPlaces> mzansiPlaces = new ArrayList<MzansiPlaces>();

            for (int i = 0; i < placesArray.length(); i++) {
                JSONObject googlePlaces = placesArray.getJSONObject(i);
                MzansiPlaces mzansiPlace = new MzansiPlaces();

                mzansiPlace.setId(googlePlaces.getString(id));
                mzansiPlace.setName(googlePlaces.getString(name));
                mzansiPlace.setReference(googlePlaces.getString(reference));
                mzansiPlace.setIcon(googlePlaces.getString(icon));
                mzansiPlace.setVicinity(googlePlaces.getString(vicinity));

                if (googlePlaces.has(rating)) {
                    mzansiPlace.setRating(googlePlaces.getDouble(rating));
                }

                mzansiPlaces.add(mzansiPlace);
            }
            return mzansiPlaces;
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //public static List<MzansiPlacesDetails> getPlacesDetailsFromJSON(String placesJsonStr) throws JSONException {
    public static List<Details> getPlacesDetailsFromJSON(String placesJsonStr) throws JSONException {
        Log.d(TAG, "########## getPlacesDetailsFromJSON()");

        final String result = "result";
        final String formatted_address = "formatted_address";
        final String international_phone_number = "international_phone_number";
        final String website = "website";
        final String url = "url";

        try {
            JSONObject placeDetailsJson = new JSONObject(placesJsonStr);
            JSONObject placeResult = placeDetailsJson.getJSONObject(result);

            JSONObject geometry = placeResult.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            JSONArray typesArray = placeResult.getJSONArray("types");


            /*
             * Add data to the Array
             */
            Details mzansiPlacesDetails = new Details();
            List<Details> mzansiPlacesDetailsList = new ArrayList<Details>();

            mzansiPlacesDetails.setName(placeResult.getString("name"));
            mzansiPlacesDetails.setFormatted_address(placeResult.getString(formatted_address));
            mzansiPlacesDetails.setInternational_phone_number(placeResult.getString(international_phone_number));
            mzansiPlacesDetails.setWebsite(placeResult.getString(website));
            mzansiPlacesDetails.setUrl(placeResult.getString(url));
            mzansiPlacesDetails.setLatitude(location.getDouble("lat"));
            mzansiPlacesDetails.setLongitude(location.getDouble("lng"));
            mzansiPlacesDetails.setOverallRating(placeResult.getDouble("rating"));

            if (typesArray.toString().contains("lodging")) {
                mzansiPlacesDetails.setType("hotel");
            }
            else if (typesArray.toString().contains("restaurant")) {
                mzansiPlacesDetails.setType("restaurant");
            }
            else if (typesArray.toString().contains("point_of_interest")) {
                mzansiPlacesDetails.setType("point_of_interest");
            }

            mzansiPlacesDetailsList.add(mzansiPlacesDetails);

            return mzansiPlacesDetailsList;
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Reviews> getPlacesReviewsFromJSON(String placesJsonStr) throws JSONException {
        Log.d(TAG, "########## getPlacesReviewsFromJSON()");

        final String result = "result";

        try {
            JSONObject placeDetailsJson = new JSONObject(placesJsonStr);
            JSONObject placeResult = placeDetailsJson.getJSONObject(result);

            /*
             * Reviews
             */
            JSONArray reviewsArray = placeResult.getJSONArray("reviews");
            List<Reviews> reviews = new ArrayList<Reviews>();

            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject review = reviewsArray.getJSONObject(i);
                Reviews reviewModel = new Reviews();

                reviewModel.setAuthor_name(review.getString("author_name"));
                //reviewModel.setAuthor_url(review.getString("author_url"));
                //reviewModel.setLang(review.getString("language"));

                if (review.has("profile_photo_url")) {
                    reviewModel.setProfile_photo_url(review.getString("profile_photo_url"));
                }

                reviewModel.setRating(review.getInt("rating"));
                reviewModel.setReview_text(review.getString("text"));

                reviews.add(reviewModel);
            }

            return reviews;

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Details> parsePlacesJSON(String result) throws JSONException {
        Log.d(TAG, "ParsePlacesData.parsePlacesJSON()");

        final String details = "details";
        final String reviews = "reviews";

        Details placeDetails = new Details();

        List<Details> results = new ArrayList<Details>();

        JSONObject root = new JSONObject(result);
        JSONArray detailsArray = root.getJSONArray(details);
        JSONArray reviewsArray = root.getJSONArray(reviews);

        for (int i=0; i<detailsArray.length(); i++) {

            JSONObject x = detailsArray.getJSONObject(i);

            Log.d(TAG, "detailsArray contents: " + detailsArray.getJSONObject(i));

            placeDetails.setFormatted_address(x.getString("formatted_address"));
            placeDetails.setInternational_phone_number(x.getString("international_phone_number"));
            placeDetails.setWebsite(x.getString("website"));
            placeDetails.setUrl(x.getString("url"));
            placeDetails.setLatitude(x.getDouble("lat"));
            placeDetails.setLongitude(x.getDouble("lng"));

        }
        results.add(placeDetails);

        for (int i=0; i<reviewsArray.length(); i++) {

            JSONObject x = reviewsArray.getJSONObject(i);

            Details placeReviews = new Details();

            placeReviews.setAuthor_name(x.getString("author_name"));
            placeReviews.setAuthor_url(x.getString("author_url"));
            placeReviews.setLang(x.getString("language"));
            placeReviews.setProfile_photo_url(x.getString("profile_photo_url"));
            placeReviews.setRating(Double.valueOf(x.getString("rating")));
            placeReviews.setReview_text(x.getString("text"));

            results.add(placeReviews);
        }

        /*
         * Debug what's in the array
         */
        for (int i=0; i<results.size(); i++) {
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Address: " + results.get(i).getFormatted_address());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Phn nr: " + results.get(i).getInternational_phone_number());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Website: " + results.get(i).getWebsite());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Lat: " + results.get(i).getLatitude());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Long: " + results.get(i).getLongitude());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Author: " + results.get(i).getAuthor_name());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Author URL: " + results.get(i).getAuthor_url());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Author profile URL: " + results.get(i).getProfile_photo_url());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Rating: " + results.get(i).getRating());
            Log.d(TAG, ParsePlacesData.class.getSimpleName() + ".parsePlacesJSON() Review text: " + results.get(i).getReview_text());
        }

        return results;
    }
}
