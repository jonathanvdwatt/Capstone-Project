package local.watt.mzansitravel.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import java.sql.SQLException;

import local.watt.mzansitravel.Tasks.LoadPlaces;

/**
 * Created by f4720431 on 2016/10/27.
 */
public class DataProvider extends ContentProvider {
    private static final String TAG = DataProvider.class.getSimpleName();

    // Indicates that the incoming query is for a place id
    public static final int PLACE_QUERY = 1;

    // Indicates that the incoming query is for a place type
    //public static final int PLACE_TYPE_QUERY = 2;

    // Indicates an invalid content uri
    public static final int INVALID_URI = -1;

    // Constants for building SQLite tables during initialization
    private static final String TEXT_TYPE = "TEXT";
    private static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String INTEGER_TYPE = "INTEGER";

    // Defines a SQL statement which builds the Places table;
    private static final String CREATE_PLACES_TABLE_SQL = "CREATE TABLE IF NOT EXISTS" + " " +
            DataProviderContract.PLACES_TABLE_NAME + " " +
            "(" + " " +
            DataProviderContract.ROW_ID + " " + PRIMARY_KEY_TYPE + " ," +
            DataProviderContract.PLACE_ID_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_NAME_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_RATING_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_PHONE_NUMBER_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_WEBSITE_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_ADDRESS_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_VICINITY_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_COORDINATES_COLUMN + " " + TEXT_TYPE + " ," +
            DataProviderContract.PLACE_TYPE_COLUMN + " " + TEXT_TYPE +
            ")";

    // Defines a helper object for the backing database
    private SQLiteOpenHelper mHelper;

    // Stores the MIME types served by this provider
    private static final SparseArray<String> sMimeTypes;

    // Defines a helper object that matches content URIs to table-specific parameters
    private static final UriMatcher sUriMatcher;

    // Initializes meta-data used by the content provider
    static {

        // Creates an object that associates content URIs with numeric codes
        sUriMatcher = new UriMatcher(0);

        /*
         * Sets up an array that maps content URIs to MIME types, via a mapping between the
         * URIs and an integer code. These are custom MIME types that apply to tables and rows
         * in this particular provider.
         */
        sMimeTypes = new SparseArray<String>();

        // Adds a URI "match" entry that maps place id URIs to a numeric code
        sUriMatcher.addURI(
                DataProviderContract.AUTHORITY,
                DataProviderContract.PLACES_TABLE_NAME,
                PLACE_QUERY);
    }

    // Closes the SQLite database helper class, to avoid memory leaks
    public void close() {
        mHelper.close();
    }

    /**
     * Defines a helper class that opens the SQLite database for this provider when a request is
     * received. If the database doesn't yet exist, the helper creates it.
     */
    private class DataProviderHelper extends SQLiteOpenHelper {
        /**
         * Instantiates a new SQLite database using the supplied database name and version
         *
         * @param context The current context
         */
        DataProviderHelper(Context context) {
            super(context,
                    DataProviderContract.DATABASE_NAME,
                    null,
                    DataProviderContract.DATABASE_VERSION);
        }


        /**
         * Executes the queries to drop all of the tables from the database.
         *
         * @param db A handle to the provider's backing database.
         */
        private void dropTables(SQLiteDatabase db) {

            // If the table doesn't exist, don't throw an error
            db.execSQL("DROP TABLE IF EXISTS " + DataProviderContract.PLACES_TABLE_NAME);
        }

        /**
         * Does setup of the database. The system automatically invokes this method when
         * SQLiteDatabase.getWriteableDatabase() or SQLiteDatabase.getReadableDatabase() are
         * invoked and no db instance is available.
         *
         * @param db the database instance in which to create the tables.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Creates the tables in the backing database for this provider
            Log.d(TAG, "Create places table SQL: " + CREATE_PLACES_TABLE_SQL);
            db.execSQL(CREATE_PLACES_TABLE_SQL);
        }

        /**
         * Handles upgrading the database from a previous version. Drops the old tables and creates
         * new ones.
         *
         * @param db The database to upgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(DataProviderHelper.class.getName(),
                    "Upgrading database from version " + version1 + " to "
                            + version2 + ", which will destroy all the existing data");

            // Drops all the existing tables in the database
            dropTables(db);

            // Invokes the onCreate callback to build new tables
            onCreate(db);
        }
        /**
         * Handles downgrading the database from a new to a previous version. Drops the old tables
         * and creates new ones.
         * @param db The database object to downgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onDowngrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(DataProviderHelper.class.getName(),
                    "Downgrading database from version " + version1 + " to "
                            + version2 + ", which will destroy all the existing data");

            // Drops all the existing tables in the database
            dropTables(db);

            // Invokes the onCreate callback to build new tables
            onCreate(db);

        }
    }

    // Initializes the content provider
    @Override
    public boolean onCreate() {
        // Creates a new database helper object
        mHelper = new DataProviderHelper(getContext());

        return true;
    }

    // Returns the result of querying the chosen table
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        Log.d(TAG, "query(\n" + uri + "\n" + projection + "\n" + selection.toString() + "\n" + selectionArgs[0] + "\n" + sortOrder + "\n)");

        SQLiteDatabase db = mHelper.getReadableDatabase();
        // Decodes the content URI and maps it to a code
        switch (sUriMatcher.match(uri)) {

            // If the query is for a picture URL
            case PLACE_QUERY:

                Log.d(TAG, "PLACE_QUERY");

                // Does the query against a read-only version of the database
                Cursor returnCursor = db.query(
                        DataProviderContract.PLACES_TABLE_NAME, // The table
                        projection,                             // The table columns
                        selection,                              // The WHERE clause
                        selectionArgs,                          // The WHERE arguments
                        null,                                   // GROUP BY
                        null,                                   // HAVING
                        null                                    // ORDER BY
                );

                // Sets the ContentResolver to watch this content URI for data changes
                returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return returnCursor;

            case INVALID_URI:

                throw new IllegalArgumentException("Query -- Invalid URI:" + uri);
        }

        return null;
    }

    // Returns the mimeType associated with the Uri (query).
    @Override
    public String getType(Uri uri) {
        return sMimeTypes.get(sUriMatcher.match(uri));
    }

    // Insert a single row into a table
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // Decode the URI to choose which action to take
        switch (sUriMatcher.match(uri)) {

            // For the place id table
            case PLACE_QUERY:

                // Creates a writeable database or gets one from cache
                SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();

                // Inserts the row into the table and returns the new row's _id value
                long id = localSQLiteDatabase.insert(
                        DataProviderContract.PLACES_TABLE_NAME,
                        null,
                        values
                );

                // If the insert succeeded, notify a change and return the new row's content URI.
                if (-1 != id) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.withAppendedPath(uri, Long.toString(id));
                } else {

                    throw new SQLiteException("Insert error:" + uri);
                }
        }

        return null;
    }

    // Return an UnsupportedOperationException if delete is called
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Delete -- unsupported operation " + uri);
    }

    // Updates one or more rows in a table.

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Decodes the content URI and choose which insert to use
        switch (sUriMatcher.match(uri)) {

            // A place id content URI
            case PLACE_QUERY:

                // Creats a new writeable database or retrieves a cached one
                SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();

                // Updates the table
                int rows = localSQLiteDatabase.update(
                        DataProviderContract.PLACES_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                // If the update succeeded, notify a change and return the number of updated rows.
                if (0 != rows) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return rows;
                } else {

                    throw new SQLiteException("Update error:" + uri);
                }
        }

        return -1;
    }
}
