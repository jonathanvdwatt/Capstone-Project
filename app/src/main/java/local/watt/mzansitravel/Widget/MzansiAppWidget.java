package local.watt.mzansitravel.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.RemoteViews;

import java.net.MulticastSocket;
import java.util.Random;

import local.watt.mzansitravel.Activities.SavedPlacesActivity;
import local.watt.mzansitravel.Data.DataProviderContract;
import local.watt.mzansitravel.R;
import local.watt.mzansitravel.Utils.MyFetchService;

/**
 * Implementation of App Widget functionality.
 */
public class MzansiAppWidget extends AppWidgetProvider {
    private static final String TAG = MzansiAppWidget.class.getSimpleName();

    private static String mMode;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate()");

        final int count = appWidgetIds.length;

        for (int i=0; i<count; i++) {
            int widgetId = appWidgetIds[i];

            // Create an intent to launch SavedPlacesActivity
            Intent intent = new Intent(context, SavedPlacesActivity.class);

            switch (widgetId) {
                case R.id.widget_hotels:
                    //mMode = "hotel";
                    intent.putExtra("mode", R.string.hotel_place_type_text);
                    break;
                case R.id.widget_restaurants:
                    //mMode = "restaurant";
                    intent.putExtra("mode", R.string.restaurant_place_type_text);
                    break;
                case R.id.widget_places:
                    //mMode = "point_of_interest";
                    intent.putExtra("mode", R.string.point_of_interest_place_type_text);
                    break;
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an onClick listener
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mzansi_app_widget);

            views.setOnClickPendingIntent(R.id.widget_hotels, pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_restaurants, pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_places, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
}