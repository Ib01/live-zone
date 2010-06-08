package ibsta.LiveZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;


public class DBAdapter {
	
	  private SQLiteDatabase db;
	  private final Context context;
	  private myDbHelper dbHelper;
	  
	  private static final String DATABASE_NAME = "LiveZone.db";
	  private static final String DATABASE_TABLE = "IntentAction";
	  private static final int DATABASE_VERSION = 1;
	  	  
	  // Database structure
	  public static final String KEY_ID="_id";
	  public static final String KEY_ACTION="action";
	  public static final int ACTION_COLUMN = 1;
	  
	  private static final String DATABASE_CREATE = 
		  "create table " + DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_ACTION + " text not null);";
	  

	  
	  public DBAdapter(Context _context) {
	    context = _context;
	    dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	  
	  public DBAdapter open() throws SQLException {
	    db = dbHelper.getWritableDatabase();
	    return this;
	  }
	  
	  public void close() {
	      db.close();
	  }
	  
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
		  
		  /*	  
				  
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
		throw new SQLException("No to do item found for row: " +
		                  _rowIndex);
		}
		String task = cursor.getString(TASK_COLUMN);
		long created = cursor.getLong(CREATION_DATE_COLUMN);
		ToDoItem result = new ToDoItem(task, new Date(created));
		return result;
		*/
				 
	  }
	  
	  public int updateEntry(long _rowIndex, String action) {
	    
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(KEY_ACTION, action);
	    return db.update(DATABASE_TABLE, contentValues, KEY_ID + "=" + _rowIndex, null);
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
		      _db.execSQL(DATABASE_CREATE);
		    }
		    
		    // Called when there is a database version mismatch meaning that
		    // the version of the database on disk needs to be upgraded to
		    // the current version.
		    @Override
		    public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
		                           int _newVersion) {
		      // Log the version upgrade.
		      Log.w("DBAdapter", "Upgrading from version " +
		                              _oldVersion + " to " +
		                              _newVersion +
		                              ", which will destroy all old data");
		      
		      // Upgrade the existing database to conform to the new version.
		      // Multiple previous versions can be handled by comparing
		      // _oldVersion and _newVersion values.
		      // The simplest case is to drop the old table and create a
		      // new one.
		      _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		      
		      // Create a new one.
		      onCreate(_db);
		    }
		    
	  }
}
