package ibsta.LiveZone.UI.Controls;


import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.PluginInfo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectedPluginList extends LinearLayout {

	PluginListAdapter adapter;
	SelectedPluginList panel;
	
	public SelectedPluginList(Context context) {
		super(context);
		initialise(context);
	}

	public SelectedPluginList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise(context);
	}
	
	private void initialise(Context context){
		adapter = new PluginListAdapter(context); 
		panel = this;
	}
	
	public void AddPlugin(PluginInfo plugin){
		
		adapter.add(plugin);
		View v = adapter.getView(adapter.getCount() -1, null, null);
		this.addView(v);
	}
	
	public ArrayList<PluginInfo> GetPlugins(){
		ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
		
		for(int i = 0; i < adapter.getCount(); i++){
			plugins.add(adapter.getItem(i));
		}
			
		return plugins;
	}

	
	
	
	
	
	public class PluginListAdapter extends ArrayAdapter<PluginInfo> {
		
		public PluginListAdapter(Context activity) {
			super(activity, 0, new ArrayList<PluginInfo>());
		}
		
	    public PluginListAdapter(Context activity, List<PluginInfo> plugins) {
	        super(activity, 0, plugins);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        
	    	//inflate view
	    	LayoutInflater inflater =
	                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        View rowView = inflater.inflate(R.layout.selectedpluginlistitem, null);
	        PluginInfo pluginInfo = getItem(position);

	        //set the row image
	        ImageView imageView = (ImageView) rowView.findViewById(R.id.selectedPluginImage);
	        
	        if(pluginInfo.icon == null)
	        	imageView.setImageResource(R.drawable.icon); //set a default image if the plugin doesn't have one 
	        else
	        	imageView.setImageDrawable(pluginInfo.icon);
	        
	        //set the row text
	        TextView textView = (TextView) rowView.findViewById(R.id.selectedPluginTitle);
	        textView.setText(pluginInfo.label);

	        Button rem = (Button) rowView.findViewById(R.id.selectedPluginRemoveButton);
	        //rem.setTag(pluginInfo);
	        rem.setOnClickListener(new RemovePluginClickListener());
	        
	        
	        //set an id for the row view
	        rowView.setTag(pluginInfo);
	        
	        return rowView;
	        
	    }

	}
	
	
	private class RemovePluginClickListener implements OnClickListener{
		 public void onClick(View v) {
		 			 
			 PluginInfo plin = (PluginInfo)((LinearLayout)v.getParent()).getTag();
			 
			 View vw = panel.findViewWithTag(plin);
			 panel.removeView(vw);
			 
			 adapter.remove(plin);
		 }
	}
	

}












