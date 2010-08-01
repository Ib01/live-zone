package ibsta.LiveZone;

import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

public class AlertManager {

	private final Context appContext;
	private final LocationManager locationManager;
	public long defaultExpiration;
	public float defaultRadius;
	private static final String ALERT_SERVICE_PACKAGE_NAME =  "ibsta.LiveZone";
	private static final String ALERT_SERVICE_ACTIVITY_NAME =  "ibsta.LiveZone.AlertService";
	private static final String ALERT_SERVICE_ACTION =  "ibsta.LiveZone.ProximityAlertService";
	
	
	public AlertManager(Context _appContext){
		
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
	public void addProximityAlert(String action, Plugin plugin, double latitude, double longtitude){
		
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
	public void addProximityAlert(String action, Plugin plugin, double latitude, double longtitude, float radius){
		
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
	public void addProximityAlert(String action, Plugin plugin, double latitude, double longtitude, float radius, long alertExpiration){
		
		//opportunity to change defaults
		defaultExpiration = alertExpiration;
		defaultRadius = radius;
		
    	locationManager.addProximityAlert(latitude, longtitude, radius, alertExpiration, getPendingActivityIntent(action, plugin));
	}
	
	
	/**Cancel a proximity alert. all data used to create an alert must also be provided to cancel it.  
	 * @param action
	 * @param plugin
	 * @param latitude
	 * @param longtitude
	 * @param radius
	 */
	public void cancelProximityAlert(String action, Plugin plugin, double latitude, double longtitude, float radius){
		
		locationManager.removeProximityAlert(getPendingActivityIntent(action, plugin));
	}

	/*
	 * need to ensure we use identical PendingIntent's when adding and removing
	*/
	private PendingIntent getPendingActivityIntent(String action, Plugin plugin){
		
		Intent intent = new Intent(action);
		intent.setComponent(new ComponentName(plugin.packageName, plugin.activityName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent proximityIntent = PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	return proximityIntent;
	}
	
	
	/**register a proximity alert for the AlertService Service  
	 * @param alert id
	 * @param latitude
	 * @param longtitude
	 * @param radius
	 */
	public void addAlertServiceProximityAlert(ProximityAlert alert){
		
		addAlertServiceProximityAlert(
			alert.id,
			Double.valueOf(alert.latitude), 
			Double.valueOf(alert.longtitude), 
			Float.valueOf(alert.radius)
		);
	}
	
	
	/**register a proximity alert for the AlertService Service  
	 * @param alert id
	 * @param latitude
	 * @param longtitude
	 * @param radius
	 */
	public void addAlertServiceProximityAlert(int alertId, double latitude, double longtitude, float radius){
		
		locationManager.addProximityAlert(latitude, longtitude, radius, defaultExpiration, getPendingAlertServiceIntent(alertId));
	}
	
	/**Cancel AlertService proximity alert. all data used to create an alert must also be provided to cancel it.  
	 */
	public void cancelAlertServiceProximityAlert(int alertId){
		
		locationManager.removeProximityAlert(getPendingAlertServiceIntent(alertId));
	}
	
	
	private PendingIntent getPendingAlertServiceIntent(int alertId){

		Intent intent = new Intent(ALERT_SERVICE_ACTION);
		intent.setComponent(new ComponentName(ALERT_SERVICE_PACKAGE_NAME, ALERT_SERVICE_ACTIVITY_NAME));
		intent.setData(Uri.parse(String.valueOf(alertId)));
		
		//TEST THAT WE CAN TREAT ALERTS UNIQUELY BY THEIR DATA.  
		PendingIntent proximityIntent = PendingIntent.getService(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	return proximityIntent;
	}
	
	
}









