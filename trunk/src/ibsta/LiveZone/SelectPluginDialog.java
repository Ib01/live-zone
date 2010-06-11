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

//Class is very tightly bound to ZoneItem. 
public class SelectPluginDialog extends Dialog{

	private final Activity activityContext;
	private static final String DIALOG_TITLE = "Select a Live Zone plugin";
	private static final int DIALOG_LAYOUT_ID = R.layout.plugindialog;
	private static final int DIALOG_LIST_ID = R.id.pluginDialogList;
	
	public SelectPluginDialog(Context context)  {
		
		super(context);
		
		activityContext = (Activity)context;
		
		//inflate dialog
		this.setContentView(DIALOG_LAYOUT_ID);
		this.setTitle(DIALOG_TITLE);
		
		//get all plugins that implement our actions / intent filters. note we are only 
		//interested in plugins that implement ALL our actions 
		ArrayList<String> al = new ArrayList<String>();
		al.add(activityContext.getResources().getText(R.string.enter_intent_filter).toString());
		al.add(activityContext.getResources().getText(R.string.exit_intent_filter).toString());
		PluginManager pm = new PluginManager(activityContext, al);
		PluginListAdapter pa = new PluginListAdapter(activityContext, pm.getPlugins());
		//PluginListAdapter pa = new PluginListAdapter(activityContext, mockPlugins());
		
	    ListView list = (ListView) this.findViewById(DIALOG_LIST_ID);   
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
		
		private static final int LIST_ITEM_LAYOUT_ID = R.layout.pluginlistitem;
		private static final int LIST_ITEM_IMAGE_ID = R.id.pluginListItemImage;
		private static final int LIST_ITEM_TEXT_ID = R.id.pluginListItemText;
		
	    public PluginListAdapter(Activity activity, List<PluginInfo> plugins) {
	        super(activity, 0, plugins);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        
	    	Activity activity = (Activity) getContext();
	        LayoutInflater inflater = activity.getLayoutInflater();

	        // Inflate the views from XML
	        View rowView = inflater.inflate(LIST_ITEM_LAYOUT_ID, null);
	        PluginInfo pluginInfo = getItem(position);

	        // Load the image and set it on the ImageView
	        ImageView imageView = (ImageView) rowView.findViewById(LIST_ITEM_IMAGE_ID);
	        
	        if(pluginInfo.icon == null)
	        	imageView.setImageResource(R.drawable.icon); //set a default image if we the plugin doesn't have one 
	        else
	        	imageView.setImageDrawable(pluginInfo.icon);
	        
	        // Set the text on the TextView
	        TextView textView = (TextView) rowView.findViewById(LIST_ITEM_TEXT_ID);
	        textView.setText(pluginInfo.label);

	        rowView.setTag(pluginInfo.label);
	        
	        return rowView;
	    }

	}
	
	
	
	
	
	// DELETE THIS
	private List<PluginInfo> mockPlugins(){
		
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

	
	
	
	
	
