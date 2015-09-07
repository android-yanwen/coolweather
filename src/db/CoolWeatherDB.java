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
	 * ���ݿ���
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * ���ݿ�汾
	 */
	public static final int DB_VERSION = 1 ;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	/**
	 * �����췽��˽�л�
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
	 * ����CoolWeatherDBʵ������synchronized���α�ʾ��һ���߳��е���ʱ������������ִ������ܱ���һ���̵߳��ã��൱�ڸ��������˸�����
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
	 * ��Province���ݲ��뵽���ݿ�
	 * @param province
	 */
	public void saveProvince(Province province){
		ContentValues contentValues = new ContentValues();
		contentValues.put("province_name", province.getProvinceName());
		contentValues.put("province_code", province.getProvinceCode());
		db.insert("Province", null, contentValues);
	}
	/**
	 * �����ݿ��ȡȫ������ʡ����Ϣ
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
	 * ��City���ݲ��뵽���ݿ���
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
	 * �����ݿ��ȡʡ���еĳ�����Ϣ
	 * @return
	 */
	public List<City> loadCity(){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/**
	 * �������ݲ��뵽���ݿ���
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
	 * �����ݿ��ȡ����������
	 * @return
	 */
	public List<County> loadCounty(){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
}