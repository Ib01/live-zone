package ibsta.LiveZone.Data;


import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * @author ib
 * 
 * manages addin information.  gets information on services and  activities that implement 
 * all of the intent actions provided to the constructor 
 */
public class PluginManager
{
	private static final int ACTIVITY_COMPONENT_TYPE = 0;
	private static final int SERVICE_COMPONENT_TYPE = 1;
	private final PackageManager packageManager;
	private final Context appContext;
	private final ArrayList<String> _intentActions;
	
	public PluginManager(Context context, ArrayList<String> intentActions)
	{
		appContext = context;
		packageManager = appContext.getPackageManager();
		_intentActions = intentActions;
	}
	
	public PluginManager(Context context, String intentAction)
	{
		appContext = context;
		packageManager = appContext.getPackageManager();
		_intentActions = new ArrayList<String>();
		_intentActions.add(intentAction);
	}
	
	public ArrayList<PluginInfo> getActivityPlugins()
	{
		return parseQueryResults(queryPackageManager(ACTIVITY_COMPONENT_TYPE));
	}
	
	public ArrayList<PluginInfo> getServicePlugins()
	{
		return parseQueryResults(queryPackageManager(SERVICE_COMPONENT_TYPE));
	}
	
	public ArrayList<PluginInfo> getPlugins()
	{
		return mockPlugins();
		
		/*List<ResolveInfo> rinfs = queryPackageManager(ACTIVITY_COMPONENT_TYPE);
		rinfs.addAll(queryPackageManager(SERVICE_COMPONENT_TYPE));
		
		return parseQueryResults(rinfs);*/
	}
	
	
	
	
	
	/**
	 * Get info on services and activities that have intent-filters for all of our actions.
	 * note: if an activity or service has a filter for one activity but not for another it will
	 * be excluded from the returned list
	 * @return  
	 */
	private List<ResolveInfo> queryPackageManager(int componentType)
	{
		List<ResolveInfo> rinf = new ArrayList<ResolveInfo>();
		
		for(String action :_intentActions)
		{
			if(rinf.isEmpty())
				rinf =  getResolveInfos(componentType, action);
			else{
				// we only want items common to both lists
				rinf =  retainValueEqualListItems(rinf, getResolveInfos(componentType, action));
			}
		}
			
		return rinf;
	}
	
	
	private List<ResolveInfo> getResolveInfos(int componentType, String action)
	{
		switch(componentType)
		{
			case ACTIVITY_COMPONENT_TYPE:
				return packageManager.queryIntentActivities(new Intent(action), 0);
			case SERVICE_COMPONENT_TYPE:
				return packageManager.queryIntentServices(new Intent(action), 0);
			default: 
				return packageManager.queryIntentServices(new Intent(action), 0);
		}
		
	}
	
	private ArrayList<PluginInfo> parseQueryResults(List<ResolveInfo> resolveInfos){
		
		return parseQueryResults(new ArrayList<PluginInfo>(), resolveInfos);
	}
	
	private ArrayList<PluginInfo> parseQueryResults(ArrayList<PluginInfo> pluginList, List<ResolveInfo> resolveInfos)
	{
		
		for(ResolveInfo ri : resolveInfos)
        {			
			PluginInfo plin = new PluginInfo(
					ri.activityInfo.packageName,
					ri.activityInfo.name,
					ri.activityInfo.loadLabel(packageManager).toString(),
					ri.activityInfo.loadIcon(packageManager)
					);
			
			pluginList.add(plin);
        }
		
		return pluginList;
	}
	
	
	//retains all items in retainList that are also in compare list
	private List<ResolveInfo> retainValueEqualListItems(List<ResolveInfo> retainList, List<ResolveInfo> compareList)
	{
		List<ResolveInfo> removeList = new ArrayList<ResolveInfo>(); 
		
		for(ResolveInfo rli : retainList){
			boolean itemFound = false;
			
			for(ResolveInfo cli : compareList){
				
				if(
					rli.activityInfo.packageName.equals(cli.activityInfo.packageName) && 
					rli.activityInfo.name.equals(cli.activityInfo.name) &&
					rli.activityInfo.loadLabel(packageManager).toString().equals(cli.activityInfo.loadLabel(packageManager).toString())){
					
					itemFound = true;
					break;
				}
			}
			
			if(itemFound == false)
				removeList.add(rli);
		}
		
		
		for(ResolveInfo rmli : removeList){
			retainList.remove(rmli);
		}
		
		return retainList;
	}
	
	
	
	

	// DELETE THIS
	private ArrayList<PluginInfo> mockPlugins(){
		
		ArrayList<PluginInfo> pl = new ArrayList<PluginInfo>(); 
		
		pl.add(new PluginInfo("","","item 1", null));
		pl.add(new PluginInfo("","","item 2", null));
		pl.add(new PluginInfo("","","item 3", null));
		pl.add(new PluginInfo("","","item 4", null));
		pl.add(new PluginInfo("","","item 5", null));
		pl.add(new PluginInfo("","","item 6", null));
		pl.add(new PluginInfo("","","item 7", null));
		pl.add(new PluginInfo("","","item 8", null));
		pl.add(new PluginInfo("","","item 9", null));
		pl.add(new PluginInfo("","","item 10", null));
		pl.add(new PluginInfo("","","item 11", null));
		pl.add(new PluginInfo("","","item 12", null));
		
		return pl;
	}
	
	

}













