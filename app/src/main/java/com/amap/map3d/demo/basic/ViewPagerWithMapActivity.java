package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;


import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.map3d.demo.R;

import java.util.ArrayList;


/**
 * AMapV2地图中介绍如何TextureMapView显示一个基本地图
 */
public class ViewPagerWithMapActivity extends Activity{
	private ViewPager viewPager;
	private ArrayList<View> pageViews;

	private AMap mMap;
	private TextureMapView textureMapView;
	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_withmap_activity);

		viewPager = (ViewPager) findViewById(R.id.guidePages);

		textureMapView = new TextureMapView(this);
		mapView = new MapView(this);

		textureMapView.onCreate(savedInstanceState);
		mapView.onCreate(savedInstanceState);

		pageViews = new ArrayList<View>();

		TextView textView0 = new TextView(this);
		textView0.setTextColor(Color.BLACK);
		textView0.setTextSize(20);
		textView0.setText("ViewPager中使用TextureMapView可以防止滑动黑边的问题\n\n 向左滑动查看TextureMapView地图");

		TextView textView2 = new TextView(this);
		textView2.setTextColor(Color.BLACK);
		textView2.setTextSize(20);
		textView2.setText("ViewPager中使用MapView滑动会出现黑边\n" +
				"\n" +
				" 向右滑动查看TextureMapView地图 \n" +
				"\n" +
				" 向左滑动查看MapView地图(有黑边)" );


		pageViews.add(textView0);
		pageViews.add(textureMapView);// mapview
		pageViews.add(textView2);
		pageViews.add(mapView);

		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());

		initMap();

	}

	private void initMap() {
		if (mMap == null) {
			mMap = textureMapView.getMap();
		}

	}

//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onResume() {
//		super.onResume();
//		textureMapView.onResume();
//		mapView.onResume();
//	}
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onPause() {
//		super.onPause();
//		textureMapView.onPause();
//		mapView.onPause();
//	}
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		textureMapView.onSaveInstanceState(outState);
//		mapView.onSaveInstanceState(outState);
//	}
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		textureMapView.onDestroy();
//		mapView.onDestroy();
//	}

	boolean isFirstTextureMapView = true;
	boolean isFirstMapView = true;

	// 指引页面数据适配器
	class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			if (arg1 == 1 || arg1 == 3) {
				//mapview不会destory
				return;
			}

			((ViewPager) arg0).removeView(pageViews.get(arg1));

		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			Log.i("amap","instantiateItem position: " + arg1);
			if (arg1 != 1 && arg1 != 3) {
				((ViewPager) arg0).addView(pageViews.get(arg1));
			}

			//TextureMapview 只会添加一次
			if (isFirstTextureMapView && arg1 == 1) {
				isFirstTextureMapView = false;
				((ViewPager) arg0).addView(pageViews.get(arg1));
			}

			//MapView 只会添加一次
			if (isFirstMapView && arg1 == 3) {
				isFirstMapView = false;
				((ViewPager) arg0).addView(pageViews.get(arg1));
			}

			return pageViews.get(arg1);

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	// 指引页面更改事件监听器
	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			 Log.i("amap","onPageScrolled:" + arg0 + "," + arg1 + "," + arg2);
			// textureMapView.
		}

		@Override
		public void onPageSelected(int arg0) {

		}
	}

}
