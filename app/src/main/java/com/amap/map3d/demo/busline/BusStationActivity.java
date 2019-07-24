package com.amap.map3d.demo.busline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.busline.BusStationSearch.OnBusStationSearchListener;
import com.amap.api.services.core.AMapException;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ToastUtil;

import java.util.ArrayList;

public class BusStationActivity extends Activity implements
		OnMarkerClickListener, InfoWindowAdapter, OnItemSelectedListener,
		OnBusStationSearchListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	private ProgressDialog progDialog = null;// 进度框
	private EditText searchName;// 输入公交线路名称
	private Spinner selectCity;// 选择城市下拉列表
	private String[] itemCitys = { "北京-010", "郑州-0371", "上海-021" };
	private String cityCode = "";// 城市区号
	private BusStationQuery busLineQuery;// 公交线路查询的查询类
	private BusStationSearch busLineSearch;// 公交线路列表查询

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.busline_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		Button searchByName = (Button) findViewById(R.id.searchbyname);
		searchByName.setText("公交站点查询");
		searchByName.setOnClickListener(this);
		selectCity = (Spinner) findViewById(R.id.cityName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, itemCitys);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectCity.setAdapter(adapter);
		selectCity.setPrompt("请选择城市：");
		selectCity.setOnItemSelectedListener(this);
		searchName = (EditText) findViewById(R.id.busName);
		searchName.setHint("请输入站点名称");
	}

	/**
	 * 设置marker的监听和信息窗口的监听
	 */
	private void setUpMap() {
		aMap.setOnMarkerClickListener(this);
		aMap.setInfoWindowAdapter(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 公交线路搜索
	 */
	public void searchLine() {
		// currentpage = 0;// 第一页默认从0开始
		showProgressDialog();
		String search = searchName.getText().toString().trim();
		if ("".equals(search)) {
			search = "望京";
			searchName.setText(search);
		}
		busLineQuery = new BusStationQuery(search, cityCode);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
//		busLineQuery.setPageSize(10);// 设置每页返回多少条数据
		// busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
		busLineSearch = new BusStationSearch(this, busLineQuery);// 设置条件
		busLineSearch.setOnBusStationSearchListener(this);// 设置查询结果的监听
		
		busLineSearch.setQuery(new BusStationQuery(search,cityCode));
		busLineSearch.searchBusStationAsyn();
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null) {
			progDialog = new ProgressDialog(this);
		}
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索:\n");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 提供一个给默认信息窗口定制内容的方法
	 */
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	/**
	 * 提供一个个性化定制信息窗口的方法
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	/**
	 * 点击marker回调函数
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;// 点击marker时把此marker显示在地图中心点
	}

	/**
	 * 选择城市
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String cityString = itemCitys[position];
		cityCode = cityString.substring(cityString.indexOf("-") + 1);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		cityCode = "010";
	}

	/**
	 * 公交站点查询结果回调
	 */
	@Override
	public void onBusStationSearched(BusStationResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getPageCount() > 0
					&& result.getBusStations() != null
					&& result.getBusStations().size() > 0) {
				ArrayList<BusStationItem> item = (ArrayList<BusStationItem>) result
						.getBusStations();
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < item.size(); i++) {
					BusStationItem stationItem = item.get(i);


					buf.append(" station: ").append(i).append(" name: ")
							.append(stationItem.getBusStationName());
					Log.d("LG", "stationName:"
							+ stationItem.getBusStationName() + "stationpos:"
							+ stationItem.getLatLonPoint().toString());
				}
				String text = buf.toString();
				Toast.makeText(BusStationActivity.this, text,
						Toast.LENGTH_SHORT).show();
			} else {
				ToastUtil.show(BusStationActivity.this, R.string.no_result);
			}
		} else  {
			ToastUtil.showerror(BusStationActivity.this, rCode);
		} 
	}

	/**
	 * 查询公交线路
	 */
	@Override
	public void onClick(View v) {
		searchLine();
	}
}
