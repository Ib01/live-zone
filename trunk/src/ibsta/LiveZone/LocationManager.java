package ibsta.LiveZone;

import ibsta.LiveZone.Data.PluginInfo;
import ibsta.LiveZone.Data.PluginManager;
import ibsta.LiveZone.UI.LiveZone.LocationReceiver;

import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.widget.TextView;
import android.widget.Toast;

public class LocationManager {
	
	private android.location.LocationManager locationManager;
	
	public LocationManager(Context context){
		
		locationManager = (android.location.LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        String prov = locationManager.getBestProvider(crit, true);
        
        if(prov == null)
        {
        	Toast.makeText(getApplicationContext(), "NO location providers available", Toast.LENGTH_LONG).show();	
        	return;
        }
        	
        
        //get location with best provider. COULD THROW ERROR?
        LocationListener locListener = new LocationReceiver();
        m_LocationManager.requestLocationUpdates(prov, 0, 0, locListener);
	}
	
	
	public void GetCurrentLocationAsync()
	{
	
	}
	
	
	
}


