package com.amap.map3d.demo.trace;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.amap.map3d.demo.R;

/**
 * 轨迹纠偏功能 示例
 */
public class TraceActivity extends Activity implements TraceListener,
		OnClickListener, OnCheckedChangeListener, OnItemSelectedListener {
	String TAG = "TraceActivity";
	private Button mGraspButton, mCleanFinishOverlay;
	private Spinner mRecordChoose;
	private TextView mResultShow, mLowSpeedShow;
	private RadioGroup mCoordinateTypeGroup;
	private int mCoordinateType = LBSTraceClient.TYPE_AMAP;
	private MapView mMapView;
	private AMap mAMap;
	private LBSTraceClient mTraceClient;

	private String[] mRecordChooseArray;
	private List<TraceLocation> mTraceList;

	private static String mDistanceString, mStopTimeString;
	private static final String DISTANCE_UNIT_DES = " KM";
	private static final String TIME_UNIT_DES = " 分钟";

	private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<Integer, TraceOverlay>();
	private int mSequenceLineID = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		mGraspButton = (Button) findViewById(R.id.grasp_button);
		mCleanFinishOverlay = (Button) findViewById(R.id.clean_finish_overlay_button);
		mCleanFinishOverlay.setOnClickListener(this);
		mGraspButton.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		mResultShow = (TextView) findViewById(R.id.show_all_dis);
		mLowSpeedShow = (TextView) findViewById(R.id.show_low_speed);
		mRecordChoose = (Spinner) findViewById(R.id.record_choose);
		mDistanceString = getResources().getString(R.string.distance);
		mStopTimeString = getResources().getString(R.string.stop_time);
		mCoordinateTypeGroup = (RadioGroup) findViewById(R.id.coordinate_type_group);
		mCoordinateTypeGroup.setOnCheckedChangeListener(this);
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
		mTraceList = TraceAsset.parseLocationsData(this.getAssets(),
				"traceRecord" + File.separator + "AMapTrace.txt");
		mRecordChooseArray = TraceAsset.recordNames(this.getAssets());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mRecordChooseArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 绑定 Adapter到Spinner
		mRecordChoose.setAdapter(adapter);
		mRecordChoose.setOnItemSelectedListener(this);
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

	/**
	 * 轨迹纠偏失败回调
	 */
	@Override
	public void onRequestFailed(int lineID, String errorInfo) {
		Log.d(TAG, "onRequestFailed");
		Toast.makeText(this.getApplicationContext(), errorInfo,
				Toast.LENGTH_SHORT).show();
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
			setDistanceWaitInfo(overlay);
		}
	}

	/**
	 * 轨迹纠偏过程回调
	 */
	@Override
	public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
		Log.d(TAG, "onTraceProcessing");
		if (segments == null) {
			return;
		}
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
			overlay.add(segments);
		}
	}

	/**
	 * 轨迹纠偏结束回调
	 */
	@Override
	public void onFinished(int lineID, List<LatLng> linepoints, int distance,
			int watingtime) {
		Log.d(TAG, "onFinished");
		Toast.makeText(this.getApplicationContext(), "onFinished",
				Toast.LENGTH_SHORT).show();
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
			overlay.setDistance(distance);
			overlay.setWaitTime(watingtime);
			setDistanceWaitInfo(overlay);
		}

	}

	/**
	 * 调起一次轨迹纠偏
	 */
	private void traceGrasp() {
		if (mOverlayList.containsKey(mSequenceLineID)) {
			TraceOverlay overlay = mOverlayList.get(mSequenceLineID);
			overlay.zoopToSpan();
			int status = overlay.getTraceStatus();
			String tipString = "";
			if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
				tipString = "该线路轨迹纠偏进行中...";
				setDistanceWaitInfo(overlay);
			} else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
				setDistanceWaitInfo(overlay);
				tipString = "该线路轨迹已完成";
			} else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
				tipString = "该线路轨迹失败";
			} else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
				tipString = "该线路轨迹纠偏已经开始";
			}
			Toast.makeText(this.getApplicationContext(), tipString,
					Toast.LENGTH_SHORT).show();
			return;
		}
		TraceOverlay mTraceOverlay = new TraceOverlay(mAMap);
		mOverlayList.put(mSequenceLineID, mTraceOverlay);
		List<LatLng> mapList = traceLocationToMap(mTraceList);
		mTraceOverlay.setProperCamera(mapList);
		mResultShow.setText(mDistanceString);
		mLowSpeedShow.setText(mStopTimeString);
		mTraceClient = new LBSTraceClient(this.getApplicationContext());
		mTraceClient.queryProcessedTrace(mSequenceLineID, mTraceList,
				mCoordinateType, this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.grasp_button:
			traceGrasp();
			break;
		case R.id.clean_finish_overlay_button:
			cleanFinishTrace();
			break;
		}
	}

	/**
	 * 清除地图已完成或出错的轨迹
	 */
	private void cleanFinishTrace() {
		Iterator iter = mOverlayList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			TraceOverlay overlay = (TraceOverlay) entry.getValue();
			if (overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FINISH
					|| overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FAILURE) {
				overlay.remove();
				mOverlayList.remove(key);
			}
		}
	}

	/**
	 * 设置显示总里程和等待时间
	 *
	 * @param overlay
	 */
	private void setDistanceWaitInfo(TraceOverlay overlay) {
		int distance = -1;
		int waittime = -1;
		if (overlay != null) {
			distance = overlay.getDistance();
			waittime = overlay.getWaitTime();
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		;
		mResultShow.setText(mDistanceString
				+ decimalFormat.format(distance / 1000d) + DISTANCE_UNIT_DES);
		mLowSpeedShow.setText(mStopTimeString
				+ decimalFormat.format(waittime / 60d) + TIME_UNIT_DES);
	}

	/**
	 * 坐标系类别选择回调
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.button_amap:
			mCoordinateType = LBSTraceClient.TYPE_AMAP;
			break;
		case R.id.button_gps:
			mCoordinateType = LBSTraceClient.TYPE_GPS;
			break;
		case R.id.button_baidu:
			mCoordinateType = LBSTraceClient.TYPE_BAIDU;
			break;
		default:
			mCoordinateType = LBSTraceClient.TYPE_AMAP;
		}
	}

	/**
	 * Spinner 下拉选择
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (mTraceList != null) {
			mTraceList.clear();
		}
		mSequenceLineID = 1000 + pos;
		mTraceList = TraceAsset.parseLocationsData(
				TraceActivity.this.getAssets(), "traceRecord" + File.separator
						+ mRecordChooseArray[pos]);
		String recordName = mRecordChooseArray[pos];
		if (recordName == null) {
			return;
		}
		if (recordName.startsWith("AMap")) {
			mCoordinateTypeGroup.check(R.id.button_amap);
		} else if (recordName.startsWith("Baidu")) {
			mCoordinateTypeGroup.check(R.id.button_baidu);
		} else if (recordName.startsWith("GPS")) {
			mCoordinateTypeGroup.check(R.id.button_gps);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	/**
	 * 轨迹纠偏点转换为地图LatLng
	 *
	 * @param traceLocationList
	 * @return
	 */
	public List<LatLng> traceLocationToMap(List<TraceLocation> traceLocationList) {
		List<LatLng> mapList = new ArrayList<LatLng>();
		for (TraceLocation location : traceLocationList) {
			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());
			mapList.add(latlng);
		}
		return mapList;
	}
}
