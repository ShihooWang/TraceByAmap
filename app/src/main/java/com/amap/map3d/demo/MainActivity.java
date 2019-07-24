package com.amap.map3d.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.api.maps.MapsInitializer;
import com.amap.map3d.demo.basic.Animate_CameraActivity;
import com.amap.map3d.demo.basic.BasicMapActivity;
import com.amap.map3d.demo.basic.CameraActivity;
import com.amap.map3d.demo.basic.EventsActivity;
import com.amap.map3d.demo.basic.GestureSettingsActivity;
import com.amap.map3d.demo.basic.HeatMapActivity;
import com.amap.map3d.demo.basic.LayersActivity;
import com.amap.map3d.demo.basic.LimitBoundsActivity;
import com.amap.map3d.demo.basic.LogoSettingsActivity;
import com.amap.map3d.demo.basic.MapOptionActivity;
import com.amap.map3d.demo.basic.MinMaxZoomLevelActivity;
import com.amap.map3d.demo.overlay.ParticleMapActivity;
import com.amap.map3d.demo.overlay.ParticleWeatherMapActivity;
import com.amap.map3d.demo.basic.PoiClickActivity;
import com.amap.map3d.demo.basic.ScreenShotActivity;
import com.amap.map3d.demo.basic.TwoMapActivity;
import com.amap.map3d.demo.basic.UiSettingsActivity;
import com.amap.map3d.demo.basic.ViewPagerWithMapActivity;
import com.amap.map3d.demo.basic.ZoomActivity;
import com.amap.map3d.demo.basic.map.MapImpMethodActivity;
import com.amap.map3d.demo.busline.BusStationActivity;
import com.amap.map3d.demo.busline.BuslineActivity;
import com.amap.map3d.demo.cloud.CloudActivity;
import com.amap.map3d.demo.district.DistrictActivity;
import com.amap.map3d.demo.district.DistrictWithBoundaryActivity;
import com.amap.map3d.demo.geocoder.GeocoderActivity;
import com.amap.map3d.demo.geocoder.ReGeocoderActivity;
import com.amap.map3d.demo.indoor.IndoorMapActivity;
import com.amap.map3d.demo.inputtip.InputtipsActivity;
import com.amap.map3d.demo.listmap.MapListViewActivity;
import com.amap.map3d.demo.listmap.RecycleViewActivity;
import com.amap.map3d.demo.location.CustomLocationActivity;
import com.amap.map3d.demo.location.CustomLocationModeActivity;
import com.amap.map3d.demo.location.LocationModeSourceActivity;
import com.amap.map3d.demo.location.LocationModeSourceActivity_Old;
import com.amap.map3d.demo.offlinemap.OfflineMapActivity_Old;
import com.amap.map3d.demo.opengl.OpenglActivity;
import com.amap.map3d.demo.overlay.ArcActivity;
import com.amap.map3d.demo.overlay.BuildingOverlayActivity;
import com.amap.map3d.demo.overlay.CircleActivity;
import com.amap.map3d.demo.overlay.ColourfulPolylineActivity;
import com.amap.map3d.demo.overlay.CustomMarkerActivity;
import com.amap.map3d.demo.overlay.GeodesicActivity;
import com.amap.map3d.demo.overlay.GroundOverlayActivity;
import com.amap.map3d.demo.overlay.HoleActivity;
import com.amap.map3d.demo.overlay.InfoWindowActivity;
import com.amap.map3d.demo.overlay.MarkerActivity;
import com.amap.map3d.demo.overlay.MarkerAnimationActivity;
import com.amap.map3d.demo.overlay.MarkerClickActivity;
import com.amap.map3d.demo.overlay.MultiPointOverlayActivity;
import com.amap.map3d.demo.overlay.NavigateArrowOverlayActivity;
import com.amap.map3d.demo.overlay.PolygonActivity;
import com.amap.map3d.demo.overlay.PolylineActivity;
import com.amap.map3d.demo.overlay.ProvinceHoleActivity;
import com.amap.map3d.demo.overlay.TileOverlayActivity;
import com.amap.map3d.demo.poisearch.PoiAroundSearchActivity;
import com.amap.map3d.demo.poisearch.PoiIDSearchActivity;
import com.amap.map3d.demo.poisearch.PoiKeywordSearchActivity;
import com.amap.map3d.demo.poisearch.SubPoiSearchActivity;
import com.amap.map3d.demo.route.BusRouteActivity;
import com.amap.map3d.demo.route.DriveRouteActivity;
import com.amap.map3d.demo.route.DriveRoutePlanActivity;
import com.amap.map3d.demo.route.RideRouteActivity;
import com.amap.map3d.demo.route.RouteActivity;
import com.amap.map3d.demo.route.RouteDistanceActivity;
import com.amap.map3d.demo.route.TrafficActivity;
import com.amap.map3d.demo.route.TruckRouteActivity;
import com.amap.map3d.demo.route.WalkRouteActivity;
import com.amap.map3d.demo.routepoi.RoutePOIActivity;
import com.amap.map3d.demo.share.ShareActivity;
import com.amap.map3d.demo.smooth.SmoothMoveActivity;
import com.amap.map3d.demo.tools.CalculateDistanceActivity;
import com.amap.map3d.demo.tools.ContainsActivity;
import com.amap.map3d.demo.tools.CoordConverActivity;
import com.amap.map3d.demo.tools.GeoToScreenActivity;
import com.amap.map3d.demo.trace.TraceActivity;
import com.amap.map3d.demo.trace.TraceActivity_Simple;
import com.amap.map3d.demo.view.FeatureView;
import com.amap.map3d.demo.weather.WeatherSearchActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * AMapV2地图demo总汇
 */
