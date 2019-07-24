package com.amap.map3d.demo.routepoi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.routepoisearch.RoutePOIItem;
import com.amap.api.services.routepoisearch.RoutePOISearch;
import com.amap.api.services.routepoisearch.RoutePOISearch.OnRoutePOISearchListener;
import com.amap.api.services.routepoisearch.RoutePOISearch.RoutePOISearchType;
import com.amap.api.services.routepoisearch.RoutePOISearchQuery;
import com.amap.api.services.routepoisearch.RoutePOISearchResult;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;
import com.amap.map3d.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import overlay.DrivingRouteOverlay;

/**
 * 沿途搜索 示例
 */
public class RoutePOIActivity extends Activity implements OnMapClickListener, 
OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnRouteSearchListener, OnRoutePOISearchListener {
	private AMap aMap;
	private MapView mapView;
	private Context mContext;
	private RouteSearch mRouteSearch;
	private DriveRouteResult mDriveRouteResult;
	private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
	private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
	private final int ROUTE_TYPE_DRIVE = 2;
	private int mode = RouteSearch.DrivingDefault;
	
	private ProgressDialog progDialog = null;// 搜索时进度条
	private myRoutePoiOverlay overlay;
	private TextView gasbtn,ATMbtn,Maibtn,Toibtn;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.routepoi_activity);
		
		mContext = this.getApplicationContext();
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
		setfromandtoMarker();
		searchRouteResult(ROUTE_TYPE_DRIVE, mode);
	}

	/**
	 * 添加起点和终点标记
	 */
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
		}
		registerListener();
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setRouteSearchListener(this);
		gasbtn = (TextView)findViewById(R.id.gasbtn);
		ATMbtn= (TextView)findViewById(R.id.ATMbtn);
		Maibtn= (TextView)findViewById(R.id.Maibtn);
		Toibtn= (TextView)findViewById(R.id.Toibtn);
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(RoutePOIActivity.this);
		aMap.setOnMarkerClickListener(RoutePOIActivity.this);
		aMap.setOnInfoWindowClickListener(RoutePOIActivity.this);
		aMap.setInfoWindowAdapter(RoutePOIActivity.this);
		
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
	public boolean onMarkerClick(Marker marker) {

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
			ToastUtil.show(mContext, "起点未设置");
			return;
		}
		if (mEndPoint == null) {
			ToastUtil.show(mContext, "终点未设置");
		}
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);
		if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
					null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {
		
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mDriveRouteResult = result;
					final DrivePath drivePath = mDriveRouteResult.getPaths()
							.get(0);
					DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
							mContext, aMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos(), null);
					drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();

				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
		
	}
	

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
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
	public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void ongasClick(View view) {
		searchRoutePOI(RoutePOISearchType.TypeGasStation);
		gasbtn.setTextColor(Color.BLUE);
		ATMbtn.setTextColor(Color.GRAY);
		Maibtn.setTextColor(Color.GRAY);
		Toibtn.setTextColor(Color.GRAY);
	}

	public void onATMClick(View view) {
		searchRoutePOI(RoutePOISearchType.TypeATM);
		gasbtn.setTextColor(Color.GRAY);
		ATMbtn.setTextColor(Color.BLUE);
		Maibtn.setTextColor(Color.GRAY);
		Toibtn.setTextColor(Color.GRAY);
	}

	public void onMaiClick(View view) {
		searchRoutePOI(RoutePOISearchType.TypeMaintenanceStation);
		gasbtn.setTextColor(Color.GRAY);
		ATMbtn.setTextColor(Color.GRAY);
		Maibtn.setTextColor(Color.BLUE);
		Toibtn.setTextColor(Color.GRAY);
	}
	
	public void onToiClick(View view) {
		searchRoutePOI(RoutePOISearchType.TypeToilet);
		gasbtn.setTextColor(Color.GRAY);
		ATMbtn.setTextColor(Color.GRAY);
		Maibtn.setTextColor(Color.GRAY);
		Toibtn.setTextColor(Color.BLUE);
	}
	
	private void searchRoutePOI(RoutePOISearchType type) {
		if (overlay != null) {
			overlay.removeFromMap();
		}
		RoutePOISearchQuery query = new RoutePOISearchQuery(mStartPoint ,mEndPoint, mode, type, 250);
		final RoutePOISearch search = new RoutePOISearch(this, query);
		search.setPoiSearchListener(this);
		search.searchRoutePOIAsyn();
		
	}

	@Override
	public void onRoutePoiSearched(RoutePOISearchResult result, int errorCode) {
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			if(result != null){
				List<RoutePOIItem> items = result.getRoutePois();
				if (items != null && items.size() > 0) {
					if (overlay != null) {
						overlay.removeFromMap();
					}
					overlay = new myRoutePoiOverlay(aMap, items);
					overlay.addToMap();
				} else {
					ToastUtil.show(RoutePOIActivity.this,R.string.no_result);
				}
			}
		}else{
			ToastUtil.showerror(RoutePOIActivity.this, errorCode);
		}
		
	}
	
	
	/**
	 * 自定义PoiOverlay
	 *
	 */
	
	private class myRoutePoiOverlay {
		private AMap mamap;
		private List<RoutePOIItem> mPois;
	    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
		public myRoutePoiOverlay(AMap amap ,List<RoutePOIItem> pois) {
			mamap = amap;
	        mPois = pois;
		}

	    /**
	     * 添加Marker到地图中。
	     * @since V2.1.0
	     */
	    public void addToMap() {
	        for (int i = 0; i < mPois.size(); i++) {
	            Marker marker = mamap.addMarker(getMarkerOptions(i));
	            RoutePOIItem item = mPois.get(i);
				marker.setObject(item);
	            mPoiMarks.add(marker);
	        }
	    }

	    /**
	     * 去掉PoiOverlay上所有的Marker。
	     *
	     * @since V2.1.0
	     */
	    public void removeFromMap() {
	        for (Marker mark : mPoiMarks) {
	            mark.remove();
	        }
	    }

	    /**
	     * 移动镜头到当前的视角。
	     * @since V2.1.0
	     */
	    public void zoomToSpan() {
	        if (mPois != null && mPois.size() > 0) {
	            if (mamap == null)
	                return;
	            LatLngBounds bounds = getLatLngBounds();
	            mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
	        }
	    }

	    private LatLngBounds getLatLngBounds() {
	        LatLngBounds.Builder b = LatLngBounds.builder();
	        for (int i = 0; i < mPois.size(); i++) {
	            b.include(new LatLng(mPois.get(i).getPoint().getLatitude(),
	                    mPois.get(i).getPoint().getLongitude()));
	        }
	        return b.build();
	    }

	    private MarkerOptions getMarkerOptions(int index) {
	        return new MarkerOptions()
	                .position(
	                        new LatLng(mPois.get(index).getPoint()
	                                .getLatitude(), mPois.get(index)
	                                .getPoint().getLongitude()))
	                .title(getTitle(index)).snippet(getSnippet(index));
	    }

	    protected String getTitle(int index) {
	        return mPois.get(index).getTitle();
	    }

	    protected String getSnippet(int index) {
	        return mPois.get(index).getDistance() + "米  " + mPois.get(index).getDuration() + "秒";
	    }

	    /**
	     * 从marker中得到poi在list的位置。
	     *
	     * @param marker 一个标记的对象。
	     * @return 返回该marker对应的poi在list的位置。
	     * @since V2.1.0
	     */
	    public int getPoiIndex(Marker marker) {
	        for (int i = 0; i < mPoiMarks.size(); i++) {
	            if (mPoiMarks.get(i).equals(marker)) {
	                return i;
	            }
	        }
	        return -1;
	    }

	    /**
	     * 返回第index的poi的信息。
	     * @param index 第几个poi。
	     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
	     * @since V2.1.0
	     */
	    public RoutePOIItem getPoiItem(int index) {
	        if (index < 0 || index >= mPois.size()) {
	            return null;
	        }
	        return mPois.get(index);
	    }
	}
}

