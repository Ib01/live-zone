package ibsta.LiveZone.UI;

import ibsta.LiveZone.R;
import ibsta.LiveZone.ZoneManger;
import ibsta.LiveZone.Data.PluginInfo;
import ibsta.LiveZone.Data.PluginManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class LiveZone extends Activity {
	
	private LocationManager m_LocationManager;
	Location m_bestLocation = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	//show layout
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //UI event handlers
        Button button = (Button)findViewById(R.id.ProximityAlert);
	    button.setOnClickListener(mSetProximityClick);
	    
	    
	   /* ArrayList<String> al = new ArrayList<String>();
		al.add(this.getResources().getText(R.string.enter_intent_filter).toString());
		al.add(this.getResources().getText(R.string.exit_intent_filter).toString());
		PluginManager pm = new PluginManager(this, al);
	    PluginListAdapter pa = new PluginListAdapter(this, pm.getPlugins());
	    
	    ListView lv = (ListView)findViewById(R.id.ListView01);
	    lv.setAdapter(pa);
	    */
	    
        
	    //set up location objects
        m_LocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        
       
//        GetCurrentLocation();
        
    /*    
		List<String> providers = m_LocationManager.getAllProviders();
		TextView txtView = (TextView) findViewById(R.id.AvailableProviders);
		txtView.setText(String.format("providers: %s", providers.toString()));
		
		TextView txt = (TextView) findViewById(R.id.Latitude);m_LocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		txt.setText("... Acquiring your current location");
		
		txt = (TextView) findViewById(R.id.Longtitude);
		txt.setText("... Acquiring your current location");
		*/
		
		
         
    }
    
   
    
    
    
    private OnClickListener mSetProximityClick = new OnClickListener() {
	    public void onClick(View v) {
	    	
	    	PluginManager pm = new PluginManager(getApplicationContext(), "ibsta.LiveZone.ProximityAlert");
	    	ArrayList<PluginInfo> plins = pm.getActivityPlugins();
	    	
	    	
	    	ZoneManger zm = new ZoneManger(getApplicationContext());
	    	zm.addProximityAlert("ibsta.LiveZone.ProximityAlert", plins.get(0), 149.155421, -35.239395, 100.0f);
	    
	    	TextView txtView = (TextView) findViewById(R.id.AvailableProviders);
			txtView.setText(String.format("%d", plins.size()));
	    	
	    	
	    	/*
	    	DBAdapter da = new DBAdapter(getApplicationContext());
	    	da.open();
	    	da.insertEntry("test");
	    	String p = da.getEntry(1);
	    	
	    	TextView txtView = (TextView) findViewById(R.id.AvailableProviders);
			txtView.setText(p);*/
	    	
			
	/*		Intent i = new Intent("ibsta.LiveZone.ProximityAlert");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setComponent(new ComponentName("ibsta.Activity2Test","ibsta.Activity2Test.Activity2Test"));
			
			getApplicationContext().startActivity(i);
			*/
			
	    	//SetProximityAlert();
	    	
	    }
	};
	
	
	 /*public class ProximityIntentReceiver extends BroadcastReceiver{
	    	int activateCount = 0;
	    	
			@Override
			public void onReceive(Context context, Intent intent) {
				
				boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
				

				String disp = String.format("Entering: %s. num times received: %d", String.valueOf(entering), ++activateCount);
				
				TextView txtView = (TextView) findViewById(R.id.AvailableProviders);
				txtView.setText(disp);
				
				Toast.makeText(getApplicationContext(), disp, Toast.LENGTH_LONG).show();
			}
	    	
	    }
    */
	 
	 
	 
	 
	 
	 
	 
	 
    /*
	 public void GetCurrentLocation(){
	        //get best provider
	        Criteria crit = new Criteria();
	        crit.setAccuracy(Criteria.ACCURACY_FINE);
	        String prov = m_LocationManager.getBestProvider(crit, true);
	        
	        if(prov == null)
	        {
	        	Toast.makeText(getApplicationContext(), "NO location providers available", Toast.LENGTH_LONG).show();	
	        	return;
	        }
	        	
	        
	        //get location with best provider. COULD THROW ERROR?
	        LocationListener locListener = new LocationReceiver();
	        m_LocationManager.requestLocationUpdates(prov, 0, 0, locListener);
	    }
*/
    
	
   /*
   * && Location and Proximity Listeners  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
   * */
	
   /* public class LocationReceiver implements LocationListener {

    	int m_attempts = 0;
    	int m_maxAttempts = 10;
    	float m_targetAccuracy = 50;
    	
    	public void onLocationChanged(Location location) {
    		
    		//store the most accurate location found so far
    		if(m_bestLocation == null || location.getAccuracy() < m_bestLocation.getAccuracy())
    			m_bestLocation = location;
    		
    		//terminate location fix when we are within acceptable parameters
    		if(m_bestLocation.getAccuracy() <= m_targetAccuracy || m_attempts++ >= m_maxAttempts){
    			
    			
    			TextView txt = (TextView) findViewById(R.id.Latitude);
        		txt.setText(String.format("%f", m_bestLocation.getLatitude()));
        		
        		txt = (TextView) findViewById(R.id.Longtitude);
        		txt.setText(String.format("%f", m_bestLocation.getLongitude()));
        		
        		
        		txt = (TextView) findViewById(R.id.Provider);
        		txt.setText(String.format("%s", m_bestLocation.getProvider()));
        		
        		
        		txt = (TextView) findViewById(R.id.Accuracy);
        		txt.setText(String.format("%.2f. attampts: %d",  m_bestLocation.getAccuracy(), m_attempts));
        		
        		
        		m_LocationManager.removeUpdates(this);	
    		}
    		
    	}
    	
    	public void onProviderDisabled(String provider) {
    	}
    	public void onProviderEnabled(String provider) {
    	}
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}

    } // end LocationHandler
    
    
    */
   
    
}



/*
ProximityIntentReceiver proximityIntentReceiver;

proximityIntentReceiver = new ProximityIntentReceiver();
 
IntentFilter intentFilter = new IntentFilter("LiveZone_ProximityAlert");
registerReceiver(proximityIntentReceiver, intentFilter);
*/
//NotificationManager
//MessageHandler
/*
 * Toast.makeText(getApplicationContext()
		debug tools
		logcat
		
		traceview
		Debug.startMethodTracing()		
		
		heirarchy viewer*/
		
		





