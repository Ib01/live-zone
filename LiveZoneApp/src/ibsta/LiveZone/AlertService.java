/**
 * 
 */
package ibsta.LiveZone;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * @author ib
 *
 */
public class AlertService extends Service {


	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		Toast.makeText(getApplicationContext(), "created", Toast.LENGTH_LONG).show();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		Toast.makeText(getApplicationContext(), "started: " + intent.getData().toString(), Toast.LENGTH_LONG).show();
		
		NotificationManager notificationManager = new NotificationManager(this.getApplicationContext());  
		
		boolean entering = intent.getBooleanExtra(android.location.LocationManager.KEY_PROXIMITY_ENTERING, false);
		String alertId =   intent.getData().toString();
				
		notificationManager.notify(entering, alertId);
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Toast.makeText(getApplicationContext(), "destroyed", Toast.LENGTH_LONG).show();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
