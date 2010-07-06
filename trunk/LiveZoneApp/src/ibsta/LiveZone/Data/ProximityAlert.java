package ibsta.LiveZone.Data;

import java.util.ArrayList;

public class ProximityAlert {
	public final String latitude;
	public final String longtitude;
	public final String accuracy;
	public final ArrayList<ZoneAction> actions;
	  
	public ProximityAlert(String latitude, String longtitude, String accuracy, ArrayList<ZoneAction> actions)
	{
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.accuracy = accuracy;
		this.actions = actions;
	}
	
}
