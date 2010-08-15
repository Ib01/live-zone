package ibsta.LiveZoneUITests;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


/**
 * this service runs in its own process
 * @author ib
 *
 */
public class ZoneAlertService extends Service {

	private String componentName = "LiveZoneUITests"; // for error logging
	ZoneAlertManager zoneAlertManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Toast.makeText(getApplicationContext(), 
    			"Creating ZoneAlertService", 
    			Toast.LENGTH_LONG).show();
		
		Log.i(componentName, "onCreate");
		
		zoneAlertManager = new ZoneAlertManager(getApplicationContext());
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		/*Database db =  new Database(this.getApplicationContext());
		db.open();
		db.AddLocationRow();
		db.close();
		*/
		
		Toast.makeText(getApplicationContext(), 
    			"Starting ZoneAlertService done", 
    			Toast.LENGTH_LONG).show();
		
		Log.i(componentName, "onStart");
		
		
		zoneAlertManager.getLocationAsync();
	}

	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		Toast.makeText(getApplicationContext(), 
    			"Destroying ZoneAlertService", 
    			Toast.LENGTH_LONG).show();
		
		Log.i(componentName, "onDestroy");
		
		zoneAlertManager.cancelGetLocationAsync();
	}

	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
