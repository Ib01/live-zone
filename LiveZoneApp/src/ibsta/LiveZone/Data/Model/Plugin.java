package ibsta.LiveZone.Data.Model;

import android.graphics.drawable.Drawable;

public class Plugin {
	
	public final int id; //primary key
	public final String label;
	public final Drawable icon;
	public final String packageName;
	public final String activityName;
	
	public Plugin(int id, String _packageName, String _activityName, String _label, Drawable _icon)
	{
		this.id = id;
		packageName = _packageName;
		activityName = _activityName;
		label = _label;
		icon = _icon;
	}
	
	public Plugin(String _packageName, String _activityName, String _label, Drawable _icon)
	{
		this.id = -1;
		packageName = _packageName;
		activityName = _activityName;
		label = _label;
		icon = _icon;
	}
	

}
