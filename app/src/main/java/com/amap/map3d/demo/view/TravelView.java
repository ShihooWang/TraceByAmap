package com.amap.map3d.demo.view;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.route.DrivePlanPath;
import com.amap.api.services.route.DriveRoutePlanResult;
import com.amap.api.services.route.TimeInfo;
import com.amap.api.services.route.TimeInfosElement;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.ui.barChart.BarChartData;
import com.amap.map3d.demo.ui.barChart.BarChartView;
import com.amap.map3d.demo.util.DisplayUtil;
import com.amap.map3d.demo.util.utils;

import java.util.ArrayList;
import java.util.List;

public class TravelView extends ConstraintLayout {
    private BarChartView barChartView;
    private TextView beginTime;
    private TextView arriveTime;
    private TextView distance;

    private TextView beginTimeTitle;
    private TextView arriveTimeTitle;
    private TextView distanceTextTitle;
    private TextView distanceContent;

    private TextView setArriveTimeTitle;
    private TextView setArriveTime;

    //提前十分钟
    private static long diffTime = 60 * 5;

    public interface IndexListener {
        public void onClicked(int index);
    }
    public TravelView(Context context) {
        super(context);
    }

    public TravelView(Context context, AttributeSet attrs) {
        super(context,attrs);
        initView(context);
    }

    public TravelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void init(final DriveRoutePlanResult result, final IndexListener indexListener, long arriveTime) {
        int groupSize = result.getTimeInfos().size();
        List<BarChartData> groupDataList = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            TimeInfo info = result.getTimeInfos().get(i);
            BarChartData groupData = new BarChartData();
            groupData.setName(utils.times(info.getStartTime()));

            TimeInfosElement element = info.getElements().get(0);
            groupData.setValue(element.getDuration());
            groupData.setUnit("min");
            groupDataList.add(groupData);
        }
        barChartView.setDataList(groupDataList);
        int[] color1 = new int[]{Color.parseColor("#add8e6"), Color.parseColor("#87cefa")};

        int[] color2 = new int[]{Color.parseColor("#008b8b"), Color.parseColor("#00bfff")};
        barChartView.setBarChartColor(color1, color2);

        barChartView.setIndexListener(new BarChartView.IndexListener(){
            public void onClicked(int index){
                if (indexListener != null) {
                    indexListener.onClicked(index);
                    setPathInfo(result, index, true);
                }
            }
        });

        if (arriveTime > 0) {
            //multiGroupHistogramView.setVisibility(VISIBLE);
            beginTimeTitle.setText("建议出发时间");
            arriveTimeTitle.setText("预计到达时间");
            distanceTextTitle.setText("预计耗时");
            ViewGroup.LayoutParams params = barChartView.getLayoutParams();
            params.height = DisplayUtil.dp2px(100);
            barChartView.setLayoutParams(params);
            setArriveTime.setText(utils.times(arriveTime));
            setArriveTimeTitle.setVisibility(VISIBLE);
            setArriveTime.setVisibility(VISIBLE);
            int index = setArriveInfo(result, arriveTime);
            indexListener.onClicked(index);
            barChartView.setClickedPosition(index);
            barChartView.enableClicked(false);
        } else {
            //multiGroupHistogramView.setVisibility(VISIBLE);
            beginTimeTitle.setText("出发时间");
            arriveTimeTitle.setText("到达时间");
            ViewGroup.LayoutParams params = barChartView.getLayoutParams();
            params.height = DisplayUtil.dp2px(130);
            barChartView.setLayoutParams(params);
            setArriveTimeTitle.setVisibility(INVISIBLE);
            setArriveTime.setVisibility(INVISIBLE);
            barChartView.enableClicked(true);
            barChartView.setClickedPosition(0);
            indexListener.onClicked(0);
            setPathInfo(result, 0, true);
        }
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.travel_time, this);
        barChartView = findViewById(R.id.bar_chart_View);
        beginTime           = findViewById(R.id.begin_time_content);
        arriveTime          = findViewById(R.id.arrive_time_content);
        distance            = findViewById(R.id.distance_content);
        beginTimeTitle      = findViewById(R.id.begin_time_text);
        arriveTimeTitle     = findViewById(R.id.arrive_time_text);
        setArriveTimeTitle  = findViewById(R.id.set_arrive_time_text);
        setArriveTime       = findViewById(R.id.set_arrive_time_text_content);
        distanceTextTitle   = findViewById(R.id.distance_text);
        distanceContent     = findViewById(R.id.distance_content);
    }

    private void setPathInfo(final DriveRoutePlanResult result, int index, boolean setDistance) {
        if(index >= 0 && result.getTimeInfos().size() > index) {
            TimeInfo info = result.getTimeInfos().get(index);
            TimeInfosElement element = info.getElements().get(0);
            beginTime.setText(utils.times(info.getStartTime()));
            arriveTime.setText(String.valueOf(utils.times(info.getStartTime() + (long)element.getDuration() *60 )));
            DrivePlanPath path = result.getPaths().get(element.getPathindex());
            if (setDistance) {
                distance.setText(String.valueOf((int)path.getDistance()/1000));
            } else {
                distance.setText(String.valueOf(element.getDuration()) + "分");
            }
        } else {
            beginTime.setText("error");
            arriveTime.setText("error");
            distance.setText("error");
        }

    }

    private int setArriveInfo(final DriveRoutePlanResult result, long arriveTime) {
        int lastIndex = -1;
        for (int i = 0; i < result.getTimeInfos().size(); i++) {
            TimeInfo info = result.getTimeInfos().get(i);
            if (info.getElements().size() < 0) {
                Log.e("","getElementsCount error:" + info.getElements().size());
                return lastIndex;
            }
            TimeInfosElement element = info.getElements().get(0);
            if (info.getStartTime() + element.getDuration()*60 <= arriveTime - utils.commendDiff*60) {
                lastIndex = i;
            } else {
                break;
            }
        }


        setPathInfo(result, lastIndex, false);

        return lastIndex;
    }
}