public final class MainActivity extends ListActivity {
	private static class DemoDetails {
		private final int titleId;
		private final int descriptionId;
		private final Class<? extends android.app.Activity> activityClass;

		public DemoDetails(int titleId, int descriptionId,
				Class<? extends android.app.Activity> activityClass) {
			super();
			this.titleId = titleId;
			this.descriptionId = descriptionId;
			this.activityClass = activityClass;
		}
	}

	private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {
		public CustomArrayAdapter(Context context, DemoDetails[] demos) {
			super(context, R.layout.feature, R.id.title, demos);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FeatureView featureView;
			if (convertView instanceof FeatureView) {
				featureView = (FeatureView) convertView;
			} else {
				featureView = new FeatureView(getContext());
			}
			DemoDetails demo = getItem(position);
			featureView.setTitleId(demo.titleId, demo.activityClass!=null);
			return featureView;
		}
	}

	private static final DemoDetails[] demos = {
//		            创建地图
			new DemoDetails(R.string.map_create, R.string.blank, null),
//			显示地图
			new DemoDetails(R.string.basic_map, R.string.basic_description,
					BasicMapActivity.class),
//			显示地图
			new DemoDetails(R.string.list_map, R.string.basic_description,
					MapListViewActivity.class),
//			显示地图
			new DemoDetails(R.string.recycle_map, R.string.basic_description,
					RecycleViewActivity.class),
//			6种实现地图方式
			new DemoDetails(R.string.basic_map_6, R.string.basic_description_temp,
					MapImpMethodActivity.class),
//			Fragment创建地图
//			new DemoDetails(R.string.base_fragment_map, R.string.base_fragment_description,
//					BaseMapSupportFragmentActivity.class),
//			new DemoDetails(R.string.basic_texturemapview, R.string.basic_texturemapview_description,
//					TextureMapViewActivity.class),
			new DemoDetails(R.string.viewpager_map, R.string.viewpger_map_description,
					ViewPagerWithMapActivity.class),
//			地图多实例
		    new DemoDetails(R.string.multi_inst, R.string.blank, 
		    		TwoMapActivity.class),
//		           室内地图
		    new DemoDetails(R.string.indoormap_demo, R.string.indoormap_description,
		            IndoorMapActivity.class),
//		    amapoptions实现地图
		    new DemoDetails(R.string.mapOption_demo,
					R.string.mapOption_description, MapOptionActivity.class),
//-----------与地图交互-----------------------------------------------------------------------------------------------
		    new DemoDetails(R.string.map_interactive, R.string.blank, null),
		    //缩放控件、定位按钮、指南针、比例尺等的添加
		    new DemoDetails(R.string.uisettings_demo,
					R.string.uisettings_description, UiSettingsActivity.class),
			//地图logo位置改变
			new DemoDetails(R.string.logo,
					R.string.uisettings_description, LogoSettingsActivity.class),
			//地图图层
			new DemoDetails(R.string.layers_demo, R.string.layers_description,
					LayersActivity.class),
			//缩放、旋转、拖拽和改变仰角操作地图
			new DemoDetails(R.string.gesture,
					R.string.uisettings_description, GestureSettingsActivity.class),
			//监听点击、长按、拖拽地图等事件
			new DemoDetails(R.string.events_demo, R.string.events_description,
					EventsActivity.class),
			//地图POI点击
			new DemoDetails(R.string.poiclick_demo,
					R.string.poiclick_description, PoiClickActivity.class),	
			//改变地图中心点
			new DemoDetails(R.string.camera_demo, R.string.camera_description, 
					CameraActivity.class),
			//地图动画效果
			new DemoDetails(R.string.animate_demo, R.string.animate_description,
					Animate_CameraActivity.class),
			//改变缩放级别	
			new DemoDetails(R.string.map_zoom, R.string.blank, ZoomActivity.class),

			//地图截屏
			new DemoDetails(R.string.screenshot_demo,
					R.string.screenshot_description, ScreenShotActivity.class),

			//自定义最小最大缩放级别
			new DemoDetails(R.string.set_min_max_zoomlevel,
					R.string.set_min_max_zoomlevel_description, MinMaxZoomLevelActivity.class),

			//自定义地图显示区域
			new DemoDetails(R.string.limit_bounds,
					R.string.limit_bounds_description, LimitBoundsActivity.class),
//----------------------------------------------------------------------------------------------------------------------------------------
			//在地图上绘制	
			new DemoDetails(R.string.map_overlay, R.string.blank, null),
			//绘制点
			new DemoDetails(R.string.marker_demo, R.string.marker_description,
					MarkerActivity.class),
			//marker点击回调
			new DemoDetails(R.string.marker_click, R.string.marker_click,
					MarkerClickActivity.class),
			//Marker 动画功能
			new DemoDetails(R.string.marker_animation_demo, R.string.marker_animation_description,
					MarkerAnimationActivity.class),
			//绘制地图上的信息窗口
			new DemoDetails(R.string.infowindow_demo, R.string.infowindow_demo, InfoWindowActivity.class),
			//绘制自定义点
			new DemoDetails(R.string.custommarker_demo, R.string.blank,
					CustomMarkerActivity.class),		
			//绘制默认定位小蓝点
			new DemoDetails(R.string.locationmodesource_demo_old,R.string.locationmodesource_description,LocationModeSourceActivity_Old.class),
			new DemoDetails(R.string.locationmodesource_demo,R.string.locationmodesource_description,LocationModeSourceActivity.class),
			//绘制自定义定位小蓝点图标
			new DemoDetails(R.string.customlocation_demo,R.string.customlocation_demo,CustomLocationActivity.class),
			new DemoDetails(R.string.customlocationmode_demo,R.string.customlocationmode_demo,CustomLocationModeActivity.class),
			//绘制实线、虚线
			new DemoDetails(R.string.polyline_demo,
					R.string.polyline_description, PolylineActivity.class),
			//多彩线
			new DemoDetails(R.string.colourline_demo,
					R.string.colourline_description, ColourfulPolylineActivity.class),		
			//大地曲线
			new DemoDetails(R.string.geodesic_demo, R.string.geodesic_description,
					GeodesicActivity.class),		
//			绘制弧线
			new DemoDetails(R.string.arc_demo, R.string.arc_description,
					ArcActivity.class),
			//绘制带导航箭头的线
			new DemoDetails(R.string.navigatearrow_demo,
					R.string.navigatearrow_description,
					NavigateArrowOverlayActivity.class),
			//绘制圆
			new DemoDetails(R.string.circle_demo, R.string.circle_description,
					CircleActivity.class),
		    //矩形、多边形
			new DemoDetails(R.string.polygon_demo,
					R.string.polygon_description, PolygonActivity.class),
			//绘制热力图
			new DemoDetails(R.string.heatmap_demo,
					R.string.heatmap_description, HeatMapActivity.class),
			//绘制groundoverlay
			new DemoDetails(R.string.groundoverlay_demo,
					R.string.groundoverlay_description,GroundOverlayActivity.class),
			//绘制opengl
			new DemoDetails(R.string.opengl_demo, R.string.opengl_description,
					OpenglActivity.class),
			//绘制tileOverlay
			new DemoDetails(R.string.tileoverlay_demo, R.string.tileoverlay_demo,
					TileOverlayActivity.class),
			//绘制自定义建筑物
			new DemoDetails(R.string.buildingoverlay, R.string.tileoverlay_demo,
					BuildingOverlayActivity.class),
			new DemoDetails(R.string.multipoint_demo, R.string.multipoint_description,
					MultiPointOverlayActivity.class),
			new DemoDetails(R.string.hole_demo, R.string.hole_decription,
					HoleActivity.class),
			new DemoDetails(R.string.province_hole_demo, R.string.province_hole_decription,
					ProvinceHoleActivity.class),
			new DemoDetails(R.string.particle_demo, R.string.particle_decription,
					ParticleMapActivity.class),
			new DemoDetails(R.string.particle_weather_demo, R.string.particle_weather_decription,
					ParticleWeatherMapActivity.class),
//-----------------------------------------------------------------------------------------------------------------------------------------------------
			//获取地图数据
			new DemoDetails(R.string.search_data, R.string.blank, null),
			//关键字检索
			new DemoDetails(R.string.poikeywordsearch_demo,
					R.string.poikeywordsearch_description,
					PoiKeywordSearchActivity.class),
			//周边搜索
			new DemoDetails(R.string.poiaroundsearch_demo,
					R.string.poiaroundsearch_description,
					PoiAroundSearchActivity.class),
//			ID检索
			new DemoDetails(R.string.poiidsearch_demo,
					R.string.poiidsearch_demo,
					PoiIDSearchActivity.class),
			//沿途搜索
			new DemoDetails(R.string.routepoisearch_demo, 
					R.string.routepoisearch_demo, 
					RoutePOIActivity.class),
//			输入提示查询
			new DemoDetails(R.string.inputtips_demo, R.string.inputtips_description,
					InputtipsActivity.class),
//			POI父子关系
			new DemoDetails(R.string.subpoi_demo, R.string.subpoi_description,
					SubPoiSearchActivity.class),
//			天气查询
			new DemoDetails(R.string.weather_demo,
					R.string.weather_description, WeatherSearchActivity.class),
//			地理编码
			new DemoDetails(R.string.geocoder_demo,
					R.string.geocoder_description, GeocoderActivity.class),
//			逆地理编码
			new DemoDetails(R.string.regeocoder_demo,
					R.string.regeocoder_description, ReGeocoderActivity.class),
//			行政区划查询
			new DemoDetails(R.string.district_demo,
					R.string.district_description, DistrictActivity.class),
//			行政区边界查询
			new DemoDetails(R.string.district_boundary_demo,
					R.string.district_boundary_description,
					DistrictWithBoundaryActivity.class),
//			公交路线查询
			new DemoDetails(R.string.busline_demo,
					R.string.busline_description, BuslineActivity.class),
//			公交站点查询
			new DemoDetails(R.string.busstation_demo,
					R.string.blank, BusStationActivity.class),
			// 交通态势
			new DemoDetails(R.string.traffic_demo,
					R.string.traffic_decription, TrafficActivity.class),
//			云图
			new DemoDetails(R.string.cloud_demo, R.string.cloud_description,
					CloudActivity.class),
//			出行路线规划
			new DemoDetails(R.string.search_route, R.string.blank, null),
//			驾车出行路线规划
			new DemoDetails(R.string.route_drive, R.string.blank, DriveRouteActivity.class),
			//驾车未来出行路线规划
			new DemoDetails(R.string.route_plan_drive, R.string.blank, DriveRoutePlanActivity.class),
//			步行出行路线规划
			new DemoDetails(R.string.route_walk, R.string.blank, WalkRouteActivity.class),
//			公交出行路线规划
			new DemoDetails(R.string.route_bus, R.string.blank, BusRouteActivity.class),
//			骑行出行路线规划
			new DemoDetails(R.string.route_ride, R.string.blank, RideRouteActivity.class),
			new DemoDetails(R.string.route_truck, R.string.blank, TruckRouteActivity.class),
			new DemoDetails(R.string.route_distance, R.string.blank, RouteDistanceActivity.class),
//			route综合demo
			new DemoDetails(R.string.route_demo, R.string.route_description,
					RouteActivity.class),
//			短串分享
			new DemoDetails(R.string.search_share, R.string.blank, null),		
			new DemoDetails(R.string.share_demo, R.string.share_description,
					ShareActivity.class),
			
//			离线地图
			new DemoDetails(R.string.map_offline, R.string.blank, null),
			new DemoDetails(R.string.offlinemap_demo,
					R.string.offlinemap_description, OfflineMapActivity_Old.class),
			new DemoDetails(R.string.offlinemap_ui_demo,
					R.string.offlinemap_ui_description, com.amap.api.maps.offlinemap.OfflineMapActivity.class),
			
//			地图计算工具
			new DemoDetails(R.string.map_tools, R.string.blank, null),

//			其他坐标系转换为高德坐标系
			new DemoDetails(R.string.coordconvert_demo, R.string.coordconvert_demo, CoordConverActivity.class),
//			地理坐标和屏幕像素坐标转换
			new DemoDetails(R.string.convertgeo2point_demo, R.string.convertgeo2point_demo, GeoToScreenActivity.class),
//			两点间距离计算
			new DemoDetails(R.string.calculateLineDistance, R.string.calculateLineDistance, CalculateDistanceActivity.class),
//			判断点是否在多边形内
			new DemoDetails(R.string.contains_demo, R.string.contains_demo, ContainsActivity.class),


//			地图计算工具
			new DemoDetails(R.string.map_expand, R.string.blank, null),
//			轨迹纠偏
			new DemoDetails(R.string.trace_demo, R.string.trace_description, TraceActivity.class),
			new DemoDetails(R.string.trace_demo_simple, R.string.trace_description_simple, TraceActivity_Simple.class),
//			平滑移动
			new DemoDetails(R.string.smooth_move_demo, R.string.smooth_move_description, SmoothMoveActivity.class)



	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		setTitle("3D地图Demo" + MapsInitializer.getVersion());
		ListAdapter adapter = new CustomArrayAdapter(
				this.getApplicationContext(), demos);
		setListAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.exit(0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		DemoDetails demo = (DemoDetails) getListAdapter().getItem(position);
		if (demo.activityClass != null) {
			Log.i("MY","demo!=null");
			startActivity(new Intent(this.getApplicationContext(),
					demo.activityClass));
		}

	}



	/*************************************** 权限检查******************************************************/

	/**
	 * 需要进行检测的权限数组
	 */
	protected String[] needPermissions = {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE
	};

	private static final int PERMISSON_REQUESTCODE = 0;

	/**
	 * 判断是否需要检测，防止不停的弹框
	 */
	private boolean isNeedCheck = true;

	@Override
	protected void onResume() {
		try{
			super.onResume();
			if (Build.VERSION.SDK_INT >= 23) {
				if (isNeedCheck) {
					checkPermissions(needPermissions);
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

	/**
	 * @param
	 * @since 2.5.0
	 */
	@TargetApi(23)
	private void checkPermissions(String... permissions) {
		try{
			if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
				List<String> needRequestPermissonList = findDeniedPermissions(permissions);
				if (null != needRequestPermissonList
						&& needRequestPermissonList.size() > 0) {
					try {
						String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
						Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
						method.invoke(this, array, 0);
					} catch (Throwable e) {

					}
				}
			}

		}catch(Throwable e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取权限集中需要申请权限的列表
	 *
	 * @param permissions
	 * @return
	 * @since 2.5.0
	 */
	@TargetApi(23)
	private List<String> findDeniedPermissions(String[] permissions) {
		try{
			List<String> needRequestPermissonList = new ArrayList<String>();
			if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
				for (String perm : permissions) {
					if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
							|| shouldShowMyRequestPermissionRationale(perm)) {
						needRequestPermissonList.add(perm);
					}
				}
			}
			return needRequestPermissonList;
		}catch(Throwable e){
			e.printStackTrace();
		}
		return null;
	}

	private int checkMySelfPermission(String perm) {
		try {
			Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
			Integer permissionInt = (Integer) method.invoke(this, perm);
			return permissionInt;
		} catch (Throwable e) {
		}
		return -1;
	}

	private boolean shouldShowMyRequestPermissionRationale(String perm) {
		try {
			Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
			Boolean permissionInt = (Boolean) method.invoke(this, perm);
			return permissionInt;
		} catch (Throwable e) {
		}
		return false;
	}

	/**
	 * 检测是否说有的权限都已经授权
	 *
	 * @param grantResults
	 * @return
	 * @since 2.5.0
	 */
	private boolean verifyPermissions(int[] grantResults) {
		try{
			for (int result : grantResults) {
				if (result != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
		return true;
	}

	@TargetApi(23)
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] paramArrayOfInt) {
		try{
			if (Build.VERSION.SDK_INT >= 23) {
				if (requestCode == PERMISSON_REQUESTCODE) {
					if (!verifyPermissions(paramArrayOfInt)) {
						showMissingPermissionDialog();
						isNeedCheck = false;
					}
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

	/**
	 * 显示提示信息
	 *
	 * @since 2.5.0
	 */
	private void showMissingPermissionDialog() {
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");

			// 拒绝, 退出应用
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try{
								finish();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});

			builder.setPositiveButton("设置",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								startAppSettings();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});

			builder.setCancelable(false);

			builder.show();
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

	/**
	 * 启动应用的设置
	 *
	 * @since 2.5.0
	 */
	private void startAppSettings() {
		try{
			Intent intent = new Intent(
					Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
