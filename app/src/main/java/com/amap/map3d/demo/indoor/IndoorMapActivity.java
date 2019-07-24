package com.amap.map3d.demo.indoor;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.IndoorBuildingInfo;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;


/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class IndoorMapActivity extends Activity {
	private MapView mapView;
	private AMap aMap;

    IndoorFloorSwitchView floorSwitchView;
    private Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indoormap_activity);
	    /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
      //  MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写


       /* //放置室内地图楼层控制控件
        floorSwitchView = new IndoorFloorSwitchView(this);

        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,400);

        List<String> list = new ArrayList<String>();
        list.add("F1");
        list.add("F2");
        list.add("F3");
//        list.add("F4");
//        list.add("F5");

        floorSwitchView.setItems(list);
        floorSwitchView.setSeletion("F2");

        addContentView(floorSwitchView, layoutParams);*/

        floorSwitchView = (IndoorFloorSwitchView) findViewById(R.id.indoor_switchview);

		init();


        // 设置楼层控制控件监听
        floorSwitchView.setOnIndoorFloorSwitchListener(new MyIndoorSwitchViewAdapter());


        // 设置室内地图回调监听
        aMap.setOnIndoorBuildingActiveListener(new AMap.OnIndoorBuildingActiveListener() {
            @Override
            public void OnIndoorBuilding(final IndoorBuildingInfo indoorBuildingInfo) {
                Log.i("amap", "indoor OnIndoorBuilding " + indoorBuildingInfo);
                if(indoorBuildingInfo != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
							floorSwitchView.setVisible(true);
                            //相同室内图，不需要替换楼层总数，只需要设置选中的楼层即可
							if(mIndoorBuildingInfo == null || !mIndoorBuildingInfo.poiid.equals(indoorBuildingInfo.poiid)) {
                                floorSwitchView
                                        .setItems(indoorBuildingInfo.floor_names);
								floorSwitchView
										.setSeletion(indoorBuildingInfo.activeFloorName);
                            }


                            mIndoorBuildingInfo = indoorBuildingInfo;
                        }
                    });
                } else {
					Log.i("amap", "indoor OnIndoorBuilding  indoor disappear");
					floorSwitchView.setVisible(false);
				}
            }
        });

		
		aMap.setOnMapLoadedListener(new OnMapLoadedListener() {
			
			@Override
			public void onMapLoaded() {
				// 室内地图默认不显示，这里把它设置成显示
				aMap.showIndoorMap(true);
                // 关闭SDK自带的室内地图控件
                aMap.getUiSettings().setIndoorSwitchEnabled(false);
				
				//移动到有室内地图的地方,放大级别才可以看见
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.91095, 116.37296), 19));
				
			}
		});
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		
            aMap.getUiSettings().setScaleControlsEnabled(true);
		
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
     * 室内地图信息
     */
    IndoorBuildingInfo mIndoorBuildingInfo = null;


    private class MyIndoorSwitchViewAdapter implements
            IndoorFloorSwitchView.OnIndoorFloorSwitchListener {

        @Override
        public void onSelected(final int selectedIndex) {
            Log.i("amap", "indoor onselected " + selectedIndex);
            if (mIndoorBuildingInfo != null) {
                mIndoorBuildingInfo.activeFloorIndex = mIndoorBuildingInfo.floor_indexs[selectedIndex];
                mIndoorBuildingInfo.activeFloorName = mIndoorBuildingInfo.floor_names[selectedIndex];

                aMap.setIndoorBuildingInfo(mIndoorBuildingInfo);

            }
        }

    }

}
