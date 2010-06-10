package ibsta.LiveZone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectPluginDialog extends Dialog{

	private final Activity activityContext;
	public PluginInfo SelectedPlugin;
	
	public SelectPluginDialog(Context context) {
		
		super(context);
		
		activityContext = (Activity)context;
		
		this.setContentView(R.layout.plugindialog);
		this.setTitle("Select a Live Zone plugin");
		
		PluginManager pm = new PluginManager(activityContext, "ibsta.LiveZone.ProximityAlert");
		PluginListAdapter pa = new PluginListAdapter(activityContext, pm.GetActivityPlugins());
		//PluginListAdapter pa = new PluginListAdapter(this, MockPlugins());
		
	    ListView list = (ListView) this.findViewById(R.id.pluginDialogList);   
	    list.setOnItemClickListener(pluginListItemClickListener);
	    
	    list.setAdapter(pa);
	}
	
	
	//PluginSelect
	private OnItemClickListener pluginListItemClickListener = new OnItemClickListener() {    
		
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
			Toast.makeText(
					activityContext.getApplicationContext(), (String)view.getTag(), Toast.LENGTH_SHORT).show();    
				
			//SelectedPlugin
				dismiss();
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

	
	
	
	
	
