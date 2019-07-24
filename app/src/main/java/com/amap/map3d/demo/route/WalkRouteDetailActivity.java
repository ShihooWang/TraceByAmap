package com.amap.map3d.demo.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;

public class WalkRouteDetailActivity extends Activity {
	private WalkPath mWalkPath;
	private TextView mTitle,mTitleWalkRoute;
	private ListView mWalkSegmentList;
	private WalkSegmentListAdapter mWalkSegmentListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		getIntentData();
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitle.setText("步行路线详情");
		mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
		String dur = AMapUtil.getFriendlyTime((int) mWalkPath.getDuration());
		String dis = AMapUtil
				.getFriendlyLength((int) mWalkPath.getDistance());
		mTitleWalkRoute.setText(dur + "(" + dis + ")");
		mWalkSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mWalkSegmentListAdapter = new WalkSegmentListAdapter(
				this.getApplicationContext(), mWalkPath.getSteps());
		mWalkSegmentList.setAdapter(mWalkSegmentListAdapter);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mWalkPath = intent.getParcelableExtra("walk_path");
	}

	public void onBackClick(View view) {
		this.finish();
	}

}
