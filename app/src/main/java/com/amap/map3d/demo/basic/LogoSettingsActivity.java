package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.map3d.demo.R;

/**
 * UI settings一些选项设置响应事件
 */
public class LogoSettingsActivity extends Activity implements
		OnCheckedChangeListener {
	private AMap aMap;
	private MapView mapView;
	private UiSettings mUiSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo_settings_activity);
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
			mUiSettings = aMap.getUiSettings();
		}
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.logo_position);
		radioGroup.setOnCheckedChangeListener(this);
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
	 * 设置logo位置，左下，底部居中，右下
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (aMap != null) {
			if (checkedId == R.id.bottom_left) {
				mUiSettings
						.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
			} else if (checkedId == R.id.bottom_center) {
				mUiSettings
						.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);// 设置地图logo显示在底部居中
			} else if (checkedId == R.id.bottom_right) {
				mUiSettings
						.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
			}
		}
	}
}
