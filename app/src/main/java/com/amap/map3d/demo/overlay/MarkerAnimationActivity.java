package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.map3d.demo.R;

/**
 * AMapV2地图中简单介绍一些Marker的用法.
 * Marker动画功能介绍
 */
public class MarkerAnimationActivity extends Activity implements OnClickListener {
	private MarkerOptions markerOption;
	private AMap aMap;
	private MapView mapView;
	private LatLng latlng = new LatLng(39.761, 116.434);

	Marker screenMarker = null;
	Marker growMarker = null;

	private Marker breatheMarker = null;
	private Marker breatheMarker_center = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.marker_animation_activity);
		/*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
		// Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		// MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		Button growMarkerBt = (Button) findViewById(R.id.growMarker);
		growMarkerBt.setOnClickListener(this);
		Button jumpMarkerBt = (Button) findViewById(R.id.jumpMarker);
		jumpMarkerBt.setOnClickListener(this);
		Button breatheMarkerBt = (Button) findViewById(R.id.breatheMarker);
		breatheMarkerBt.setOnClickListener(this);



		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
			@Override
			public void onMapLoaded() {
				addMarkersToMap();
			}
		});

		// 设置可视范围变化时的回调的接口方法
		aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {

			}

			@Override
			public void onCameraChangeFinish(CameraPosition postion) {
				//屏幕中心的Marker跳动
                startJumpAnimation();
			}
		});
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
	 * 在地图上添加marker
	 */
	private void addMarkersToMap() {

		addMarkerInScreenCenter();

		addGrowMarker();
	}


	/**
	 * 在屏幕中心添加一个Marker
	 */
	private void addMarkerInScreenCenter() {
		LatLng latLng = aMap.getCameraPosition().target;
		Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
		screenMarker = aMap.addMarker(new MarkerOptions()
				.anchor(0.5f,0.5f)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
		//设置Marker在屏幕上,不跟随地图移动
	 	screenMarker.setPositionByPixels(screenPosition.x,screenPosition.y);

	}

	/**
	 * 添加一个从地上生长的Marker
	 */
	public void addGrowMarker() {
		if(growMarker == null) {
			MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
					.position(latlng);
			growMarker = aMap.addMarker(markerOptions);
		} 
		
		startGrowAnimation();
	}

	/**
	 * 地上生长的Marker
	 */
	private void startGrowAnimation() {
		if(growMarker != null) {
			Animation animation = new ScaleAnimation(0,1,0,1);
			animation.setInterpolator(new LinearInterpolator());
			//整个移动所需要的时间
			animation.setDuration(1000);
			//设置动画
			growMarker.setAnimation(animation);
			//开始动画
			growMarker.startAnimation();
		}
	}

	/**
	 * 屏幕中心marker 跳动
	 */
	public void startJumpAnimation() {

		if (screenMarker != null ) {
			//根据屏幕距离计算需要移动的目标点
			final LatLng latLng = screenMarker.getPosition();
			Point point =  aMap.getProjection().toScreenLocation(latLng);
			point.y -= dip2px(this,125);
			LatLng target = aMap.getProjection()
					.fromScreenLocation(point);
			//使用TranslateAnimation,填写一个需要移动的目标点
			Animation animation = new TranslateAnimation(target);
			animation.setInterpolator(new Interpolator() {
				@Override
				public float getInterpolation(float input) {
					// 模拟重加速度的interpolator
					if(input <= 0.5) {
						return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
					} else {
						return (float) (0.5f - Math.sqrt((input - 0.5f)*(1.5f - input)));
					}
				}
			});
			//整个移动所需要的时间
			animation.setDuration(600);
			//设置动画
			screenMarker.setAnimation(animation);
			//开始动画
			screenMarker.startAnimation();

		} else {
			Log.e("amap","screenMarker is null");
		}
	}

	private void startBreatheAnimation() {
		// 呼吸动画
		if(breatheMarker == null) {
			LatLng latLng = new LatLng(latlng.latitude - 0.02, latlng.longitude - 0.02);
			breatheMarker = aMap.addMarker(new MarkerOptions().position(latLng).zIndex(1)
					.anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_circle_64)));
			// 中心的marker
			breatheMarker_center = aMap.addMarker(new MarkerOptions().position(latLng).zIndex(2)
					.anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_circle_64)));
		}

		// 动画执行完成后，默认会保持到最后一帧的状态
		AnimationSet animationSet = new AnimationSet(true);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0f);
		alphaAnimation.setDuration(2000);
		// 设置不断重复
		alphaAnimation.setRepeatCount(Animation.INFINITE);

		ScaleAnimation scaleAnimation = new ScaleAnimation(1, 3.5f, 1, 3.5f);
		scaleAnimation.setDuration(2000);
		// 设置不断重复
		scaleAnimation.setRepeatCount(Animation.INFINITE);


		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setInterpolator(new LinearInterpolator());
		breatheMarker.setAnimation(animationSet);
		breatheMarker.startAnimation();
	}


	//dip和px转换
	private static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 从地上生长的Marker
		 */
		case R.id.growMarker:
			startGrowAnimation();
			break;
		/**
		 * marker 跳动动画
		 */
		case R.id.jumpMarker:
			startJumpAnimation();
			break;
		/**
		 * marker 跳动动画
		 */
		case R.id.breatheMarker:
			startBreatheAnimation();
			break;
		default:
			break;
		}
	}
}
