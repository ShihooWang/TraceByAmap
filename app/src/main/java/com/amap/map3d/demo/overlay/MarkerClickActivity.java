package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中简单介绍一些Marker的用法.
 */
public class MarkerClickActivity extends Activity implements OnClickListener, OnMarkerClickListener{
	private MarkerOptions markerOption;
	private AMap aMap;
	private MapView mapView;
	private LatLng latlng = new LatLng(39.91746, 116.396481);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.marker_activity);
		/*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
		// Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		// MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		Button clearMap = (Button) findViewById(R.id.clearMap);
		clearMap.setOnClickListener(this);
		Button resetMap = (Button) findViewById(R.id.resetMap);
		resetMap.setOnClickListener(this);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	private void setUpMap() {
		aMap.setOnMarkerClickListener(this);
		addMarkersToMap();// 往地图上添加marker
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
	 * 在地图上添加marker
	 */
	private void addMarkersToMap() {

		markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
				.position(latlng)
				.draggable(true);
		 aMap.addMarker(markerOption);
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		if (aMap != null) {
			jumpPoint(marker);
		}
		Toast.makeText(MarkerClickActivity.this, "您点击了Marker", Toast.LENGTH_LONG).show();
		return true;
	}

	/**
	 * marker点击时跳动一下
	 */
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 清空地图上所有已经标注的marker
		 */
		case R.id.clearMap:
			if (aMap != null) {
				aMap.clear();
			}
			break;
		/**
		 * 重新标注所有的marker
		 */
		case R.id.resetMap:
			if (aMap != null) {
				aMap.clear();
				addMarkersToMap();
			}
			break;
		default:
			break;
		}
	}
}
