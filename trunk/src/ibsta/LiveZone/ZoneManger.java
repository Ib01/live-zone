package ibsta.LiveZone;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class ZoneManger {

	private final Context appContext;
	private final LocationManager locationManager;
	public long defaultExpiration;
	public float defaultRadius;
	
	public ZoneManger(Context _appContext){
		
		appContext = _appContext;
		locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		defaultExpiration = -1;
		defaultRadius = 100.0f;
	}
	
	
	/**
	 * add a Proximity Alert. uses default radius of 100.0f, and a default expiration for the alert of -1, 
	 * or never expires
	 * @param action
	 * @param plugin
	 * @param latitude
	 * @param longtitude
	 */
	public void addProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude){
		
		addProximityAlert(action, plugin, latitude, longtitude, defaultRadius);
		
		
	}
	
	
	/**
	 * add a Proximity Alert. uses a default expiration for the alert of -1, 
	 * or never expires
	 * @param action
	 * @param plugin
	 * @param latitude
	 * @param longtitude
	 * @param radius
	 */
	public void addProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude, float radius){
		
		addProximityAlert(action, plugin, latitude, longtitude, radius, defaultExpiration);
	}
	
	
	/**  add a proximity alert that will directly call the plug in
	 * @param action
	 * @param plugin
	 * @param latitude
	 * @param longtitude
	 * @param radius
	 * @param alertExpiration
	 */
	public void addProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude, float radius, long alertExpiration){
		
		//opportunity to change defaults
		defaultExpiration = alertExpiration;
		defaultRadius = radius;
		
    	locationManager.addProximityAlert(latitude, longtitude, radius, alertExpiration, getPendingIntent(action, plugin));
	}
	
	
	/**Cancel a proximity alert. all data used to create an alert must also be provided to cancel it.  
	 * @param action
	 * @param plugin
	 * @param latitude
	 * @param longtitude
	 * @param radius
	 */
	public void cancelProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude, float radius){
		
		locationManager.removeProximityAlert(getPendingIntent(action, plugin));
	}

	/*
	 * need to ensure we use identical PendingIntent's when adding and removing
	*/
	private PendingIntent getPendingIntent(String action, PluginInfo plugin){
		
		Intent intent = new Intent(action);
		intent.setComponent(new ComponentName(plugin.packageName, plugin.activityName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent proximityIntent = PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	return proximityIntent;
	}
	
	
	
}









