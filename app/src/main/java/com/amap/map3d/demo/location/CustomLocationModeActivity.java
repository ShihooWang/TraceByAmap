package com.amap.map3d.demo.location;


import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中介绍使用active deactive进行定位<br>
 * 自定义定位模式，当sdk中提供的定位模式不满足要求时，可以自定义<br>
 *
 * 自定义效果<br>
 * 1、定位成功后， 小蓝点和和地图一起移动到定位点<br>
 * 2、手势操作地图后模式修改为 仅定位不移动到中心点<br>
 *
 *
 */
public class CustomLocationModeActivity extends Activity implements LocationSource,
		AMapLocationListener, AMap.OnMapTouchListener {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;


	boolean useMoveToLocationWithMapMode = true;

	//自定义定位小蓝点的Marker
	Marker locationMarker;

	//坐标和经纬度转换工具
	Projection projection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏


		mapView = new MapView(this);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mapView);


		TextView textView = new TextView(this);
		textView.setText("自定义效果\n" +
				" 1、定位成功后， 小蓝点和和地图一起移动到定位点\n" +
				" 2、手势操作地图后模式修改为 仅定位不移动到中心点");
		layout.addView(textView);

		setContentView(layout);


		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

		aMap.setOnMapTouchListener(this);
	}


	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();

		useMoveToLocationWithMapMode = true;
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();

		useMoveToLocationWithMapMode = false;
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
		if(null != mlocationClient){
			mlocationClient.onDestroy();
		}
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getErrorCode() == 0) {
				LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
				//展示自定义定位小蓝点
				if(locationMarker == null) {
					//首次定位
					locationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
							.anchor(0.5f, 0.5f));

					//首次定位,选择移动到地图中心点并修改级别到15级
					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
				} else {

					if(useMoveToLocationWithMapMode) {
						//二次以后定位，使用sdk中没有的模式，让地图和小蓝点一起移动到中心点（类似导航锁车时的效果）
						startMoveLocationAndMap(latLng);
					} else {
						startChangeLocation(latLng);
					}
					
				}


			} else {
				String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
				Log.e("AmapErr",errText);
			}
		}
	}


	/**
	 * 修改自定义定位小蓝点的位置
	 * @param latLng
	 */
	private void startChangeLocation(LatLng latLng) {

		if(locationMarker != null) {
			LatLng curLatlng = locationMarker.getPosition();
			if(curLatlng == null || !curLatlng.equals(latLng)) {
				locationMarker.setPosition(latLng);
			}
		}
	}

	/**
	 * 同时修改自定义定位小蓝点和地图的位置
	 * @param latLng
	 */
	private void startMoveLocationAndMap(LatLng latLng) {

		//将小蓝点提取到屏幕上
		if(projection == null) {
			projection = aMap.getProjection();
		}
		if(locationMarker != null && projection != null) {
			LatLng markerLocation = locationMarker.getPosition();
			Point screenPosition = aMap.getProjection().toScreenLocation(markerLocation);
			locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

		}

		//移动地图，移动结束后，将小蓝点放到放到地图上
		myCancelCallback.setTargetLatlng(latLng);
		//动画移动的时间，最好不要比定位间隔长，如果定位间隔2000ms 动画移动时间最好小于2000ms，可以使用1000ms
		//如果超过了，需要在myCancelCallback中进行处理被打断的情况
		aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng),1000,myCancelCallback);

	}


	MyCancelCallback myCancelCallback = new MyCancelCallback();

	@Override
	public void onTouch(MotionEvent motionEvent) {
		Log.i("amap","onTouch 关闭地图和小蓝点一起移动的模式");
		useMoveToLocationWithMapMode = false;
	}

	/**
	 * 监控地图动画移动情况，如果结束或者被打断，都需要执行响应的操作
	 */
	class MyCancelCallback implements AMap.CancelableCallback {

		LatLng targetLatlng;
		public void setTargetLatlng(LatLng latlng) {
			this.targetLatlng = latlng;
		}

		@Override
		public void onFinish() {
			if(locationMarker != null && targetLatlng != null) {
				locationMarker.setPosition(targetLatlng);
			}
		}

		@Override
		public void onCancel() {
			if(locationMarker != null && targetLatlng != null) {
				locationMarker.setPosition(targetLatlng);
			}
		}
	};



	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			//设置定位监听
			mlocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
			//是指定位间隔
			mLocationOption.setInterval(2000);
			//设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}


}