package ibsta.LiveZone.Data.Model;

import java.util.ArrayList;

public class ProximityAlert {
	
	public final int id; //primary key
	public final String latitude;
	public final String longtitude;
	public final String accuracy;
	public final ArrayList<ZoneAction> actions;
	  
	public ProximityAlert(int id, String latitude, String longtitude, String accuracy, ArrayList<ZoneAction> actions){
		
		this.id = id;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.accuracy = accuracy;
		this.actions = actions;
	}
	
	public ProximityAlert(String latitude, String longtitude, String accuracy, ArrayList<ZoneAction> actions){
		
		this.id = -1;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.accuracy = accuracy;
		this.actions = actions;
	}
}
