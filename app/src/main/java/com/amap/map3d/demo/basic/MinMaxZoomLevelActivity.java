package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.map3d.demo.R;

/**
 * 设置最大最小级别示例
 */
public class MinMaxZoomLevelActivity extends Activity {

    private MapView mapView;
    private AMap aMap;

    private EditText textMinZoomLevel;
    private EditText textMaxZoomLevel;
    private TextView textCurrentLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.min_max_zoomlevel_activity);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();

        textMinZoomLevel = (EditText) findViewById(R.id.text_min);
        textMaxZoomLevel = (EditText) findViewById(R.id.text_max);
        textCurrentLevel = (TextView) findViewById(R.id.text_current_level);

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                textCurrentLevel.setText("当前地图的缩放级别为: " + cameraPosition.zoom);
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                textCurrentLevel.setText("当前地图的缩放级别为: " + aMap.getCameraPosition().zoom);
            }
        });

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
     * 设置最大最小缩放级别
     * @param view
     */
    public void set(View view) {
        String minZoomLevel = textMinZoomLevel.getText().toString();
        String maxZoomLevel = textMaxZoomLevel.getText().toString();

        if (minZoomLevel.length() > 0) {
            aMap.setMinZoomLevel(Float.valueOf(minZoomLevel));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(Float.valueOf(minZoomLevel)));
        }

        if (maxZoomLevel.length() > 0) {
            aMap.setMaxZoomLevel(Float.valueOf(maxZoomLevel));
        }


    }

    /**
     * 重置最大最小缩放级别
     * @param view
     */
    public void reset(View view) {
        aMap.resetMinMaxZoomPreference();
        textMinZoomLevel.setText("");
        textMaxZoomLevel.setText("");
    }

}
