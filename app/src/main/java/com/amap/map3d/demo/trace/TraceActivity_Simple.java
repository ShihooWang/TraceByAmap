package com.amap.map3d.demo.trace;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.amap.api.trace.TraceStatusListener;
import com.amap.map3d.demo.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATE;

/**
 * 轨迹纠偏功能 示例 使用起来更简单
 */
public class TraceActivity_Simple extends Activity {
	private List<TraceLocation> mListPoint = new ArrayList<>();
	private List<TraceLocation> originPosList;
	private LBSTraceClient lbsTraceClient;
//	private Gson mGson = new Gson();

	final int CAR_MAX_SPEED = 22; // 80km/h
	private Boolean isFirst = true;         // 是否是第一次定位点
	private TraceLocation weight1 = new TraceLocation();        // 权重点1
	private TraceLocation weight2;                          // 权重点2
	private List<TraceLocation> w1TempList = new ArrayList<>();     // w1的临时定位点集合
	private List<TraceLocation> w2TempList = new ArrayList<>();     // w2的临时定位点集合
	private int w1Count = 0; // 统计w1Count所统计过的点数
	private int posCount = 0;
	private TraceLocation posTraceLocation;
	private int beginPos = 0;


	private final Timer timer = new Timer();

	TimerTask task = new TimerTask() {

		private MarkerOptions markerOption;

		@Override
		public void run() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					//处理延时任务
					if (posCount==9 || posCount==30 || posCount==31 || posCount==70 || posCount==71 || posCount==72 || posCount==120 || posCount==121 || posCount==122 ||
							posCount==164 || posCount==165 || posCount==190 || posCount==210 || posCount==230) {
						// 创建跳点
						TraceLocation traceLocation = originPosList.get(posCount);
						LatLng latLng = new LatLng(traceLocation.getLatitude()+0.0160900, traceLocation.getLongitude() - 0.0160000);
						traceLocation.setLatitude(latLng.latitude);
						traceLocation.setLongitude(latLng.longitude);
						// 绘制跳点
						markerOption = new MarkerOptions();
						markerOption.position(latLng);
						markerOption.draggable(false);//设置Marker可拖动
						markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
								.decodeResource(getResources(), R.drawable.marker_red)));
						// 将Marker设置为贴地显示，可以双指下拉地图查看效果
						markerOption.setFlat(true);//设置marker平贴地图效果
						aMap.addMarker(markerOption);
						posTraceLocation = traceLocation;
					}if (posCount==9 || posCount==30 || posCount==31 || posCount==70 || posCount==71 || posCount==72 || posCount==120 || posCount==121 || posCount==122 ||
							posCount==164 || posCount==165 || posCount==190 || posCount==210 || posCount==230) {
						// 创建跳点
						TraceLocation traceLocation = originPosList.get(posCount);
						LatLng latLng = new LatLng(traceLocation.getLatitude()+0.0160900, traceLocation.getLongitude() - 0.0160000);
						traceLocation.setLatitude(latLng.latitude);
						traceLocation.setLongitude(latLng.longitude);
						// 绘制跳点
						markerOption = new MarkerOptions();
						markerOption.position(latLng);
						markerOption.draggable(false);//设置Marker可拖动
						markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
								.decodeResource(getResources(), R.drawable.marker_red)));
						// 将Marker设置为贴地显示，可以双指下拉地图查看效果
						markerOption.setFlat(true);//设置marker平贴地图效果
						aMap.addMarker(markerOption);
						posTraceLocation = traceLocation;
					}else {
						// 绘制点得到正常点
						posTraceLocation = originPosList.get(posCount);
					}
					Boolean isSHow = filterPos(posTraceLocation);

					if (isSHow) {
						for (int i = beginPos; i < mListPoint.size(); i++) {
							TraceLocation traceLocation = mListPoint.get(i);
							LatLng startLocation_gps = new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude());
							MarkerOptions markerOption = new MarkerOptions();
							markerOption.position(startLocation_gps);
							markerOption.draggable(false);//设置Marker可拖动
							markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
									.decodeResource(getResources(), R.drawable.marker_blue)));
							// 将Marker设置为贴地显示，可以双指下拉地图查看效果
							markerOption.setFlat(true);//设置marker平贴地图效果
							aMap.addMarker(markerOption);
						}

						beginPos = mListPoint.size();
					}

					posCount++;

					if (posCount == 270) {
						timer.cancel();
					}

				}
			});
		}
	};


	private MapView mMapView;
	private AMap aMap;

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
		if (aMap == null) {
			aMap = mMapView.getMap();
			aMap.getUiSettings().setRotateGesturesEnabled(false);
			aMap.getUiSettings().setZoomControlsEnabled(false);
		}
		initView();
	}

	public void initView() {

//		BitmapDescriptor mPoint = BitmapDescriptorFactory.fromResource(R.drawable.start);
//		// 设置司机定位点
//		MyLocationStyle myLocationStyle;
//		myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//		myLocationStyle.myLocationIcon(mPoint);
//		myLocationStyle.myLocationType(LOCATION_TYPE_LOCATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//		myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//		aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//		aMap.getUiSettings().setMyLocationButtonEnabled(true); // 设置默认定位按钮是否显示，非必需设置。
//		aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//
//		lbsTraceClient = LBSTraceClient.getInstance(this);

		if (originPosList == null) {
			originPosList = new ArrayList<>();
			originPosList = TraceAsset.parseLocationsData(this.getAssets(),
					"traceRecord" + File.separator + "AMapTrace.txt");
			LatLng positionLatLng = new LatLng(originPosList.get(0).getLatitude(),originPosList.get(0).getLongitude());
			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 16));
		}

		timer.schedule(task, 500, 500);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
		mMapView.onDestroy();
		timer.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
		mMapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
		mMapView.onSaveInstanceState(outState);
	}


	/**
	 * @param aMapLocation
	 * @return 是否获得有效点，需要存储和计算距离
	 */
	private Boolean filterPos(TraceLocation aMapLocation) {
		String filterString = "";
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//		Date date = new Date(aMapLocation.getTime());
//		String time = df.format(date);//定位时间
//		filterString = time + "开始虑点" + "\r\n";

		try {
			// 获取的第一个定位点不进行过滤
			if (isFirst) {
				isFirst = false;
				weight1.setLatitude(aMapLocation.getLatitude());
				weight1.setLongitude(aMapLocation.getLongitude());
				weight1.setTime(aMapLocation.getTime());

				/****************存储数据到文件中，后面好分析******************/

				filterString += "第一次" + " : ";

				/**********************************/

				// 将得到的第一个点存储入w1的缓存集合
				final TraceLocation traceLocation = new TraceLocation();
				traceLocation.setLatitude(aMapLocation.getLatitude());
				traceLocation.setLongitude(aMapLocation.getLongitude());
				traceLocation.setTime(aMapLocation.getTime());
				w1TempList.add(traceLocation);
				w1Count++;

				return true;

			} else {
				filterString += "非第一次" + " : ";
//                // 过滤静止时的偏点，在静止时速度小于1米就算做静止状态
//                if (aMapLocation.getSpeed() < 1) {
//                    return false;
//                }

				/****************存储数据到文件中，后面好分析******************/
//				SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//				Date date1 = new Date(aMapLocation.getTime());
//				String time1 = df1.format(date1); // 定位时间
//				String testString = time1 + ":" + aMapLocation.getTime() + "," + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude() + "," + aMapLocation.getSpeed() + "\r\n";
////				FileWriteUtil.getInstance().save("tutu_driver_true.txt", testString);
//				Log.d("wsh",testString);

				if (weight2 == null) {
					filterString += "weight2=null" + " : ";
					// 计算w1与当前定位点p1的时间差并得到最大偏移距离D
					long offsetTimeMils = aMapLocation.getTime() - weight1.getTime();
					long offsetTimes = (offsetTimeMils / 1000);
					long MaxDistance = offsetTimes * CAR_MAX_SPEED;
					float distance = AMapUtils.calculateLineDistance(
							new LatLng(weight1.getLatitude(), weight1.getLongitude()),
							new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
					filterString += "distance = " + distance + ",MaxDistance = " + MaxDistance + " : ";

					if (distance > MaxDistance) {
						filterString += "distance > MaxDistance" + "当前点 距离大: 设置w2位新的点，并添加到w2TempList";
						// 将设置w2位新的点，并存储入w2临时缓存
						weight2 = new TraceLocation();
						weight2.setLatitude(aMapLocation.getLatitude());
						weight2.setLongitude(aMapLocation.getLongitude());
						weight2.setTime(aMapLocation.getTime());
						w2TempList.add(weight2);
						return false;
					} else {
						filterString += "distance < MaxDistance" + "当前点 距离小 : 添加到w1TempList";
						// 将p1加入到做坐标集合w1TempList
						TraceLocation traceLocation = new TraceLocation();
						traceLocation.setLatitude(aMapLocation.getLatitude());
						traceLocation.setLongitude(aMapLocation.getLongitude());
						traceLocation.setTime(aMapLocation.getTime());
						w1TempList.add(traceLocation);
						w1Count++;
						// 更新w1权值点
						weight1.setLatitude(weight1.getLatitude() * 0.2 + aMapLocation.getLatitude() * 0.8);
						weight1.setLongitude(weight1.getLongitude() * 0.2 + aMapLocation.getLongitude() * 0.8);
						weight1.setTime(aMapLocation.getTime());
						weight1.setSpeed(aMapLocation.getSpeed());

//						if (w1TempList.size() > 3) {
//							filterString += "d1TempList.size() > 3" + " : 更新";
//							// 将w1TempList中的数据放入finalList，并将w1TempList清空
//							mListPoint.addAll(w1TempList);
//							w1TempList.clear();
//							return true;
//						} else {
//							filterString += "d1TempList.size() < 3" + " : 不更新";
//							return false;
//						}
						if (w1Count > 3){
							filterString += " : 更新";
							mListPoint.addAll(w1TempList);
							w1TempList.clear();
							return true;
						}else {
							filterString += " w1Count<3: 不更新";
							return false;
						}
					}

				} else {
					filterString += "weight2 != null" + " : ";
					// 计算w2与当前定位点p1的时间差并得到最大偏移距离D
					long offsetTimeMils = aMapLocation.getTime() - weight2.getTime();
					long offsetTimes = (offsetTimeMils / 1000);
					long MaxDistance = offsetTimes * 16;
					float distance = AMapUtils.calculateLineDistance(
							new LatLng(weight2.getLatitude(), weight2.getLongitude()),
							new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));

					filterString += "distance = " + distance + ",MaxDistance = " + MaxDistance + " : ";

					if (distance > MaxDistance) {
						filterString += "当前点 距离大: weight2 更新";
						w2TempList.clear();
						// 将设置w2位新的点，并存储入w2临时缓存
						weight2 = new TraceLocation();
						weight2.setLatitude(aMapLocation.getLatitude());
						weight2.setLongitude(aMapLocation.getLongitude());
						weight2.setTime(aMapLocation.getTime());

						w2TempList.add(weight2);

						return false;
					} else {
						filterString += "当前点 距离小: 添加到w2TempList";

						// 将p1加入到做坐标集合w2TempList
						TraceLocation traceLocation = new TraceLocation();
						traceLocation.setLatitude(aMapLocation.getLatitude());
						traceLocation.setLongitude(aMapLocation.getLongitude());
						traceLocation.setTime(aMapLocation.getTime());
						w2TempList.add(traceLocation);

						// 更新w2权值点
						weight2.setLatitude(weight2.getLatitude() * 0.2 + aMapLocation.getLatitude() * 0.8);
						weight2.setLongitude(weight2.getLongitude() * 0.2 + aMapLocation.getLongitude() * 0.8);
						weight2.setTime(aMapLocation.getTime());
						weight2.setSpeed(aMapLocation.getSpeed());

						if (w2TempList.size() > 4) {
							// 判断w1所代表的定位点数是否>4,小于说明w1之前的点为从一开始就有偏移的点
							if (w1Count > 4) {
								filterString += "w1Count > 4" + "计算增加W1";
								mListPoint.addAll(w1TempList);
							} else {
								filterString += "w1Count < 4" + "计算丢弃W1";
								w1TempList.clear();
							}
							filterString += "w2TempList.size() > 4" + " : 更新到偏移点";

							// 将w2TempList集合中数据放入finalList中
							mListPoint.addAll(w2TempList);

							// 1、清空w2TempList集合 2、更新w1的权值点为w2的值 3、将w2置为null
							w2TempList.clear();
							weight1 = weight2;
							weight2 = null;
							return true;

						} else {
							filterString += "w2TempList.size() < 4" + "\r\n";
							return false;
						}
					}
				}
			}
		} finally {
//            FileWriteUtil.getInstance().save("tutu_driver_filter.txt", filterString);
			Log.d("wsh",filterString);
		}
	}
}
