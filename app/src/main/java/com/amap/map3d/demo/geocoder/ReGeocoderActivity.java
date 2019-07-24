package com.amap.map3d.demo.geocoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;
import com.amap.map3d.demo.util.ThreadUtil;
import com.amap.map3d.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 地理编码与逆地理编码功能介绍
 */
public class ReGeocoderActivity extends Activity implements
		OnGeocodeSearchListener, OnClickListener, OnMarkerClickListener {
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;
	private String addressName;
	private AMap aMap;
	private MapView mapView;
	private LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);
	private Marker regeoMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geocoder_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			aMap.setOnMarkerClickListener(this);
		}
		Button regeoButton = (Button) findViewById(R.id.geoButton);
		Button regeoButton_ = (Button)findViewById(R.id.regeoButton);
		regeoButton.setText("ResGeoCoding(39.90865,116.39751)");
		regeoButton.setOnClickListener(this);
		regeoButton_.setVisibility(View.VISIBLE);
		regeoButton_.setText("逆地理编码同步方法(线程池)");
		regeoButton_.setOnClickListener(this);
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		progDialog = new ProgressDialog(this);
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
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		showDialog();
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addressName = result.getRegeocodeAddress().getFormatAddress()
						+ "附近";
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(latLonPoint), 15));
				regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
				ToastUtil.show(ReGeocoderActivity.this, addressName);
			} else {
				ToastUtil.show(ReGeocoderActivity.this, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this, rCode);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 响应逆地理编码按钮
		 */
		case R.id.geoButton:
			getAddress(latLonPoint);
			break;
		case R.id.regeoButton:
			getAddresses();
		default:
			break;
		}
	}

	/**
	 * 响应逆地理编码的批量请求
	 */
	private void getAddresses() {
		List<LatLonPoint> geopointlist = readLatLonPoints();
		for (final LatLonPoint point : geopointlist) {
			ThreadUtil.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					try {
						RegeocodeQuery query = new RegeocodeQuery(point, 200,
								GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
						RegeocodeAddress result = geocoderSearch.getFromLocation(query);// 设置同步逆地理编码请求

						if (result != null && result.getFormatAddress() != null) {
							aMap.addMarker(new MarkerOptions()
									.position(new LatLng(point.getLatitude(), point.getLongitude()))
									.title(result.getFormatAddress()));
						}
					} catch (AMapException e) {
						Message msg = msgHandler.obtainMessage();
						msg.arg1 = e.getErrorCode();
						msgHandler.sendMessage(msg);
					}
				}
			});
		}
	}
	private Handler msgHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			ToastUtil.showerror(ReGeocoderActivity.this, msg.arg1);
		}
	};
	private List<LatLonPoint> readLatLonPoints() {
		List<LatLonPoint> points = new ArrayList<LatLonPoint>();
		for (int i = 0; i < coords.length; i += 2) {
			points.add(new LatLonPoint(coords[i+1], coords[i]));
		}
		return points;
	}
	
	private double[] coords = { 116.339925,39.976587,
			116.328467,39.976357, 
			116.345289,39.966556, 
			116.321428,39.967477,
			116.358421,39.961556,
			116.366146,39.961293,
			116.359666,39.953234,
			116.373013,39.948628,
			116.355374,39.94037, 
			116.41713,39.940666,
			116.433309,39.940929, 
			116.461933,39.949319, 
			116.473907,39.938461,
			116.478971,39.933854, 
			116.491631,39.96603, 
			116.489399,39.971029,
			116.495364,39.98517, 
			116.530812,39.99556, 
			116.5607,39.996023,
			116.525982,40.022825,
			116.568511,40.016843, 
			116.584046,40.014608,
			116.422599,40.012439,
			116.44131,40.00616, 
			116.39303,40.026998,
			116.384147,40.039222, 
			116.388352,39.928242};

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}
}