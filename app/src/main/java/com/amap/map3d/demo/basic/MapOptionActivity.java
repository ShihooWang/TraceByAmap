package com.amap.map3d.demo.basic;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.CameraPosition;
import com.amap.map3d.demo.util.Constants;

/**
 * 通过Java代码添加一个SupportMapFragment对象
 */
public class MapOptionActivity extends FragmentActivity {

	private static final String MAP_FRAGMENT_TAG = "map";
	static final CameraPosition LUJIAZUI = new CameraPosition.Builder()
			.target(Constants.SHANGHAI).zoom(18).bearing(0).tilt(30).build();
	private AMap aMap;
	private SupportMapFragment aMapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		AMapOptions aOptions = new AMapOptions();
		aOptions.zoomGesturesEnabled(false);// 禁止通过手势缩放地图
		aOptions.scrollGesturesEnabled(false);// 禁止通过手势移动地图
		aOptions.tiltGesturesEnabled(false);// 禁止通过手势倾斜地图
		aOptions.camera(LUJIAZUI);
		if (aMapFragment == null) {
			aMapFragment = SupportMapFragment.newInstance(aOptions);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(android.R.id.content, aMapFragment,
					MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		initMap();
	}

	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = aMapFragment.getMap();// amap对象初始化成功
		}
	}
}
