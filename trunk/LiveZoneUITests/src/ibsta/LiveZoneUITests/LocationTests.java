package ibsta.LiveZoneUITests;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocationTests extends Activity implements OnClickListener {
    
	/*private LocationManager locationManager;
	private LocationListener locationListener;*/
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
        
        /*locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationReceiver();*/
        
        TextView t = (TextView)findViewById(R.id.TextView01);
		t.setText(t.getText() + "1 ");
        
        
    }

    
    
    
	public void onClick(View v) {
		
		
		
		Log.i(componentName, "in onClick. Thread Id: " + String.valueOf(Thread.currentThread().getId()));
		
		AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Intent i=new Intent(this, OnAlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
		
		
		if(v.getId() == R.id.quit)
		{
			mgr.cancel(pi);
			this.finish();
		}
		else
		{
			//3600000
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 120000, pi);	
		
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void T3(View v){
		
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
	}
	
	
	
	
	
	
	
	
	
	
	
	private void T2(View v){
		
		zam = new ZoneAlertManager(getApplicationContext());
		
		if(v.getId() == R.id.quit)
		{
			zam.cancelGetLocationAsync();
		}
		else
			zam.getLocationAsync();
		
		
	}
	
	
	private void T1()
	{
		Database db =  new Database(this.getApplicationContext());
		db.open();
		db.AddLocationRow();
		db.close();
				
				
		TextView t = (TextView)findViewById(R.id.TextView01);
		t.setText("done\n\n");
		
		
		Intent i = new Intent(this, ZoneAlertService.class);		
        startService(i);
		
		/*
		if(v.getId() == R.id.network)
			getUpdates(LocationManager.NETWORK_PROVIDER);
		else
			if(v.getId() == R.id.GPS)
				getUpdates(LocationManager.GPS_PROVIDER);
			else
				removeUpdates();*/
	}
	
	
	
	
	
	
}


