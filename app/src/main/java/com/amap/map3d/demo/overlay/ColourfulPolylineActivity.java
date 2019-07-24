package com.amap.map3d.demo.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.map3d.demo.R;

/**
 * 绘制彩色线功 介绍
 */
public class ColourfulPolylineActivity extends Activity {

	private AMap aMap;
	private MapView mapView;
	
	private double Lat_A = 39.981167;
	private double Lon_A = 116.345103;
	
	private double Lat_B = 39.981265;
	private double Lon_B = 116.347152;
	
	private double Lat_C = 39.979387;
	private double Lon_C = 116.347356;
	
	private double Lat_D = 39.979313;
	private double Lon_D = 116.348896;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arc_activity);
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
		addPolylineInPlayGround(new LatLng(39.980215,116.34595));
		addPolylinesWithGradientColors();
		addPolylinesWithColors();
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

	private void setUpMap() {

		aMap.moveCamera(CameraUpdateFactory.zoomTo(4));
		aMap.setMapTextZIndex(2);
	}

	/**
	 * 多段颜色（非渐变色）
	 */
	private void addPolylinesWithColors() {
		//四个点
		LatLng A = new LatLng(Lat_A + 0.0001, Lon_A + 0.0001);
		LatLng B = new LatLng(Lat_B + 0.0001, Lon_B + 0.0001);
		LatLng C = new LatLng(Lat_C + 0.0001, Lon_C + 0.0001);
		LatLng D = new LatLng(Lat_D + 0.0001, Lon_D + 0.0001);
		
		//用一个数组来存放颜色，四个点对应三段颜色
		List<Integer> colorList = new ArrayList<Integer>();
		colorList.add(Color.RED);
		colorList.add(Color.YELLOW);
		colorList.add(Color.GREEN);
//		colorList.add(Color.BLACK);
		
		PolylineOptions options = new PolylineOptions();
		options.width(20);//设置宽度
		
		//加入四个点
		options.add(A,B,C,D);
		
		//加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
		options.colorValues(colorList);
		
		aMap.addPolyline(options);
	}
	
	
	/**
	 * 多段颜色（渐变色）
	 */
	private void addPolylinesWithGradientColors() {
		//四个点
		LatLng A = new LatLng(Lat_A + 0.0004, Lon_A + 0.0004);
		LatLng B = new LatLng(Lat_B + 0.0004, Lon_B + 0.0004);
		LatLng C = new LatLng(Lat_C + 0.0004, Lon_C + 0.0004);
		LatLng D = new LatLng(Lat_D + 0.0004, Lon_D + 0.0004);
		
		//用一个数组来存放颜色，渐变色，四个点需要设置四个颜色
		List<Integer> colorList = new ArrayList<Integer>();
		colorList.add(Color.RED);
		colorList.add(Color.YELLOW);
		colorList.add(Color.GREEN);
		colorList.add(Color.BLACK);//如果第四个颜色不添加，那么最后一段将显示上一段的颜色
		
		PolylineOptions options = new PolylineOptions();
		options.width(20);//设置宽度
		
		//加入四个点
		options.add(A,B,C,D);
		
		//加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
		options.colorValues(colorList);
		
		//加上这个属性，表示使用渐变线
		options.useGradient(true);
		
		aMap.addPolyline(options);
	}
	//操场线
	  public void addPolylineInPlayGround(LatLng centerpoint) {
	    	double r = 6371000.79;
	    	int r1 = 50;
	    	PolylineOptions options = new PolylineOptions();
	    	int numpoints =36;
	    	double phase = 2 * Math.PI / numpoints;
	    	//颜色数组
	    	List<Integer> colorList = new ArrayList<Integer>();
			int[] colors = new int[]{Color.argb(255, 0, 255, 0),Color.argb(255, 255, 255, 0),Color.argb(255, 255, 0, 0)};
			Random random = new Random();
	    	//画图
	    	for (int i = 0; i <numpoints; i++) {
				double dx = (r1 * Math.cos(i*phase));
				double dy = (r1*Math.sin(i*phase))*1.6;//乘以1.6 椭圆比例
				
				double dlng = dx/(r*Math.cos(centerpoint.latitude*Math.PI/180)*Math.PI/180);
				double dlat = dy/(r*Math.PI/180);
				double newlng = centerpoint.longitude+dlng;
				
				//跑道两边为直线
				if (newlng<centerpoint.longitude - 0.00046) {
					newlng = centerpoint.longitude - 0.00046;
				}else if (newlng > centerpoint.longitude + 0.00046) {
					newlng = centerpoint.longitude + 0.00046;
				}
				options.add(new LatLng(centerpoint.latitude+dlat,newlng));	
			}
	    	
	    	//随机颜色赋值
	    	for (int i = 0; i < numpoints; i = i+2) {
	    		int color = colors[random.nextInt(3)];
	    		colorList.add(color);//添加颜色
	    		colorList.add(color);
			}

	    	//确保首位相接，添加后一个点及颜色与第一点相同
	    	options.add(options.getPoints().get(0));
	    	colorList.add(colorList.get(0));

	    	
	    	
	    	
	    	List<Integer> colorListnew = new ArrayList<Integer>();
	    	colorListnew.add(Color.RED);
	    	colorListnew.add(Color.YELLOW);
	    	colorListnew.add(Color.GREEN);
	    	aMap.addPolyline(options.width(15)
	    			.colorValues(colorList).useGradient(true));

	    	aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerpoint, 17));  
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

}
