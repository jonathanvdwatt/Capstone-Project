package local.watt.mzansitravel.Utils;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import local.watt.mzansitravel.Models.MzansiPlaces;
import local.watt.mzansitravel.Models.MzansiPlacesDetails;
import local.watt.mzansitravel.Models.Reviews;

/**
 * Created by f4720431 on 2016/09/14.
 */
public class SearchPlaces {
    private static final String TAG = SearchPlaces.class.getSimpleName();

    private static final HttpTransport mHttpTransport = new NetHttpTransport();
    private static final String mApiKey = "AIzaSyCSz3cmm4a8PIpQYsK8diXKLDgRmvTHzV8";
    private static final String mPlacesSearchUri = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String mNearbySearchUri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String mPlacesDetailsUri = "https://maps.googleapis.com/maps/api/place/details/json?";
    private double mLatitude;
    private double mLongitude;
    private double mRadius;
    private String mTypes;
    private String mPlaceId;

    public String getPlaces(double latitude, double longitude, double radius, String placeTypes) throws Exception {
        Log.d(TAG, "########## SearchPlaces.getPlaces()");
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mTypes = placeTypes;

        try {
            HttpRequestFactory httpRequestFactory = createRequestFactory(mHttpTransport);
            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(mNearbySearchUri));

            request.getUrl().put("key", mApiKey);
            request.getUrl().put("location", mLatitude + "," + mLongitude);
            request.getUrl().put("radius", mRadius);
            request.getUrl().put("type", "restaurant");

            String data = request.execute().parseAsString();

            return data;
        } catch (HttpResponseException e) {
            e.printStackTrace();
            return null;
        }

        /*
        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(mHttpTransport);
            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(mPlacesSearchUri));

            request.getUrl().put("key", mApiKey);
            request.getUrl().put("location", mLatitude + "," + mLongitude);
            request.getUrl().put("radius", mRadius);
            request.getUrl().put("sensor", "false");

            String data = request.execute().parseAsString();

            return data;

        } catch (HttpResponseException e) {
            e.printStackTrace();
            return null;
        }
        */
    }

    public String getPlaceDetails(String placeId) {
        Log.d(TAG, "SearchPlaces.getPlaceDetails()");
        this.mPlaceId = placeId;

        try {
            HttpRequestFactory httpRequestFactory = createRequestFactory(mHttpTransport);
            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(mPlacesDetailsUri));

            request.getUrl().put("key", mApiKey);
            request.getUrl().put("placeid", mPlaceId);
            request.getUrl().put("sensor", "false");

            String data = request.execute().parseAsString();

            return data;

        } catch (HttpResponseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpRequestFactory createRequestFactory(final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                HttpHeaders headers = new HttpHeaders();
                headers.setUserAgent("MzansiTravel");
                request.setHeaders(headers);
                JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
                request.setParser(parser);
            }
        });
    }
}
