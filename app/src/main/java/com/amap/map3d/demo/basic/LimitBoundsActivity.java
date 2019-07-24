package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.map3d.demo.R;

/**
 * 设置显示区域
 */
public class LimitBoundsActivity extends Activity {

    private MapView mapView;
    private AMap aMap;

    // 西南坐标
    private LatLng southwestLatLng = new LatLng(39.674949, 115.932873);
    // 东北坐标
    private LatLng northeastLatLng = new LatLng(40.159453, 116.767834);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.limit_bounds_activity);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();

        aMap.addMarker(new MarkerOptions().position(southwestLatLng));
        aMap.addMarker(new MarkerOptions().position(northeastLatLng));

        aMap.moveCamera(CameraUpdateFactory.zoomTo(8f));

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
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

    /**
     * 设置限制区域
     * @param view
     */
    public void set(View view) {

        LatLngBounds latLngBounds = new LatLngBounds(southwestLatLng, northeastLatLng);
        aMap.setMapStatusLimits(latLngBounds);

    }

}
