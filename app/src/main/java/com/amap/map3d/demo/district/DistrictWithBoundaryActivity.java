 

package com.amap.map3d.demo.district;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearch.OnDistrictSearchListener;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ThreadUtil;
import com.amap.map3d.demo.util.ToastUtil;

/**
 * 行政区划边界查询
 */
public class DistrictWithBoundaryActivity extends Activity implements OnClickListener,
		OnDistrictSearchListener {

	private Button mButton;
	private EditText mEditText;
	private MapView mMapView;

	private AMap mAMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.district_boundary_activity);
		mButton = (Button) findViewById(R.id.search_button);
		mEditText = (EditText) findViewById(R.id.city_text);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();
		mButton.setOnClickListener(this);

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
		mAMap.clear();
		DistrictSearch search = new DistrictSearch(getApplicationContext());
		DistrictSearchQuery query = new DistrictSearchQuery( );
 		query.setKeywords(mEditText.getText().toString());
		query.setShowBoundary(true);
		search.setQuery(query);

		search.setOnDistrictSearchListener(this);

		search.searchDistrictAsyn();

	}

	/**
	 * 返回District（行政区划）异步处理的结果
	 */
	@Override
	public void onDistrictSearched(DistrictResult districtResult) {
		if (districtResult == null || districtResult.getDistrict()==null) {
			return;
		}
		//通过ErrorCode判断是否成功
		if(districtResult.getAMapException() != null && districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
			final DistrictItem item = districtResult.getDistrict().get(0);

			if (item == null) {
				return;
			}
			LatLonPoint centerLatLng = item.getCenter();
			if (centerLatLng != null) {
				mAMap.moveCamera(

						CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 8));
			}


			ThreadUtil.getInstance().execute(new Runnable() {
				@Override
				public void run() {

					String[] polyStr = item.districtBoundary();
					if (polyStr == null || polyStr.length == 0) {
						return;
					}
					for (String str : polyStr) {
						String[] lat = str.split(";");
						PolylineOptions polylineOption = new PolylineOptions();
						boolean isFirst = true;
						LatLng firstLatLng = null;
						for (String latstr : lat) {
							String[] lats = latstr.split(",");
							if (isFirst) {
								isFirst = false;
								firstLatLng = new LatLng(Double
										.parseDouble(lats[1]), Double
										.parseDouble(lats[0]));
							}
							polylineOption.add(new LatLng(Double
									.parseDouble(lats[1]), Double
									.parseDouble(lats[0])));
						}
						if (firstLatLng != null) {
							polylineOption.add(firstLatLng);
						}

						polylineOption.width(10).color(Color.BLUE);
						mAMap.addPolyline(polylineOption);
					}
				}
			});
		} else {
			if(districtResult.getAMapException() != null) {
				ToastUtil.showerror(this.getApplicationContext(), districtResult.getAMapException().getErrorCode());
			}
		}

	}
}
