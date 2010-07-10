package ibsta.LiveZone.UI.Controls;

import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Model.Plugin;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

//Class is very tightly bound to ZoneItem. 
public class PluginDialog extends Dialog{

	//private final Activity activityContext;
	private static final String DIALOG_TITLE = "Select a Live Zone plugin";
	private static final int DIALOG_LAYOUT_ID = R.layout.plugindialog;
	private static final int DIALOG_LIST_ID = R.id.pluginDialogList;
	private OnPluginSelectedListener onPluginSelectedListener = null;
	
	public PluginDialog(Context context)  {
		
		super(context);
		
		//inflate dialog
		this.setContentView(DIALOG_LAYOUT_ID);
		this.setTitle(DIALOG_TITLE);
		
		PluginList list = (PluginList) this.findViewById(DIALOG_LIST_ID);   
	    list.setOnItemClickListener(pluginListItemClickListener);
	}
	
	
	//PluginSelect
	private OnItemClickListener pluginListItemClickListener = new OnItemClickListener() {    
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
				onPluginSelected((Plugin)view.getTag());				
				dismiss();
		}
		
	}; 
	
	
	
	private void onPluginSelected(Plugin selectedPlugin){

		if(onPluginSelectedListener != null) {
			onPluginSelectedListener.onPluginSelected(selectedPlugin);
		}
	}
	
	// Allows the user to set an Listener and react to the event
	public void setOnPluginSelectedListener(OnPluginSelectedListener listener) {
		onPluginSelectedListener = listener;
	}
	
	public interface OnPluginSelectedListener {
		public abstract void onPluginSelected(Plugin selectedPlugin);
	}
	

}










	
	
	
	
	
