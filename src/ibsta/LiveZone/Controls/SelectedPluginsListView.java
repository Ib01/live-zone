package ibsta.LiveZone.Controls;

import ibsta.LiveZone.PluginInfo;
import ibsta.LiveZone.PluginManager;
import ibsta.LiveZone.R;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectedPluginsListView extends ListView {
	
	public SelectedPluginsListView(Context context) {
		super(context);
		InitialiseControl(context);
	}
	
	public SelectedPluginsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		InitialiseControl(context);
	}

	public SelectedPluginsListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		InitialiseControl(context);
	}

	private void InitialiseControl(Context context)
	{
		ArrayList<String> al = new ArrayList<String>();
		al.add(context.getResources().getText(R.string.enter_intent_filter).toString());
		al.add(context.getResources().getText(R.string.exit_intent_filter).toString());
		PluginManager pm = new PluginManager(context, al);
		
		PluginListAdapter pa = new PluginListAdapter(context, pm.getPlugins());
		
		this.setAdapter(pa);
	}
	
	
	
	public class PluginListAdapter extends ArrayAdapter<PluginInfo> {
		
		private static final int LIST_ITEM_LAYOUT_ID = R.layout.pluginlistitem;
		private static final int LIST_ITEM_IMAGE_ID = R.id.pluginListItemImage;
		private static final int LIST_ITEM_TEXT_ID = R.id.pluginListItemText;
		
	    public PluginListAdapter(Context activity, List<PluginInfo> plugins) {
	        super(activity, 0, plugins);
	        
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        
	    	 LayoutInflater inflater =
	                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


	        // Inflate the views from XML
	        View rowView = inflater.inflate(LIST_ITEM_LAYOUT_ID, null);
	        PluginInfo pluginInfo = getItem(position);

	        // Load the image and set it on the ImageView
	        ImageView imageView = (ImageView) rowView.findViewById(LIST_ITEM_IMAGE_ID);
	        
	        if(pluginInfo.icon == null)
	        	imageView.setImageResource(R.drawable.icon); //set a default image if the plugin doesn't have one 
	        else
	        	imageView.setImageDrawable(pluginInfo.icon);
	        
	        // Set the text on the TextView
	        TextView textView = (TextView) rowView.findViewById(LIST_ITEM_TEXT_ID);
	        textView.setText(pluginInfo.label);

	        rowView.setTag(pluginInfo.label);
	        
	        return rowView;
	    }

	}
}
