package ibsta.LiveZone.Data.Model;

import java.util.ArrayList;

public class ProximityAlert {
	
	public final int id; //primary key
	public final String name;
	public final String latitude;
	public final String longtitude;
	public final String accuracy;
	public final ArrayList<ZoneAction> actions;
	  
	public ProximityAlert(int id, String name, String latitude, String longtitude, String accuracy, ArrayList<ZoneAction> actions){
		
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.accuracy = accuracy;
		this.actions = actions;
	}
	
	public ProximityAlert(String name, String latitude, String longtitude, String accuracy, ArrayList<ZoneAction> actions){
		
		this.id = -1;
		this.name = name;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.accuracy = accuracy;
		this.actions = actions;
	}
}
