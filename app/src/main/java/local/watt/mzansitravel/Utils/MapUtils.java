package local.watt.mzansitravel.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by f4720431 on 2016/09/11.
 */
public class MapUtils {
    public static LatLngBounds getLatLngBounds(LatLng center) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder.include(center);
        return builder.build();
    }
}
