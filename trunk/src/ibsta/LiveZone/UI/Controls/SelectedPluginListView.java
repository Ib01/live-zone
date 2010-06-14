package ibsta.LiveZone.UI.Controls;

import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.PluginInfo;
import ibsta.LiveZone.Data.PluginManager;

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

public class SelectedPluginListView extends ListView {
	
	public SelectedPluginListView(Context context) {
		super(context);
		InitialiseControl(context);
	}
	
	public SelectedPluginListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		InitialiseControl(context);
	}

	public SelectedPluginListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		InitialiseControl(context);
	}

	private void InitialiseControl(Context context)
	{
		
		//start with empty data?
		PluginListAdapter pa = new PluginListAdapter(context, new ArrayList<PluginInfo>());
		this.setAdapter(pa);
	}
	
	public void AddPlugin(PluginInfo plugin)
	{
		((PluginListAdapter)this.getAdapter()).add(plugin);
	}
	
	
	
	public class PluginListAdapter extends ArrayAdapter<PluginInfo> {
		
		private static final int LIST_ITEM_LAYOUT_ID = R.layout.selectedpluginlistitem;
		private static final int LIST_ITEM_IMAGE_ID = R.id.pluginListItemImage;
		private static final int LIST_ITEM_TEXT_ID = R.id.pluginListItemText;
		
	    public PluginListAdapter(Context activity, List<PluginInfo> plugins) {
	        super(activity, 0, plugins);
	        
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        
	    	 LayoutInflater inflater =
	                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        View rowView = inflater.inflate(LIST_ITEM_LAYOUT_ID, null);
	        PluginInfo pluginInfo = getItem(position);

	        ImageView imageView = (ImageView) rowView.findViewById(LIST_ITEM_IMAGE_ID);
	        
	        if(pluginInfo.icon == null)
	        	imageView.setImageResource(R.drawable.icon); //set a default image if the plugin doesn't have one 
	        else
	        	imageView.setImageDrawable(pluginInfo.icon);
	        
	        TextView textView = (TextView) rowView.findViewById(LIST_ITEM_TEXT_ID);
	        textView.setText(pluginInfo.label);

	        rowView.setTag(pluginInfo);
	        
	        return rowView;
	        
	        
	    }

	}
	
}
