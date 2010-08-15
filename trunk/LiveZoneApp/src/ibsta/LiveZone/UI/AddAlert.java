package ibsta.LiveZone.UI;


import ibsta.LiveZone.LocationManager;
import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Database;
import ibsta.LiveZone.Data.Preferences;
import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import ibsta.LiveZone.LocationManager.OnSearchCompleteListener;
import ibsta.LiveZone.Services.AlertManager;
import ibsta.LiveZone.UI.Controls.ActionPanelList;
import ibsta.LiveZone.UI.Controls.PluginDialog;
import ibsta.LiveZone.UI.Controls.ActionPanelList.OnPluginAddListener;
import ibsta.LiveZone.UI.Controls.PluginDialog.OnPluginSelectedListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddAlert extends Activity implements OnPluginAddListener, OnClickListener, OnPluginSelectedListener, OnSearchCompleteListener
{
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	Location m_bestLocation = null;
	LocationManager locationManager;
	ActionPanelList actionPanelList;
	Preferences preferences; 
	int alertId;
	AlertManager alertManager;
	Database database;
	
	
	//Called when the activity is first created.
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.addalert);
		
		database = new Database(this.getApplicationContext());
		locationManager = new LocationManager(this.getApplicationContext());
		alertManager = new AlertManager(getApplicationContext());
		preferences = new Preferences(this);
		
		actionPanelList = (ActionPanelList)findViewById(R.id.zoneItemActionPanelList);
		actionPanelList.setOnPluginAddListener(this);
		
		Button btn = (Button)findViewById(R.id.addActionList);
		btn.setOnClickListener(this);
		
		btn = (Button)findViewById(R.id.saveAlert);
		btn.setOnClickListener(this);
		
		alertId = preferences.GetSelectedAlert();
		
		if(alertId != -1)
			PopulateForm();
		else
		{
			locationManager.setOnSearchCompleteListener(this);
			locationManager.getUpdates();
		}
	}
	
	
	
	protected void onStart(){
		//Activity state is still available
		super.onStart();
	}
    
    protected void onRestart(){
    	//Activity state is still available
    	super.onRestart();
    }

    protected void onResume(){
    	//Activity state is still available
    	super.onResume();
    }

    protected void onPause(){
    	//save state here. This is first oportunity the sys 
    	//has of potentially killing the activity.  ie the activity could go 
    	//2 ways from here: to resume or to stop and then destroy 
    	super.onPause();
    	
    }

    protected void onStop(){
    	//activity is completely obscured. makes it even more likely it will be killed
    	super.onStop();
    }

    protected void onDestroy(){
    	//after this the activity will be destroyed.  when entering the activity again 
    	// on create will be called.  which means all state info will need to be reestablished.
    	//do any destruction here, eg ... shutting down of threads etc.
    
    	//if getting current locaton, cancel such updates
    	super.onDestroy();
    	locationManager.removeUpdates();
    }
	
    
    private void PopulateForm()
    {
    	EditText nme = (EditText)findViewById(R.id.addAlert_name);
    	EditText lat = (EditText)findViewById(R.id.addalert_latitude);
		EditText lng = (EditText)findViewById(R.id.addalert_longtitude);
		EditText rad = (EditText)findViewById(R.id.addalert_radius);
    	
		database.open();
		
		ProximityAlert pa = database.getAlert(alertId);
		
		actionPanelList.setActionItems(pa.actions);
		nme.setText(pa.name);
		lat.setText(pa.latitude);
		lng.setText(pa.longtitude);
		rad.setText(pa.radius);
		
		database.close();	
    
    }
    
    
	
    private int saveAlert(){
		
    	EditText nme = (EditText)findViewById(R.id.addAlert_name);
    	EditText lat = (EditText)findViewById(R.id.addalert_latitude);
		EditText lng = (EditText)findViewById(R.id.addalert_longtitude);
		EditText rad = (EditText)findViewById(R.id.addalert_radius);
    	
		//if adding a new alert, alertId will be -1 
		ProximityAlert alert = new ProximityAlert 
		(
				alertId,
				nme.getText().toString(), 
				lat.getText().toString(), 
				lng.getText().toString(), 
				rad.getText().toString(), 
				1,
				actionPanelList.getActionItems()
		);
		
		database.open();
		int id = database.saveAlert(alert);
		database.close();
		
		return id;
	}
    
    
    
    private void activateAlert(int alertId)
    {
    	EditText lat = (EditText)findViewById(R.id.addalert_latitude);
		EditText lng = (EditText)findViewById(R.id.addalert_longtitude);
		EditText rad = (EditText)findViewById(R.id.addalert_radius);
	
    	//am.addAlertServiceProximityAlert(149.155421, -35.239395, 100.0f);
    	
    	alertManager.addAlertServiceProximityAlert(
    			alertId,
    			Double.valueOf(lat.getText().toString()), 
    			Double.valueOf(lng.getText().toString()), 
    			Float.valueOf(rad.getText().toString())
    			);	
    }
    
    
	
	//called when the location manager has finished searching for users current location
	public void onSearchComplete(Location location){
		
		EditText lat = (EditText)findViewById(R.id.addalert_latitude);
		lat.setText(String.valueOf(location.getLatitude()));
		
		EditText lng = (EditText)findViewById(R.id.addalert_longtitude);
		lng.setText(String.valueOf(location.getLongitude()));
	}
	
	//All button clicks on the form routed here
	public void onClick(View v) {
		
		switch (v.getId())
		{
			case R.id.addActionList:
				actionPanelList.createAction();
				break;
			case R.id.saveAlert:
				if(formValid()){
					activateAlert(saveAlert());
					this.finish();
				}
				else
					Toast.makeText(getApplicationContext(), "action panel not valid", Toast.LENGTH_LONG).show();
					
		}
	}
	
	private boolean formValid()
	{
		EditText nme = (EditText)findViewById(R.id.addAlert_name);
    	EditText lat = (EditText)findViewById(R.id.addalert_latitude);
		EditText lng = (EditText)findViewById(R.id.addalert_longtitude);
		EditText rad = (EditText)findViewById(R.id.addalert_radius);
		
		return(
				actionPanelList.actionPanelIsValid() &&
				nme.getText().toString().length() > 0 &&
				lat.getText().toString().length() > 0 &&
				lng.getText().toString().length() > 0 &&
				rad.getText().toString().length() > 0
		);
		
	}
	
	
	
	
	//called when an add plugin button is clicked on inside our actionPanelList
	public void onPluginAdd() {
		
		showDialog(DIALOG_PLUGIN_SELECT_ID);
	}
	
	
	//called by the api when we call showDialog
	protected Dialog onCreateDialog(int id) {    
		
		PluginDialog selDlg;
		
		switch (id) {
		
			default:
				selDlg = new PluginDialog(this);
				selDlg.setOnPluginSelectedListener(this);
				break;
		}
		
		return selDlg;
	}
	
	//called when a plugin has been clicked on in our select plugin dialog
	public void onPluginSelected(Plugin selectedPlugin) {
		
		actionPanelList.addPluginToActionItem(selectedPlugin);
	 	
	}
	

	
}
	








			
