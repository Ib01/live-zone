package ibsta.LiveZone.Data;

import ibsta.LiveZone.Data.Model.Plugin;
import ibsta.LiveZone.Data.Model.ProximityAlert;
import ibsta.LiveZone.Data.Model.ZoneAction;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class Database {
	
	  private SQLiteDatabase db;
	  private final Context context;
	  private myDbHelper dbHelper;
	  
	  private static final int DATABASE_VERSION = 6;
	  private static final String DATABASE_NAME = "LiveZone.db";
	  	
	  //
	  // LOCATION TABLE
	  //
	  public static final String TABLE_LOCATION = "location";
	  public static final String COLUMN_LOCATION_ID="location_id";
	  public static final String COLUMN_NAME="name";
	  public static final String COLUMN_LATITUDE="latitude";
	  public static final String COLUMN_LONGTITUDE="longtitude";
	  public static final String COLUMN_ACCURACY="accuracy";
	  
	  private static final String SQL_CREATE_LOCATION_TABLE = 
		  "create table " 
		  + TABLE_LOCATION 
		  + " (" 
		  + COLUMN_LOCATION_ID + " integer primary key autoincrement, " 
		  + COLUMN_NAME + " text not null, "
		  + COLUMN_LATITUDE + " text not null, "
		  + COLUMN_LONGTITUDE + " text not null, "
		  + COLUMN_ACCURACY + " text not null"
		  + ");";
	  
	  public static final int INDEX_LOCATION_ID = 0;
	  public static final int INDEX_NAME = 1;
	  public static final int INDEX_LATITUDE = 2;
	  public static final int INDEX_LONGTITUDE = 3;
	  public static final int INDEX_ACCURACY = 4;
	  
	  //
	  // ACTION TABLE
	  //
	  public static final String TABLE_ACTION = "action";
	  public static final String COLUMN_ACTION_ID="action_id";
	  public static final String COLUMN_ENTERING_ZONE ="entering_zone";
	  public static final String COLUMN_LEAVING_ZONE="leaving_zone";
	  public static final String COLUMN_LOCATION_FK="location_fk";
	  
	  private static final String SQL_CREATE_ACTION_TABLE = 
		  "create table " 
		  + TABLE_ACTION 
		  + " (" 
		  + COLUMN_ACTION_ID + " integer primary key autoincrement, " 
		  + COLUMN_ENTERING_ZONE + " integer not null, "
		  + COLUMN_LEAVING_ZONE + " integer not null, "
		  + COLUMN_LOCATION_FK + " integer not null"
		  + ");";
	  
	  public static final int INDEX_ACTION_ID = 0;
	  public static final int INDEX_ENTERING_ZONE = 1;
	  public static final int INDEX_LEAVING_ZONE = 2;
	  public static final int INDEX_LOCATION_FK = 3;
	  
	  //
	  // PLUGIN TABLE
	  //
	  public static final String TABLE_PLUGIN = "plugin";
	  public static final String COLUMN_PLUGIN_ID="plugin_id";
	  public static final String COLUMN_PACKAGE_NAME="package_name";
	  public static final String COLUMN_ACTIVITY_NAME="activity_name";
	  public static final String COLUMN_LABEL="label";
	  public static final String COLUMN_ACTION_FK="action_fk";
	 
	  private static final String SQL_CREATE_PLUGIN_TABLE = 
		  "create table " 
		  + TABLE_PLUGIN 
		  + " (" 
		  + COLUMN_PLUGIN_ID + " integer primary key autoincrement, " 
		  + COLUMN_PACKAGE_NAME + " text not null, "
		  + COLUMN_ACTIVITY_NAME + " text not null, "
		  + COLUMN_LABEL + " text not null, "
		  + COLUMN_ACTION_FK + " integer not null"
		  + ");";
	  
	  public static final int INDEX_PLUGIN_ID = 0;
	  public static final int INDEX_PACKAGE_NAME = 1;
	  public static final int INDEX_ACTIVITY_NAME = 2;
	  public static final int INDEX_LABEL = 3;
	  public static final int INDEX_ACTION_FK = 4;
	  
	  //
	  //SQL STATEMENTS
	  //
	  private static final String SQL_GET_ALERT =
		  "select * from " 
		  + TABLE_LOCATION 
		  + " join " + TABLE_ACTION + " on " + COLUMN_LOCATION_ID + "=" + COLUMN_LOCATION_FK
		  + " join " + TABLE_PLUGIN + " on " + COLUMN_ACTION_ID + "=" + COLUMN_ACTION_FK
		  + " where " + COLUMN_LOCATION_ID + "=?";
	  
	  private static final String SQL_GET_LOCATION = 
		  "select * from " + TABLE_LOCATION + " where " + COLUMN_LOCATION_ID + "=?";
	  
	  private static final String SQL_GET_ACTION_FOR_LOCATION = 
		  "select * from " + TABLE_ACTION + " where " + COLUMN_LOCATION_FK + "=?";
	  
	  private static final String SQL_GET_PLUGIN_FOR_ACTION = 
		  "select * from " + TABLE_PLUGIN + " where " + COLUMN_ACTION_FK + "=?";
	  
	  private static final String SQL_GET_ALERTS_SHALLOW = 
		  "select * from " + TABLE_LOCATION;
	  
	  //
	  //TRIGGERS
	  //
	  public static final String TRIG_LOCATION_DELETE = "delete_"+ TABLE_LOCATION;
	  
	  private static final String SQL_LOCATION_DELETE =
	  "CREATE TRIGGER ["+TRIG_LOCATION_DELETE+"]" +
	  " BEFORE DELETE" +
	  " ON [" + TABLE_LOCATION + "]" +
	  " FOR EACH ROW" +
	  " BEGIN" +
	  " DELETE FROM " + TABLE_ACTION + " WHERE " + TABLE_ACTION + "." + COLUMN_LOCATION_FK + " = old." + COLUMN_LOCATION_ID + ";" +
	  " END";
	  
	  public static final String TRIG_ACTION_DELETE = "delete_"+ TABLE_ACTION;
	  
	  private static final String SQL_ACTION_DELETE =
	  "CREATE TRIGGER ["+TRIG_ACTION_DELETE+"]" +
	  " BEFORE DELETE" +
	  " ON [" + TABLE_ACTION + "]" +
	  " FOR EACH ROW" +
	  " BEGIN" +
	  " DELETE FROM " + TABLE_PLUGIN + " WHERE " + TABLE_PLUGIN + "." + COLUMN_ACTION_FK + " = old." + COLUMN_ACTION_ID + ";" +
	  " END";
	  
	  
	  
	  //CONSTRUCTORS
	  
	  public Database(Context _context, int dbVersion) {
		  context = _context;
		  dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	  
	  public Database(Context _context) {
		  context = _context;
		  dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	  
	  public Database open() throws SQLException {
		  db = dbHelper.getWritableDatabase();
		  return this;
	  }
	  
	  
	  
	  //
	  // PUBLIC METHODS /////////////////////////////////////////////////////////
	  //
	  
	  public boolean isOpen() {
		  return db.isOpen();
	  }
		  
	  public void close() {
		  db.close();
	  }
	  
	  
	  
	  public int saveAlert(ProximityAlert alert) {
		  
		  //NOTE: SHOULD PROBABLY BE USING TRANSACTIONS HERE
		  
		  //if we are updating an existing alert keep the 
		  //location parent but delete all child records
		  int locId;
		  if(alert.id == -1){
			  locId = AddLocationRow(alert);
			  if(locId < 0)
				  return locId; //error occured
		  }
		  else{
			  UpdateLocationRow(alert);
			  deleteActionsForLocation(alert.id);
			  locId = alert.id;
		  }
		  
		  for(ZoneAction za : alert.actions)
		  {
			  int actId = AddActionRow(za,locId);  
			  if(actId < 0)
				  return actId; //error occured
			  
			  for(Plugin pi : za.plugins)
			  {
				  int pinId = AddPluginRow(pi, actId);
				  if(pinId < 0)
					  return pinId; //error occured
			  }
		  }
		
		  return locId;
	  }
	  
	  /**
		 * Delete an alert. 
		 * 
		 * @param alertId. id of the item to delete
		 * @return returns the number of rows effected. Note: if delete 
		 * succeeds this will be one. internally however more than 1 recorded 
		 * will be deleted 
		 */
	  public int deleteAlert(int alertId) {
		  
		  String[] params = {((Integer)alertId).toString()};
		  
		  //delete operation useses cascade delete triggers so we don't have 
		  //to do anything other than delete the location. this will also delete 
		  //any attached actions and plugins
		  return db.delete(TABLE_LOCATION, COLUMN_LOCATION_ID + "=?" , params);
	  }
	  
	  
	  public int deleteActionsForLocation(int locationId) {
		  
		  String[] params = {((Integer)locationId).toString()};
		  
		  //delete operation useses cascade delete trigger so we don't have 
		  //to do anything other than delete the action.  this will also delet 
		  //any attached plugins
		  return db.delete(TABLE_ACTION, COLUMN_LOCATION_FK + "=?" , params);
	  }
	  
	  
	 /**
		 * get alert as a cursor
		 * 
		 * @param alertId. id of the item to get
		 * @return a cursor
		 */
	  public Cursor getAlertCursor(int alertId) {
		  
		  String[] params = {((Integer)alertId).toString()};
		  return db.rawQuery(SQL_GET_ALERT, params);
	  }
	 
	 /**
		 * get alert as an alert object
		 * 
		 * @param alertId. id of the item to get
		 * @return a ProximityAlert object.  this will be null 
		 * if no alert was found
		 */
	  public ProximityAlert getAlert(int alertId) {
		  
		  return getLocation(alertId);
	  }
	  
	  /**
		 * get alert as an alert object. get alert record without child 
		 * action or plugin data 
		 * 
		 * @param alertId. id of the item to get
		 * @return a ProximityAlert object.  this will be null 
		 * if no alert was found
		 */
	  public ProximityAlert getAlertShallow(int alertId) {
		  
		  return getLocation(alertId, true);
	  }
	  
	  public ArrayList<ProximityAlert> getAlertsShallow() {
		  
		  ArrayList<ProximityAlert> pal = new ArrayList<ProximityAlert>();
		  Cursor cursor = db.rawQuery(SQL_GET_ALERTS_SHALLOW, null);
		  
		  if(cursor.moveToFirst())
		  {
			do {
				  pal.add(
						new ProximityAlert(
						cursor.getInt(INDEX_LOCATION_ID),
						cursor.getString(INDEX_NAME),
						cursor.getString(INDEX_LATITUDE),
						cursor.getString(INDEX_LONGTITUDE),
						cursor.getString(INDEX_ACCURACY),
						new ArrayList<ZoneAction>())
				  );
		  	} while(cursor.moveToNext());
			  
		  }
		  cursor.close();
			
		  return pal;
	  }
	  
	  
	  
	  //just for testing
	  public void deleteDB() {
		  
		  //returns rows effected
		  db.delete(TABLE_PLUGIN, null, null);
		  db.delete(TABLE_ACTION, null, null);
		  db.delete(TABLE_LOCATION, null, null);
	  }
	  
	  
	  //
	  // PRIVATE METHODS /////////////////////////////////////////////////////////
	  //
	  
	  private Cursor getLocationCursor(int locationId) {
		  String[] params = {((Integer)locationId).toString()};
		  return db.rawQuery(SQL_GET_LOCATION, params);
	  }
	  
	  private ProximityAlert getLocation(int locationId) {
		  return getLocation(locationId, false);
	  }
	  
	  private ProximityAlert getLocation(int locationId, boolean getShallow) {
		  Cursor c = getLocationCursor(locationId);
		  ProximityAlert pa = null;
		  
		  if(c.moveToFirst()){
			  
			  ArrayList<ZoneAction> za = new ArrayList<ZoneAction>();
			  if(!getShallow)
				  za = getActionsForLocation(c.getInt(INDEX_LOCATION_ID));
				  
			  pa = new ProximityAlert(
					c.getInt(INDEX_LOCATION_ID),
					c.getString(INDEX_NAME),
					c.getString(INDEX_LATITUDE),
					c.getString(INDEX_LONGTITUDE),
					c.getString(INDEX_ACCURACY),
					za); //could be dodgy: multiple cursors open at same time
		  }
		  c.close();
			
		  return pa;
	  }
	  
	
	  
	  
	  private Cursor getActionsForLocationCursor(int locationId) {
		  String[] params = {((Integer)locationId).toString()};
		  return db.rawQuery(SQL_GET_ACTION_FOR_LOCATION, params);
	  }
	  private ArrayList<ZoneAction> getActionsForLocation(int locationId) {
		  Cursor c = getActionsForLocationCursor(locationId);
		  ArrayList<ZoneAction> za = new ArrayList<ZoneAction>();
		  
		  if (c.moveToFirst()) {
				do {
						za.add(
							new ZoneAction(
								c.getInt(INDEX_ACTION_ID),
								c.getInt(INDEX_ENTERING_ZONE),
								c.getInt(INDEX_LEAVING_ZONE),
								getPluginsForAction(c.getInt(INDEX_ACTION_ID))
							)
						);
					
				} while(c.moveToNext());
			}
		  
		  c.close();
		  
		  return za;
	  }
	  
	  
	  private Cursor getPluginsForActionCursor(int actionId) {
		  String[] params = {((Integer)actionId).toString()};
		  return db.rawQuery(SQL_GET_PLUGIN_FOR_ACTION, params);
	  }
	  private ArrayList<Plugin> getPluginsForAction(int actionId) {
		  
		  Cursor c = getPluginsForActionCursor(actionId);
		  ArrayList<Plugin> pi = new ArrayList<Plugin>();
		  
		  if (c.moveToFirst()) {
				do {
					pi.add(
							new Plugin(
								c.getInt(INDEX_PLUGIN_ID),
								c.getString(INDEX_PACKAGE_NAME),
								c.getString(INDEX_ACTIVITY_NAME),
								c.getString(INDEX_LABEL),
								null
							)
						);
					
				} while(c.moveToNext());
			}
		  
		  c.close();
		  
		  return pi;
	  }
	  
	  
	 
	  private int UpdateLocationRow(ProximityAlert alert)
	  {
		  ContentValues contentValues = new ContentValues();
		  contentValues.put(COLUMN_NAME, alert.name);
		  contentValues.put(COLUMN_LATITUDE, alert.latitude);
		  contentValues.put(COLUMN_LONGTITUDE, alert.longtitude);
		  contentValues.put(COLUMN_ACCURACY, alert.area);
		  
		  //return (int)db.insert(TABLE_LOCATION, null, contentValues);
		  
		  String[] params = {String.valueOf(alert.id)};
		  return db.update(TABLE_LOCATION, contentValues, COLUMN_LOCATION_ID +"=?", params);
	  }
	  
	  
	  private int AddLocationRow(ProximityAlert alert)
	  {
		  ContentValues contentValues = new ContentValues();
		  contentValues.put(COLUMN_NAME, alert.name);
		  contentValues.put(COLUMN_LATITUDE, alert.latitude);
		  contentValues.put(COLUMN_LONGTITUDE, alert.longtitude);
		  contentValues.put(COLUMN_ACCURACY, alert.area);
		  
		  return (int)db.insert(TABLE_LOCATION, null, contentValues);
	  }
	  
	  
	  private int AddActionRow(ZoneAction action, int locationId)
	  {
		  ContentValues contentValues = new ContentValues();
		  contentValues.put(COLUMN_ENTERING_ZONE, action.entering_zone);
		  contentValues.put(COLUMN_LEAVING_ZONE, action.leaving_zone);
		  contentValues.put(COLUMN_LOCATION_FK, locationId);
		  
		  return (int)db.insert(TABLE_ACTION, null, contentValues);
	  }
	  
	  
	  private int AddPluginRow(Plugin plugin, int actionId)
	  {
		  ContentValues contentValues = new ContentValues();
		  contentValues.put(COLUMN_PACKAGE_NAME, plugin.packageName);
		  contentValues.put(COLUMN_ACTIVITY_NAME, plugin.activityName);
		  contentValues.put(COLUMN_LABEL, plugin.label);
		  contentValues.put(COLUMN_ACTION_FK, actionId);
		  
		  return (int)db.insert(TABLE_PLUGIN, null, contentValues);
	  }
	  
	  
	  
	  
	  
	  
	  private static class myDbHelper extends SQLiteOpenHelper {
		  
		  	public myDbHelper(Context context, String name,
	                          CursorFactory factory, int version) {
	
		  		super(context, name, factory, version);
		    }
		  	
		    // Called when no database exists in
		    // disk and the helper class needs
		    // to create a new one.
		    @Override
		    public void onCreate(SQLiteDatabase _db) {
		      
				_db.execSQL(SQL_CREATE_LOCATION_TABLE);
				_db.execSQL(SQL_CREATE_ACTION_TABLE);
				_db.execSQL(SQL_CREATE_PLUGIN_TABLE);
				
				//create cascade delete triggers
				_db.execSQL(SQL_ACTION_DELETE);
				_db.execSQL(SQL_LOCATION_DELETE);
		    }
		    
		    
		    
		    // Called when there is a database version mismatch meaning that
		    // the version of the database on disk needs to be upgraded to
		    // the current version.
		    @Override
		    public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
		                           int _newVersion) {
		      
		      _db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLUGIN);
		      _db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION);
		      _db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		      
		      //drop cascade delete triggers
		      _db.execSQL("DROP TRIGGER IF EXISTS " + TRIG_ACTION_DELETE);
		      _db.execSQL("DROP TRIGGER IF EXISTS " + TRIG_LOCATION_DELETE);
		      
		      // Create a new db.
		      onCreate(_db);
		    }
		    
	  }
	  
	    
	  
	  
        
		 
} 
	  
	  
	  
	  /* Cursor cursor = db.query(
	  true, 
	  DATABASE_TABLE,
      new String[] {KEY_ID, KEY_ACTION},
      KEY_ID + "=" + _rowIndex,
      null, null, null, null, null);

cursor.moveToFirst();
return cursor.getString(ACTION_COLUMN);

	  
	  
if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
throw new SQLException("No to do item found for row: " +
              _rowIndex);
}
String task = cursor.getString(TASK_COLUMN);
long created = cursor.getLong(CREATION_DATE_COLUMN);
ToDoItem result = new ToDoItem(task, new Date(created));
return result;*/
	  
	  
	  
	  
	 /* private static final String DATABASE_TABLE = "IntentAction";
	  
	  	  
	  // Database structure
	  public static final String KEY_ID="_id";
	  public static final String KEY_ACTION="action";
	  public static final int ACTION_COLUMN = 1;
	  
	  private static final String DATABASE_CREATE = 
		  "create table " + DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_ACTION + " text not null);";
	  

	  
	 
	  
	  
	  
	  public long insertEntry(String action) {
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(KEY_ACTION, action);
	    return db.insert(DATABASE_TABLE, null, contentValues);
	  }
	 
	  public boolean removeEntry(long _rowIndex) {
	    return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	  }
	  
	  public Cursor getAllEntries () {
	    return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_ACTION},
	                    null, null, null, null, null);
	  }
	  
	  public String getEntry(long _rowIndex) {
		  
		  Cursor cursor = db.query(
				  true, 
				  DATABASE_TABLE,
                  new String[] {KEY_ID, KEY_ACTION},
                  KEY_ID + "=" + _rowIndex,
                  null, null, null, null, null);
		  
		  cursor.moveToFirst();
		  return cursor.getString(ACTION_COLUMN);
		  
		  	  
				  
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
		throw new SQLException("No to do item found for row: " +
		                  _rowIndex);
		}
		String task = cursor.getString(TASK_COLUMN);
		long created = cursor.getLong(CREATION_DATE_COLUMN);
		ToDoItem result = new ToDoItem(task, new Date(created));
		return result;
		
				 
	  }
	  
	  public int updateEntry(long _rowIndex, String action) {
	    
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(KEY_ACTION, action);
	    return db.update(DATABASE_TABLE, contentValues, KEY_ID + "=" + _rowIndex, null);
	  }
*/

