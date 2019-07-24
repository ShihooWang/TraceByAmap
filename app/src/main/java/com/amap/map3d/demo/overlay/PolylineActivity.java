package com.amap.map3d.demo.overlay;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.Constants;

/**
 * AMapV2地图中简单介绍一些Polyline的用法.
 */
public class PolylineActivity extends Activity implements
		OnSeekBarChangeListener {
	private static final int WIDTH_MAX = 50;
	private static final int HUE_MAX = 255;
	private static final int ALPHA_MAX = 255;

	private AMap aMap;
	private MapView mapView;
	private Polyline polyline;
	private SeekBar mColorBar;
	private SeekBar mAlphaBar;
	private SeekBar mWidthBar;
	
	/*
	 * 为方便展示多线段纹理颜色等示例事先准备好的经纬度
	 */
	private double Lat_A = 35.909736;
	private double Lon_A = 80.947266;
	
	private double Lat_B = 35.909736;
	private double Lon_B = 89.947266;
	
	private double Lat_C = 31.909736;
	private double Lon_C = 89.947266;
	
	private double Lat_D = 31.909736;
	private double Lon_D = 99.947266;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.polyline_activity);
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
		
		addPolylinessoild();//画实线
		addPolylinesdotted();//画虚线
		addPolylinesWithTexture();//画纹理线

		//跨越180°的线
		addBeyond180Polylines();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
		mColorBar.setMax(HUE_MAX);
		mColorBar.setProgress(10);

		mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
		mAlphaBar.setMax(ALPHA_MAX);
		mAlphaBar.setProgress(255);

		mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
		mWidthBar.setMax(WIDTH_MAX);
		mWidthBar.setProgress(10);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	private void setUpMap() {
		mColorBar.setOnSeekBarChangeListener(this);
		mAlphaBar.setOnSeekBarChangeListener(this);
		mWidthBar.setOnSeekBarChangeListener(this);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.300299, 106.347656), 4));
		aMap.setMapTextZIndex(2);
	}
	//绘制一条实线
	private void addPolylinessoild() {
		LatLng A = new LatLng(Lat_A, Lon_A);
		LatLng B = new LatLng(Lat_B, Lon_B);
	    LatLng C = new LatLng(Lat_C, Lon_C);
		LatLng D = new LatLng(Lat_D, Lon_D);
		aMap.addPolyline((new PolylineOptions())
				.add(A, B, C, D)
				.width(10)
				.color(Color.argb(255, 1, 255, 255)));
	}
	// 绘制一条虚线
	private void addPolylinesdotted() {
		polyline = aMap.addPolyline((new PolylineOptions())
				.add(Constants.SHANGHAI, Constants.BEIJING, Constants.CHENGDU)
				.width(10)
				.setDottedLine(true)//设置虚线
				.color(Color.argb(255, 1, 1, 1)));
	}
	//绘制一条纹理线
	private void addPolylinesWithTexture() {
		//四个点
		LatLng A = new LatLng(Lat_A+1, Lon_A+1);
		LatLng B = new LatLng(Lat_B+1, Lon_B+1);
		LatLng C = new LatLng(Lat_C+1, Lon_C+1);
		LatLng D = new LatLng(Lat_D+1, Lon_D+1);
		
		//用一个数组来存放纹理
		List<BitmapDescriptor> texTuresList = new ArrayList<BitmapDescriptor>();
		texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
		texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));
		texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_alr_night));
		
		//指定某一段用某个纹理，对应texTuresList的index即可, 四个点对应三段颜色
		List<Integer> texIndexList = new ArrayList<Integer>();
		texIndexList.add(0);//对应上面的第0个纹理
		texIndexList.add(2);
		texIndexList.add(1);
		
		
		PolylineOptions options = new PolylineOptions();
		options.width(20);//设置宽度
		
		//加入四个点
		options.add(A,B,C,D);
		
		//加入对应的颜色,使用setCustomTextureList 即表示使用多纹理；
		options.setCustomTextureList(texTuresList);
		
		//设置纹理对应的Index
		options.setCustomTextureIndex(texIndexList);
		
		aMap.addPolyline(options);
	}

	ArrayList<LatLng> latLngList = new ArrayList<LatLng>();

	private void addBeyond180Polylines() {

		latLngList.clear();
		for (LatLng latLng : latLngs_cross_180) {
			latLngList.add(latLng);
		}

		aMap.addPolyline((new PolylineOptions())
				.addAll(latLngList)
				.width(5)
				.width(40)
				.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture)));

///////////////////////////////////////////

		latLngList.clear();
		for (LatLng latLng : latLngs_cross_minus_180) {
			latLngList.add(latLng);
		}


		polyline =  aMap.addPolyline((new PolylineOptions())
				.addAll(latLngList)
				.width(5)
				.width(40)
				.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture)));


	}

	/**
	 *  从左到右绘制一条跨越180°的线，右边部分需要处理成超过180度
	 *  正常经纬度范围是(-180°,180°)，这里为了可以超过180°第三个参数填写false，表示去除内部检查
	 */
	LatLng[] latLngs_cross_180 = {
			new LatLng(59.304104, 133.44508,false),
			new LatLng(62.220912, 145.573986,false),
			new LatLng(64.353281, 158.75758,false),
			new LatLng(64.880724, 170.886486,false),
			new LatLng(66.261601, 360 - 179.269764,false),
			new LatLng(66.048417, 360 - 155.539295,false),
			new LatLng(65.251242, 360 - 143.234608,false),
	};

	/**
	 * 从右到左绘制一条跨越-180°的线，左边部分会超过-180°，效果和跨域180°一样
	 * 也可以将点全部+360°转换成跨域180°的线
	 */
	LatLng[] latLngs_cross_minus_180 = {
			new LatLng(65.251242 - 10, - 143.234608,false),
			new LatLng(66.048417 - 10, - 155.539295,false),
			new LatLng(66.261601 - 10, - 179.269764,false),
			new LatLng(64.880724 - 10, 170.886486 - 360, false),
			new LatLng(64.353281 - 10, 158.75758  - 360, false),
			new LatLng(62.220912 - 10, 145.573986 - 360, false),
			new LatLng(59.304104 - 10, 133.44508  - 360, false),
	};


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
	 * Polyline中对填充颜色，透明度，画笔宽度设置响应事件
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (polyline == null) {
			return;
		}
		if (seekBar == mColorBar) {
			polyline.setColor(Color.argb(255, progress, 1, 1));
		} else if (seekBar == mAlphaBar) {
			float[] prevHSV = new float[3];
			Color.colorToHSV(polyline.getColor(), prevHSV);
			polyline.setColor(Color.HSVToColor(progress, prevHSV));
		} else if (seekBar == mWidthBar) {
			polyline.setWidth(progress);
		}
	}
	
}
