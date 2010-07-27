/**
 * 
 */
package ibsta.LiveZone.UI.Controls;

import ibsta.LiveZone.R;
import ibsta.LiveZone.Data.Model.ProximityAlert;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author ib
 *
 */
public class AlertPanelList extends LinearLayout implements OnClickListener {

	private OnAlertSelectedListener onAlertSelectedListener = null;
	private OnAlertDeleteListener onAlertDeleteListener = null;
	
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
		
		this.removeAllViews();
		
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
		
		Button dab = (Button) vw.findViewById(R.id.alertPanelListItem_deleteAlertButton);
		dab.setOnClickListener(this);
		dab.setTag(alert.id);
		
		//tag the list that this particular add button needs to add plugins too.
		vw.setOnClickListener(this);
		vw.setTag(alert.id);
		
		return vw;
	}

	public void onClick(View v) {
		
		int id = (Integer)v.getTag();
		
		switch (v.getId())
		{
			case R.id.alertPanelListItem_deleteAlertButton:
				onAlertDelete(id);
				RemoveAlertFromPanel(id);
				break;
			default:
				onAlertSelected(id);
		}
		
		
	}
	
	
	private void RemoveAlertFromPanel(int id)
	{
		View vw = this.findViewWithTag(id);
		this.removeView(vw);
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
	
	
	private void onAlertDelete(int alertId){

		if(onAlertDeleteListener != null) {
			onAlertDeleteListener.onAlertDelete(alertId);
		}
	}
	// Allows the user to set an Listener and react to the event
	public void setOnAlertDeleteListener(OnAlertDeleteListener listener) {
		onAlertDeleteListener = listener;
	}
	
	public interface OnAlertDeleteListener {
		public abstract void onAlertDelete(int alertId);
	}
	
	
	
	
	
}














