package ibsta.LiveZone.test;

import ibsta.LiveZone.Data.Database;
import ibsta.LiveZone.Data.PluginInfo;
import ibsta.LiveZone.Data.ProximityAlert;
import ibsta.LiveZone.Data.ZoneAction;

import java.util.ArrayList;

import junit.framework.Assert;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class DatabaseTests extends AndroidTestCase {
	
	public DatabaseTests() 
	{
	}
	
	public void testCreation()
	{
		Database db = new Database(this.getContext());
		Assert.assertNotNull(db);
	}
	
	public void testOpening()
	{
		Database db = new Database(this.getContext());
		
		//may throw an error here?
		db.open();
		Assert.assertTrue(db.isOpen());
		db.close();
	}
	
	public void testClosing()
	{
		Database db = new Database(this.getContext());
		//throw an error here?
		db.open();
		db.close();
		Assert.assertFalse(db.isOpen());
	}
	
	public void testInsertAlert()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		int id = db.insertAlert(getMockAlert());
		db.close();
		
		Assert.assertTrue(id > -1);
	}
	
	private ProximityAlert getMockAlert()
	{
		ArrayList<PluginInfo> pal = new ArrayList<PluginInfo>();
		pal.add(new PluginInfo("pn1", "an1", "lbl", null));
		pal.add(new PluginInfo("pn2", "an2", "lbl", null));
		
		ArrayList<ZoneAction> zal = new ArrayList<ZoneAction>();
		zal.add(new ZoneAction(1,1,pal));
		
		return new ProximityAlert("lat", "lng", "10.1", zal);
	}
	
	
	public void testGetAlertCursor()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		
		int id = db.insertAlert(getMockAlert());
		Cursor c = db.getAlertCursor(id);
		Assert.assertTrue(c.getCount() > 0);
		c.close();
		db.close();
	}
	
	public void testGetAlert()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		
		int id = db.insertAlert(getMockAlert());
		ProximityAlert al = db.getAlert(id);
		
		Assert.assertTrue(al.latitude.length() > 0);
		Assert.assertTrue(al.actions.get(0).entering_zone == 1);
		Assert.assertTrue(al.actions.get(0).plugins.get(1).activityName.length() > 0);
		
		db.close();
	}
	
	
	public void testDeleteAlert()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		
		int id = db.insertAlert(getMockAlert());
		int effected = db.deleteAlert(id);
		
		Assert.assertTrue(effected == 1);
		
		db.close();
	}
	
	

}











