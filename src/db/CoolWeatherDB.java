package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * 数据库版本
	 */
	public static final int DB_VERSION = 1 ;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	/**
	 * 将构造方法私有化
	 * @param context
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(
				context, 
				DB_NAME, 
				null, 
				DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	/**
	 * 创建CoolWeatherDB实例，（synchronized修饰表示在一个线程中调用时必须等这个方法执行完才能被另一个线程调用，相当于给方法加了个锁）
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/**
	 * 将Province数据插入到数据库
	 * @param province
	 */
	public void saveProvince(Province province){
		ContentValues contentValues = new ContentValues();
		contentValues.put("province_name", province.getProvinceName());
		contentValues.put("province_code", province.getProvinceCode());
		db.insert("Province", null, contentValues);
	}
	/**
	 * 从数据库获取全国所有省份信息
	 * @return
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/**
	 * 将City数据插入到数据库中
	 * @param city
	 */
	public void saveCity(City city){
		ContentValues contentValues = new ContentValues();
		contentValues.put("city_name", city.getCityName());
		contentValues.put("city_code", city.getCityCode());
		contentValues.put("province_id", city.getProvinceId());
		db.insert("City", null, contentValues);
	}
	/**
	 * 从数据库获取省份中的城市信息
	 * @return
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/**
	 * 将县数据插入到数据库中
	 * @param county
	 */
	public void saveCounty(County county){
		ContentValues contentValues = new ContentValues();
		contentValues.put("county_name", county.getCountyName());
		contentValues.put("county_code", county.getCountyCode());
		contentValues.put("city_id", county.getCityId());
		db.insert("County", null, contentValues);
	}
	/**
	 * 从数据库获取县所有数据
	 * @return
	 */
	public List<County> loadCounties(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
}
