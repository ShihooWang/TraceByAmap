package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BaseHoleOptions;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleHoleOptions;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonHoleOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 包名： com.amap.map3d.demo.overlay
 * <p>
 * 创建时间：2017/10/27
 * 项目名称：AMap3DDemo
 *
 * @author guibao.ggb
 * @email guibao.ggb@alibaba-inc.com
 * <p>
 * 类说明：绘制空心多边形的功能介绍
 */
public class HoleActivity extends Activity {

    private MapView mapView;
    private AMap aMap;

    private Circle circle;
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hole_activity);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        init();

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        aMap.moveCamera(CameraUpdateFactory.zoomTo(8));
        CircleOptions circleOptions = new CircleOptions().center(Constants.BEIJING)
                .radius(100000).strokeColor(Color.argb(200, 1, 1, 1))
                .fillColor(Color.argb(200, 80, 1, 1)).strokeWidth(10);
        circle = aMap.addCircle(circleOptions);


        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(createRectangle(Constants.SHANGHAI, 1, 1))
                .fillColor(Color.LTGRAY).strokeColor(Color.RED).strokeWidth(10);

        polygon = aMap.addPolygon(polygonOptions);
    }

    /**
     * 为圆添加空心洞
     * @param view
     */
    public void clickCircleAddHole(View view) {

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(Constants.BEIJING));

        // 清除圆的空心洞
        circle.setHoleOptions(null);

        // 构建多边形空心配置项
        PolygonHoleOptions polygonOptions = new PolygonHoleOptions();
        polygonOptions.addAll(createRectangle(new LatLng(39.769485, 115.652035), 0.25, 0.25));

        // 构建圆形空心配置项
        CircleHoleOptions circleOptions = new CircleHoleOptions();
        circleOptions.center(Constants.BEIJING).radius(10000f);

        List<BaseHoleOptions> list = new ArrayList<>();

        list.add(polygonOptions);
        list.add(circleOptions);

        // 添加空心洞
        circle.setHoleOptions(list);
    }

    /**
     * 为多边形添加空心洞
     * @param view
     */
    public void clickPolygonAddHole(View view) {

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(Constants.SHANGHAI));

        // 清除多边形的空心洞
        polygon.setHoleOptions(null);

        // 构建多边形空心配置项
        PolygonHoleOptions polygonOptions = new PolygonHoleOptions();
        polygonOptions.addAll(createRectangle(new LatLng(31.238068, 121.501654), 0.25, 0.25));

        // 构建圆形空心配置项
        LatLng latLng = new LatLng(30.746626, 120.756966);
        CircleHoleOptions circleOptions = new CircleHoleOptions();
        circleOptions.center(latLng).radius(15000f);

        List<BaseHoleOptions> list = new ArrayList<>();

        list.add(polygonOptions);
        list.add(circleOptions);

        // 添加空心洞
        polygon.setHoleOptions(list);
    }


    /**
     * 生成一个长方形的四个坐标点
     */
    private List<LatLng> createRectangle(LatLng center, double halfWidth,
                                         double halfHeight) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        return latLngs;
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
