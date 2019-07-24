package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BuildingOverlay;
import com.amap.api.maps.model.BuildingOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shixin
 * @date 2018/6/1
 */

public class BuildingOverlayActivity extends Activity {
    private MapView mapView;
    private AMap map;
    private BuildingOverlay buildingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_overlay);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.922632, 116.391874), 17));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 添加自定义建筑物图层
     *
     * @param view
     */
    public void addBuildOverlay(View view) {
        if (buildingOverlay == null) {
            buildingOverlay = map.addBuildingOverlay();
            map.showBuildings(false);
            map.showMapText(false);
            buildingOverlay.setVisible(true);
            buildingOverlay.setCustomOptions(getOptions());
        }
    }

    /**
     * 改变默认默认区域的楼块颜色。（不包括setCustomeOptions设置的自定义区域）
     *
     * @param view
     */
    public void changeBuildingColor(View view) {
        if (buildingOverlay != null) {
            BuildingOverlayOptions newOption = buildingOverlay.getDefaultOptions().setBuildingTopColor(Color.GRAY);
            buildingOverlay.setDefaultOptions(newOption);
        }
    }

    /**
     * 改变默认区域建筑的高度。（不包括setCustomeOptions设置的自定义区域）
     *
     * @param view
     */
    public void changeBuildingHight(View view) {
        if (buildingOverlay != null) {
            BuildingOverlayOptions newOption = buildingOverlay.getDefaultOptions().setBuildingHeight(200);
            buildingOverlay.setDefaultOptions(newOption);
        }
    }

    public void destroyBuildOverlay(View view) {
        if (buildingOverlay != null) {
            buildingOverlay.destroy();
            buildingOverlay = null;
        }
    }

    /**
     * 构造故宫区域的楼块显示为顶部黄色，侧面绿色
     *
     * @return
     */
    private List<BuildingOverlayOptions> getOptions() {
        /**
         * 故宫
         */
        BuildingOverlayOptions overlayOption = new BuildingOverlayOptions();
        overlayOption.setBuildingTopColor(Color.YELLOW);
        overlayOption.setBuildingSideColor(Color.GREEN);
        List<LatLng> latLngs = new ArrayList();
        /**
         * 注意:传入的点有顺序要求，需要按照区域形状的顺时针依次传入
         */
        latLngs.add(new LatLng(39.922632, 116.391874));
        latLngs.add(new LatLng(39.923126, 116.401637));
        latLngs.add(new LatLng(39.913647, 116.402152));
        latLngs.add(new LatLng(39.913252, 116.392668));


        overlayOption.setBuildingLatlngs(latLngs);

        List<BuildingOverlayOptions> list = new ArrayList();
        list.add(overlayOption);
        return list;
    }
}
