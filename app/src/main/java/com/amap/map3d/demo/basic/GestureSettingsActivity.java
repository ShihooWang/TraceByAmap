package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.map3d.demo.R;

/**
 * 手势选项设置响应事件
 */
public class GestureSettingsActivity extends Activity implements OnClickListener{
	private AMap aMap;
	private MapView mapView;
	private UiSettings mUiSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesture_settings_activity);
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
		
		CheckBox scrollToggle = (CheckBox) findViewById(R.id.scroll_toggle);
		scrollToggle.setOnClickListener(this);
		CheckBox zoom_gesturesToggle = (CheckBox) findViewById(R.id.zoom_gestures_toggle);
		zoom_gesturesToggle.setOnClickListener(this);
		CheckBox tiltToggle = (CheckBox) findViewById(R.id.tilt_toggle);
		tiltToggle.setOnClickListener(this);
		CheckBox rotateToggle = (CheckBox) findViewById(R.id.rotate_toggle);
		rotateToggle.setOnClickListener(this);


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


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		/**
		 * 设置地图是否可以手势滑动
		 */
		case R.id.scroll_toggle:
			mUiSettings.setScrollGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图是否可以手势缩放大小
		 */
		case R.id.zoom_gestures_toggle:
			mUiSettings.setZoomGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图是否可以倾斜
		 */
		case R.id.tilt_toggle:
			mUiSettings.setTiltGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图是否可以旋转
		 */
		case R.id.rotate_toggle:
			mUiSettings.setRotateGesturesEnabled(((CheckBox) view).isChecked());
			break;
		default:
			break;
		}
	}

}
