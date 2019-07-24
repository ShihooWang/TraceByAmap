package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.map3d.demo.R;

/**
 * 介绍如何在地图中绘制导航指示箭头功能
 */
public class NavigateArrowOverlayActivity extends Activity {
	private MapView mapView;
	private AMap aMap;
	private LatLng latlng1 = new LatLng(39.9871, 116.4789);
	private LatLng latlng2 = new LatLng(39.9879, 116.4777);
	private LatLng latlng3 = new LatLng(39.9897, 116.4797);
	private LatLng latlng4 = new LatLng(39.9887, 116.4813);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arc_activity);
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
			// 加一个箭头对象（NavigateArrow）对象在地图上
			aMap.addNavigateArrow(new NavigateArrowOptions().add(latlng1,
					latlng2, latlng3, latlng4).width(20));
			aMap.moveCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(new LatLng(39.9875,
							116.48047), 16f, 38.5f, 300)));

		}
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

}
