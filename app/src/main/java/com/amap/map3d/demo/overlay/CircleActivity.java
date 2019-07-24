package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.Constants;

/**
 * AMapV2地图中简单介绍一些Circle的用法.
 */
public class CircleActivity extends Activity implements OnSeekBarChangeListener {
    private static final int WIDTH_MAX = 50;
    private static final int HUE_MAX = 255;
    private static final int ALPHA_MAX = 255;
    private AMap aMap;
    private MapView mapView;
    private Circle circle;
    private SeekBar mColorBar;
    private SeekBar mAlphaBar;
    private SeekBar mWidthBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

        addPolylinescircle(Constants.BEIJING, 5000);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
        mColorBar.setMax(HUE_MAX);
        mColorBar.setProgress(50);

        mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
        mAlphaBar.setMax(ALPHA_MAX);
        mAlphaBar.setProgress(50);

        mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
        mWidthBar.setMax(WIDTH_MAX);
        mWidthBar.setProgress(25);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        mColorBar.setOnSeekBarChangeListener(this);
        mAlphaBar.setOnSeekBarChangeListener(this);
        mWidthBar.setOnSeekBarChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(Constants.BEIJING, 12));// 设置指定的可视区域地图
        // 绘制一个圆形
        circle = aMap.addCircle(new CircleOptions().center(Constants.BEIJING)
                .radius(4000).strokeColor(Color.argb(50, 1, 1, 1))
                .fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(25));


        //画虚线圆

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
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Circle中对填充颜色，透明度，画笔宽度设置响应事件
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (circle == null) {
            return;
        }
        if (seekBar == mColorBar) {
            circle.setFillColor(Color.argb(progress, 1, 1, 1));
        } else if (seekBar == mAlphaBar) {
            circle.setStrokeColor(Color.argb(progress, 1, 1, 1));
        } else if (seekBar == mWidthBar) {
            circle.setStrokeWidth(progress);
        }


    }

    /**
     *
     * @param centerpoint 中心点坐标
     * @param radius      半径 米
     */
    public void addPolylinescircle(LatLng centerpoint, int radius) {
        double r = 6371000.79;
        PolylineOptions options = new PolylineOptions();
        int numpoints = 360;
        double phase = 2 * Math.PI / numpoints;

        //画图
        for (int i = 0; i < numpoints; i++) {
            double dx = (radius * Math.cos(i * phase));
            double dy = (radius * Math.sin(i * phase));//乘以1.6 椭圆比例

            double dlng = dx / (r * Math.cos(centerpoint.latitude * Math.PI / 180) * Math.PI / 180);
            double dlat = dy / (r * Math.PI / 180);
            double newlng = centerpoint.longitude + dlng;
            options.add(new LatLng(centerpoint.latitude + dlat, newlng));
        }

        aMap.addPolyline(options.width(10).useGradient(true).setDottedLine(true));

    }
}
