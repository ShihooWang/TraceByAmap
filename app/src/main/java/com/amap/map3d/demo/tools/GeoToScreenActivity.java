package com.amap.map3d.demo.tools;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;

public class GeoToScreenActivity extends Activity implements OnMapClickListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	private EditText latView, lngView, xView, yView;
	private Button lnglat2pointBtn, point2LatlngBtn;
	private Point mPoint;
	private LatLng mLatlng;
	private int x, y;
	private float lat, lng;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location2screen_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		latView = (EditText)findViewById(R.id.pointLat);
		lngView = (EditText)findViewById(R.id.pointLng);
		xView = (EditText)findViewById(R.id.pointX);
		yView = (EditText)findViewById(R.id.pointY);
		lnglat2pointBtn = (Button)findViewById(R.id.lnglat2pointbtn);
		point2LatlngBtn = (Button)findViewById(R.id.point2Latlngbtn);
		lnglat2pointBtn.setOnClickListener(this);
		point2LatlngBtn.setOnClickListener(this);
	}

	private void setUpMap() {
		aMap.setOnMapClickListener(this);
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
	public void onMapClick(LatLng arg0) {
		latView.setText(String.valueOf(arg0.latitude));
		lngView.setText(String.valueOf(arg0.longitude));
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lnglat2pointbtn:
			toScreenLocation();
			break;
		case R.id.point2Latlngbtn:
			toGeoLocation();
			break;
		default:
			break;
		}
		
	}
	
	private void toGeoLocation() {
		if (AMapUtil.IsEmptyOrNullString(xView.getText().toString()) ||
				AMapUtil.IsEmptyOrNullString(yView.getText().toString())) {
			Toast.makeText(GeoToScreenActivity.this, "x和y为空", Toast.LENGTH_SHORT).show();
		} else {
			x = Integer.parseInt(xView.getText().toString().trim());
			y = Integer.parseInt(yView.getText().toString().trim());
			mPoint = new Point(x, y);
			mLatlng = aMap.getProjection().fromScreenLocation(mPoint);
			if (mLatlng != null) {
				latView.setText(String.valueOf(mLatlng.latitude));
				lngView.setText(String.valueOf(mLatlng.longitude));
			}
		}
		
	}
	private void toScreenLocation() {
		if (AMapUtil.IsEmptyOrNullString(latView.getText().toString()) ||
				AMapUtil.IsEmptyOrNullString(lngView.getText().toString())) {
			Toast.makeText(GeoToScreenActivity.this, "经纬度为空", Toast.LENGTH_SHORT).show();
		} else {
			lat = Float.parseFloat(latView.getText().toString().trim());
			lng = Float.parseFloat(lngView.getText().toString().trim());
			mLatlng = new LatLng(lat, lng);
			mPoint = aMap.getProjection().toScreenLocation(mLatlng);
			if (mPoint != null) {
				xView.setText(String.valueOf(mPoint.x));
				yView.setText(String.valueOf(mPoint.y));
			}
		}
	}

}
