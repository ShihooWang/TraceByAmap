package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.particle.ParticleOverlay;
import com.amap.api.maps.model.particle.ParticleOverlayOptions;
import com.amap.api.maps.model.particle.ParticleOverlayOptionsFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class ParticleWeatherMapActivity extends Activity {
    private MapView mapView;
    private AMap aMap;

    /**
     * 当前正在显示的粒子系统
     */
    private List<ParticleOverlay> curParticleOverlayList = new ArrayList<ParticleOverlay>();

    /**
     * 所有粒子系统集合
     */
    private Hashtable<String, List<ParticleOverlay>> particleMaps = new Hashtable<String, List<ParticleOverlay>>();


    /**
     * 图片缓存
     */
    private Hashtable<String, BitmapDescriptor> bitmapDescriptorHashtable = new Hashtable<String, BitmapDescriptor>();


    /**
     * Marker 集合
     */
    private List<Marker> markerList = new ArrayList<Marker>();


    /**
     * 记录marker是否显示
     */
    private boolean isWeatherMarkerShown = false;

    /**
     * 记录粒子效果显示情况
     */
    private boolean isParticleOverlayShown = false;


    /**
     * 天气Marker显示的最大缩放级别，超过这个级别，marker会隐藏
     */
    private final static int MAX_SHOW_WEATHER_MARKER_ZOOM = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        setContentView(mapView);
        init();


        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {

                initWeather();

                aMap.moveCamera(CameraUpdateFactory.zoomTo(4));
                aMap.showMapText(false);
            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(marker != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), MAX_SHOW_WEATHER_MARKER_ZOOM + 1));
                }

                return false;
            }
        });

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition position) {


                changeWeatherMarkerState(position);

                changeParticleOverlayState(position);

                // 低级别不显示文字 效果更佳
                if(isParticleOverlayShown) {
                    aMap.showMapText(true);

                } else {
                    aMap.showMapText(false);
                }
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);

            }
        });


    }

    /**
     * 修改粒子系统的效果
     *
     * @param position
     */
    private void changeParticleOverlayState(CameraPosition position) {
        //在8-20级的级别下，展示省份/城市级别的天气图层
        if (position != null) {
            float zoom = position.zoom;

            String key = getShowWeatherPositio(position);
            if(key == null) {
                return;
            }

            if (zoom > MAX_SHOW_WEATHER_MARKER_ZOOM) {
                // 如果没有显示了进行显示操作
                if (!isParticleOverlayShown) {
                    synchronized (particleMaps) {
                        showParticle(key);
                    }
                    isParticleOverlayShown = true;
                }
            } else {
                // 如果显示了进行隐藏操作
                if (isParticleOverlayShown) {
                    hideCurrentParticleOverlay();
                    isParticleOverlayShown = false;
                }
            }
        }


    }

    /**
     * 获取是哪里需要显示天气
     *
     * @param position
     * @return
     */
    private String getShowWeatherPositio(CameraPosition position) {
        if(position == null) {
            return null;
        }

        List<Marker> markers = aMap.getMapScreenMarkers();

        // 去除离屏幕最近的marker
        Marker needShowMarker = null;
        float distance = 0;
        for (Marker marker : markers) {
            LatLng markerPos = marker.getPosition();

            float curDistanct = AMapUtils.calculateLineDistance(markerPos, position.target);

            if(distance == 0) {
                distance = curDistanct;
                needShowMarker = marker;
            } else {
                if(curDistanct < distance) {
                    needShowMarker = marker;
                }
            }

        }


        if(needShowMarker != null && needShowMarker.getObject() != null) {
            return (String) needShowMarker.getObject();
        }
        return null;
    }

    /**
     * 修改天气marker的显示情况
     *
     * @param position
     */
    private void changeWeatherMarkerState(CameraPosition position) {
        //.在3-7级的缩放级别下，以天气图标展示各地天气状态；
        if (position != null) {
            float zoom = position.zoom;
            if (zoom > MAX_SHOW_WEATHER_MARKER_ZOOM) {
                // 如果显示了进行隐藏操作
                if (isWeatherMarkerShown) {
                    synchronized (markerList) {
                        for (Marker marker : markerList) {
                            marker.setVisible(false);
                        }
                    }
                    isWeatherMarkerShown = false;
                }
            } else {
                // 如果没有显示了进行显示操作
                if (!isWeatherMarkerShown) {
                    synchronized (markerList) {
                        for (Marker marker : markerList) {
                            marker.setVisible(true);
                        }
                    }
                    isWeatherMarkerShown = true;
                }
            }
        }

    }


    /**
     * 初始化添加marker
     */
    private void initWeather() {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        boolean isNeedShowWeatherMarker = false;
        if (cameraPosition != null) {
            isNeedShowWeatherMarker = cameraPosition.zoom <= MAX_SHOW_WEATHER_MARKER_ZOOM;

            // 查看首次需不需要显示粒子效果
            changeParticleOverlayState(cameraPosition);
        }
        for (int i = 0; i < latLngs.length; i++) {
            LatLng latLng = latLngs[i];
            int index = i % pngPaths.length;

            // 天气状态随机选取，如果要使用到实际场景，这里需要修改成真是数据
            String path = pngPaths[index];

            BitmapDescriptor bitmapDescriptor = bitmapDescriptorHashtable.get(path);
            if (bitmapDescriptor == null) {
                bitmapDescriptor = BitmapDescriptorFactory.fromAsset(path);
                bitmapDescriptorHashtable.put(path, bitmapDescriptor);
            }

            Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptor).visible(isNeedShowWeatherMarker));
            if (marker != null) {
                marker.setObject(path);
                markerList.add(marker);
            }

        }

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


    public void showParticle(String type) {


        String key = type;

        List<ParticleOverlay> particleSystemList = particleMaps.get(key);

        if (particleSystemList == null) {

            List<ParticleOverlayOptions> optionsList = null;

            if (pngPaths[0].equals(key)) {

                particleSystemList = new ArrayList<ParticleOverlay>();

                //rain
                optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_RAIN);
                for (ParticleOverlayOptions options : optionsList) {
                    ParticleOverlay particleSystem = aMap.addParticleOverlay(options);
                    // 可以根据屏幕大小修改一下宽高
                    particleSystemList.add(particleSystem);
                }
                particleMaps.put(key, particleSystemList);

            } else if (pngPaths[1].equals(key)) {

                particleSystemList = new ArrayList<ParticleOverlay>();

                //snowy
                optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_SNOWY);
                for (ParticleOverlayOptions options : optionsList) {
                    ParticleOverlay particleSystem = aMap.addParticleOverlay(options);
                    // 可以根据屏幕大小修改一下宽高
                    particleSystemList.add(particleSystem);
                }
                particleMaps.put(key, particleSystemList);
            } else if (pngPaths[2].equals(key)) {

                particleSystemList = new ArrayList<ParticleOverlay>();

                //sun
                optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_SUNNY);
                for (ParticleOverlayOptions options : optionsList) {
                    ParticleOverlay particleSystem = aMap.addParticleOverlay(options);
                    // 可以根据屏幕大小修改一下宽高
                    particleSystemList.add(particleSystem);
                }
                particleMaps.put(key, particleSystemList);
            } else if (pngPaths[3].equals(key)) {

                particleSystemList = new ArrayList<ParticleOverlay>();

                //haze
                optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_HAZE);
                // 雾霾由两个粒子效果组成，一个是底下滑动的背景，另一个是上面黑点
                int index = 0;
                for (ParticleOverlayOptions options : optionsList) {
                    // 可以根据屏幕大小修改一下宽高
                    if(index == 0) {
                        // 将背景图片放大，避免部分大屏幕无法铺满的问题
                        options.setStartParticleSize(2* mapView.getWidth(),2*mapView.getHeight());
                    }

                    ParticleOverlay particleOverlay = aMap.addParticleOverlay(options);


                    particleSystemList.add(particleOverlay);
                    index ++;

                }
                particleMaps.put(key, particleSystemList);
            }
        }


        //隐藏之前的
        hideCurrentParticleOverlay();

        //记录并显示现在的
        curParticleOverlayList.addAll(particleSystemList);
        for (ParticleOverlay particleSystem : curParticleOverlayList) {
            particleSystem.setVisible(true);
        }


    }

    /**
     * 引擎当前正在显示的粒子效果
     */
    private void hideCurrentParticleOverlay() {
        for (ParticleOverlay particleSystem : curParticleOverlayList) {
            particleSystem.setVisible(false);
        }

        curParticleOverlayList.clear();
    }


    private String[] pngPaths = {
            "weather/baoyu.png",
            "weather/daxue.png",
            "weather/qing.png",
            "weather/wumai.png",
    };

    private LatLng[] latLngs = {
            new LatLng(39.904211, 116.407394),
            new LatLng(39.084158, 117.200983),
            new LatLng(38.037433, 114.530235),
            new LatLng(37.873499, 112.562678),
            new LatLng(40.81739, 111.76629),
            new LatLng(41.836175, 123.431382),
            new LatLng(43.897016, 125.32568),
            new LatLng(45.742366, 126.661665),
            new LatLng(31.230372, 121.473662),
            new LatLng(32.060875, 118.762765),
            new LatLng(30.266597, 120.152585),
            new LatLng(31.733806, 117.329949),
            new LatLng(26.100779, 119.295143),
            new LatLng(28.63666, 115.81635),
            new LatLng(36.671156, 117.019915),
            new LatLng(34.765869, 113.753394),
            new LatLng(30.546557, 114.341745),
            new LatLng(28.112743, 112.9836),
            new LatLng(23.132324, 113.26641),
            new LatLng(22.815478, 108.327546),
            new LatLng(20.017377, 110.349228),
            new LatLng(29.562849, 106.551643),
            new LatLng(30.651239, 104.075809),
            new LatLng(26.600055, 106.70546),
            new LatLng(25.045806, 102.710002),
            new LatLng(29.647535, 91.117525),
            new LatLng(34.265502, 108.954347),
            new LatLng(36.05956, 103.826447),
            new LatLng(36.620939, 101.780268),
            new LatLng(38.472641, 106.259126),
            new LatLng(43.793026, 87.627704),
            new LatLng(25.044332, 121.509062),
            new LatLng(22.277468, 114.171203),
            new LatLng(22.18683, 113.543028)
    };
}
