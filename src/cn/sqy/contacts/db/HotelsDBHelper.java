package cn.sqy.contacts.db;

import java.util.ArrayList;

import cn.sqy.contacts.model.HotelDetailBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HotelsDBHelper {
	public static final String TABLE_HOTELS_DETAIL = "hotels";
	
	
	public static final String HOTEL_TITLE= "title";
	public static final String HOTEL_INFO_SHORT= "info_short";
	public static final String HOTEL_PRICE_SHORT= "price_short";
	public static final String HOTEL_AREA_SHORT= "area_short";
	public static final String HOTEL_URL = "hotel_url";
	public static final String HOTEL_ADDRESS = "hotel_address";
	public static final String HOTEL_PRICEINFO = "hotel_priceinfo";
	public static final String HOTEL_BEDINFO = "hotel_bedinfo";
	public static final String HOTEL_INFO = "hotel_info";
	public static final String HOTEL_SERVICE = "hotel_service";
	public static final String HOTEL_CONTACTWAY = "hotel_contactway";
	public static final String HOTEL_SMALLIMAGES = "hotel_smallimages";
	public static final String HOTEL_BIGIMAGES = "hotel_bigimages";
	public static final String HOTEL_STAR = "hotel_star"; // 1 Ï²»¶£¬0,
	
	
	public static final String CREATE_HOTEL_BASE_TABLE ="create table "+
			TABLE_HOTELS_DETAIL + "( _id integer primary key autoincrement,"+
			HOTEL_TITLE + " text ,"+
			HOTEL_INFO_SHORT + " text ,"+
			HOTEL_PRICE_SHORT + " text ,"+
			HOTEL_AREA_SHORT + " text ,"+
			HOTEL_URL + " text ,"+
			HOTEL_ADDRESS + " text ,"+
			HOTEL_PRICEINFO + " text ,"+
			HOTEL_BEDINFO + " text ,"+
			HOTEL_INFO + " text ,"+
			HOTEL_SERVICE + " text ,"+
			HOTEL_CONTACTWAY + " text ,"+
			HOTEL_STAR + " text ,"+
			HOTEL_SMALLIMAGES + " text ,"+
			HOTEL_BIGIMAGES + " text "+
			")";
	
	private Context context ;
	private DBHelper dbHelper ;
	private static HotelsDBHelper instance ;
	
	private HotelsDBHelper(Context _context){
		dbHelper =DBHelper.getInstance(_context);
		this.context = _context ;
	}
	public static HotelsDBHelper getInstance(Context _context){
		synchronized (HotelsDBHelper.class) {
			if(instance == null){
				instance = new HotelsDBHelper(_context);
			}
			return instance;
		}
	}
	
	public synchronized void addHotel(HotelDetailBean bean){
		SQLiteDatabase db = dbHelper.getSQLiteDatabase();
		ContentValues values = new ContentValues();
		values.put(HOTEL_TITLE, bean.getTitle());
		values.put(HOTEL_INFO_SHORT, bean.getInfo_short());
		values.put(HOTEL_PRICE_SHORT, bean.getPrice_short());
		values.put(HOTEL_AREA_SHORT, bean.getArea_short());
		values.put(HOTEL_URL, bean.getUrl());
		values.put(HOTEL_PRICEINFO, bean.getPriceinfo());
		values.put(HOTEL_BEDINFO, bean.getBedinfo());
		values.put(HOTEL_INFO, bean.getInfo_long());
		values.put(HOTEL_SERVICE, bean.getService());
		values.put(HOTEL_CONTACTWAY, bean.getContactway());
		values.put(HOTEL_STAR, bean.getStar());
		values.put(HOTEL_SMALLIMAGES, bean.getSmallimages());
		values.put(HOTEL_BIGIMAGES, bean.getBigimages());
		
		db.insert(HotelsDBHelper.TABLE_HOTELS_DETAIL, null, values);
		dbHelper.free();
	}
	
	public synchronized void deleteHotel(HotelDetailBean bean){
		SQLiteDatabase db = dbHelper.getSQLiteDatabase();
		db.delete(TABLE_HOTELS_DETAIL, HOTEL_URL+"='"+bean.getUrl()+"'", null);
		dbHelper.free();
	}
	
	public synchronized void updateHotel(HotelDetailBean bean){
		SQLiteDatabase db = dbHelper.getSQLiteDatabase();
		ContentValues values = new ContentValues();
		values.put(HOTEL_TITLE, bean.getTitle());
		values.put(HOTEL_INFO_SHORT, bean.getInfo_short());
		values.put(HOTEL_PRICE_SHORT, bean.getPrice_short());
		values.put(HOTEL_AREA_SHORT, bean.getArea_short());
		values.put(HOTEL_URL, bean.getUrl());
		values.put(HOTEL_PRICEINFO, bean.getPriceinfo());
		values.put(HOTEL_BEDINFO, bean.getBedinfo());
		values.put(HOTEL_INFO, bean.getInfo_long());
		values.put(HOTEL_SERVICE, bean.getService());
		values.put(HOTEL_CONTACTWAY, bean.getContactway());
		values.put(HOTEL_STAR, bean.getStar());
		values.put(HOTEL_SMALLIMAGES, bean.getSmallimages());
		values.put(HOTEL_BIGIMAGES, bean.getBigimages());
		
		db.update(TABLE_HOTELS_DETAIL, values, HOTEL_URL+"='"+bean.getUrl()+"'", null);
		dbHelper.free();
	}
	
	public synchronized HotelDetailBean getHotel(String url){
		HotelDetailBean result = null;
		SQLiteDatabase db = dbHelper.getSQLiteDatabase();
		Cursor cursor = db.rawQuery("select * from "+TABLE_HOTELS_DETAIL+" where "+HOTEL_URL+"='"+url+"'", null);
		if (cursor.moveToFirst()) {
			result = new HotelDetailBean();
			result.setAddress(cursor.getString(cursor.getColumnIndex(HOTEL_ADDRESS)));
			result.setArea_short(cursor.getString(cursor.getColumnIndex(HOTEL_AREA_SHORT)));
			result.setBedinfo(cursor.getString(cursor.getColumnIndex(HOTEL_BEDINFO)));
			result.setBigimages(cursor.getString(cursor.getColumnIndex(HOTEL_BIGIMAGES)));
			result.setContactway(cursor.getString(cursor.getColumnIndex(HOTEL_CONTACTWAY)));
			result.setInfo_long(cursor.getString(cursor.getColumnIndex(HOTEL_INFO)));
			result.setInfo_short(cursor.getString(cursor.getColumnIndex(HOTEL_INFO_SHORT)));
			result.setPrice_short(cursor.getString(cursor.getColumnIndex(HOTEL_PRICE_SHORT)));
			result.setPriceinfo(cursor.getString(cursor.getColumnIndex(HOTEL_PRICEINFO)));
			result.setService(cursor.getString(cursor.getColumnIndex(HOTEL_SERVICE)));
			result.setSmallimages(cursor.getString(cursor.getColumnIndex(HOTEL_SMALLIMAGES)));
			result.setTitle(cursor.getString(cursor.getColumnIndex(HOTEL_TITLE)));
			result.setUrl(cursor.getString(cursor.getColumnIndex(HOTEL_URL)));
			result.setStar(cursor.getString(cursor.getColumnIndex(HOTEL_STAR)));
		}
		dbHelper.free();
		return result;
	}
	
	
}
