package ibsta.LiveZoneUITests;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class OnAlarmReceiver extends BroadcastReceiver {
	private String componentName = "LiveZoneUITests"; // for error logging
	
	public OnAlarmReceiver() {
		super();

		Log.i(componentName, "in OnAlarmReceiver"+ String.valueOf(Thread.currentThread().getId()));
		
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(componentName, "in BroadcastReceiver"+ String.valueOf(Thread.currentThread().getId()));
		
		ZoneAlertManager zam = new ZoneAlertManager(context);
		zam.getLocationAsync();
		
		Log.i(componentName, "in BroadcastReceiver post ZoneAlertManager");
		
		/*WakefulIntentService.acquireStaticLock(context);
		context.startService(new Intent(context, AppService.class));*/
	}
}
