package ibsta.LiveZone.Services;

import ibsta.LiveZone.R;
import ibsta.LiveZone.R.drawable;
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
		context = appContext;
	
		notificationManager = (android.app.NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.t2;
		CharSequence tickerText = "LiveZone alert information available"; 
		long when = System.currentTimeMillis();	
		notification = new Notification(icon, tickerText, when);
	}
	
	
	public void notify(boolean entering, String alertId)
	{
		CharSequence contentTitle = "LiveZone Alert:";
		
		String heading = "";
		if(entering)
			heading = "Entering Zone";
		else
			heading = "Leaving Zone";
			
		CharSequence contentText = "You are: " + heading + ". alert id: " + alertId;
		
		Intent notificationIntent = new Intent(context, ibsta.LiveZone.UI.ViewAlerts.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(LIVEZONE_NOTIFICATION_ID, notification);
	}

}











