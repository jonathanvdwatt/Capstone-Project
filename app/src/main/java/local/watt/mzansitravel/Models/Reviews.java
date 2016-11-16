package local.watt.mzansitravel.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by f4720431 on 2016/09/27.
 */
public class Reviews implements Parcelable {
    public String author_name;
    public String author_url;
    public String lang;
    public String profile_photo_url;
    public int rating;
    public String review_text;

    public Reviews() {
    }

    public Reviews(String author_name, String author_url, String lang,
                   String profile_photo_url, int rating, String review_text) {
        this.author_name = author_name;
        this.author_url = author_url;
        this.lang = lang;
        this.profile_photo_url = profile_photo_url;
        this.rating = rating;
        this.review_text = review_text;
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

    public Reviews(Parcel in) {
        author_name = in.readString();
        author_url = in.readString();
        lang = in.readString();
        profile_photo_url = in.readString();
        rating = in.readInt();
        review_text = in.readString();
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author_name);
        dest.writeString(author_url);
        dest.writeString(lang);
        dest.writeString(profile_photo_url);
        dest.writeInt(rating);
        dest.writeString(review_text);
    }
}
