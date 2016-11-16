package local.watt.mzansitravel.Utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by f4720431 on 2016/09/13.
 */
public class ConnectivityChecker {
    private static final String TAG = ConnectivityChecker.class.getSimpleName();

    private Context mContext;

    public ConnectivityChecker(Context context) {
        this.mContext = context;
    }

    /*
     * Check for internet connectivity
     */
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null) {
                for (int i=0; i<networkInfo.length; i++)
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        }
        return false;
    }

    /*
     * Check if Location Services are enabled
     */
    public boolean locationServicesEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsIsEnabled) {
            return true;
        } else {
            return false;
        }
    }
}
