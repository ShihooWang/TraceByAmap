package com.amap.map3d.demo.route;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 距离测量 实现
 */
public class RouteDistanceActivity extends Activity implements DistanceSearch.OnDistanceSearchListener, AMap.OnMapLoadedListener {
	private AMap aMap;
	private MapView mapView;
	private Context mContext;
	private DistanceSearch distanceSearch;


	private List<Text> mDistanceText = new ArrayList<Text>();


	// 北京站
	private LatLonPoint start0 = new LatLonPoint(39.902896,116.42792);
	// 北京南站
	private LatLonPoint start1 = new LatLonPoint(39.865208,116.378596);

	// 北京西站
	private LatLonPoint start2 = new LatLonPoint(39.894914,116.322062);

	// 北京北站
	private LatLonPoint start3 = new LatLonPoint(39.945261,116.352994);

	// 北京市政府
	private LatLonPoint dest = new LatLonPoint(39.90455, 116.407555);


	private ProgressDialog progDialog = null;// 搜索时进度条
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route_activity);
		
		mContext = this.getApplicationContext();
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();

		setFromAndTo();
	}


	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}

		aMap.setOnMapLoadedListener(this);

		//隐藏顶部控件
		findViewById(R.id.routemap_header).setVisibility(View.GONE);
	}


	private void setFromAndTo() {

		LatLng latLngStart0 = AMapUtil.convertToLatLng(start0);
		LatLng latLngStart1 = AMapUtil.convertToLatLng(start1);
		LatLng latLngStart2 = AMapUtil.convertToLatLng(start2);
		LatLng latLngStart3 = AMapUtil.convertToLatLng(start3);
		LatLng latLngEnd = AMapUtil.convertToLatLng(dest);

		aMap.addMarker(new MarkerOptions()
		.position(latLngStart0)
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_marker_1)));
		aMap.addMarker(new MarkerOptions()
				.position(latLngStart1)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_marker_2)));
		aMap.addMarker(new MarkerOptions()
				.position(latLngStart2)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_marker_3)));
		aMap.addMarker(new MarkerOptions()
				.position(latLngStart3)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_marker_4)));

		aMap.addMarker(new MarkerOptions()
		.position(latLngEnd)
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));



		aMap.addPolyline(new PolylineOptions().add(latLngStart0,latLngEnd).color(Color.GREEN));
		aMap.addPolyline(new PolylineOptions().add(latLngStart1,latLngEnd).color(Color.GREEN));
		aMap.addPolyline(new PolylineOptions().add(latLngStart2,latLngEnd).color(Color.GREEN));
		aMap.addPolyline(new PolylineOptions().add(latLngStart3,latLngEnd).color(Color.GREEN));


		mDistanceText.add(aMap.addText(new TextOptions().position(getMidLatLng(latLngStart0, latLngEnd)).text("cal distance ...")));
		mDistanceText.add(aMap.addText(new TextOptions().position(getMidLatLng(latLngStart1, latLngEnd)).text("cal distance ...")));
		mDistanceText.add(aMap.addText(new TextOptions().position(getMidLatLng(latLngStart2, latLngEnd)).text("cal distance ...")));
		mDistanceText.add(aMap.addText(new TextOptions().position(getMidLatLng(latLngStart3, latLngEnd)).text("cal distance ...")));

	}

	/**
	 * 求两个经纬度的中点
	 * @param l1
	 * @param l2
	 * @return
	 */
	private LatLng getMidLatLng(LatLng l1, LatLng l2) {
		return new LatLng((l1.latitude + l2.latitude ) / 2, (l1.longitude + l2.longitude ) / 2);
	}




	@Override
	public void onMapLoaded() {

		//展示出所有点
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(AMapUtil.convertToLatLng(start0));
		builder.include(AMapUtil.convertToLatLng(start1));
		builder.include(AMapUtil.convertToLatLng(start2));
		builder.include(AMapUtil.convertToLatLng(start3));
		builder.include(AMapUtil.convertToLatLng(dest));
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

		distanceSearch = new DistanceSearch(this);
		distanceSearch.setDistanceSearchListener(this);

		searchDistanceResult(DistanceSearch.TYPE_DRIVING_DISTANCE);
	}

	
	/**
	 * 开始搜索路径规划方案
	 */
	public void searchDistanceResult(int mode) {
		showProgressDialog();
		DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();

		List<LatLonPoint> latLonPoints = new ArrayList<LatLonPoint>();
		latLonPoints.add(start0);
		latLonPoints.add(start1);
		latLonPoints.add(start2);
		latLonPoints.add(start3);
		distanceQuery.setOrigins(latLonPoints);
		distanceQuery.setDestination(dest);
		distanceQuery.setType(mode);

		distanceSearch.calculateRouteDistanceAsyn(distanceQuery);
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null) {
			progDialog = new ProgressDialog(this);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage("正在搜索");
			progDialog.show();
		}
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
	public void onDistanceSearched(DistanceResult distanceResult, int errorCode) {

		dissmissProgressDialog();

		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			try {
				Log.i("amap", "onDistanceSearched " + distanceResult);
				List<DistanceItem> distanceItems = distanceResult.getDistanceResults();
				DistanceSearch.DistanceQuery distanceQuery = distanceResult.getDistanceQuery();
				List<LatLonPoint> origins = distanceQuery.getOrigins();
				LatLonPoint destLatlon = distanceQuery.getDestination();

				if(distanceItems == null) {
					return;
				}

				int index = 1;
				for (DistanceItem item : distanceItems) {
					StringBuffer stringBuffer = new StringBuffer();
					//item.getOriginId() - 1 是因为 下标从1开始
					stringBuffer.append("\n\torid: ").append(item.getOriginId()).append(" ").append(origins.get(item.getOriginId() - 1)).append("\n");
					stringBuffer.append("\tdeid: ").append(item.getDestId()).append(" ").append(destLatlon).append("\n");
					stringBuffer.append("\tdis: ").append(item.getDistance()).append(" , ");
					stringBuffer.append("\tdur: ").append(item.getDuration());

					if (item.getErrorInfo() != null) {
						stringBuffer.append(" , ").append("err: ").append(item.getErrorCode()).append(" ").append(item.getErrorInfo());
					}

					stringBuffer.append("\n");
					Log.i("amap", "onDistanceSearched " + index + " : " + stringBuffer.toString());


					mDistanceText.get(index - 1).setText(item.getDistance() + " 米 " + item.getDuration() + " 秒");

					index++;




				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}

