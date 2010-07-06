package ibsta.LiveZone.Data;

import android.graphics.drawable.Drawable;

public class PluginInfo 
{
	public final String label;
	public final Drawable icon;
	public final String packageName;
	public final String activityName;
	
	public PluginInfo(String _packageName, String _activityName, String _label, Drawable _icon)
	{
		packageName = _packageName;
		activityName = _activityName;
		label = _label;
		icon = _icon;
	}
}
