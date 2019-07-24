package com.amap.map3d.demo.overlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.map3d.demo.util.Constants;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中简单介绍一些Polygon的用法.
 */
public class PolygonActivity extends Activity implements
		OnSeekBarChangeListener {
	private static final int WIDTH_MAX = 50;
	private static final int HUE_MAX = 255;
	private static final int ALPHA_MAX = 255;
	private AMap aMap;
	private MapView mapView;
	private Polygon polygon;
	private SeekBar mColorBar;
	private SeekBar mAlphaBar;
	private SeekBar mWidthBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.polygon_activity);
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
		mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
		mColorBar.setMax(HUE_MAX);
		mColorBar.setProgress(50);

		mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
		mAlphaBar.setMax(ALPHA_MAX);
		mAlphaBar.setProgress(50);

		mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
		mWidthBar.setMax(WIDTH_MAX);
		mWidthBar.setProgress(25);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	// 设置监听 添加多边形
	private void setUpMap() {
		mColorBar.setOnSeekBarChangeListener(this);
		mAlphaBar.setOnSeekBarChangeListener(this);
		mWidthBar.setOnSeekBarChangeListener(this);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.BEIJING, 5));// 设置指定的可视区域地图
		// 绘制一个长方形
		aMap.addPolygon(new PolygonOptions()
				.addAll(createRectangle(Constants.SHANGHAI, 1, 1))
				.fillColor(Color.LTGRAY).strokeColor(Color.RED).strokeWidth(1));

		PolygonOptions options = new PolygonOptions();
		int numPoints = 400;
		float semiHorizontalAxis = 5f;
		float semiVerticalAxis = 2.5f;
		double phase = 2 * Math.PI / numPoints;
		for (int i = 0; i <= numPoints; i++) {
			options.add(new LatLng(Constants.BEIJING.latitude
					+ semiVerticalAxis * Math.sin(i * phase),
					Constants.BEIJING.longitude + semiHorizontalAxis
							* Math.cos(i * phase)));
		}
		// 绘制一个椭圆
		polygon = aMap.addPolygon(options.strokeWidth(25)
				.strokeColor(Color.argb(50, 1, 1, 1))
				.fillColor(Color.argb(50, 1, 1, 1)));

//		LatLng latLng1 = new LatLng(39.967641, 116.322888);
//		LatLng latLng2 = new LatLng(39.967641, 116.434124);
//		LatLng latLng3 = new LatLng(39.951852, 116.464337);
//		LatLng latLng4 = new LatLng(39.867581, 116.458843);
//		LatLng latLng5 = new LatLng(39.870743, 116.311901);

		// 定义多边形的5个点点坐标
		LatLng latLng1 = new LatLng(42.742467, 79.842785);
		LatLng latLng2 = new LatLng(43.893433, 98.124035);
		LatLng latLng3 = new LatLng(33.058738, 101.463879);
		LatLng latLng4 = new LatLng(25.873426, 95.838879);
		LatLng latLng5 = new LatLng(30.8214661, 78.788097);

		// 声明 多边形参数对象
		PolygonOptions polygonOptions = new PolygonOptions();
		// 添加 多边形的每个顶点（顺序添加）
		polygonOptions.add(latLng1, latLng2, latLng3, latLng4, latLng5);
		polygonOptions.strokeWidth(15) // 多边形的边框
				.strokeColor(Color.argb(50, 1, 1, 1)) // 边框颜色
				.fillColor(Color.argb(1, 1, 1, 1));   // 多边形的填充色

		// 添加一个多边形
		aMap.addPolygon(polygonOptions);
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
	 * 生成一个长方形的四个坐标点
	 */
	private List<LatLng> createRectangle(LatLng center, double halfWidth,
			double halfHeight) {
		List<LatLng> latLngs = new ArrayList<LatLng>();
		latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
		latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude + halfWidth));
		latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude + halfWidth));
		latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
		return latLngs;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/**
	 * Polygon中对填充颜色，透明度，画笔宽度设置响应事件
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (polygon == null) {
			return;
		}
		if (seekBar == mColorBar) {
			polygon.setFillColor(Color.argb(progress, 1, 1, 1));
		} else if (seekBar == mAlphaBar) {
			polygon.setStrokeColor(Color.argb(progress, 1, 1, 1));
		} else if (seekBar == mWidthBar) {
			polygon.setStrokeWidth(progress);
		}
	}
}
