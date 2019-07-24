package com.amap.map3d.demo.basic.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.basic.BaseMapSupportFragmentActivity;
import com.amap.map3d.demo.basic.BasicMapActivity;
import com.amap.map3d.demo.basic.TextureMapViewActivity;

/**
 * 地图的六种实现方式：MapView、MapFragment、SupportMapFragment、TextureMapView、TextureMapFragment、TextureSupportMapFragment
 */
public class MapImpMethodActivity extends Activity {

    private MapView mapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_impmethod);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

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
     * 跳转到MapView 实现
     * @param view
     */
    public void forwardMapView(View view) {
        startActivity(new Intent(this, BasicMapActivity.class));
    }

    /**
     * 跳转到 MapFragment 实现
     * @param view
     */
    public void forwardMapFragment(View view) {
        startActivity(new Intent(this, BaseMapFragmentActivity.class));
    }

    /**
     * 跳转到 SupportMapFragment 实现
     * @param view
     */
    public void forwardSupportMapFragment(View view) {
        startActivity(new Intent(this, BaseMapSupportFragmentActivity.class));
    }

    /**
     * 跳转到 TextureMapView 实现
     * @param view
     */
    public void forwardTextureMapView(View view) {
        startActivity(new Intent(this, TextureMapViewActivity.class));
    }

    /**
     * 跳转到 TextureMapFragment 实现
     * @param view
     */
    public void forwardTextureMapFragment(View view) {
        startActivity(new Intent(this, BaseTextureMapFragmentActivity.class));
    }

    /**
     * 跳转到 TextureSupportMapFragment 实现
     * @param view
     */
    public void forwardTextureSupportMapFragment(View view) {
        startActivity(new Intent(this, BaseTextureSupportMapFragmentActivity.class));
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
}
