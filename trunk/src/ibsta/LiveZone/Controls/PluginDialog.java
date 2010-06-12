package ibsta.LiveZone.Controls;

import ibsta.LiveZone.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//Class is very tightly bound to ZoneItem. 
public class PluginDialog extends Dialog{

	//private final Activity activityContext;
	private static final String DIALOG_TITLE = "Select a Live Zone plugin";
	private static final int DIALOG_LAYOUT_ID = R.layout.plugindialog;
	private static final int DIALOG_LIST_ID = R.id.pluginDialogList;
	
	public PluginDialog(Context context)  {
		
		super(context);
		
		//inflate dialog
		this.setContentView(DIALOG_LAYOUT_ID);
		this.setTitle(DIALOG_TITLE);
		
		PluginListView list = (PluginListView) this.findViewById(DIALOG_LIST_ID);   
	    list.setOnItemClickListener(pluginListItemClickListener);
	}
	
	
	//PluginSelect
	private OnItemClickListener pluginListItemClickListener = new OnItemClickListener() {    
		
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
			Toast.makeText(
					getContext().getApplicationContext(), (String)view.getTag(), Toast.LENGTH_SHORT).show();    
				
			//SelectedPlugin
				dismiss();
			}  
		
		}; 
	
	
	

}

	
	
	
	
	
