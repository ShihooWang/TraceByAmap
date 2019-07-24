package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.particle.ParticleOverlay;
import com.amap.api.maps.model.particle.ParticleOverlayOptions;
import com.amap.api.maps.model.particle.ParticleOverlayOptionsFactory;
import com.amap.api.maps.model.particle.RandomVelocityBetweenTwoConstants;
import com.amap.map3d.demo.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class ParticleMapActivity extends Activity{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particle_map_activity);

        mapView = findViewById(R.id.map);
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

    public void onClick(View view) {


        synchronized (ParticleMapActivity.class) {

            String key = "";

            List<ParticleOverlay> particleOverlayList = null;


            List<ParticleOverlayOptions> optionsList = null;

            int id = view.getId();
            switch (id) {
                case R.id.sun:
                    key = "sun";

                    particleOverlayList = particleMaps.get(key);
                    if (particleOverlayList != null) {
                        break;
                    }

                    particleOverlayList = new ArrayList<ParticleOverlay>();

                    optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_SUNNY);
                    for (ParticleOverlayOptions options : optionsList) {
                        ParticleOverlay particleOverlay = aMap.addParticleOverlay(options);
                        // 可以根据屏幕大小修改一下宽高
                        particleOverlayList.add(particleOverlay);
                    }
                    particleMaps.put(key, particleOverlayList);


                    break;
                case R.id.rain:
                    key = "rain";

                    particleOverlayList = particleMaps.get(key);
                    if (particleOverlayList != null) {
                        break;
                    }

                    particleOverlayList = new ArrayList<ParticleOverlay>();

                    optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_RAIN);
                    for (ParticleOverlayOptions options : optionsList) {
                        ParticleOverlay particleOverlay = aMap.addParticleOverlay(options);
                        // 可以根据屏幕大小修改一下宽高
                        particleOverlayList.add(particleOverlay);
                    }
                    particleMaps.put(key, particleOverlayList);

                    break;
                case R.id.snow:
                    key = "snow";

                    particleOverlayList = particleMaps.get(key);
                    if (particleOverlayList != null) {
                        break;
                    }

                    particleOverlayList = new ArrayList<ParticleOverlay>();

                    optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_SNOWY);
                    for (ParticleOverlayOptions options : optionsList) {
                        ParticleOverlay particleOverlay = aMap.addParticleOverlay(options);
                        // 可以根据屏幕大小修改一下宽高
                        particleOverlayList.add(particleOverlay);
                    }
                    particleMaps.put(key, particleOverlayList);
                    break;
                case R.id.haze:
                    key = "haze";

                    particleOverlayList = particleMaps.get(key);
                    if (particleOverlayList != null) {
                        break;
                    }

                    particleOverlayList = new ArrayList<ParticleOverlay>();

                    optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_HAZE);

                    // 雾霾由两个粒子效果组成，一个是底下滑动的背景，另一个是上面黑点
                    int index = 0;
                    for (ParticleOverlayOptions options : optionsList) {
                        if(index == 0) {
                            // 将背景图片放大，避免部分大屏幕无法铺满的问题
                            options.setStartParticleSize(2* mapView.getWidth(),2*mapView.getHeight());
                        }

                        ParticleOverlay particleOverlay = aMap.addParticleOverlay(options);
                        // 可以根据屏幕大小修改一下宽高
                        particleOverlayList.add(particleOverlay);



                        index ++;
                    }
                    particleMaps.put(key, particleOverlayList);
                    break;
                case R.id.custom:
                    key = "custom";

                    particleOverlayList = particleMaps.get(key);
                    if (particleOverlayList != null) {
                        break;
                    }

                    particleOverlayList = new ArrayList<ParticleOverlay>();

                    com.amap.api.maps.model.particle.ParticleOverlayOptions particleSystemOptions = new com.amap.api.maps.model.particle.ParticleOverlayOptions();
                    particleSystemOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.heart));

                    int time = 10000;
                    // 设置最大数量，持续时间，是否循环
                    particleSystemOptions.setMaxParticles(100);
                    particleSystemOptions.setDuration(time);
                    particleSystemOptions.setLoop(true);

                    // 设置每个粒子 存活时间，以及发射时的状态
                    particleSystemOptions.setParticleLifeTime(time);
                    particleSystemOptions.setParticleStartSpeed(new RandomVelocityBetweenTwoConstants(10, 10, 0, 100, 100, 0));

                    // 设置发射率
                    particleSystemOptions.setParticleEmissionModule(new com.amap.api.maps.model.particle.ParticleEmissionModule(10, 1000));

                    // 设置发射位置
                    particleSystemOptions.setParticleShapeModule(new com.amap.api.maps.model.particle.SinglePointParticleShape(0.1f, 0.1f, 0, true));

                    // 设置每个粒子生命周期过程中状态变化，包含速度、旋转和颜色的变化
                    com.amap.api.maps.model.particle.ParticleOverLifeModule particleOverLifeModule = new com.amap.api.maps.model.particle.ParticleOverLifeModule();
//            particleOverLifeModule.setVelocityOverLife(new com.amap.api.maps.model.particle.RandomVelocityBetweenTwoConstants(1, 1,0,5,5,0));
                    particleOverLifeModule.setRotateOverLife(new com.amap.api.maps.model.particle.ConstantRotationOverLife(45));
                    particleSystemOptions.setParticleOverLifeModule(particleOverLifeModule);

                    particleSystemOptions.setParticleStartColor(new com.amap.api.maps.model.particle.RandomColorBetWeenTwoConstants(
                            225, 137, 134, 125,
                            198, 195, 42, 255));

                    //修正一下比例, 默认都会被绘制成1像素，设置自己的宽高,需要乘上一个比例
                    particleSystemOptions.setStartParticleSize(64, 64);

                    ParticleOverlay particleOverlay = aMap.addParticleOverlay(particleSystemOptions);
                    particleOverlayList.add(particleOverlay);

                    particleMaps.put(key, particleOverlayList);
                    break;
                default:
                    break;
            }


            //隐藏之前的
            for (ParticleOverlay particleOverlay : curParticleOverlayList) {
                particleOverlay.setVisible(false);
            }

            curParticleOverlayList.clear();


            if (particleOverlayList == null) {
                return;
            }
            //记录并显示现在的
            curParticleOverlayList.addAll(particleOverlayList);
            for (ParticleOverlay particleOverlay : curParticleOverlayList) {
                particleOverlay.setVisible(true);
            }

        }


    }
}
