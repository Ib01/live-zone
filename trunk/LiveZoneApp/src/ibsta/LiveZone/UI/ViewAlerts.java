package ibsta.LiveZone.UI;

import ibsta.LiveZone.AlertManager;
import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Database;
import ibsta.LiveZone.Data.Preferences;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import ibsta.LiveZone.UI.Controls.AlertPanelList;
import ibsta.LiveZone.UI.Controls.AlertPanelList.OnAlertDeleteListener;
import ibsta.LiveZone.UI.Controls.AlertPanelList.OnAlertEnabledStateChangedListener;
import ibsta.LiveZone.UI.Controls.AlertPanelList.OnAlertSelectedListener;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ViewAlerts extends Activity implements OnAlertEnabledStateChangedListener, OnAlertDeleteListener, OnAlertSelectedListener, OnClickListener  {

	private Preferences preferences ;
	private Database database; 
	private AlertManager alertManager;
	
	//Called when the activity is first created.
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewalerts);

		database = new Database(this.getApplicationContext());
		
		AlertPanelList al = (AlertPanelList)findViewById(R.id.viewAlert_AlertList);
		al.setOnAlertSelectedListener(this);
		al.setOnAlertDeleteListener(this);
		al.setOnAlertEnabledStateChangedListener(this);
		
		Button btn = (Button)findViewById(R.id.viewalerts_addalert);
		btn.setOnClickListener(this);
		
		preferences = new Preferences(this);
		alertManager = new AlertManager(this.getApplicationContext());
	}
	
	
	
	protected void onStart(){
		//Activity state is still available
		//activity is being created or is restarting after being fully hidden by another activity
		super.onStart();
		
		PopulateAlertList();
	}
    
    protected void onRestart(){
    	//Activity state is still available
    	//activity is being restarted after being fully hidden by another activity
    	super.onRestart();
    }

    protected void onResume(){
    	//Activity state is still available
    	//activity is being created or is restarting after being fully hidden by another activity 
    	//or is resuming after being partially hidden by another activity
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
    }
	
    
    private void PopulateAlertList()
    {
		database.open();
		
		ArrayList<ProximityAlert> pal = database.getAlertsShallow();
		
		AlertPanelList al = (AlertPanelList)findViewById(R.id.viewAlert_AlertList);
		al.setAlertItems(pal);
		
		database.close();
    }



	public void onAlertSelected(int alertId) {

		Toast.makeText(this.getApplicationContext(), String.valueOf(alertId), Toast.LENGTH_LONG).show();
		
		preferences.SetSelectedAlert(alertId);
		
		startAddAlertActivity();
	}

	
	public void onAlertEnabledStateChanged(int alertId, boolean enable) {

		database.open();
		
		if(enable){
			ProximityAlert alert = database.getAlert(alertId);	
			alertManager.addAlertServiceProximityAlert(alert);
		}
		else{
			alertManager.cancelAlertServiceProximityAlert(alertId);
		}
		
		
		database.updateAlertEnabledStatus(alertId, enable);
		database.close();
	}
	


	public void onClick(View v) {
		// the add new alert button
		
		//clear the selected alert to add a new one
		preferences.SetSelectedAlert(-1);
		startAddAlertActivity();
	}
	
	
	private void startAddAlertActivity()
	{
		Intent i = new Intent(this, AddAlert.class);
        startActivity(i);
	}



	public void onAlertDelete(int alertId) {
		database.open();
		alertManager.cancelAlertServiceProximityAlert(alertId);
		database.deleteAlert(alertId);
		database.close();
	}



	
	
	
}






