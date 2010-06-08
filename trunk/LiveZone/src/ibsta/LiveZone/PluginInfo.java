package ibsta.LiveZone;

import android.graphics.drawable.Drawable;

public class PluginInfo 
{
	public final CharSequence label;
	public final Drawable icon;
	public final String packageName;
	public final String activityName;
	
	public PluginInfo(String _packageName, String _activityName, CharSequence _label, Drawable _icon)
	{
		packageName = _packageName;
		activityName = _activityName;
		label = _label;
		icon = _icon;
	}
}
