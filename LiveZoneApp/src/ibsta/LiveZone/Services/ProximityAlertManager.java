package ibsta.LiveZone.Services;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class ProximityAlertManager {

	private final Context appContext;
	private final LocationManager locationManager;
	private OnSearchCompleteListener onSearchCompleteListener = null;
	private LocationListener locationListener;
	
	
	/*private Long startingSeconds;
	 * private boolean getMostAccurate;
	 * private final int maxSearchMiliSecs;
	private final float targetAccuracy;*/
	
	
	public ProximityAlertManager(Context context){
		
		appContext = context.getApplicationContext();
		
		locationManager = (LocationManager)appContext.getSystemService(appContext.LOCATION_SERVICE);
		locationListener = new LocationReceiver();
		
		/*maxSearchMiliSecs = 15000;
		targetAccuracy = 50;*/
	}
	
	
	
	
	
	public boolean getUpdates(){
		return getUpdates(false);
	}
	
	public boolean getUpdates(boolean getMostAccurate){
		
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		
		
        String prov = locationManager.getBestProvider(crit, true);
        
        if(prov == null){
        	Toast.makeText(appContext.getApplicationContext(), 
        			"Could not find your location. Your gps receiver and internet connection appear to be dissabled", Toast.LENGTH_LONG).show();	
        	return false;
        }
        
        this.getMostAccurate = getMostAccurate;
        startingSeconds = new java.util.Date().getTime();
        	
        //get location with best provider. COULD THROW ERROR?
        //can we use the same listener for multiple simultaneous calls to getUpdates?
        locationManager.requestLocationUpdates(prov, 0, 0, locationListener);
        return true;
	}
	
	public void removeUpdates(){
		locationManager.removeUpdates(locationListener);
	}
	
	
	
	
	
	
	//call back for android's location manager
	public class LocationReceiver implements LocationListener {

    	public void onLocationChanged(Location location) 
    	{
    		java.util.Date now = new java.util.Date();
    		
    		if(
				!getMostAccurate || 
				location.getAccuracy() <= targetAccuracy || 
				(now.getTime() - startingSeconds) > maxSearchMiliSecs
			)
    		{
    			locationManager.removeUpdates(this);
    			onSearchComplete(location);	
    		}
    		
    	}
    	
    	public void onProviderDisabled(String provider) {
    	}
    	public void onProviderEnabled(String provider) {
    	}
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}

    } // end LocationHandler
    
	
	
	
	
	
	private void onSearchComplete(Location location){

		if(onSearchCompleteListener != null) {
			onSearchCompleteListener.onSearchComplete(location);
		}
	}
	
	// Allows the user to set an Listener and react to the event
	public void setOnSearchCompleteListener(OnSearchCompleteListener listener) {
		
		onSearchCompleteListener = listener;
	}
	
	public interface OnSearchCompleteListener {
		
		public abstract void onSearchComplete(Location location);
	}
	
	
	
}
