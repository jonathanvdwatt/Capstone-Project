package local.watt.mzansitravel.Models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by f4720431 on 2016/09/13.
 */
public class MzansiPlaces implements Parcelable {
    public String id;
    public String name;
    public String reference;
    public String icon;
    public String vicinity;
    public String status;
    public Double rating;
    public Geometry geometry;
    public String formatted_address;
    public String formatted_phone_number;
    public long latitude;
    public long longitude;

    public MzansiPlaces() {
    }

    public MzansiPlaces(String id,
                        String name,
                        String reference,
                        String icon,
                        String vicinity,
                        String status,
                        double rating,
                        String formatted_address,
                        String formatted_phone_number) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.icon = icon;
        this.vicinity = vicinity;
        this.status = status;
        this.rating = rating;
        this.formatted_address = formatted_address;
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(reference);
        dest.writeString(icon);
        dest.writeString(vicinity);
        if(rating != null) {
            dest.writeDouble(rating);
        }
        dest.writeString(formatted_address);
        dest.writeString(formatted_phone_number);
    }

    /*
     * A constructor to initialize a MzansiPlaces object
     */
    public MzansiPlaces(String id,
                        String name,
                        String reference,
                        String icon,
                        String vicinity,
                        double rating,
                        Geometry geometry,
                        String formatted_address,
                        String formatted_phone_number) {

        this.id = id;
        this.name = name;
        this.reference = reference;
        this.icon = icon;
        this.vicinity = vicinity;
        this.rating = rating;
        this.geometry = geometry;
        this.formatted_address = formatted_address;
        this.formatted_phone_number = formatted_phone_number;
    }

    protected MzansiPlaces(Parcel in) {
        id = in.readString();
        name = in.readString();
        reference = in.readString();
        icon = in.readString();
        vicinity = in.readString();
        rating = in.readDouble();
        formatted_address = in.readString();
        formatted_phone_number = in.readString();
    }

    public static final Creator<MzansiPlaces> CREATOR = new Creator<MzansiPlaces>() {
        @Override
        public MzansiPlaces createFromParcel(Parcel in) {
            return new MzansiPlaces(in);
        }

        @Override
        public MzansiPlaces[] newArray(int size) {
            return new MzansiPlaces[size];
        }
    };

    public static class Geometry {
        public Location location;
    }

    public static class Location {
        public double lat;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double lng;
    }
}
