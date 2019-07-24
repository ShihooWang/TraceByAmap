package com.amap.map3d.demo.basic;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnPOIClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.model.Poi;
import com.amap.map3d.demo.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

/**  
 * 介绍底图poi点击事件
 */
public class PoiClickActivity extends Activity implements OnPOIClickListener,
		OnMarkerClickListener {

	private MapView mMapView;

	private AMap mAMap;
	
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poiclick_activity);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();
		mAMap.setOnPOIClickListener(this);
		mAMap.setOnMarkerClickListener(this);
	}

	/**
	 *  底图poi点击回调
	 */
	@Override
	public void onPOIClick(Poi poi) {
		mAMap.clear();
		Log.i("MY", poi.getPoiId()+poi.getName());
		MarkerOptions markOptiopns = new MarkerOptions();
		markOptiopns.position(poi.getCoordinate());
		TextView textView = new TextView(getApplicationContext());
		textView.setText("到"+poi.getName()+"去");
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		textView.setBackgroundResource(R.drawable.custom_info_bubble);
		markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
		mAMap.addMarker(markOptiopns);
	}

	/**
	 * Marker 点击回调
	 * @param marker
	 * @return
     */
	@Override
	public boolean onMarkerClick(Marker marker) {

		// 构造导航参数
		NaviPara naviPara = new NaviPara();
		// 设置终点位置
		naviPara.setTargetPoint(marker.getPosition());
		// 设置导航策略，这里是避免拥堵
		naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
		try {
			// 调起高德地图导航
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// 如果没安装会进入异常，调起下载页面
			AMapUtils.getLatestAMapApp(getApplicationContext());
		}
		mAMap.clear();
		return false;
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
}
