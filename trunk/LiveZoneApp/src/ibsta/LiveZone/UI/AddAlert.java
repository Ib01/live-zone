package ibsta.LiveZone.UI;


import ibsta.LiveZone.LocationManager;
import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Database;
import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import ibsta.LiveZone.LocationManager.OnSearchCompleteListener;
import ibsta.LiveZone.UI.Controls.ActionPanelList;
import ibsta.LiveZone.UI.Controls.PluginDialog;
import ibsta.LiveZone.UI.Controls.ActionPanelList.OnPluginAddListener;
import ibsta.LiveZone.UI.Controls.PluginDialog.OnPluginSelectedListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddAlert extends Activity implements OnPluginAddListener, OnClickListener, OnPluginSelectedListener, OnSearchCompleteListener
{
	static final String PREFERENCES_FILE = "LiveZonePreferences";
	static final String PREFERENCES_ID_KEY ="alertId"; 
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	Location m_bestLocation = null;
	LocationManager locationManager;
	ActionPanelList actionPanelList;
	int alertId;
	SharedPreferences preferences;
	
	
	//Called when the activity is first created.
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.addalert);
		
		actionPanelList = (ActionPanelList)findViewById(R.id.zoneItemActionPanelList);
		actionPanelList.setOnPluginAddListener(this);
		
		locationManager = new LocationManager(this);
		locationManager.setOnSearchCompleteListener(this);
		locationManager.getUpdates();
		
		Button btn = (Button)findViewById(R.id.addActionList);
		btn.setOnClickListener(this);
		
		btn = (Button)findViewById(R.id.saveAlert);
		btn.setOnClickListener(this);
		
		
		preferences = this.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		alertId = preferences.getInt(PREFERENCES_ID_KEY, -1);
		
		if(alertId != -1)
		{
			Database database = new Database(this.getApplicationContext());
			database.open();
			actionPanelList.setActionItems(database.getAlert(alertId).actions);
			database.close();	
			
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
    	super.onResume();
    	
    	int id = SaveAlert();
    	
		SharedPreferences.Editor ed = preferences.edit();
		ed.putInt(PREFERENCES_ID_KEY, id);
		ed.commit();
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
	
	
    private int SaveAlert(){
		
		ProximityAlert alert = new ProximityAlert
		("name", "lat", "lng", "10", actionPanelList.getActionItems());
		
		Database database = new Database(this.getApplicationContext());
		
		database.open();
		int id = database.saveAlert(alert);
		database.close();
		
		return id;
	}
    
    
    
    
	
	//called when the location manager has finished searching for users current location
	public void onSearchComplete(Location location){
		
		EditText text = (EditText)findViewById(R.id.Location);
		text.setText(location.toString());
		
		locationManager.removeUpdates();
	}
	
	//All button clicks on the form routed here
	public void onClick(View v) {
		
		switch (v.getId())
		{
			case R.id.addActionList:
				actionPanelList.createAction();
				break;
			case R.id.saveAlert:
				SetAlert();
		}
		
	}
	
	private void SetAlert()
	{
		
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
	








			
