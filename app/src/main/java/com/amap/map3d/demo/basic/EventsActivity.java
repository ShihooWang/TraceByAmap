package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnMapTouchListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.VisibleRegion;
import com.amap.map3d.demo.util.Constants;
import com.amap.map3d.demo.util.ToastUtil;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中简单介绍OnMapClickListener, OnMapLongClickListener,
 * OnCameraChangeListener三种监听器用法
 */

public class EventsActivity extends Activity implements OnMapClickListener,
		OnMapLongClickListener, OnCameraChangeListener, OnMapTouchListener {
	private AMap aMap;
	private MapView mapView;
	private TextView mTapTextView;
	private TextView mCameraTextView;
	private TextView mTouchTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_activity);
		/*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
		// Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		// MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
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
			setUpMap();
		}
		mTapTextView = (TextView) findViewById(R.id.tap_text);
		mCameraTextView = (TextView) findViewById(R.id.camera_text);
		mTouchTextView = (TextView) findViewById(R.id.touch_text);
	}

	/**
	 * amap添加一些事件监听器
	 */
	private void setUpMap() {

		aMap.setOnMapClickListener(this);// 对amap添加单击地图事件监听器
		aMap.setOnMapLongClickListener(this);// 对amap添加长按地图事件监听器
		aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
		aMap.setOnMapTouchListener(this);// 对amap添加触摸地图事件监听器
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
	 * 对单击地图事件回调
	 */
	@Override
	public void onMapClick(LatLng point) {
		mTapTextView.setText("tapped, point=" + point);
	}

	/**
	 * 对长按地图事件回调
	 */
	@Override
	public void onMapLongClick(LatLng point) {
		mTapTextView.setText("long pressed, point=" + point);
	}

	/**
	 * 对正在移动地图事件回调
	 */
	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		mCameraTextView.setText("onCameraChange:" + cameraPosition.toString());
	}

	/**
	 * 对移动地图结束事件回调
	 */
	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		mCameraTextView.setText("onCameraChangeFinish:"
				+ cameraPosition.toString());
		VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion(); // 获取可视区域、

		LatLngBounds latLngBounds = visibleRegion.latLngBounds;// 获取可视区域的Bounds
		boolean isContain = latLngBounds.contains(Constants.SHANGHAI);// 判断上海经纬度是否包括在当前地图可见区域
		if (isContain) {
			ToastUtil.show(EventsActivity.this, "上海市在地图当前可见区域内");
		} else {
			ToastUtil.show(EventsActivity.this, "上海市超出地图当前可见区域");
		}
	}

	/**
	 * 对触摸地图事件回调
	 */
	@Override
	public void onTouch(MotionEvent event) {

		mTouchTextView.setText("触摸事件：屏幕位置" + event.getX() + " " + event.getY());
	}
}
