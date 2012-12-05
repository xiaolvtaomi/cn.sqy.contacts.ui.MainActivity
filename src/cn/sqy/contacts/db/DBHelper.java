package cn.sqy.contacts.db;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {
	private Context context;
	public static DBHelper singleton = null;
	private static final String DATABASE_NAME = "hotels_detail.db";
	private static final String TAG = DBHelper.class.getCanonicalName();
	private static int VERSION = 1;
	
	private MySQLiteOpenHelper helper = null;
	private SQLiteDatabase db = null;
	
	private BlockingQueue<SQLiteDatabase> dbCache = new ArrayBlockingQueue<SQLiteDatabase>(1);
	
	public DBHelper(Context _context){
		this.context = _context;
	}
	
	
	public static DBHelper getInstance(Context context) {
		if (singleton == null) {
			synchronized (DBHelper.class) {
				if (singleton == null) {
					singleton = new DBHelper(context);
				}
			}
		}
		return singleton;
	}
	
	public void open(){
		Log.i(TAG,"open(),Thread=<"+Thread.currentThread().getId()+">"+Thread.currentThread().getName());
		helper = new MySQLiteOpenHelper(context);
		try {
			db = helper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = helper.getReadableDatabase();
		}
		dbCache.add(db);
	}
	
	/**
	 *
	 * @return db
	 */
	public SQLiteDatabase getSQLiteDatabase() {
		try {
			StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
			String info = stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()+"("+stackTraceElement.getLineNumber()+")";
//			Log.w(TAG,"getSQLiteDatabase(),Thread="+Thread.currentThread().getName()+","+info);
			SQLiteDatabase tempDb = dbCache.poll(5L, TimeUnit.SECONDS);
			return tempDb != null ? tempDb : db;
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e(TAG,"Take SQLiteDatabase object error",e);
			return db;
		}
	}
	
	public void free(){
		try {
			StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
			String info = stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()+"("+stackTraceElement.getLineNumber()+")";
//			Log.w(TAG,"free(),Thread="+Thread.currentThread().getName()+","+info);
			if(dbCache.size()>0){
				dbCache.clear();
			}
			dbCache.offer(db, 5L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.e(TAG,"Put SQLiteDatabase object error",e);
			e.printStackTrace();
		}
	}

	public void close() {
		helper.close();
		dbCache.clear();
	}
	
	class MySQLiteOpenHelper extends SQLiteOpenHelper{
		
		public MySQLiteOpenHelper(Context _context){
			super(_context, DATABASE_NAME, null, VERSION);
		}

		public MySQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.beginTransaction();
			try{
				db.execSQL(HotelsDBHelper.CREATE_HOTEL_BASE_TABLE);
				db.execSQL(ZhaopinDBHelper.CREATE_ZHAOPIN_TABLE);
			}catch(Exception e){
				e.printStackTrace();
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			Log.i(TAG,"oncreate(),Thread=<"+Thread.currentThread().getId()+">"+Thread.currentThread().getName());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists "+ HotelsDBHelper.TABLE_HOTELS_DETAIL);
			db.execSQL("drop table if exists "+ ZhaopinDBHelper.TABLE_ZHAOPIN_STAR);
			onCreate(db);
		}
	}
}
