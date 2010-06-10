package ibsta.LiveZone;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
		
		switch (id) {
			case DIALOG_PLUGIN_SELECT_ID:
				return new SelectPluginDialog(this);
		}
		
		return new SelectPluginDialog(this);
		 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	








			
