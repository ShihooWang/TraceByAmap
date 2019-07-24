package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ToastUtil;

/**
 * UI settings一些选项设置响应事件
 */
public class UiSettingsActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	private UiSettings mUiSettings;
	private RadioGroup zoomRadioGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_settings_activity);
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
		Button buttonScale = (Button) findViewById(R.id.buttonScale);
		buttonScale.setOnClickListener(this);
		CheckBox scaleToggle = (CheckBox) findViewById(R.id.scale_toggle);
		scaleToggle.setOnClickListener(this);
		CheckBox zoomToggle = (CheckBox) findViewById(R.id.zoom_toggle);
		zoomToggle.setOnClickListener(this);
		zoomRadioGroup = (RadioGroup) findViewById(R.id.zoom_position);
		zoomRadioGroup.setOnCheckedChangeListener(this);
		CheckBox compassToggle = (CheckBox) findViewById(R.id.compass_toggle);
		compassToggle.setOnClickListener(this);
		CheckBox mylocationToggle = (CheckBox) findViewById(R.id.mylocation_toggle);
		mylocationToggle.setOnClickListener(this);

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
			if (checkedId == R.id.zoom_bottom_right) {
				mUiSettings
						.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
			} else if (checkedId == R.id.zoom_center_right) {
				mUiSettings
						.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
			}
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		/**
		 * 一像素代表多少米
		 */
		case R.id.buttonScale:
			float scale = aMap.getScalePerPixel();
			ToastUtil.show(UiSettingsActivity.this, "每像素代表" + scale + "米");
			break;
		/**
		 * 设置地图默认的比例尺是否显示
		 */
		case R.id.scale_toggle:
			mUiSettings.setScaleControlsEnabled(((CheckBox) view).isChecked());

			break;
		/**
		 * 设置地图默认的缩放按钮是否显示
		 */
		case R.id.zoom_toggle:
			mUiSettings.setZoomControlsEnabled(((CheckBox) view).isChecked());
			zoomRadioGroup.setVisibility(((CheckBox) view).isChecked()?View.VISIBLE:View.GONE);
			break;
		/**
		 * 设置地图默认的指南针是否显示
		 */
		case R.id.compass_toggle:
			mUiSettings.setCompassEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图默认的定位按钮是否显示
		 */
		case R.id.mylocation_toggle:
			mUiSettings.setMyLocationButtonEnabled(((CheckBox) view)
					.isChecked()); // 是否显示默认的定位按钮
			aMap.setMyLocationEnabled(((CheckBox) view).isChecked());// 是否可触发定位并显示定位层
			break;
		default:
			break;
		}
	}

}
