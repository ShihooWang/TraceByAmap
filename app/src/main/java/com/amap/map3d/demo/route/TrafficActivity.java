package com.amap.map3d.demo.route;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.traffic.CircleTrafficQuery;
import com.amap.api.services.traffic.RoadTrafficQuery;
import com.amap.api.services.traffic.TrafficSearch;
import com.amap.api.services.traffic.TrafficStatusInfo;
import com.amap.api.services.traffic.TrafficStatusResult;
import com.amap.map3d.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 包名： com.amapsearch.apis.traffic
 * <p>
 * 创建时间：2017/4/12
 * 项目名称：AndroidSearchSDK
 *
 * <p>
 * 类说明：
 */
public class TrafficActivity extends Activity implements View.OnClickListener, TrafficSearch.OnTrafficSearchListener {

    private AMap aMap;
    private MapView mapView;

    ProgressDialog progDialog;

    TrafficSearch trafficSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_activity);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        aMap = mapView.getMap();

        findViewById(R.id.traffic_road).setOnClickListener(this);
        findViewById(R.id.traffic_circle).setOnClickListener(this);

        trafficSearch = new TrafficSearch(this);
        trafficSearch.setTrafficSearchListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.traffic_road :
                loadRoadTraffic();
                break;
            case R.id.traffic_circle :
                loadCircleTraffic();
                break;
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void loadRoadTraffic() {

        showProgressDialog();
        RoadTrafficQuery roadTrafficQuery = new RoadTrafficQuery("北环大道", "440300", TrafficSearch.ROAD_LEVEL_NORMAL_WAY);
        trafficSearch.loadTrafficByRoadAsyn(roadTrafficQuery);
    }

    private void loadCircleTraffic() {

        showProgressDialog();
        LatLonPoint latLonPoint = new LatLonPoint(39.98641364, 116.3057764);
        CircleTrafficQuery trafficQuery = new CircleTrafficQuery(latLonPoint, 500, TrafficSearch.ROAD_LEVEL_NORMAL_WAY);
        trafficSearch.loadTrafficByCircleAsyn(trafficQuery);
    }

    @Override
    public void onRoadTrafficSearched(TrafficStatusResult roadTrafficResult, int errorCode) {
        Log.e("amap", "===>>>> result code " + errorCode);
        dissmissProgressDialog();// 隐藏对话框
        aMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(TrafficStatusInfo trafficStatusInfo : roadTrafficResult.getRoads()) {
            List<LatLonPoint> list = trafficStatusInfo.getCoordinates();
            if(list != null){
                LatLonPoint latLonPoint = list.get(0);

                aMap.addText(new TextOptions().position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude())).text(trafficStatusInfo.getName() + " " + trafficStatusInfo.getDirection()));

                List<LatLng> latLngs = new ArrayList<LatLng>();
                for(LatLonPoint latLonPoint2 : list)
                {
                    LatLng latLng = new LatLng(latLonPoint2.getLatitude(), latLonPoint2.getLongitude());
                    builder.include(latLng);
                    latLngs.add(latLng);
                }
                int color = Color.GREEN;
                switch (Integer.parseInt(trafficStatusInfo.getStatus())) {
                    case 0: {
                        color = Color.GRAY;
                    }

                    break;
                    case 1: {
                        color = Color.GREEN;
                    }

                    break;
                    case 2: {
                        color = Color.YELLOW;
                    }

                    break;
                    case 3: {
                        color = Color.RED;
                    }

                    break;
                }
                aMap.addPolyline((new PolylineOptions()).addAll(latLngs).color(color));
            }
        }

        LatLngBounds bounds  = builder.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,10));

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}