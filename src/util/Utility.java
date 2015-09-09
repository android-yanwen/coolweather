package util;

import model.City;
import model.County;
import model.Province;
import android.text.TextUtils;
import db.CoolWeatherDB;

public class Utility {
	/**
	 * 解析和处理服务器返回的省级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB
			, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			for(String p : allProvinces){
				String[] array = p.split("\\|");
				Province province = new Province();
				province.setProvinceCode(array[0]);
				province.setProvinceName(array[1]);
				// 将解析出来的数据储存到 Province 表
				coolWeatherDB.saveProvince(province);
			}
			return true;
		}
		return false;
	}
	/**
	 * 解析处理服务器返回的市级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB
			, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			for(String c : allCities){
				String[] array = c.split("\\|");
				City city = new City();
				city.setCityCode(array[0]);
				city.setCityName(array[1]);
				city.setProvinceId(provinceId);
				coolWeatherDB.saveCity(city);
			}
			return true;
		}
		return false;
	}
	/**
	 * 解析处理服务器返回的县级数据
	 * @param coolWeatherDB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB
			, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			for(String c : allCounties){
				String[] array = c.split("\\|");
				County county = new County();
				county.setCountyCode(array[0]);
				county.setCountyName(array[1]);
				county.setCityId(cityId);
				coolWeatherDB.saveCounty(county);
			}
			return true;
		}
		return false;
	}
}
