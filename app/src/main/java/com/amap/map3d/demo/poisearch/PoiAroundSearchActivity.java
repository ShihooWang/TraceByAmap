package com.amap.map3d.demo.poisearch;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 介绍poi周边搜索功能
 */
public class PoiAroundSearchActivity extends Activity implements OnClickListener, 
OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMarkerClickListener, 
OnPoiSearchListener {
	private MapView mapview;
	private AMap mAMap;

	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private LatLonPoint lp = new LatLonPoint(39.993743, 116.472995);// 116.472995,39.993743
	private Marker locationMarker; // 选择的点
	private Marker detailMarker;
	private Marker mlastMarker;
	private PoiSearch poiSearch;
	private myPoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据
	
	private RelativeLayout mPoiDetail;
	private TextView mPoiName, mPoiAddress;
	private String keyWord = "";
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
			mAMap.setOnMapClickListener(this);
			mAMap.setOnMarkerClickListener(this);
			mAMap.setOnInfoWindowClickListener(this);
			mAMap.setInfoWindowAdapter(this);
			TextView searchButton = (TextView) findViewById(R.id.btn_search);
			searchButton.setOnClickListener(this);
			locationMarker = mAMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
					.position(new LatLng(lp.getLatitude(), lp.getLongitude())));
			locationMarker.showInfoWindow();

		}
		setup();
		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 14));
	}
	private void setup() {
		mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
		mPoiDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(PoiSearchActivity.this,
//						SearchDetailActivity.class);
//				intent.putExtra("poiitem", mPoi);
//				startActivity(intent);
				
			}
		});
		mPoiName = (TextView) findViewById(R.id.poi_name);
		mPoiAddress = (TextView) findViewById(R.id.poi_address);
		mSearchText = (EditText)findViewById(R.id.input_edittext);
	}
	/**
	 * 开始进行poi搜索
	 */
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		keyWord = mSearchText.getText().toString().trim();
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 5000, true));//
			// 设置搜索区域为以lp点为圆心，其周围5000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}
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
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		if (rcode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						//清除POI信息显示
						whetherToShowDetailInfo(false);
						//并还原点击marker样式
						if (mlastMarker != null) {
							resetlastmarker();
						}				
						//清理之前搜索结果的marker
						if (poiOverlay !=null) {
							poiOverlay.removeFromMap();
						}
						mAMap.clear();
						poiOverlay = new myPoiOverlay(mAMap, poiItems);
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
						
						mAMap.addMarker(new MarkerOptions()
						.anchor(0.5f, 0.5f)
						.icon(BitmapDescriptorFactory
								.fromBitmap(BitmapFactory.decodeResource(
										getResources(), R.drawable.point4)))
						.position(new LatLng(lp.getLatitude(), lp.getLongitude())));
						
						mAMap.addCircle(new CircleOptions()
						.center(new LatLng(lp.getLatitude(),
								lp.getLongitude())).radius(5000)
						.strokeColor(Color.BLUE)
						.fillColor(Color.argb(50, 1, 1, 1))
						.strokeWidth(2));

					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						ToastUtil.show(PoiAroundSearchActivity.this,
								R.string.no_result);
					}
				}
			} else {
				ToastUtil
						.show(PoiAroundSearchActivity.this, R.string.no_result);
			}
		} else  {
			ToastUtil.showerror(this.getApplicationContext(), rcode);
		}
	}


	@Override
	public boolean onMarkerClick(Marker marker) {
		
		if (marker.getObject() != null) {
			whetherToShowDetailInfo(true);
			try {
				PoiItem mCurrentPoi = (PoiItem) marker.getObject();
				if (mlastMarker == null) {
					mlastMarker = marker;
				} else {
					// 将之前被点击的marker置为原来的状态
					resetlastmarker();
					mlastMarker = marker;
				}
				detailMarker = marker;
				detailMarker.setIcon(BitmapDescriptorFactory
									.fromBitmap(BitmapFactory.decodeResource(
											getResources(),
											R.drawable.poi_marker_pressed)));

				setPoiItemDisplayContent(mCurrentPoi);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else {
			whetherToShowDetailInfo(false);
			resetlastmarker();
		}


		return true;
	}

	// 将之前被点击的marker置为原来的状态
	private void resetlastmarker() {
		int index = poiOverlay.getPoiIndex(mlastMarker);
		if (index < 10) {
			mlastMarker.setIcon(BitmapDescriptorFactory
					.fromBitmap(BitmapFactory.decodeResource(
							getResources(),
							markers[index])));
		}else {
			mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
			BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
		}
		mlastMarker = null;
		
	}


	private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
		mPoiName.setText(mCurrentPoi.getTitle());
		mPoiAddress.setText(mCurrentPoi.getSnippet()+mCurrentPoi.getDistance());
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			doSearchQuery();
			break;

		default:
			break;
		}
		
	}
	
	private int[] markers = {R.drawable.poi_marker_1,
			R.drawable.poi_marker_2,
			R.drawable.poi_marker_3,
			R.drawable.poi_marker_4,
			R.drawable.poi_marker_5,
			R.drawable.poi_marker_6,
			R.drawable.poi_marker_7,
			R.drawable.poi_marker_8,
			R.drawable.poi_marker_9,
			R.drawable.poi_marker_10
			};
	
	private void whetherToShowDetailInfo(boolean isToShow) {
		if (isToShow) {
			mPoiDetail.setVisibility(View.VISIBLE);

		} else {
			mPoiDetail.setVisibility(View.GONE);

		}
	}


	@Override
	public void onMapClick(LatLng arg0) {
		whetherToShowDetailInfo(false);
		if (mlastMarker != null) {
			resetlastmarker();
		}
	}
	
	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(this, infomation);

	}
	
	
	/**
	 * 自定义PoiOverlay
	 *
	 */
	
	private class myPoiOverlay {
		private AMap mamap;
		private List<PoiItem> mPois;
	    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
		public myPoiOverlay(AMap amap ,List<PoiItem> pois) {
			mamap = amap;
	        mPois = pois;
		}

	    /**
	     * 添加Marker到地图中。
	     * @since V2.1.0
	     */
	    public void addToMap() {
	    	if(mPois != null) {
				int size = mPois.size();
				for (int i = 0; i < size; i++) {
					Marker marker = mamap.addMarker(getMarkerOptions(i));
					PoiItem item = mPois.get(i);
					marker.setObject(item);
					mPoiMarks.add(marker);
				}
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
			if(mPois != null) {
				int size = mPois.size();
				for (int i = 0; i < size; i++) {
					b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
							mPois.get(i).getLatLonPoint().getLongitude()));
				}
			}
	        return b.build();
	    }

	    private MarkerOptions getMarkerOptions(int index) {
	        return new MarkerOptions()
	                .position(
	                        new LatLng(mPois.get(index).getLatLonPoint()
	                                .getLatitude(), mPois.get(index)
	                                .getLatLonPoint().getLongitude()))
	                .title(getTitle(index)).snippet(getSnippet(index))
	                .icon(getBitmapDescriptor(index));
	    }

	    protected String getTitle(int index) {
	        return mPois.get(index).getTitle();
	    }

	    protected String getSnippet(int index) {
	        return mPois.get(index).getSnippet();
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
	    public PoiItem getPoiItem(int index) {
	        if (index < 0 || index >= mPois.size()) {
	            return null;
	        }
	        return mPois.get(index);
	    }

		protected BitmapDescriptor getBitmapDescriptor(int arg0) {		
			if (arg0 < 10) {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
						BitmapFactory.decodeResource(getResources(), markers[arg0]));
				return icon;
			}else {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
						BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
				return icon;
			}	
		}
	}

}
