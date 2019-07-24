package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中简单介绍一些GroundOverlay的用法.
 */
public class GroundOverlayActivity extends Activity {

	private AMap amap;
	private MapView mapview;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groundoverlay_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapview = (MapView) findViewById(R.id.map);
		mapview.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (amap == null) {
			amap = mapview.getMap();
			addOverlayToMap();
		}
	}

	/**
	 * 往地图上添加一个groundoverlay覆盖物
	 */
	private void addOverlayToMap() {
		amap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.936713,
				116.386475), 18));// 设置当前地图显示为北京市恭王府
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(new LatLng(39.935029, 116.384377))
				.include(new LatLng(39.939577, 116.388331)).build();

		amap.addGroundOverlay(new GroundOverlayOptions()
				.anchor(0.5f, 0.5f)
				.transparency(0.7f)
//				.zIndex(GlobalConstants.ZindexLine - 1)
				.image(BitmapDescriptorFactory
						.fromResource(R.drawable.groundoverlay))
						
				.positionFromBounds(bounds));
	}

	/**
	 * 方法必须重写
	 */
	protected void onResume() {
		super.onResume();
		mapview.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
	}
}
