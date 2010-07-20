package ibsta.LiveZone.test;

import ibsta.LiveZone.Data.Database;
import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import ibsta.LiveZone.Data.Model.ZoneAction;

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
	
	public void testSaveAlert()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		int id = db.saveAlert(getMockAlert());
		db.close();
		
		Assert.assertTrue(id > -1);
	}
	
	
	
	public void testGetAlertCursor()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		
		int id = db.saveAlert(getMockAlert());
		Cursor c = db.getAlertCursor(id);
		Assert.assertTrue(c.getCount() > 0);
		c.close();
		db.close();
	}
	
	public void testGetAlert()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		db.deleteDB();
		
		int id = db.saveAlert(getMockAlert());
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
		// start with no data
		db.deleteDB();
		
		//insert
		int id = db.saveAlert(getMockAlert());
		
		//check insert worked
		ProximityAlert al = db.getAlert(id);
		Assert.assertTrue(al != null);
		Assert.assertTrue(al.latitude.length() > 0);
		Assert.assertTrue(al.actions.get(0).entering_zone == 1);
		Assert.assertTrue(al.actions.get(0).plugins.get(1).activityName.length() > 0);
			
		//delete
		int effected = db.deleteAlert(id);
		
		//check delete worked
		Assert.assertTrue(effected == 1);
		al = db.getAlert(id);
		Assert.assertTrue(al == null);
		
		db.close();
	}
	
	
	public void testDeleteActionsForLocation()
	{
		Database db = new Database(this.getContext());
		
		db.open();
		// start with no data
		db.deleteDB();
		
		//insert
		int id = db.saveAlert(getMockAlert());
		db.deleteActionsForLocation(id);
		
		//check insert worked
		ProximityAlert al = db.getAlert(id);
		Assert.assertTrue(al != null);
		Assert.assertTrue(al.latitude.length() > 0);
		Assert.assertTrue(al.actions.isEmpty());
			
		db.close();
		
	}
	
	
	public void testGetAlertShallow(){
		
		Database db = new Database(this.getContext());
		
		db.open();
		
		int id = db.saveAlert(getMockAlert());
		ProximityAlert al = db.getAlertShallow(id);
		Assert.assertTrue(al != null);
		Assert.assertTrue(al.latitude.length() > 0);
		Assert.assertTrue(al.actions.isEmpty());
		
		db.close();
		
	}
	
	
	public void testGetAlertsShallow(){
		
		Database db = new Database(this.getContext());
		
		db.open();
		
		int id = db.saveAlert(getMockAlert());
		ArrayList<ProximityAlert> pal = db.getAlertsShallow();
		
		Assert.assertTrue(!pal.isEmpty());
		Assert.assertTrue(pal.get(0).latitude.length() > 0);
		
		db.close();
		
	}
	
	
	
	
	
	
	
	private ProximityAlert getMockAlert()
	{
		ArrayList<Plugin> pal = new ArrayList<Plugin>();
		pal.add(new Plugin("pn1", "an1", "lbl", null));
		pal.add(new Plugin("pn2", "an2", "lbl", null));
		
		ArrayList<ZoneAction> zal = new ArrayList<ZoneAction>();
		zal.add(new ZoneAction(1,1,pal));
		
		return new ProximityAlert("name", "lat", "lng", "10.1", zal);
	}
	
}











