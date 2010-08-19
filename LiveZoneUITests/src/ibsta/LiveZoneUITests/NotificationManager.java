package ibsta.LiveZoneUITests;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationManager {
	
	private static final int LIVEZONE_NOTIFICATION_ID = 1;
	private final Context context;
	private final android.app.NotificationManager notificationManager;
	int icon;
	CharSequence tickerText;
	
	public NotificationManager(Context appContext){
		
		context = appContext.getApplicationContext();
		notificationManager = (android.app.NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		icon = R.drawable.t2;
		tickerText = "LiveZone alert information available";
	}
	
	
	public void notify(String contentTitle, String contentText)
	{
		Intent notificationIntent = new Intent(context, ibsta.LiveZoneUITests.LocationTests.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		notificationManager.notify(LIVEZONE_NOTIFICATION_ID, notification);
	}

}











