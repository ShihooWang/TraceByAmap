package com.amap.map3d.demo.route;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.TMC;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;

/**
 * 驾车路线详情
 */
public class DriveRouteDetailActivity extends Activity {
	private DrivePath mDrivePath;
	private DriveRouteResult mDriveRouteResult;
	private TextView mTitle, mTitleDriveRoute, mDesDriveRoute;
	private ListView mDriveSegmentList;
	private DriveSegmentListAdapter mDriveSegmentListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);

		getIntentData();
		init();
	}

	private void init() {
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitleDriveRoute = (TextView) findViewById(R.id.firstline);
		mDesDriveRoute = (TextView) findViewById(R.id.secondline);
		mTitle.setText("驾车路线详情");
		String dur = AMapUtil.getFriendlyTime((int) mDrivePath.getDuration());
		String dis = AMapUtil.getFriendlyLength((int) mDrivePath
				.getDistance());
		mTitleDriveRoute.setText(dur + "(" + dis + ")");
		int taxiCost = (int) mDriveRouteResult.getTaxiCost();
		mDesDriveRoute.setText("打车约"+taxiCost+"元");
		mDesDriveRoute.setVisibility(View.VISIBLE);
		configureListView();
	}

	private void configureListView() {
		mDriveSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mDriveSegmentListAdapter = new DriveSegmentListAdapter(
				this.getApplicationContext(), mDrivePath.getSteps());
		mDriveSegmentList.setAdapter(mDriveSegmentListAdapter);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mDrivePath = intent.getParcelableExtra("drive_path");
		mDriveRouteResult = intent.getParcelableExtra("drive_result");
		for (int i = 0; i < mDrivePath.getSteps().size(); i++) {
			DriveStep step = mDrivePath.getSteps().get(i);
			List<TMC> tmclist = step.getTMCs();
			for (int j = 0; j < tmclist.size(); j++) {
				String s = ""+tmclist.get(j).getPolyline().size();
				Log.i("MY", s+tmclist.get(j).getStatus()
						+tmclist.get(j).getDistance()
						+tmclist.get(j).getPolyline().toString());
			}
		}
	}

	public void onBackClick(View view) {
		this.finish();
	}
}
