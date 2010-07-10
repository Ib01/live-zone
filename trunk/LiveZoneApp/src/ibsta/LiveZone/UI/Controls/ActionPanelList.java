package ibsta.LiveZone.UI.Controls;


import java.util.ArrayList;

import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.Data.Model.ZoneAction;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class ActionPanelList extends LinearLayout implements OnClickListener {

	private OnPluginAddListener onPluginAddListener = null;
	private SelectedPluginList activeSelectedPluginList = null; //the selectedpluginslist we are currently adding to 
	
	public ActionPanelList(Context context) {
		super(context);
		initialise(context);
	}

	public ActionPanelList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise(context);
	}
	
	private void initialise(Context context){
		
		addActionPanel();
	}
	
	public void addActionPanel(){
		
		this.addView(getView());
	}
	
	
	public ArrayList<ZoneAction> GetActionList()
	{
		int cnt = this.getChildCount();
		ArrayList<ZoneAction> actions = new ArrayList<ZoneAction>(); 
		
		for(int i = 0; i < cnt; i++){
			
			View v = this.getChildAt(i);
			int onEnter = 0;
			int onLeave = 0;
			
			CheckBox ent = (CheckBox) v.findViewById(R.id.actionpanellistitem_onentercheckbox);
			if(ent.isChecked())
				onEnter = 1;
			
			CheckBox ext = (CheckBox) v.findViewById(R.id.actionpanellistitem_onexitcheckbox);
			if(ext.isChecked())
				onLeave = 1;
			
			SelectedPluginList plins = (SelectedPluginList) v.findViewById(R.id.actionpanellistitem_selectedPluginPanel);
					
			actions.add(
					new ZoneAction(
						onEnter, 
						onLeave,
						plins.GetPlugins()
					)
			);
			
		}
			
		return actions;
	}
	
	
	
	
	
	//add a plugin to selected plugin list next to the button the user clicked on   
	public void addPluginToActionItem(Plugin plugin){
		
		if(activeSelectedPluginList != null)
			activeSelectedPluginList.AddPlugin(plugin);
	}
	
	
	public View getView() {
		
		LayoutInflater inflater =
            (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View vw = inflater.inflate(R.layout.actionpanellistitem, null);
		
		Button btn = (Button) vw.findViewById(R.id.actionpanellistitem_addpluginbutton);
		
		//tag the list that this particular add button needs to add plugins too.
		SelectedPluginList spl = (SelectedPluginList) vw.findViewById(R.id.actionpanellistitem_selectedPluginPanel);
		btn.setTag(spl);
		
		btn.setOnClickListener(this);
		
		return vw;
	}
	
	static final int DIALOG_PLUGIN_SELECT_ID = 0;
	
	public void onClick(View v) {
		 
		activeSelectedPluginList = ((SelectedPluginList)v.getTag());
		onPluginAdd();
	 }
	
	// Allows the user to set an Listener and react to the event
	public void setOnPluginAddListener(OnPluginAddListener listener) {
		onPluginAddListener = listener;
	}
	
	private void onPluginAdd(){

		if(onPluginAddListener != null) {
			onPluginAddListener.onPluginAdd();
		}
	}
	
	public interface OnPluginAddListener {
		public abstract void onPluginAdd();
	}
	

}












