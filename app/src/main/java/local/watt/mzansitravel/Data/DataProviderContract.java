package local.watt.mzansitravel.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by f4720431 on 2016/10/27.
 */
public class DataProviderContract implements BaseColumns {

    // Content provider database name
    public static final String DATABASE_NAME = "Places.DB8";

    // The starting version of the database
    public static final int DATABASE_VERSION = 1;

    // The URI scheme for content URIs
    public static final String SCHEME = "content";

    // The provider's authority
    public static final String AUTHORITY = "local.watt.mzansitravel.Data.contentprovider";

    // The DataProvider content URI
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);

    // Places table primary key column name
    public static final String ROW_ID = BaseColumns._ID;

    // Places table name
    public static final String PLACES_TABLE_NAME = "Places";

    // Places table content URI
    public static final Uri PLACE_TABLE_CONTENTURI =
            Uri.withAppendedPath(CONTENT_URI, PLACES_TABLE_NAME);

    // Places table place id column
    public static final String PLACE_ID_COLUMN = "PlaceId";

    // Places table place name column
    public static final String PLACE_NAME_COLUMN = "PlaceName";

    // Places table place rating column
    public static final String PLACE_RATING_COLUMN = "PlaceRating";

    // Places table place phone number column
    public static final String PLACE_PHONE_NUMBER_COLUMN = "PlacePhoneNumber";

    // Places table place website column
    public static final String PLACE_WEBSITE_COLUMN = "PlaceWebsite";

    // Places table place vicinity column
    public static final String PLACE_VICINITY_COLUMN = "PlaceVicinity";

    // Places table place address column
    public static final String PLACE_ADDRESS_COLUMN = "PlaceAddress";

    // Places table place coordinates column
    public static final String PLACE_COORDINATES_COLUMN = "PlaceCoordinates";

    // Places table place type column
    public static final String PLACE_TYPE_COLUMN = "PlaceType";
}
