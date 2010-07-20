package ibsta.LiveZone.UI;

import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Database;
import ibsta.LiveZone.Data.Preferences;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import ibsta.LiveZone.UI.Controls.AlertPanelList;
import ibsta.LiveZone.UI.Controls.AlertPanelList.OnAlertSelectedListener;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ViewAlerts extends Activity implements OnAlertSelectedListener, OnClickListener  {

	private Preferences preferences ;
	
	//Called when the activity is first created.
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewalerts);
	
		PopulateAlertList();
		AlertPanelList al = (AlertPanelList)findViewById(R.id.viewAlert_AlertList);
		al.setOnAlertSelectedListener(this);
		
		Button btn = (Button)findViewById(R.id.viewalerts_addalert);
		btn.setOnClickListener(this);
		
		preferences = new Preferences(this);
		

		//startService(new Intent(this, AlertService.class));
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
    }
	
    
    private void PopulateAlertList()
    {
    	Database db = new Database(this.getApplicationContext());
		
		db.open();
		
		ArrayList<ProximityAlert> pal = db.getAlertsShallow();
		
		AlertPanelList al = (AlertPanelList)findViewById(R.id.viewAlert_AlertList);
		al.setAlertItems(pal);
		
		db.close();
    }



	public void onAlertSelected(int alertId) {

		Toast.makeText(this.getApplicationContext(), String.valueOf(alertId), Toast.LENGTH_LONG).show();
		
		preferences.SetSelectedAlert(alertId);
		
		startAddAlertActivity();
	}



	public void onClick(View v) {
		// the add new alert button
		
		startAddAlertActivity();
	}
	
	
	private void startAddAlertActivity()
	{
		Intent i = new Intent(this, AddAlert.class);
        startActivity(i);
	}
	
	
}






