package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.Constants;

/**
 * AMapV2地图中简单介绍一些Camera的用法.
 */
public class CameraActivity extends Activity implements OnClickListener {
	private MapView mapView;
	private AMap aMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_activity);
	    /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
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
		}
		
		Button Lujiazui = (Button) findViewById(R.id.Lujiazui);
		Lujiazui.setOnClickListener(this);
		
		Button Zhongguancun = (Button) findViewById(R.id.Zhongguancun);
		Zhongguancun.setOnClickListener(this);
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
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
	 */
	private void changeCamera(CameraUpdate update) {

		aMap.moveCamera(update);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 点击“去中关村”按钮响应事件
		 */
		case R.id.Zhongguancun:
			changeCamera(
					CameraUpdateFactory.newCameraPosition(new CameraPosition(
							Constants.ZHONGGUANCUN, 18, 30, 30)));
			aMap.clear();
			aMap.addMarker(new MarkerOptions().position(Constants.ZHONGGUANCUN)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			break;

		/**
		 * 点击“去陆家嘴”按钮响应事件
		 */
		case R.id.Lujiazui:
			changeCamera(
					CameraUpdateFactory.newCameraPosition(new CameraPosition(
							Constants.SHANGHAI, 18, 30, 0)));
			aMap.clear();
			aMap.addMarker(new MarkerOptions().position(Constants.SHANGHAI)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			break;
		
		default:
			break;
		}
	}
}
