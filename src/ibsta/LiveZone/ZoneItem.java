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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ZoneItem extends Activity 
{
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	Dialog pluginDialog;
	
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
				return CreatePluginSelectDialog();
		}
		
		return CreatePluginSelectDialog();
		 
	}
	
	private Dialog CreatePluginSelectDialog(){
		
		pluginDialog = new Dialog(this);
		pluginDialog.setContentView(R.layout.plugindialog);
		pluginDialog.setTitle("Select a Live Zone plugin");
		
		PluginManager pm = new PluginManager(getApplicationContext(), "ibsta.LiveZone.ProximityAlert");
		//PluginSelectDialogListAdapter pa = new PluginSelectDialogListAdapter(this, pm.GetActivityPlugins());
		PluginListAdapter pa = new PluginListAdapter(this, MockPlugins());
		
	    ListView list = (ListView) pluginDialog.findViewById(R.id.pluginDialogList);   
	    list.setOnItemClickListener(pluginListItemClickListener);
	    
	    list.setAdapter(pa);
	    
	    return pluginDialog;
	}
	
	
	
	//PluginSelect
	private OnItemClickListener pluginListItemClickListener = new OnItemClickListener() {    
		
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
			Toast.makeText(
					getApplicationContext(), (String)view.getTag(), Toast.LENGTH_SHORT).show();    

			pluginDialog.dismiss();
			}  
		
		}; 
	
	
	
	public class PluginListAdapter extends ArrayAdapter<PluginInfo> {

	    public PluginListAdapter(Activity activity, List<PluginInfo> plugins) {
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
	        
	        if(pluginInfo.icon != null)
	        	imageView.setImageDrawable(pluginInfo.icon);

	        // Set the text on the TextView
	        TextView textView = (TextView) rowView.findViewById(R.id.pluginListItemText);
	        textView.setText(pluginInfo.label);

	        rowView.setTag(pluginInfo.label);
	        
	        return rowView;
	    }

	}

	
	
	// DELETE THIS
	private List<PluginInfo> MockPlugins(){
		
		List<PluginInfo> pl = new ArrayList<PluginInfo>(); 
		
		pl.add(new PluginInfo("","","item 1", null));
		pl.add(new PluginInfo("","","item 2", null));
		pl.add(new PluginInfo("","","item 3", null));
		pl.add(new PluginInfo("","","item 4", null));
		pl.add(new PluginInfo("","","item 5", null));
		pl.add(new PluginInfo("","","item 6", null));
		
		return pl;
	}
	
	
	
}
	








			
