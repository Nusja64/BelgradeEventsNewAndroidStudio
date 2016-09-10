	package com.example.nikola.belgradeevents;
	
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.nikola.belgradeevents.model.Event;
import com.example.nikola.belgradeevents.model.Location;
import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

	
	public class BrokerSQLite {
	
	private static final int _dbVersion =  2 ;
	private static final String _dbName ="BelgradEvents.db";
	private static final String _tblEvent ="[Event]";
	private static final String _tblLocation ="[Location]";
	private static final String _tblTag ="[Tag]";
	
	public NumberFormat baseFormat = NumberFormat.getInstance(Locale.ENGLISH);
	
	private DbHelper _dbHelper;
	private final Context _dbContext;
	private static Context context;
	private SQLiteDatabase _dbDatabase=null;
	
	
	/*creating db*/
	private static class DbHelper extends SQLiteOpenHelper{
	
		public DbHelper(Context context) {
			super(context, _dbName, null, _dbVersion);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) throws SQLException{	
			/****** Script for SelectTopNRows command from SSMS  ******/
			//TODO
			
			
			db.execSQL(" CREATE TABLE " + _tblEvent + " (" +
					" [EventID] INTEGER," +				
					" [name] TEXT," +
					" [starting_at] TIMESTAP," +
					" [ending_at] TIMESTAP," +
					" [is_trending] INTEGER)" );
			
			db.execSQL(" CREATE TABLE " + _tblLocation + " (" +
					" [EventID] INTEGER," +				
					" [lng] NUMERIC," +
					" [lat] NUMERIC)" );
			
			db.execSQL(" CREATE TABLE " + _tblTag + " (" +
					" [EventID] INTEGER," +				
					" [tagName] TEXT)" );

		}
				
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
			deleteApplicationData(context);
		}
		
	}
	
	private static void deleteApplicationData(Context context)
	{
		File cache = context.getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists())
		{
			String[] children = appDir.list();
			for (String s : children)
			{
				if (!s.equals("lib"))
				{
					deleteDir(new File(appDir, s));
				}
			}
		}
	}
	
	public static boolean deleteDir(File dir)
	{
		if (dir != null && dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	
	public void firstInit() throws SQLException {
		if(_dbDatabase==null)
		{
			 DbHelper local = new DbHelper(_dbContext);
			 local.getWritableDatabase();
			 local = new DbHelper(_dbContext);
			 local.getWritableDatabase();
		}	
	}
	
	public BrokerSQLite(Context c){
		_dbContext = c;
		context = c;
		baseFormat.setMaximumFractionDigits(2);
		baseFormat.setMinimumFractionDigits(2);
	}
	

	public BrokerSQLite open() throws SQLException {
		if(_dbDatabase==null)
		{
			_dbHelper = new DbHelper(_dbContext);
			_dbDatabase = _dbHelper.getWritableDatabase();
		}
		else if(!_dbDatabase.isOpen())
		{
			_dbDatabase = _dbHelper.getWritableDatabase();
		}
		
		
		return this;
	}
	
	public void close(){
		_dbDatabase.close();	
	}
	
	
	public void Event_I(Event event)  throws Exception{
		ContentValues cv = new ContentValues();
		cv.put("EventID", event.getEventID());
		cv.put("name", event.getName());
		cv.put("starting_at", event.getStarting_at());
		cv.put("ending_at", event.getEnding_at());
		cv.put("is_trending", event.Is_trending());
		_dbDatabase.insert(_tblEvent, null, cv);
		Tag_I_U(event);
	}
	
	public void Location_I(Event event)  throws Exception{
		ContentValues cv = new ContentValues();
		cv.put("EventID", event.getEventID());
		cv.put("lng", event.getLocation().getLng());
		cv.put("lat", event.getLocation().getLat());
		
		_dbDatabase.insert(_tblLocation, null, cv);
	}
	
	public void Tag_I_U(Event event)  throws Exception{
		
		ContentValues cv = new ContentValues();
		ArrayList<String> tags = new ArrayList<String>();
		tags  = (ArrayList<String>) event.getTags();
		
		for (String tag : tags) {
			cv.put("tagName", tag);
			cv.put("EventID", event.getEventID());
			_dbDatabase.insert(_tblTag, null, cv);
			
		}
	}
		public void Delete_Event(Event event)  throws Exception{	
			
			_dbDatabase.delete(_tblEvent, "EventID= "+event.getEventID(), null);
			_dbDatabase.delete(_tblLocation, "EventID= "+event.getEventID(), null);
			_dbDatabase.delete(_tblTag, "EventID= "+event.getEventID(), null);
			
		}
		
public ArrayList<Event> select_event()  throws Exception{	
			
	 ArrayList<Event> list = new ArrayList<Event>();
	 String sql = " SELECT EventID,name,starting_at,ending_at,is_trending " +
			 		" FROM " + _tblEvent;
			 
	Cursor c = _dbDatabase.rawQuery(sql, null);
	for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
		Event event = new Event();
		
		event.setEventID(c.getInt(0));
		event.setName(c.getString(1));
		event.setStarting_at(c.getString(2));
		event.setEnding_at(c.getString(3));
		event.setIs_trending(c.getInt(4));
		
		//seting location
				
				Location location = new Location();
				location = selectLocation(event.getEventID());
				event.setLocation(location);
	    //seting tags		 
				 ArrayList<String> tags = new ArrayList<String>();
				 tags  = selectTags(event.getEventID());
				 event.setTags(tags);
				 list.add(event);
		
	}
	

	
	return list;
	

}

	public Location selectLocation(int eventID){
		
		 Location location = new Location();
		 String sqlLocation = " SELECT EventID,lng,lat" +
			 		" FROM " + _tblLocation +" WHERE EventID = "+eventID;
		 
		 Cursor cloc = _dbDatabase.rawQuery(sqlLocation, null);
		 for(cloc.moveToFirst();!cloc.isAfterLast();cloc.moveToNext())
		 {
			 	location.setLat(cloc.getDouble(2));
			 	location.setLng(cloc.getDouble(1));
			 
		 }
	
	
		 return location;
	
	}
	
	public int getEventID(double lng, double lat){
		int ID = 0;
		 
		 String sql = " SELECT EventID" +
			 		  " FROM " + _tblLocation +" WHERE lng= "+lng+" AND lat ="+lat;
		 
		 Cursor c = _dbDatabase.rawQuery(sql, null);
		 for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
		 {
			 	ID = c.getInt(0);
			 
		 }
	
	
		 return ID;
	
		
	}
	
public ArrayList<String> selectTags(int eventID){
	
		ArrayList<String> tags = new ArrayList<String>();
		
		String sqlTag = " SELECT EventID,tagName " +
		 		" FROM " + _tblTag +" WHERE EventID = "+eventID;
		Cursor cTag = _dbDatabase.rawQuery(sqlTag, null);
	 
		for(cTag.moveToFirst();!cTag.isAfterLast();cTag.moveToNext())
		{
		 	  String tag = "";	
		 	  tag = cTag.getString(1);
		 	  tags.add(tag);
		 
		}
	
	
		 return tags;
	
	}
		
}