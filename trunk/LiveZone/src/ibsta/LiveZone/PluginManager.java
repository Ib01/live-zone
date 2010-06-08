package ibsta.LiveZone;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;


public class PluginManager
{
	private final Context appContext;
	public final String intentAction;
	
	public PluginManager(Context context, String _intentAction)
	{
		appContext = context;
		intentAction = _intentAction;
	}
	
	
	public ArrayList<PluginInfo> GetActivityPlugins()
	{
		return GetPlugins(true);
	}
	
	public ArrayList<PluginInfo> GetServicePlugins()
	{
		return GetPlugins(false);
	}
	
	private ArrayList<PluginInfo> GetPlugins(boolean getActivityComponent)
	{
		ArrayList<PluginInfo> list = new ArrayList<PluginInfo>();
		
		PackageManager pm = appContext.getPackageManager();
		
		List<ResolveInfo> rinfs;
		if(getActivityComponent){
			rinfs = pm.queryIntentActivities(new Intent(intentAction), 0);
		}
		else{
			rinfs = pm.queryIntentServices(new Intent(intentAction), 0);
		}
		
		for(ResolveInfo ri : rinfs)
        {
			PluginInfo plin = new PluginInfo(
					ri.activityInfo.packageName,
					ri.activityInfo.name,
					ri.activityInfo.loadLabel(pm),
					ri.activityInfo.loadIcon(pm)
					);
			
			list.add(plin);
        }
		
		return list;
	}

}













