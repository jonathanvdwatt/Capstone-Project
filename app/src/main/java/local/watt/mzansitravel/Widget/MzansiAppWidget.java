package local.watt.mzansitravel.Widget;

import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import java.io.IOException;
import java.io.InputStream;
import java.net.MulticastSocket;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import local.watt.mzansitravel.Activities.SavedPlacesActivity;
import local.watt.mzansitravel.Data.DataProvider;
import local.watt.mzansitravel.Data.DataProviderContract;
import local.watt.mzansitravel.R;
import local.watt.mzansitravel.Utils.MyFetchService;

/**
 * Implementation of App Widget functionality.
 */
public class MzansiAppWidget extends AppWidgetProvider {
    private static final String TAG = MzansiAppWidget.class.getSimpleName();

    /*
    private Uri sUri = DataProviderContract.PLACE_TABLE_CONTENTURI;
    private String[] sProjection = {
            DataProviderContract.ROW_ID,
            DataProviderContract.PLACE_NAME_COLUMN,
            DataProviderContract.PLACE_RATING_COLUMN,
            DataProviderContract.PLACE_TYPE_COLUMN
    };
    private String sSelection = DataProviderContract.PLACE_TYPE_COLUMN + "=?";
    private String[] sSelectionArgs = {"hotel", "restaurant", "point_of_interest"};
    private String sSortOrder = null;

    private String mPlaceName;
    private String mPlaceRating;
    private String mPlaceType;
    */

    private RemoteViews mRemoteViews;

    //public static final String ACTION_SAVED_HOTELS = "ActionSavedHotels";
    //public static final String ACTION_SAVED_RESTAURANTS = "ActionSavedRestaurants";
    //public static final String ACTION_SAVED_POINTS_OF_INTEREST = "ActionSavedPointsOfInterest";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.mzansi_app_widget);

        Intent active = new Intent(context, SavedPlacesActivity.class);
        active.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        active.putExtra("mode", "hotel");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.hotel_widget_button, pendingIntent);

        active = new Intent(context, SavedPlacesActivity.class);
        active.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        active.putExtra("mode", "restaurant");
        pendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.restaurant_widget_button, pendingIntent);

        active = new Intent(context, SavedPlacesActivity.class);
        active.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        active.putExtra("mode", "point_of_interest");
        pendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.point_of_interest_widget_button, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);

        /*
        Log.d(TAG, "onUpdate()");

        ComponentName thisWidget = new ComponentName(context, MzansiAppWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.mzansi_app_widget);
            Intent intent = new Intent(context, SavedPlacesActivity.class);

            for (int i=0; i < sSelectionArgs.length; i++) {
                Log.d(TAG, "ARG: " + sSelectionArgs[i]);

                if (sSelectionArgs[i] == "hotel") {
                    String selectionArg[] = {"hotel"};
                    //getPlaces(context, selectionArg);
                    mPlaceType = "hotel";
                    //updateUI(context, mPlaceType);
                    startSavedPlacesActivity(context, intent, mPlaceType, R.id.hotel_widget_button);
                } else if (sSelectionArgs[i] == "restaurant") {
                    String selectionArg[] = {"restaurant"};
                    //getPlaces(context, selectionArg);
                    mPlaceType = "restaurant";
                    //updateUI(context, mPlaceType);
                    startSavedPlacesActivity(context, intent, mPlaceType, R.id.restaurant_widget_button);
                } else if (sSelectionArgs[i] == "point_of_interest") {
                    String selectionArg[] = {"point_of_interest"};
                    //getPlaces(context, selectionArg);
                    mPlaceType = "point_of_interest";
                    //updateUI(context, mPlaceType);
                    startSavedPlacesActivity(context, intent, mPlaceType, R.id.point_of_interest_widget_button);
                }

                appWidgetManager.updateAppWidget(widgetId, mRemoteViews);
            }
        }
        */
    }

    /*
    public void getPlaces(Context context, String[] selectionArg) {

        Log.d(TAG, "getPlaces()");

        Cursor cursor = context.getContentResolver().query(
                sUri,
                sProjection,
                sSelection,
                selectionArg,
                sSortOrder
        );

        Log.d(TAG, "Count: " + cursor.getCount());

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            mPlaceName = cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_NAME_COLUMN));
            mPlaceRating = cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_RATING_COLUMN));
        } else {
            mPlaceName = "No data to display";
        }

        cursor.close();
    }
    */

    /*
    public void startSavedPlacesActivity(
            Context context,
            Intent intent,
            String mode,
            int layoutResourceId) {

        Log.d(TAG, "startSavedPlacesActivity()");

        Log.d(TAG, "Applying Mode: " + mode);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mode", mode);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mRemoteViews.setOnClickPendingIntent(layoutResourceId, pendingIntent);
    }
    */

    /*
    public void updateUI(Context context, String placeType) {

        Log.d(TAG, "updateUI()");

        switch (placeType) {
            case "hotel":
                mRemoteViews.setTextViewText(R.id.hotel, mPlaceName);
                mRemoteViews.setTextViewText(R.id.hotel_rating, mPlaceRating);
                break;
            case "restaurant":
                mRemoteViews.setTextViewText(R.id.restaurant, mPlaceName);
                mRemoteViews.setTextViewText(R.id.restaurant_rating, mPlaceRating);
                break;
            case "point_of_interest":
                mRemoteViews.setTextViewText(R.id.point_of_interest, mPlaceName);
                mRemoteViews.setTextViewText(R.id.point_of_interest_rating, mPlaceRating);
                break;
        }
    }
    */
}