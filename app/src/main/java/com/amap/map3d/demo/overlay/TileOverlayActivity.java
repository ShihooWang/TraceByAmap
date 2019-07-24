package com.amap.map3d.demo.overlay;

import java.net.URL;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.UrlTileProvider;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中简单介绍TileOverlay绘制
 */
public class TileOverlayActivity extends Activity {

	private AMap aMap;
	private MapView mapView;
	final String url = "http://mt2.google.cn/vt/lyrs=r&scale=2&hl=zh-CN&gl=cn&x=%d&y=%d&z=%d";
	//final String url = "http://a.tile.openstreetmap.org/%d/%d/%d.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arc_activity);
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
			setUpMap();
		}
	}

	private void setUpMap() {

		aMap.moveCamera(CameraUpdateFactory.zoomTo(4));
		aMap.setMapTextZIndex(2);

		aMap.addTileOverlay(new TileOverlayOptions().tileProvider(new UrlTileProvider(256, 256) {
			
			@Override
			public URL getTileUrl(int x, int y, int zoom) {
				try {
					return new URL(String.format(url, x, y, zoom));
				} catch (Exception  e) {
					e.printStackTrace();
				}
				return null;
			}
		})
		.diskCacheEnabled(true)  
        .diskCacheDir("/storage/emulated/0/amap/cache")
        .diskCacheSize(100000)  
        .memoryCacheEnabled(true)  
        .memCacheSize(100000));
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
