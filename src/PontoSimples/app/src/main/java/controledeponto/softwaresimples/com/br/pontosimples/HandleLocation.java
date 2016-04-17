package controledeponto.softwaresimples.com.br.pontosimples;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class HandleLocation {

    private static HandleLocation instance;

    private Context context;
    private LocationManager mLocationManager;
    private Location location;


    public static HandleLocation getInstance(Context c) {
        if (instance==null) {
            instance = new HandleLocation (c);
        }
        return instance;
    }


    private HandleLocation (Context c) {
        this.context = c;
        this.mLocationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
    }


    public boolean getLocation(LocationListener mLocationListener) {

        boolean b = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(b) {
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null);
        }

        return b;

    }

}