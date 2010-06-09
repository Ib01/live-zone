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
	
	
	public void AddProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude){
		
		AddProximityAlert(action, plugin, latitude, longtitude, defaultRadius);
	}
	
	
	public void AddProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude, float radius){
		
		AddProximityAlert(action, plugin, latitude, longtitude, radius, defaultExpiration);
	}
	
	
	/*
	 * add a proximity alert that will directly call the plug in
	*/
	public void AddProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude, float radius, long alertExpiration){
		
		//opportunity to change defaults
		defaultExpiration = alertExpiration;
		defaultRadius = radius;
		
    	locationManager.addProximityAlert(latitude, longtitude, radius, alertExpiration, GetPendingIntent(action, plugin));
	}
	
	
	public void CancelProximityAlert(String action, PluginInfo plugin, double latitude, double longtitude, float radius){
		
		locationManager.removeProximityAlert(GetPendingIntent(action, plugin));
	}

	/*
	 * need to ensure we use identical PendingIntent's when adding and removing
	*/
	private PendingIntent GetPendingIntent(String action, PluginInfo plugin){
		
		Intent intent = new Intent(action);
		intent.setComponent(new ComponentName(plugin.packageName, plugin.activityName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent proximityIntent = PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	return proximityIntent;
	}
	
	
	
}









