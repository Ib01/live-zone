package ibsta.LiveZone.UI;


import ibsta.LiveZone.LocationManager;
import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.LocationManager.OnSearchCompleteListener;
import ibsta.LiveZone.UI.Controls.ActionPanelList;
import ibsta.LiveZone.UI.Controls.PluginDialog;
import ibsta.LiveZone.UI.Controls.ActionPanelList.OnPluginAddListener;
import ibsta.LiveZone.UI.Controls.PluginDialog.OnPluginSelectedListener;
import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddAlert extends Activity implements OnPluginAddListener, OnClickListener, OnPluginSelectedListener, OnSearchCompleteListener
{
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	Location m_bestLocation = null;
	LocationManager locationManager;
	ActionPanelList actionPanelList;
	
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
		
		/*LinearLayout LL = (LinearLayout)findViewById(R.id.actionPanel);
		LL.setOnClickListener(l)*/
	}
	
	
	
	//called when the location manager has finished searching for users current location
	public void onSearchComplete(Location location){
		
		EditText text = (EditText)findViewById(R.id.Location);
		text.setText(location.toString());
		
		locationManager.removeUpdates();
	}
	
	//All button clicks on the form routed here
	public void onClick(View v) {
		
		actionPanelList.addActionPanel();
		
		actionPanelList.GetActionList();
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
	








			
