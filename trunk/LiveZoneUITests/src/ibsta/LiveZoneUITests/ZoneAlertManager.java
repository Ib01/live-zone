package ibsta.LiveZoneUITests;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class ZoneAlertManager {
	
	private String componentName = "LiveZoneUITests"; // for error logging
	private LocationManager locationManager;
	private TimeRunnable timeRunnable;
	private Handler timeHandler;
	private final Context appContext;
	
	NotificationManager notificationManager;
	
	public ZoneAlertManager(Context context)
	{
		appContext = context.getApplicationContext();
		locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		timeRunnable = new TimeRunnable();
		timeHandler = new Handler();
		timeRunnable.busy = false;
		
		notificationManager = new NotificationManager(appContext);
	}
	
	
	public void getLocationAsync(){
		
		if(!timeRunnable.busy){
		
			timeRunnable.busy = true;
			timeRunnable.GPSReceiver = (GPSReceiver) setUpdate(new GPSReceiver(), LocationManager.GPS_PROVIDER);
			timeRunnable.NetworkReceiver = (NetworkReceiver) setUpdate(new NetworkReceiver(), LocationManager.NETWORK_PROVIDER);
					
			timeHandler.postDelayed(timeRunnable, 30000);
		}
		
	}
	
	
	
	private class TimeRunnable implements Runnable
	{
		public boolean busy;
		private GPSReceiver GPSReceiver;
		private NetworkReceiver NetworkReceiver;
		
		public void run() {
			String provider = "";
			Location bestLocation = null;
			String gps = "not available";
			String ntwk = "not available";
			
			if(GPSReceiver != null)
				if(GPSReceiver.bestLocation != null){
					gps = String.format("lat: %s. lng: %s.", 
							String.valueOf(GPSReceiver.bestLocation.getLatitude()), 
							String.valueOf(GPSReceiver.bestLocation.getLongitude()));
					
					bestLocation = GPSReceiver.bestLocation;
					provider = "gps";
				}
			
			if(NetworkReceiver != null)
				if(NetworkReceiver.bestLocation != null){
					ntwk = String.format("lat: %s. lng: %s.", 
							String.valueOf(NetworkReceiver.bestLocation.getLatitude()), 
							String.valueOf(NetworkReceiver.bestLocation.getLongitude()));
					
					if(
							bestLocation == null  ||
							bestLocation.getAccuracy() > NetworkReceiver.bestLocation.getAccuracy()	
					){
						bestLocation = NetworkReceiver.bestLocation;
						provider = "nwk";
					}
				}
			
			String msg = String.format("gps co-ords: %s. ntwk co-ords: %s",gps, ntwk);
			Log.i(componentName, msg);
			//Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show();
			
			msg = String.format("%f:%f.Ac:%.2f. %s",bestLocation.getLatitude(), bestLocation.getLongitude(), bestLocation.getAccuracy(), provider);
			
			notificationManager.notify("location aquired", msg);
			
			CancelUpdates();
		}
		
		public void CancelUpdates(){
			locationManager.removeUpdates(GPSReceiver);
			locationManager.removeUpdates(NetworkReceiver);
			busy = false;
		}
	}
	
	
	/**
	 * gets the current location using GPS 
	 * 
	 * @param alertId. id of the item to get
	 * @return a cursor
	 */
	private LocationReceiver setUpdate(LocationReceiver receiver, String provider){
		
		if(locationManager.isProviderEnabled(provider)){
			
		    try{
		    	
		    	locationManager.requestLocationUpdates(provider, 0, 0, receiver);	
		    }
		    catch(IllegalArgumentException ex){
		    	
		    	// should really never happen
		    	if(!locationManager.isProviderEnabled(provider)){
		    		String msg = String.format("%s provider is not enabled", provider);
		    		Log.e(componentName, msg);
		    		
		    		return null;
		    	}
		    }
		}
		
		return receiver;
	}
	
	
	
	public void cancelGetLocationAsync(){
		
		timeRunnable.CancelUpdates();
		timeHandler.removeCallbacks(timeRunnable);
	}
	
	
	
	
	
	
	//need separate classes for network calls and gps for the remove functions to work  
	private class NetworkReceiver extends LocationReceiver{
	}
	
	private class GPSReceiver extends LocationReceiver{
	}
	
	//call back for android's location manager
	private class LocationReceiver implements LocationListener {
		
		public Location bestLocation = null;
		
    	public void onLocationChanged(Location location) 
    	{
    		if(bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy())
    		{
    			bestLocation = location;
    		}
    	}
    	
    	public void onProviderDisabled(String provider) {
    	}
    	public void onProviderEnabled(String provider) {
    	}
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}

    } 
	
	

}




//java.util.Date now = new java.util.Date();
/*if(
	(now.getTime() - startingMiliSeconds) > maxSearchMiliSecs
)
{
	
	bestLocation = null;
	locationManager.removeUpdates(this);
}
*/