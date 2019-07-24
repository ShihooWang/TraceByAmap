package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.map3d.demo.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class ProvinceHoleActivity extends Activity implements DistrictSearch.OnDistrictSearchListener {
    private AMap aMap;
    private MapView mapView;
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        setContentView(mapView);
        init();




    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {

        List<LatLng> latLngs = new ArrayList();
        //绘制一个全世界的多边形
        latLngs.add(new LatLng(84.9, -179.9));
        latLngs.add(new LatLng(84.9, 179.9));
        latLngs.add(new LatLng(-84.9, 179.9));
        latLngs.add(new LatLng(-84.9, -179.9));

        polygon = aMap.addPolygon(new PolygonOptions().addAll(latLngs).fillColor(Color.rgb(245,245,245)).zIndex(10));

        searchDistrict();

        aMap.moveCamera(CameraUpdateFactory.zoomTo(8));

    }

    private void searchDistrict() {
        String province = "北京市";

        DistrictSearch districtSearch = new DistrictSearch(getApplicationContext());
        DistrictSearchQuery districtSearchQuery = new DistrictSearchQuery( );
        districtSearchQuery.setKeywords(province);
        districtSearchQuery.setShowBoundary(true);
        districtSearchQuery.setShowChild(true);
        districtSearch.setQuery(districtSearchQuery);

        districtSearch.setOnDistrictSearchListener(ProvinceHoleActivity.this);

        //请求边界数据 开始展示
        districtSearch.searchDistrictAsyn();
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
    public void onDistrictSearched(DistrictResult districtResult) {
        if (districtResult == null || districtResult.getDistrict()==null) {
            return;
        }
        showDistrictBounds(districtResult);

    }


    protected void showDistrictBounds(DistrictResult districtResult) {
        //通过ErrorCode判断是否成功
        if(districtResult.getAMapException() != null && districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
            final DistrictItem item = districtResult.getDistrict().get(0);

            if (item == null) {
                return;
            }

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    String[] polyStr = item.districtBoundary();
                    if (polyStr == null || polyStr.length == 0) {
                        return;
                    }


                    List<com.amap.api.maps.model.BaseHoleOptions> holeOptionsList = new ArrayList<>();
                    for (String str : polyStr) {
                        String[] lat = str.split(";");
                        com.amap.api.maps.model.PolygonHoleOptions polygonHoleOptions = new com.amap.api.maps.model.PolygonHoleOptions();
                        List<LatLng> holeLatLngs = new ArrayList<>();
                        boolean isFirst = true;
                        LatLng firstLatLng = null;
                        int index = 0;

                        // 可以过滤掉一些点，提升绘制效率
                        int  offset = 50;
                        if(lat.length < 400) {
                            offset = 20;
                        }

                        for (String latstr : lat) {
                            index ++;
                            if(index % offset !=0) {
                                continue;
                            }
                            String[] lats = latstr.split(",");
                            if (isFirst) {
                                isFirst = false;
                                firstLatLng = new LatLng(Double
                                        .parseDouble(lats[1]), Double
                                        .parseDouble(lats[0]));
                            }
                            holeLatLngs.add(new LatLng(Double
                                    .parseDouble(lats[1]), Double
                                    .parseDouble(lats[0])));
                        }
                        if (firstLatLng != null) {
                            holeLatLngs.add(firstLatLng);
                        }

                        polygonHoleOptions.addAll(holeLatLngs);
                        holeOptionsList.add(polygonHoleOptions);
                    }

                    if(holeOptionsList.size() > 0) {
                        polygon.setHoleOptions(holeOptionsList);
                    }

                }
            });
        } else {
            if(districtResult.getAMapException() != null) {
                Log.e("amap","error "+ districtResult.getAMapException().getErrorCode());
            }
        }
    }


}
