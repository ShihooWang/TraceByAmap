package com.amap.map3d.demo.trace;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.amap.api.trace.TraceStatusListener;
import com.amap.map3d.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹纠偏功能 示例 使用起来更简单
 */
public class TraceActivity_Simple2222 extends Activity implements TraceStatusListener,
		OnClickListener {
	private MapView mMapView;
	private AMap mAMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace_simple);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.getUiSettings().setRotateGesturesEnabled(false);
			mAMap.getUiSettings().setZoomControlsEnabled(false);
		}
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
		if (mMapView != null) {
			mMapView.onDestroy();
		}
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.start_bt:
			startTrace();
			break;
		case R.id.stop_bt:
			stopTrace();
			break;
		}
	}

	LBSTraceClient traceClient = null;
	TraceOverlay traceOverlay;

	/**
	 * 停止轨迹纠偏
	 */
	private void stopTrace() {
		if(traceClient == null) {
			traceClient = LBSTraceClient.getInstance(TraceActivity_Simple2222.this);
		}
		traceClient.stopTrace();
	}

	/**
	 * 开启轨迹纠偏
	 */
	private void startTrace() {

		if(traceClient == null) {
			traceClient = LBSTraceClient.getInstance(TraceActivity_Simple2222.this);
		}
        traceClient.startTrace(this);
	}

	@Override
	public void onTraceStatus(List<TraceLocation> locations, List<LatLng> rectifications, String errorInfo) {
		try {
			if (!LBSTraceClient.TRACE_SUCCESS.equals(errorInfo)) {
				Log.e("amap", " source count->" + (locations == null ? "0" : locations.size()) + "   result count->" + (rectifications == null ? "0" : rectifications.size()));

				StringBuffer stringBuffer = new StringBuffer();

				if(locations != null) {
					for (TraceLocation location : locations) {
						stringBuffer.append("{");
						stringBuffer.append("\"lon\":").append(location.getLongitude()).append(",");
						stringBuffer.append("\"lat\":").append(location.getLatitude()).append(",");
						stringBuffer.append("\"loctime\":").append(location.getTime()).append(",");
						stringBuffer.append("\"speed\":").append(location.getSpeed()).append(",");
						stringBuffer.append("\"bearing\":").append(location.getBearing());
						stringBuffer.append("}");
						stringBuffer.append("\n");
					}
				}
				Log.i("amap","轨迹纠偏失败，请先检查以下几点:\n" +
						"定位是否成功\n" +
						"onTraceStatus第一个参数中 经纬度、时间、速度和角度信息是否为空\n" +
						"若仍不能解决问题，请将以下内容通过官网(lbs.amap.com)工单系统反馈给我们 \n" + errorInfo + " \n "
						+ stringBuffer.toString());


				return;
			}

			showSourceLocations(locations);
			showTracedLocations(rectifications);

//			if (traceOverlay != null) {
//				traceOverlay.remove();
//				traceOverlay = null;
//			}
//			//将得到的轨迹点显示在地图上
//			if (traceOverlay == null) {
//				traceOverlay = new TraceOverlay(mAMap, rectifications);
////				traceOverlay.zoopToSpan();
//			} else {
//				traceOverlay.add(rectifications);
//			}


		} catch (Throwable e) {
			e.printStackTrace();
		}

	}



	Polyline tracedPolyline = null;

	/**
	 * 展示纠偏后的点
	 * @param rectifications
	 */
	private void showTracedLocations(List<LatLng> rectifications) {
		if(tracedPolyline == null) {
			tracedPolyline = mAMap.addPolyline(new PolylineOptions()
					.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture))
					.width(20).zIndex(0));
		}
		if(rectifications == null) {
			return;
		}
		tracedPolyline.setPoints(rectifications);
	}



	Polyline polyline = null;

	/**
	 * 展示原始定位点
	 * @param locations
	 */
	private void showSourceLocations(List<TraceLocation> locations) {
		if(polyline == null) {
			polyline = mAMap.addPolyline(new PolylineOptions().color(Color.parseColor("#88FF0000")).width(40).zIndex(0));
		}
		if(locations == null) {
			return;
		}
		List<LatLng> latLngs = new ArrayList<>();
		for(TraceLocation traceLocation : locations) {
			latLngs.add(new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude()));
		}
		polyline.setPoints(latLngs);
	}

}
