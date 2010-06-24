package ibsta.LiveZone.UI;


import ibsta.LiveZone.LocationManager;
import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.PluginInfo;
import ibsta.LiveZone.LocationManager.OnSearchCompleteListener;
import ibsta.LiveZone.UI.Controls.ActionPanel;
import ibsta.LiveZone.UI.Controls.PluginDialog;
import ibsta.LiveZone.UI.Controls.PluginDialog.OnPluginSelectedListener;
import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ZoneItem extends Activity 
{
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	Location m_bestLocation = null;
	LocationManager locationManager;

	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoneitem);
		
		Button button = (Button)findViewById(R.id.Button01);
		button.setOnClickListener(mSetProximityClick);
		
		locationManager = new LocationManager(this);
		locationManager.setOnSearchCompleteListener(new SearchCompleteListener());
		locationManager.getUpdates();
		
	}
	
	private OnClickListener mSetProximityClick = new OnClickListener() {
		public void onClick(View v) {
			
			showDialog(DIALOG_PLUGIN_SELECT_ID);
		}
	};
	
	//called by the api when we call showDialog
	protected Dialog onCreateDialog(int id) {    
		
		PluginDialog selDlg;
		
		switch (id) {
		
			default:
				selDlg = new PluginDialog(this);
				selDlg.setOnPluginSelectedListener(new PluginSelectedListener());
				break;
		}
		
		return selDlg;
	}
	
	

	
	
	//plugin selected from plugin dialog
	private class PluginSelectedListener implements OnPluginSelectedListener{

		public void onPluginSelected(PluginInfo selectedPlugin) {

			Toast.makeText(
					getApplicationContext(), selectedPlugin.label, Toast.LENGTH_SHORT).show();    
			
			
			ActionPanel lv = (ActionPanel)findViewById(R.id.selectedPluginPanel);
			lv.AddPlugin(selectedPlugin);
		 	
		}
		
	}
	
	//location managers search has completed
	private class SearchCompleteListener implements OnSearchCompleteListener{
		public void onSearchComplete(Location location){
			
			EditText text = (EditText)findViewById(R.id.Location);
			text.setText(location.toString());
			
			locationManager.removeUpdates();
		}
	};

	
	
}
	








			
