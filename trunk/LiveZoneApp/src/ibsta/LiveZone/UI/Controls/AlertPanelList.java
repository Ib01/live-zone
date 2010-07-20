/**
 * 
 */
package ibsta.LiveZone.UI.Controls;

import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import ibsta.LiveZone.UI.Controls.PluginDialog.OnPluginSelectedListener;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author ib
 *
 */
public class AlertPanelList extends LinearLayout implements OnClickListener {

	private OnAlertSelectedListener onAlertSelectedListener = null;
	
	/**
	 * @param context
	 */
	public AlertPanelList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AlertPanelList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	
	public void setAlertItems(ArrayList<ProximityAlert> alerts){
		
		for(ProximityAlert pa : alerts){
			this.addView(getView(pa));	
		}
	}

	
	
	private View getView(ProximityAlert alert) {
		
		LayoutInflater inflater =
            (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View vw = inflater.inflate(R.layout.alertpanellistitem, null);
		
		TextView nme = (TextView) vw.findViewById(R.id.alertpanellistitem_alertName);
		nme.setText(alert.name);
		
		//tag the list that this particular add button needs to add plugins too.
		vw.setOnClickListener(this);
		vw.setTag(alert.id);
		
		return vw;
	}

	public void onClick(View v) {

		
		onAlertSelected((Integer)v.getTag());
	}
	
	
	private void onAlertSelected(int alertId){

		if(onAlertSelectedListener != null) {
			onAlertSelectedListener.onAlertSelected(alertId);
		}
	}
	
	// Allows the user to set an Listener and react to the event
	public void setOnAlertSelectedListener(OnAlertSelectedListener listener) {
		onAlertSelectedListener = listener;
	}
	
	public interface OnAlertSelectedListener {
		public abstract void onAlertSelected(int alertId);
	}
	
	
}














