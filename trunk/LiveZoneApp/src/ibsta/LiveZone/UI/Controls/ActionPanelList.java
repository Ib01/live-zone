package ibsta.LiveZone.UI.Controls;


import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.PluginInfo;
import ibsta.LiveZone.UI.Controls.PluginDialog.OnPluginSelectedListener;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	
	//add a plugin to selected plugin list next to the button the user clicked on   
	public void addPluginToActionItem(PluginInfo plugin){
		
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












