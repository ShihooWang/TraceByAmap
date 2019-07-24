package com.amap.map3d.demo.route;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePlanPath;
import com.amap.api.services.route.DriveRoutePlanResult;
import com.amap.api.services.route.RouteSearch.DrivePlanQuery;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TimeInfo;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.ui.dataChoose.DataChooseDialog;
import com.amap.map3d.demo.util.AMapUtil;
import com.amap.map3d.demo.util.ToastUtil;
import com.amap.map3d.demo.util.utils;
import com.amap.map3d.demo.view.SettingView;
import com.amap.map3d.demo.view.TravelView;
import overlay.DrivingRoutePlanOverlay;

/**
 * 驾车出行路线规划 实现
 */
public class DriveRoutePlanActivity extends Activity implements OnMapClickListener,
OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter, RouteSearch.OnRoutePlanSearchListener ,View.OnClickListener{
	private AMap aMap;
	private MapView mapView;
	private Context mContext;
	private RouteSearch mRouteSearch;
	private Button button;
	private ImageView ArriveTimeSetting;
	private DriveRoutePlanResult mDriveRoutePlanResult;
	private LatLonPoint mStartPoint = new LatLonPoint(39.942295,116.335891);//起点，39.942295,116.335891
	private LatLonPoint mEndPoint = new LatLonPoint(39.995576,116.481288);//终点，39.995576,116.481288
	
	private final int ROUTE_TYPE_DRIVE = 2;

	private ProgressDialog progDialog = null;// 搜索时进度条

	private TravelView travelView;
	private SettingView settingView;
	private TextView travelTitle;

	private long arriveTime = 0;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.plan_activity);
		travelView = findViewById(R.id.travel_view);
		settingView = findViewById(R.id.setting_view);
		mContext = this.getApplicationContext();
		mapView = (MapView) findViewById(R.id.route_map);
		button = findViewById(R.id.get_path);
		ArriveTimeSetting = findViewById(R.id.set_arrive_time);
		mapView.onCreate(bundle);// 此方法必须重写
		init();

	}

	private void setfromandtoMarker() {
		aMap.addMarker(new MarkerOptions()
		.position(AMapUtil.convertToLatLng(mStartPoint))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
		aMap.addMarker(new MarkerOptions()
		.position(AMapUtil.convertToLatLng(mEndPoint))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));		
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.getUiSettings().setZoomControlsEnabled(false);
		}
		registerListener();
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setOnRoutePlanSearchListener(this);
		travelTitle = findViewById(R.id.title);
		button.setOnClickListener(this);

		settingView.setButtonVisible(new SettingView.ButtonVisibleListener() {
			@Override
			public void onButtonVisible(int visible) {
				setButtonVisible(visible);
			}
		});
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(DriveRoutePlanActivity.this);
		aMap.setOnMarkerClickListener(DriveRoutePlanActivity.this);
		aMap.setOnInfoWindowClickListener(DriveRoutePlanActivity.this);
		aMap.setInfoWindowAdapter(DriveRoutePlanActivity.this);
		
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(int routeType, int mode) {
		if (mStartPoint == null) {
			ToastUtil.show(mContext, "定位中，稍后再试...");
			return;
		}
		if (mEndPoint == null) {
			ToastUtil.show(mContext, "终点未设置");
		}
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);
		if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
			int  time = (int)(System.currentTimeMillis() /1000 );
			DrivePlanQuery query = new DrivePlanQuery(fromAndTo, time + utils.queryFirstInterval,  utils.queryInterval* 60, 48);// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDrivePlanAsyn(query);// 异步路径规划驾车模式查询
		}
	}

	@Override
	public void onDriveRoutePlanSearched(final DriveRoutePlanResult result, int errorCode) {
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getPaths() != null) {
				travelView.setVisibility(View.VISIBLE);
				travelView.init(result, new TravelView.IndexListener() {
					@Override
					public void onClicked(int index) {
						Log.d("qyd","onDriveRoutePlanSearched onClicked index:" + index);
                        if (result.getPaths().size() > 0) {
                            mDriveRoutePlanResult = result;

                            DrivingRoutePlanOverlay drivingRouteOverlay = new DrivingRoutePlanOverlay(
                                    mContext, aMap, mDriveRoutePlanResult, index);
                            drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                            drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                            drivingRouteOverlay.removeFromMap();
                            drivingRouteOverlay.addToMap();
                            drivingRouteOverlay.zoomToSpan();

                        } else if (result != null && result.getPaths() == null) {
                            ToastUtil.show(mContext, R.string.no_result);
                        }
					}
				}, arriveTime );

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}
	

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null) {
			progDialog = new ProgressDialog(this);
		}
		    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    progDialog.setIndeterminate(false);
		    progDialog.setCancelable(true);
		    progDialog.setMessage("正在搜索");
		    progDialog.show();
	    }

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
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
	public void onBackPressed(){
		if (travelView != null && travelView.getVisibility() == View.VISIBLE) {
			button.setVisibility(View.VISIBLE);
			travelView.setVisibility(View.GONE);
			travelTitle.setVisibility(View.GONE);
			settingView.setVisibility(View.GONE);
		} else {
			finish();
		}
	}

	private void request() {
		setfromandtoMarker();
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
	}

	private void showSetting() {
		settingView.setVisibility(View.VISIBLE);
		setButtonVisible(View.GONE);
	}

	public void setButtonVisible(int visible) {
		if (button != null) {
			button.setVisibility(visible);
		}

		if (ArriveTimeSetting != null) {
			ArriveTimeSetting.setVisibility(visible);
		}
	}

//	private void setArriveTime() {
//		DataChooseDialog dataChooseDialog = new DataChooseDialog(DriveRoutePlanActivity.this, new DataChooseDialog.DateChooseInterface() {
//			@Override
//
//			public void getDateTime(String time) {
//				arriveTime = utils.date2TimeStamp(time,"yyyy-MM-dd HH:mm");
//				if (arriveTime < utils.getCurrentTime()) {
//					arriveTime = 0;
//					Toast.makeText(DriveRoutePlanActivity.this,"请选择未来时间点",Toast.LENGTH_LONG).show();
//				} else if (arriveTime > utils.getCurrentTime() + 48*utils.queryInterval*60){
//					arriveTime = 0;
//					String out = String.format("请选择最近%.1f小时以内时间", 48*utils.queryInterval/60f);
//					Toast.makeText(DriveRoutePlanActivity.this,out,Toast.LENGTH_LONG).show();
//				}
//			}
//		});
//		dataChooseDialog.showChooseDialog();
//	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.get_path:
				request();
				break;
			case R.id.set_arrive_time:
//				setArriveTime();
				break;
			case R.id.setting:
				showSetting();
			default:
				break;
		}
	}
	
}

