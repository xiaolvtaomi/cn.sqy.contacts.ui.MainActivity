package cn.sqy.contacts.db;

import cn.sqy.contacts.model.HotelDetailBean;
import cn.sqy.contacts.model.ZhaopinDetailBean;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ZhaopinDBHelper {
	public static final String TABLE_ZHAOPIN_STAR = "zhaopinstar";
	
	public static final String ZP_URL = "url";
	public static final String ZP_TITLE = "title";
	public static final String ZP_ADDRESS = "address";	
	public static final String ZP_CONTENT = "content";
	
	public static final String CREATE_ZHAOPIN_TABLE ="create table "+
			TABLE_ZHAOPIN_STAR + "( _id integer primary key autoincrement,"+
			ZP_URL + " text ,"+
			ZP_TITLE + " text ,"+
			ZP_ADDRESS + " text ,"+
			ZP_CONTENT + " text "+
			")";
	
	private Context context ;
	private DBHelper dbHelper ;
	private static ZhaopinDBHelper instance ;
	
	private ZhaopinDBHelper(Context _context){
		dbHelper =DBHelper.getInstance(_context);
		this.context = _context ;
	}
	public static ZhaopinDBHelper getInstance(Context _context){
		synchronized (ZhaopinDBHelper.class) {
			if(instance == null){
				instance = new ZhaopinDBHelper(_context);
			}
			return instance;
		}
	}
	
	public synchronized void addZhaopin(ZhaopinDetailBean bean){
		SQLiteDatabase db = dbHelper.getSQLiteDatabase();
		ContentValues values = new ContentValues();
		values.put(ZP_URL, bean.getUrl());
		values.put(ZP_TITLE, bean.getTitle());
		values.put(ZP_ADDRESS, bean.getAddress());
		values.put(ZP_CONTENT, bean.getContent());
		
		db.insert(ZhaopinDBHelper.TABLE_ZHAOPIN_STAR, null, values);
		dbHelper.free();
	}
	
	public synchronized ZhaopinDetailBean getZhaopin(String url){
		ZhaopinDetailBean result = null;
		SQLiteDatabase db = dbHelper.getSQLiteDatabase();
		Cursor cursor = db.rawQuery("select * from "+TABLE_ZHAOPIN_STAR+" where "+ZP_URL+"='"+url+"'", null);
		if (cursor.moveToFirst()) {
			result = new ZhaopinDetailBean();
			result.setAddress(cursor.getString(cursor.getColumnIndex(ZP_ADDRESS)));
			result.setTitle(cursor.getString(cursor.getColumnIndex(ZP_TITLE)));
			result.setUrl(cursor.getString(cursor.getColumnIndex(ZP_URL)));
			result.setContent(cursor.getString(cursor.getColumnIndex(ZP_CONTENT)));
		}
		if(cursor!=null){
			cursor.close();
			cursor = null;
		}
		dbHelper.free();
		return result;
	}
	
	public synchronized void deleteZhaopin(ZhaopinDetailBean bean){
		SQLiteDatabase db = dbHelper.getSQLiteDatabase();
		db.delete(TABLE_ZHAOPIN_STAR, ZP_URL+"='"+bean.getUrl()+"'", null);
		dbHelper.free();
	}
	
}
