package com.amap.map3d.demo.district;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearch.OnDistrictSearchListener;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 政区划查询
 */
public class DistrictActivity extends Activity implements
		OnDistrictSearchListener, OnItemSelectedListener {

	public static final String COUNTRY = "country"; // 行政区划，国家级

	public static final String PROVINCE = "province"; // 行政区划，省级

	public static final String CITY = "city"; // 行政区划，市级

	public static final String DISTRICT = "district"; // 行政区划，区级

	public static final String BUSINESS = "biz_area"; // 行政区划，商圈级

	//当前选中的级别
	private String selectedLevel = COUNTRY;
	
	// 当前行政区划
	private DistrictItem currentDistrictItem = null;

	// 下级行政区划集合
	private Map<String, List<DistrictItem>> subDistrictMap = new HashMap<String, List<DistrictItem>>();

	// 省级列表
	private List<DistrictItem> provinceList = new ArrayList<DistrictItem>();

	// 市级列表
	private List<DistrictItem> cityList = new ArrayList<DistrictItem>();

	// 区县级列表
	private List<DistrictItem> districtList = new ArrayList<DistrictItem>();

	// 是否已经初始化
	private boolean isInit = false;

	private Spinner spinnerProvince;
	private Spinner spinnerCity;
	private Spinner spinnerDistrict;
	private TextView tv_countryInfo = null;
	private TextView tv_provinceInfo = null;
	private TextView tv_cityInfo = null;
	private TextView tv_districtInfo = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.district_activity);

		tv_countryInfo = (TextView) findViewById(R.id.tv_countryInfo);
		tv_provinceInfo = (TextView) findViewById(R.id.tv_provinceInfo);
		tv_cityInfo = (TextView) findViewById(R.id.tv_cityInfo);
		tv_districtInfo = (TextView) findViewById(R.id.tv_districtInfo);

		spinnerProvince = (Spinner) findViewById(R.id.spinner_province);
		spinnerCity = (Spinner) findViewById(R.id.spinner_city);
		spinnerDistrict = (Spinner) findViewById(R.id.spinner_district);

		spinnerProvince.setOnItemSelectedListener(this);
		spinnerCity.setOnItemSelectedListener(this);
		spinnerDistrict.setOnItemSelectedListener(this);

		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 返回District（行政区划）异步处理的结果
	 */
	@Override
	public void onDistrictSearched(DistrictResult result) {
		List<DistrictItem> subDistrictList  = null;
		if (result != null) {
			if (result.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {

				List<DistrictItem> district = result.getDistrict();

				if (!isInit) {
					isInit = true;
					currentDistrictItem = district.get(0);
					tv_countryInfo.setText(getDistrictInfoStr(currentDistrictItem));
				}

				// 将查询得到的区划的下级区划写入缓存
				for (int i = 0; i < district.size(); i++) {
					DistrictItem districtItem = district.get(i);
					subDistrictMap.put(districtItem.getAdcode(),
							districtItem.getSubDistrict());
				}
				// 获取当前区划的下级区划列表
				subDistrictList = subDistrictMap
						.get(currentDistrictItem.getAdcode());
			} else {
				ToastUtil.showerror(this, result.getAMapException().getErrorCode());
			}
		}
		setSpinnerView(subDistrictList);
	}

	/**
	 * 获取行政区划的信息字符串
	 * @param districtItem
	 * @return
	 */
	private String getDistrictInfoStr(DistrictItem districtItem){
		StringBuffer sb = new StringBuffer();
		String name = districtItem.getName();
		String adcode = districtItem.getAdcode();
		String level = districtItem.getLevel();
		String citycode = districtItem.getCitycode();
		LatLonPoint center = districtItem.getCenter();
		sb.append("区划名称:" + name + "\n");
		sb.append("区域编码:" + adcode + "\n");
		sb.append("城市编码:" + citycode + "\n");
		sb.append("区划级别:" + level + "\n");
		if (null != center) {
			sb.append("经纬度:(" + center.getLongitude() + ", "
					+ center.getLatitude() + ")\n");
		}
		return sb.toString();
	}
	
	// 设置spinner视图
	private void setSpinnerView(List<DistrictItem> subDistrictList) {
		List<String> nameList = new ArrayList<String>();
		if(subDistrictList != null && subDistrictList.size() > 0){
			for (int i = 0; i < subDistrictList.size(); i++) {
				nameList.add(subDistrictList.get(i).getName());
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, nameList);
			
			if (selectedLevel.equalsIgnoreCase(COUNTRY)) {
				provinceList = subDistrictList;
				spinnerProvince.setAdapter(adapter);
			}

			if (selectedLevel
					.equalsIgnoreCase(PROVINCE)) {
				cityList = subDistrictList;
				spinnerCity.setAdapter(adapter);
			}

			if (selectedLevel.equalsIgnoreCase(CITY)) {
				districtList = subDistrictList;
				//如果没有区县，将区县说明置空
				if(null == nameList || nameList.size() <= 0){
					tv_districtInfo.setText("");
				}
				spinnerDistrict.setAdapter(adapter);
			}
		} else {
			List<String> emptyNameList = new ArrayList<String>();
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, emptyNameList);
			if (selectedLevel.equalsIgnoreCase(COUNTRY)) {
				
				spinnerProvince.setAdapter(emptyAdapter);
				spinnerCity.setAdapter(emptyAdapter);
				spinnerDistrict.setAdapter(emptyAdapter);
				tv_provinceInfo.setText("");
				tv_cityInfo.setText("");
				tv_districtInfo.setText("");
			}

			if (selectedLevel
					.equalsIgnoreCase(PROVINCE)) {
			 
				spinnerCity.setAdapter(emptyAdapter);
				spinnerDistrict.setAdapter(emptyAdapter);
				tv_cityInfo.setText("");
				tv_districtInfo.setText("");
			}
			
			if (selectedLevel
					.equalsIgnoreCase(CITY)) {
				spinnerDistrict.setAdapter(emptyAdapter);
				tv_districtInfo.setText("");
			}
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置行政区划查询监听
		DistrictSearch districtSearch = new DistrictSearch(this);
		districtSearch.setOnDistrictSearchListener(this);
		// 查询中国的区划
		DistrictSearchQuery query = new DistrictSearchQuery();
		query.setKeywords("中国");
		districtSearch.setQuery(query);
		// 异步查询行政区
		districtSearch.searchDistrictAsyn();

	}

	/**
	 * 查询下级区划
	 * 
	 * @param districtItem
	 *            要查询的区划对象
	 */
	private void querySubDistrict(DistrictItem districtItem) {
		DistrictSearch districtSearch = new DistrictSearch(DistrictActivity.this);
		districtSearch.setOnDistrictSearchListener(DistrictActivity.this);
		// 异步查询行政区
		DistrictSearchQuery query = new DistrictSearchQuery();
		query.setKeywords(districtItem.getName());
		districtSearch.setQuery(query);
		districtSearch.searchDistrictAsyn();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		DistrictItem districtItem = null;
		switch (parent.getId()) {
		case R.id.spinner_province:
			districtItem = provinceList.get(position);
			selectedLevel = PROVINCE;
			tv_provinceInfo.setText(getDistrictInfoStr(districtItem));
			break;
		case R.id.spinner_city:
			selectedLevel = CITY;
			districtItem = cityList.get(position);
			tv_cityInfo.setText(getDistrictInfoStr(districtItem));
			break;
		case R.id.spinner_district:
			selectedLevel = DISTRICT;
			districtItem = districtList.get(position);
			tv_districtInfo.setText(getDistrictInfoStr(districtItem));
			break;
		default:
			break;
		}

		if (districtItem != null) {
			currentDistrictItem = districtItem;
			// 先查缓存如果缓存存在则直接从缓存中查找，无需再执行查询请求
			List<DistrictItem> cache = subDistrictMap.get(districtItem
					.getAdcode());
			if (null != cache) {
				setSpinnerView(cache);
			} else {
				querySubDistrict(districtItem);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
