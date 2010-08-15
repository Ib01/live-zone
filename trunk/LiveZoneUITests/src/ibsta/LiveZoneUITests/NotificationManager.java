package ibsta.LiveZoneUITests;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationManager {
	
	private static final int LIVEZONE_NOTIFICATION_ID = 1;
	private final android.app.Notification notification;
	private final Context context;
	private final android.app.NotificationManager notificationManager;
	
	public NotificationManager(Context appContext){
		context = appContext.getApplicationContext();
	
		notificationManager = (android.app.NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.t2;
		CharSequence tickerText = "LiveZone alert information available"; 
		long when = System.currentTimeMillis();	
		notification = new Notification(icon, tickerText, when);
	}
	
	
	public void notify(String contentTitle, String contentText)
	{
		Intent notificationIntent = new Intent(context, ibsta.LiveZoneUITests.LocationTests.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(LIVEZONE_NOTIFICATION_ID, notification);
	}

}











