package ibsta.LiveZone.UI;

import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.PluginInfo;
import ibsta.LiveZone.UI.Controls.PluginDialog;
import ibsta.LiveZone.UI.Controls.SelectedPluginListView;
import ibsta.LiveZone.UI.Controls.PluginDialog.OnPluginSelectedListener;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ZoneItem extends Activity 
{
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	//Dialog pluginDialog;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoneitem);
		
		Button button = (Button)findViewById(R.id.Button01);
		button.setOnClickListener(mSetProximityClick);
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
	
	

	
	
	
	private class PluginSelectedListener implements OnPluginSelectedListener{

		public void onPluginSelected(PluginInfo selectedPlugin) {

			Toast.makeText(
					getApplicationContext(), selectedPlugin.label, Toast.LENGTH_SHORT).show();    
			
			
			SelectedPluginListView lv = (SelectedPluginListView)findViewById(R.id.selectedPluginListView);
			lv.AddPlugin(selectedPlugin); 
			

			
			
		}
		
	}
	
	

	
	
}
	








			
