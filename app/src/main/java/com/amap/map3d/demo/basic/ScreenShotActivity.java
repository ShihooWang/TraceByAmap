package com.amap.map3d.demo.basic;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.map3d.demo.util.Constants;
import com.amap.map3d.demo.util.ToastUtil;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中对截屏简单介绍
 */
public class ScreenShotActivity extends Activity implements
		OnMapScreenShotListener {
	private AMap aMap;
	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screenshot_activity);
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
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	/**
	 * 对地图添加一个marker
	 */
	private void setUpMap() {
		aMap.addMarker(new MarkerOptions().position(Constants.FANGHENG)
				.title("方恒").snippet("方恒国际中心大楼A座"));
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
	 * 对地图进行截屏
	 */
	public void getMapScreenShot(View v) {
		aMap.getMapScreenShot(this);
	}

	/**
	 * 截屏时回调的方法。
	 *
	 * @param bitmap 调用截屏接口返回的截屏对象。
	 */
	@Override
	public void onMapScreenShot(Bitmap bitmap) {
		
	}

	/**
	 * 带有地图渲染状态的截屏回调方法。
	 * 根据返回的状态码，可以判断当前视图渲染是否完成。
	 *
	 * @param bitmap 调用截屏接口返回的截屏对象。
	 * @param arg1 地图渲染状态， 1：地图渲染完成，0：未绘制完成
	 */
	@Override
	public void onMapScreenShot(Bitmap bitmap, int arg1) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if(null == bitmap){
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(
					Environment.getExternalStorageDirectory() + "/test_"
							+ sdf.format(new Date()) + ".png");
			boolean b = bitmap.compress(CompressFormat.PNG, 100, fos);
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			StringBuffer buffer = new StringBuffer();
			if (b)
				buffer.append("截屏成功 ");
			else {
				buffer.append("截屏失败 ");
			}
			if (arg1 != 0)
				buffer.append("地图渲染完成，截屏无网格");
			else {
				buffer.append( "地图未渲染完成，截屏有网格");
			}
			ToastUtil.show(ScreenShotActivity.this, buffer.toString());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
