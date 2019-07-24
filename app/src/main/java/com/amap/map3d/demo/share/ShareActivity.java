package com.amap.map3d.demo.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.LatLonSharePoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.share.ShareSearch;
import com.amap.api.services.share.ShareSearch.OnShareSearchListener;
import com.amap.api.services.share.ShareSearch.ShareDrivingRouteQuery;
import com.amap.api.services.share.ShareSearch.ShareFromAndTo;
import com.amap.api.services.share.ShareSearch.ShareNaviQuery;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ToastUtil;

/**
 * 展示我的位置、POI、路径规划、导航 转短串分享功能
 * 
 */
public class ShareActivity extends Activity implements OnClickListener,
		OnShareSearchListener {
	private MapView mMapView;
	private AMap mAMap;
	private Button mLocationButton, mRouteButton, mPoiButton, mNaviButton;
	private WebView mUrlView;
	private ShareSearch mShareSearch;
	private ProgressDialog mProgDialog = null;// 搜索时进度条
	private LatLonPoint START = new LatLonPoint(39.989646, 116.480864);
	private LatLonPoint END = new LatLonPoint(39.983456, 116.3154950);
	private LatLonPoint POI_POINT = new LatLonPoint(39.989646, 116.480864);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_activity);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		mLocationButton = (Button) findViewById(R.id.share_location);
		mRouteButton = (Button) findViewById(R.id.share_route);
		mPoiButton = (Button) findViewById(R.id.share_poi);
		mNaviButton = (Button) findViewById(R.id.share_navi);
		mUrlView = (WebView)findViewById(R.id.url_view);
		mLocationButton.setOnClickListener(this);
		mRouteButton.setOnClickListener(this);
		mPoiButton.setOnClickListener(this);
		mNaviButton.setOnClickListener(this);
		mShareSearch = new ShareSearch(this.getApplicationContext());
		mShareSearch.setOnShareSearchListener(this);
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
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
		mMapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewID = v.getId();
		switch (viewID) {
		case R.id.share_poi: {
			String title = "高德软件有限公司";
			String snippet = "方恒国际中心A座";
			sharePOI(title, snippet);
			break;
		}
		case R.id.share_location: {
			String snippet = "方恒国际中心A座";
			shareLocation(snippet);
			break;
		}
		case R.id.share_route: {
			shareRoute();
			break;
		}
		case R.id.share_navi: {
			shareNavi();
			break;
		}
		}
	}

	/**
	 * POI转短串分享
	 */
	private void sharePOI(String title, String snippet) {
		addPOIMarker(title, snippet);
		PoiItem item = new PoiItem(null, POI_POINT, title, snippet);
		showProgressDialog();
		mShareSearch.searchPoiShareUrlAsyn(item);
	}

	/**
	 * 添加待分享POI地图展示
	 */
	private void addPOIMarker(String title, String snippet) {
		mAMap.clear();
		addMarker(POI_POINT.getLatitude(), POI_POINT.getLongitude(), title,
				snippet, BitmapDescriptorFactory.defaultMarker());
		LatLng latlng = new LatLng(POI_POINT.getLatitude(),
				POI_POINT.getLongitude());
		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
	}

	/**
	 * 位置转短串分享
	 * 
	 * @param snippet
	 */
	private void shareLocation(String snippet) {
		addTestLocationMarker(snippet);
		LatLonSharePoint point = new LatLonSharePoint(POI_POINT.getLatitude(),
				POI_POINT.getLongitude(), snippet);
		showProgressDialog();
		mShareSearch.searchLocationShareUrlAsyn(point);
	}

	/**
	 * 添加模拟位置地图展示
	 * 
	 * @param snippet
	 */
	private void addTestLocationMarker(String snippet) {
		mAMap.clear();
		String title = "我的位置";
		addMarker(POI_POINT.getLatitude(), POI_POINT.getLongitude(), title,
				snippet,
				BitmapDescriptorFactory
						.fromResource(R.drawable.location_marker));
		LatLng latlng = new LatLng(POI_POINT.getLatitude(),
				POI_POINT.getLongitude());
		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
	}

	/**
	 * 添加marker方法
	 * 
	 * @param lat
	 * @param lon
	 * @param title
	 * @param snippet
	 * @param icon
	 */
	private void addMarker(double lat, double lon, String title,
			String snippet, BitmapDescriptor icon) {
		MarkerOptions markerOption = new MarkerOptions();
		LatLng markerPoint = new LatLng(lat, lon);
		markerOption.position(markerPoint);
		markerOption.title(title).snippet(snippet);
		markerOption.icon(icon);
		mAMap.addMarker(markerOption);
	}

	/**
	 * 驾车路径规划短串分享
	 */
	private void shareRoute() {
		addRouteMarker();
		ShareFromAndTo fromAndTo = new ShareFromAndTo(START, END);
		ShareDrivingRouteQuery query = new ShareDrivingRouteQuery(fromAndTo,
				ShareSearch.DrivingDefault);
		showProgressDialog();
		mShareSearch.searchDrivingRouteShareUrlAsyn(query);
	}

	/**
	 * 添加线路marker
	 */
	private void addRouteMarker() {
		mAMap.clear();
		addMarker(START.getLatitude(), START.getLongitude(), "", "",
				BitmapDescriptorFactory.fromResource(R.drawable.start));
		addMarker(END.getLatitude(), END.getLongitude(), "", "",
				BitmapDescriptorFactory.fromResource(R.drawable.end));
		LatLngBounds.Builder builder = LatLngBounds.builder();
		builder.include(new LatLng(START.getLatitude(), START.getLongitude()));
		builder.include(new LatLng(END.getLatitude(), END.getLongitude()));
		mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
				50));
	}

	/**
	 * 导航短串分享
	 */
	private void shareNavi() {
		addRouteMarker();
		ShareFromAndTo fromAndTo = new ShareFromAndTo(START, END);
		ShareNaviQuery query = new ShareNaviQuery(fromAndTo,
				ShareSearch.NaviDefault);
		showProgressDialog();
		mShareSearch.searchNaviShareUrlAsyn(query);
	}

	@Override
	public void onBusRouteShareUrlSearched(String url, int errorCode) {
		// TODO Auto-generated method stub
		dissmissProgressDialog();
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			mUrlView.loadUrl(url);
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onDrivingRouteShareUrlSearched(String url, int errorCode) {
		// TODO Auto-generated method stub
		dissmissProgressDialog();
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			mUrlView.loadUrl(url);
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onLocationShareUrlSearched(String url, int errorCode) {
		// TODO Auto-generated method stub
		dissmissProgressDialog();
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			mUrlView.loadUrl(url);
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onNaviShareUrlSearched(String url, int errorCode) {
		// TODO Auto-generated method stub
		dissmissProgressDialog();
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			mUrlView.loadUrl(url);
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onPoiShareUrlSearched(String url, int errorCode) {
		// TODO Auto-generated method stub
		dissmissProgressDialog();
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			mUrlView.loadUrl(url);
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onWalkRouteShareUrlSearched(String url, int errorCode) {
		// TODO Auto-generated method stub
		dissmissProgressDialog();
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			mUrlView.loadUrl(url);
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}


	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (mProgDialog == null) {
			mProgDialog = new ProgressDialog(this);
		}
		mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgDialog.setIndeterminate(false);
		mProgDialog.setCancelable(true);
		mProgDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (mProgDialog != null && mProgDialog.isShowing()) {
			mProgDialog.dismiss();
		}
	}
}
