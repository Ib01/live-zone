package ibsta.LiveZone;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ZoneItem extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoneitem);
		
		
		ArrayAdapter<CharSequence> ad = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
		m_adapterForSpinner = new ArrayAdapter(this, android.R.layout.simple_spinner_item);        
		m_adapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_myDynamicSpinner.setAdapter(m_adapterForSpinner);
		m_adapterForSpinner.add(“dummy item”);
	}
		
}
