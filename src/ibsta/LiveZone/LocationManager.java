package ibsta.LiveZone;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class LocationManager 
{
	private final android.location.LocationManager locationManager;
	private OnSearchCompleteListener onSearchCompleteListener = null;
	private LocationListener locationListener;
	
	Context context;
	
	public LocationManager(Context context){
		
		this.context = context;
		locationManager = (android.location.LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean getUpdates(){
		
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		
        String prov = locationManager.getBestProvider(crit, true);
        
        if(prov == null){
        	Toast.makeText(context.getApplicationContext(), 
        			"Could not find your location. Is your gps receiver and internet connection dissabled", Toast.LENGTH_LONG).show();	
        	return false;
        }
        	
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

    	float m_targetAccuracy = 50;
    	
    	public void onLocationChanged(Location location) {
    		
    		onSearchComplete(location);
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
















