package ibsta.LiveZone;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
	
	
	
	private OnClickListener mSetProximityClick = new OnClickListener() {
		public void onClick(View v) {
			showDialog(DIALOG_PLUGIN_SELECT_ID);
		}
	};
	
	
	protected Dialog onCreateDialog(int id) {    
		
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.plugindialog);
		dialog.setTitle("Custom Dialog");
		
		PopulatePluginDialog(dialog);
		
		return dialog; 
	}
	
	private void PopulatePluginDialog(Dialog dialog)
	{
		
		// Bound in XML  ListView, As the container for the Item     
	    ListView list = (ListView) dialog.findViewById(R.id.pluginDialogList);   
	    
	    PluginManager pm = new PluginManager(getApplicationContext(), "ibsta.LiveZone.ProximityAlert");
    	ArrayList<PluginInfo> plins = pm.GetActivityPlugins();
    	
	    PluginDialogListAdapter pa = new PluginDialogListAdapter(this, plins);
	    
	    
	    list.setAdapter(pa);
	}
	
	
	
	public class PluginDialogListAdapter extends ArrayAdapter<PluginInfo> {

	    public PluginDialogListAdapter(Activity activity, List<PluginInfo> plugins) {
	        super(activity, 0, plugins);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        Activity activity = (Activity) getContext();
	        LayoutInflater inflater = activity.getLayoutInflater();

	        // Inflate the views from XML
	        View rowView = inflater.inflate(R.layout.pluginlistitem, null);
	        PluginInfo pluginInfo = getItem(position);

	        // Load the image and set it on the ImageView
	        ImageView imageView = (ImageView) rowView.findViewById(R.id.pluginListItemImage);
	        imageView.setImageDrawable(pluginInfo.icon);

	        // Set the text on the TextView
	        TextView textView = (TextView) rowView.findViewById(R.id.pluginListItemText);
	        textView.setText(pluginInfo.label);

	        return rowView;
	    }

	}

	
	
}
	








			
