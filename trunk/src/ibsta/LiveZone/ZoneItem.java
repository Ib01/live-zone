package ibsta.LiveZone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ZoneItem extends Activity 
{
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoneitem);
		
		Button button = (Button)findViewById(R.id.Button01);
	    button.setOnClickListener(mSetProximityClick);
	}
	
	protected Dialog onCreateDialog(int id) {    
		
		Dialog dialog = null;    
		switch(id) {
		
			case DIALOG_PLUGIN_SELECT_ID:
				dialog = new Dialog(getApplicationContext());
				dialog.setContentView(R.layout.plugindialog);
				dialog.setTitle("Custom Dialog");
				TextView text = (TextView) dialog.findViewById(R.id.pdEditText);
				text.setText("Hello, this is a custom dialog!");
			break;        
		}    
		
		return dialog;
	}
	
			
}
