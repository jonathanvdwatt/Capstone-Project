package local.watt.mzansitravel.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by f4720431 on 2016/10/13.
 */
public class Details implements Parcelable {
    private static final String TAG = Details.class.getSimpleName();

    public String name;
    public String formatted_address;
    public String international_phone_number;
    public String website;
    public String url;
    public Double latitude;
    public Double longitude;
    public String author_name;
    public String author_url;
    public String lang;
    public String profile_photo_url;
    public Double rating;
    public String review_text;
    public double overallRating;
    public String type;

    public Details() {
        Log.d(TAG, "Details()");
    }

    protected Details(Parcel in) {
        formatted_address = in.readString();
        international_phone_number = in.readString();
        website = in.readString();
        url = in.readString();
        author_name = in.readString();
        author_url = in.readString();
        lang = in.readString();
        profile_photo_url = in.readString();
        rating = in.readDouble();
        review_text = in.readString();
    }

    public static final Creator<Details> CREATOR = new Creator<Details>() {
        @Override
        public Details createFromParcel(Parcel in) {
            return new Details(in);
        }

        @Override
        public Details[] newArray(int size) {
            return new Details[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(double overallRating) {
        this.overallRating = overallRating;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formatted_address);
        dest.writeString(international_phone_number);
        dest.writeString(website);
        dest.writeString(url);
        dest.writeString(author_name);
        dest.writeString(author_url);
        dest.writeString(lang);
        dest.writeString(profile_photo_url);
        dest.writeDouble(rating);
        dest.writeString(review_text);
    }
}
