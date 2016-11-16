package local.watt.mzansitravel.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by f4720431 on 2016/09/14.
 */
public class MzansiPlacesDetails implements Parcelable {
    public String formatted_address;
    public String international_phone_number;
    public String website;
    public String url;
    public Double latitude;
    public Double longitude;

    public MzansiPlacesDetails() {
    }

    protected MzansiPlacesDetails(Parcel in) {
        formatted_address = in.readString();
        international_phone_number = in.readString();
        website = in.readString();
        url = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<MzansiPlacesDetails> CREATOR = new Creator<MzansiPlacesDetails>() {
        @Override
        public MzansiPlacesDetails createFromParcel(Parcel in) {
            return new MzansiPlacesDetails(in);
        }

        @Override
        public MzansiPlacesDetails[] newArray(int size) {
            return new MzansiPlacesDetails[size];
        }
    };

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

    /*
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }
    */

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
        dest.writeString(String.valueOf(latitude));
        dest.writeString(String.valueOf(longitude));
    }
}
