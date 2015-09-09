package activity;


import java.util.ArrayList;
import java.util.List;

import util.HttpUtil;
import util.HttpUtil.HttpCallbackListener;
import util.Utility;

import model.City;
import model.County;
import model.Province;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.coolweather.R;

import db.CoolWeatherDB;

public class ChooseAreaActivity extends Activity{
	private List<String> dataList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private ListView list_view;
	private TextView title_text;
	private CoolWeatherDB coolWeatherDB;
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel = 0;
	private Province selectedProvince;
	private City selectedCity;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		adapter = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_list_item_1, 
				dataList);
		list_view = (ListView) findViewById(R.id.list_view);
		list_view.setAdapter(adapter);
		title_text = (TextView) findViewById(R.id.title_text);
		// 获取数据库操作实例
		coolWeatherDB = CoolWeatherDB.getInstance(getApplicationContext());
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int index,
					long position) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
//					Log.d("TAG", "index:" + index + "," + selectedProvince.getProvinceName());
					queryCities();
				} else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}
			}
		});
		queryProvinces();
	}
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有就从服务器查询
	 */
	private void queryProvinces(){
		provinceList = coolWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else{
			queryFromServer(null, "province");
		}
	}
	/**
	 * 查询所有的市，优先从数据库查询，如果没有就从服务器查询
	 */
	private void queryCities(){
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City c : cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	/**
	 * 查询所有的县，有限从数据库查询，如果没有就从服务器查询
	 */
	private void queryCounties(){
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	private void queryFromServer(final String code, final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(coolWeatherDB, response);
				} else if("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				} else if("county".equals(type)){
					result = Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							} else if("city".equals(type)){
								queryCities();
							} else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// 通过 runUiThread() 方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT)
						.show();
					}
				});
			}
		});
	}
	/**
	 *  显示进度对话框
	 */
	private void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	/**
	 * 重写返回键
	 */
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		} else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		} else {
			finish();
		}
	}
}
