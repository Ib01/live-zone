package ibsta.LiveZone.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	static final String PREFERENCES_FILE = "LiveZonePreferences";
	static final String PREFERENCES_ID_KEY ="alertId";
	private final SharedPreferences preferences;
	
	public Preferences(Context context){
		preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
	}
	
	
	public void SetSelectedAlert(int alertid)
	{
		SharedPreferences.Editor ed = preferences.edit();
		ed.putInt(PREFERENCES_ID_KEY, alertid);
		ed.commit();
	}
	
	public int GetSelectedAlert()
	{
		return preferences.getInt(PREFERENCES_ID_KEY, -1);
	}

}
