package com.amap.map3d.demo.poisearch;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;
import com.amap.map3d.demo.util.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * poiID搜索功能
 */
public class PoiIDSearchActivity extends Activity implements OnClickListener ,OnMarkerClickListener, 
OnPoiSearchListener {
	private MapView mapview;
	private AMap mAMap;

	private Marker detailMarker;
	private PoiSearch poiSearch;
	private PoiItem mPoi;
	
	private RelativeLayout mPoiDetail;
	private TextView mPoiName, mPoiAddress, mPoiInfo;
	private String ID = "";
	private EditText mSearchText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poiaroundsearch_activity);
		mapview = (MapView)findViewById(R.id.mapView);
		mapview.onCreate(savedInstanceState);
		init();
	}
	
	
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mapview.getMap();
			mAMap.setOnMarkerClickListener(this);
			TextView searchButton = (TextView) findViewById(R.id.btn_search);
			searchButton.setOnClickListener(this);
		}
		setup();
	}
	private void setup() {
		mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
		mPoiName = (TextView) findViewById(R.id.poi_name);
		mPoiAddress = (TextView) findViewById(R.id.poi_address);
		mPoiInfo = (TextView)findViewById(R.id.poi_info);
		mSearchText = (EditText)findViewById(R.id.input_edittext);
		mSearchText.setText("B0FFFZ7A7D");
		mSearchText.setHint("请输入搜索ID");
		detailMarker = mAMap.addMarker(new MarkerOptions());
//		mPoiDetail.setOnClickListener(this);;
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		ID = mSearchText.getText().toString().trim();
		poiSearch = new PoiSearch(this, null);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIIdAsyn(ID);;// 异步搜索
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapview.onResume();
		whetherToShowDetailInfo(false);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
	}
	
	@Override
	public void onPoiItemSearched(PoiItem item, int rCode) {

		if (rCode == AMapException.CODE_AMAP_SUCCESS) {
			if (item != null) {
				mPoi = item;
				detailMarker.setPosition(AMapUtil.convertToLatLng(mPoi.getLatLonPoint()));
				setPoiItemDisplayContent(mPoi);
				whetherToShowDetailInfo(true);
			}
		} else {
			ToastUtil.showerror(this, rCode);
		}
	}


	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		
	}


	@Override
	public boolean onMarkerClick(Marker marker) {

		return true;
	}

	private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
		if (mCurrentPoi != null) {
			mPoiName.setText(mCurrentPoi.getTitle());
			mPoiAddress.setText(mCurrentPoi.getSnippet());
			mPoiInfo.setText("营业时间：" + mCurrentPoi.getPoiExtension().getOpentime()
					+ "     评分：" + mCurrentPoi.getPoiExtension().getmRating());
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			doSearchQuery();
			break;

		default:
			break;
		}
		
	}
	
	private void whetherToShowDetailInfo(boolean isToShow) {
		if (isToShow) {
			mPoiDetail.setVisibility(View.VISIBLE);

		} else {
			mPoiDetail.setVisibility(View.GONE);

		}
	}


}
