package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class BasicMapActivity extends Activity implements OnClickListener{
	private MapView mapView;
	private AMap aMap;
	private Button basicmap;
	private Button rsmap;
	private Button nightmap;
	private Button navimap;

	private CheckBox mStyleCheckbox;


	private CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basicmap_activity);
	    /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
		//Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		//  MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);

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
		setMapCustomStyleFile(this);
		basicmap = (Button)findViewById(R.id.basicmap);
		basicmap.setOnClickListener(this);
		rsmap = (Button)findViewById(R.id.rsmap);
		rsmap.setOnClickListener(this);
		nightmap = (Button)findViewById(R.id.nightmap);
		nightmap.setOnClickListener(this);
		navimap = (Button)findViewById(R.id.navimap);
		navimap.setOnClickListener(this);

		mStyleCheckbox = (CheckBox) findViewById(R.id.check_style);

		mStyleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if(mapStyleOptions != null) {
					// 设置自定义样式
					mapStyleOptions.setEnable(b);
//					mapStyleOptions.setStyleId("your id");
					aMap.setCustomMapStyle(mapStyleOptions);
				}
			}
		});

	}

	private void setMapCustomStyleFile(Context context) {
		String styleName = "style_new.data";
		InputStream inputStream = null;
		try {
			inputStream = context.getAssets().open(styleName);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);

			if(mapStyleOptions != null) {
				// 设置自定义样式
				mapStyleOptions.setStyleData(b);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.basicmap:
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
				break;
			case R.id.rsmap:
				aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
				break;
			case R.id.nightmap:
				aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
				break;
			case R.id.navimap:
				aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
				break;
		}

		mStyleCheckbox.setChecked(false);

	}

}
