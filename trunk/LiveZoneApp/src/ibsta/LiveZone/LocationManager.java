package ibsta.LiveZone;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class LocationManager 
{
	private final Context context;
	private final int maxSearchMiliSecs;
	private final float targetAccuracy;
	private final android.location.LocationManager locationManager;
	private boolean getMostAccurate;
	private OnSearchCompleteListener onSearchCompleteListener = null;
	private LocationListener locationListener;
	private Long startingSeconds;
	
	
	public LocationManager(Context context){
		this.context = context;
		maxSearchMiliSecs = 15000;
		targetAccuracy = 50;
		locationManager = (android.location.LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	
	public LocationManager(Context context, int maxSearchMiliSecs, float targetAccuracy){
		this.context = context;
		this.maxSearchMiliSecs = maxSearchMiliSecs;
		this.targetAccuracy = targetAccuracy;
		locationManager = (android.location.LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean getUpdates(){
		return getUpdates(false);
	}
	
	public boolean getUpdates(boolean getMostAccurate){
		
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		
        String prov = locationManager.getBestProvider(crit, true);
        
        if(prov == null){
        	Toast.makeText(context.getApplicationContext(), 
        			"Could not find your location. Your gps receiver and internet connection appear to be dissabled", Toast.LENGTH_LONG).show();	
        	return false;
        }
        
        this.getMostAccurate = getMostAccurate;
        startingSeconds = new java.util.Date().getTime();
        	
        //get location with best provider. COULD THROW ERROR?
        locationListener = new LocationReceiver();
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
















