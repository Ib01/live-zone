package ibsta.LiveZone.Data.Model;


import java.util.ArrayList;

public class ZoneAction {

	public final int id; //primary key
	public final Integer entering_zone;
	public final Integer leaving_zone;
	public final ArrayList<Plugin> plugins;
	
	
	public ZoneAction(int id, Integer entering_zone, Integer leaving_zone, ArrayList<Plugin> plugins){
		
		this.id = id;
		this.entering_zone = entering_zone;
		this.leaving_zone = leaving_zone;
		this.plugins = plugins;
	}
	
	public ZoneAction(Integer entering_zone, Integer leaving_zone, ArrayList<Plugin> plugins){
		
		this.id = -1;
		this.entering_zone = entering_zone;
		this.leaving_zone = leaving_zone;
		this.plugins = plugins;
	}
	
}
