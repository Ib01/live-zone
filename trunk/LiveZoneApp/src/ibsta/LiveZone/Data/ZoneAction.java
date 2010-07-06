package ibsta.LiveZone.Data;

import java.util.ArrayList;

public class ZoneAction {

	public final Integer entering_zone;
	public final Integer leaving_zone;
	public final ArrayList<PluginInfo> plugins;
	
	public ZoneAction(Integer entering_zone, Integer leaving_zone, ArrayList<PluginInfo> plugins)
	{
		this.entering_zone = entering_zone;
		this.leaving_zone = leaving_zone;
		this.plugins = plugins;
	}
}
