package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.map3d.demo.R;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 海量点demo实例
 */
public class MultiPointOverlayActivity extends Activity  {
	private MapView mapView;
	private AMap aMap;

	Marker marker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView = new MapView(this);
		setContentView(mapView);

		mapView.onCreate(savedInstanceState);// 此方法必须重写

		init();


		//添加一个Marker用来展示海量点点击效果
		marker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_blue);

		MultiPointOverlayOptions overlayOptions = new MultiPointOverlayOptions();
		overlayOptions.icon(bitmapDescriptor);
		overlayOptions.anchor(0.5f,0.5f);

		final MultiPointOverlay multiPointOverlay = aMap.addMultiPointOverlay(overlayOptions);
		aMap.setOnMultiPointClickListener(new AMap.OnMultiPointClickListener() {
			@Override
			public boolean onPointClick(MultiPointItem pointItem) {
				android.util.Log.i("amap ", "onPointClick");

				if(marker.isRemoved()) {
					 //调用amap clear之后会移除marker，重新添加一个
					marker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
				}
				//添加一个Marker用来展示海量点点击效果
				marker.setPosition(pointItem.getLatLng());
				marker.setToTop();
				return false;
			}
		});

		aMap.moveCamera(CameraUpdateFactory.zoomTo(3));

        showProgressDialog();
		//开启子线程读取文件
		new Thread(new Runnable() {
			@Override
			public void run() {

				List<MultiPointItem> list = new ArrayList<MultiPointItem>();
				FileOutputStream outputStream = null;
				InputStream inputStream = null;
				String filePath = null;
				try {
					inputStream = MultiPointOverlayActivity.this.getResources().openRawResource(R.raw.point10w);
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String line = "";
					while((line = bufferedReader.readLine()) != null) {
						if(isDestroy) {
							//已经销毁地图了，退出循环
							return;
						}

						String[] str = line.split(",");
						if(str == null) {
							continue;
						}
						double lat = Double.parseDouble(str[1].trim());
						double lon = Double.parseDouble(str[0].trim());

						LatLng latLng = new LatLng(lat, lon, false);//保证经纬度没有问题的时候可以填false

						MultiPointItem multiPointItem = new MultiPointItem(latLng);
						list.add(multiPointItem);
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (inputStream != null)
							inputStream.close();

						if (outputStream != null)
							outputStream.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if(multiPointOverlay != null) {
					multiPointOverlay.setItems(list);
					multiPointOverlay.setEnable(true);
				}

                dissmissProgressDialog();

				//
			}
		}).start();

	}


	boolean isDestroy = false;

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}

	}

    private ProgressDialog progDialog = null;// 添加海量点时
    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在解析添加海量点中，请稍后...");
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
        dissmissProgressDialog();
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
		isDestroy = true;
	}



}
