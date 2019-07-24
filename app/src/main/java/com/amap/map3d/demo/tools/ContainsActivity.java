package com.amap.map3d.demo.tools;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.map3d.demo.R;

/**
 * 点是否在多边形内 示例
 */
public class ContainsActivity extends Activity implements OnMapClickListener {
	private AMap aMap;
	private MapView mapView;
	private Polygon polygon;
	private TextView Text;
	private Marker marker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arc_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
	}
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		Text = (TextView) findViewById(R.id.info_text);
		Text.setText("请单击地图");
	}

	private void setUpMap() {
		aMap.setOnMapClickListener(this);
		// 绘制一个长方形
		PolygonOptions pOption = new PolygonOptions();
		pOption.add(new LatLng(39.926516, 116.389366));
		pOption.add(new LatLng(39.924870, 116.403270));
		pOption.add(new LatLng(39.918090, 116.406274));
		pOption.add(new LatLng(39.909466, 116.397863));
		pOption.add(new LatLng(39.913021, 116.387134));
		polygon = aMap.addPolygon(pOption.strokeWidth(4)
				.strokeColor(Color.argb(50, 1, 1, 1))
				.fillColor(Color.argb(50, 1, 1, 1)));
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.926516,
				116.389366), 16));
//		
	}

	/**
	 * 点击地图的回调
     */
	@Override
	public void onMapClick(LatLng arg0) {
		if (marker != null) {
			marker.remove();
		}
		marker = aMap.addMarker(new MarkerOptions().position(arg0));
		boolean b1 = polygon.contains(arg0);
		Toast.makeText(ContainsActivity.this, "是否在围栏里面：" + b1, Toast.LENGTH_SHORT).show();
		
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
