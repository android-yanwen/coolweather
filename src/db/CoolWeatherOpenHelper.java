package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
	/**
	 * Province�������
	 */
	public static final String CREATER_PROVINCE = "create table Province (" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)";
	/**
	 * City�������
	 */
	public static final String CREATER_CITY = "create table City (" +
			"id integer primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)";
	/**
	 * County�������
	 */
	public static final String CREATE_COUNTY = "create table County (" +
			"id integer primary key autoincrement," +
			"county_name text," +
			"county_code text," +
			"city_id integer)";
	/**
	 * ���캯��
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATER_PROVINCE);
		db.execSQL(CREATER_CITY);
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
