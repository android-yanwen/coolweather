package activity;


import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import util.HttpUtil;
import util.HttpUtil.HttpCallbackListener;
import util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel = 0;
	private Province selectedProvince;
	private City selectedCity;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private ProgressDialog progressDialog;
	/**
	 * �Ƿ��Ǵ�WeatherActivity����ת����
	 */
	private boolean isFromWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
			Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		adapter = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_list_item_1, 
				dataList);
		list_view = (ListView) findViewById(R.id.list_view);
		list_view.setAdapter(adapter);
		title_text = (TextView) findViewById(R.id.title_text);
		// ��ȡ���ݿ����ʵ��
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
				} else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvinces();
	}
	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�оʹӷ�������ѯ
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
			title_text.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else{
			queryFromServer(null, "province");
		}
	}
	/**
	 * ��ѯ���е��У����ȴ����ݿ��ѯ�����û�оʹӷ�������ѯ
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
	 * ��ѯ���е��أ����ȴ����ݿ��ѯ�����û�оʹӷ�������ѯ
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
				// ͨ�� runUiThread() �����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_SHORT)
						.show();
					}
				});
			}
		});
	}
	/**
	 *  ��ʾ���ȶԻ���
	 */
	private void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	/**
	 * ��д���ؼ�
	 */
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		} else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		} else {
			if(isFromWeatherActivity){
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
