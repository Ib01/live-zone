package ibsta.LiveZoneUITests;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocationTests extends Activity implements OnClickListener {
    
	private LocationManager locationManager;
	private LocationListener locationListener;
	public Long startingMiliSeconds;
	public Long maxSearchMiliSecs = 20000L;
	private ZoneAlertManager zam;
	private String componentName = "LiveZoneUITests"; // for error logging
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button gl = (Button)findViewById(R.id.network);
        Button q = (Button)findViewById(R.id.quit);
        Button gps = (Button)findViewById(R.id.GPS);
        gl.setOnClickListener(this);
        q.setOnClickListener(this);
        gps.setOnClickListener(this);
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationReceiver();
        
        zam = new ZoneAlertManager(getApplicationContext());
    }

    
    
    
	public void onClick(View v) {
		
		
		
		/*if(v.getId() == R.id.quit)
		{
			zam.cancelGetLocationAsync();
		}
		else
			zam.getLocationAsync();
		*/
		
		
		
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent("ibsta.LiveZoneUITests.ZoneAlertService");
		intent.setComponent(new ComponentName("ibsta.LiveZoneUITests", "ibsta.LiveZoneUITests.ZoneAlertService"));
		
		PendingIntent proximityIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		if(v.getId() == R.id.quit)
		{
			am.cancel(proximityIntent);
			Log.i(componentName, "LocationTests: AlarmManager alert canceled");
			return;
			
		}
		
		
		long now = new java.util.Date().getTime();

		am.setRepeating(AlarmManager.RTC_WAKEUP, now, 300000, proximityIntent);
		
		
		
		/*Database db =  new Database(this.getApplicationContext());
		db.open();
		db.AddLocationRow();
		db.close();
		
		
		  TextView t = (TextView)findViewById(R.id.TextView01);
	       t.setText("done\n\n");*/
		
		
		//Intent i = new Intent(this, ZoneAlertService.class);		
        //startService(i);
		
		/*
		if(v.getId() == R.id.network)
			getUpdates(LocationManager.NETWORK_PROVIDER);
		else
			if(v.getId() == R.id.GPS)
				getUpdates(LocationManager.GPS_PROVIDER);
			else
				removeUpdates();*/
		
	}
	
	public boolean getUpdates(String provider){
		
		/*Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		
        String prov = locationManager.getBestProvider(crit, true);
        
        if(prov == null){
        	Toast.makeText(getApplicationContext(), 
        			"Could not find your location. Your gps receiver and internet connection appear to be dissabled", Toast.LENGTH_LONG).show();	
        	return false;
        }
        */
        startingMiliSeconds = new java.util.Date().getTime();
        
        
        
        if(!locationManager.isProviderEnabled(provider))
        {
        	Toast.makeText(getApplicationContext(), 
        			"provider is not enabled", Toast.LENGTH_LONG).show();	
        	return false;
        }
        
        TextView t = (TextView)findViewById(R.id.TextView01);
        t.setText("Getting location 1\n\n");
        
        
        //get location with best provider. COULD THROW ERROR?
        //can we use the same listener for multiple simultaneous calls to getUpdates?
        locationManager.requestLocationUpdates(provider, 180000, 0, locationListener);
        return true;
	}
	
	public void removeUpdates(){
		locationManager.removeUpdates(locationListener);
	}
	
	
	protected void onDestroy(){
    	//after this the activity will be destroyed.  when entering the activity again 
    	// on create will be called.  which means all state info will need to be reestablished.
    	//do any destruction here, eg ... shutting down of threads etc.
    
    	//if getting current locaton, cancel such updates
    	super.onDestroy();
    	removeUpdates();
    }
	
	
	
	//call back for android's location manager
	public class LocationReceiver implements LocationListener {

		Location bestLocation = null;
		
		
    	public void onLocationChanged(Location location) 
    	{
    		//java.util.Date now = new java.util.Date();
    		
    		//String.valueOf((now.getTime() - startingMiliSeconds)) + " \n " +
    		
    		TextView t = (TextView)findViewById(R.id.TextView01);
    		t.setText(
    				t.getText() + 
    				"lat: " + String.valueOf(location.getLatitude())  + " \n " +  
    				"lng: " + String.valueOf(location.getLongitude()) + " \n " +
    				"Acc: " + String.valueOf(location.getAccuracy()) + "\n\n");
    		
    		/*if(bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy())
    		{
    			bestLocation = location;
    		}
    		
    		
    		if(
				(now.getTime() - startingMiliSeconds) > maxSearchMiliSecs
			)
    		{
    			t.setText(
        				t.getText() + 
        				"BEST LOCATION \n" +
        				"lat: " + String.valueOf(bestLocation.getLatitude())  + " \n " +  
        				"lng: " + String.valueOf(bestLocation.getLongitude()) + " \n " +
        				"Acc: " + String.valueOf(bestLocation.getAccuracy()) + "\n\n");
    			
    			bestLocation = null;
    			locationManager.removeUpdates(this);
    		}*/
    		
    	}
    	
    	public void onProviderDisabled(String provider) {
    	}
    	public void onProviderEnabled(String provider) {
    	}
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}

    } // end LocationHandler
	
	
	
	
}


