package overlay;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePlanPath;
import com.amap.api.services.route.DrivePlanStep;
import com.amap.api.services.route.DriveRoutePlanResult;
import com.amap.api.services.route.TMC;
import com.amap.api.services.route.TimeInfo;
import com.amap.api.services.route.TimeInfosElement;
import com.amap.map3d.demo.util.AMapUtil;

import java.util.ArrayList;
import java.util.List;

public class DrivingRoutePlanOverlay extends RouteOverlay{

    private Context mContext;

    DriveRoutePlanResult driveRoutePlanResult;

    private boolean isColorfulline = true;
    private float mWidth = 25;
    private List<LatLng> mLatLngsOfPath;
    private int selectIndex = 0;

    private PolylineOptions mPolylineOptions;
    private PolylineOptions mPolylineOptionscolor;

    public DrivingRoutePlanOverlay(Context context, AMap amap, DriveRoutePlanResult result, int index) {
        super(context);

        mContext                = context;
        mAMap                   = amap;
        driveRoutePlanResult    = result;
        selectIndex             = index;

        if (result != null) {
            startPoint          = AMapUtil.convertToLatLng(result.getStartPos());
            endPoint            = AMapUtil.convertToLatLng(result.getTargetPos());
        }
    }

    public void setIsColorfulline(boolean iscolorfulline) {
        this.isColorfulline = iscolorfulline;
    }

    /**
     * 添加驾车路线添加到地图上显示。
     */
    public void addToMap() {
        initPolylineOptions();
        try {
            if (mAMap == null) {
                return;
            }

            if (mWidth == 0 || driveRoutePlanResult == null) {
                return;
            }

            if (driveRoutePlanResult.getTimeInfos() == null || driveRoutePlanResult.getPaths() == null) {
                return;
            }

            mLatLngsOfPath = new ArrayList<LatLng>();
            List<TMC> tmcs = new ArrayList<TMC>();

            List<TimeInfo> timeInfos = driveRoutePlanResult.getTimeInfos();

            if (selectIndex >= 0 && selectIndex < timeInfos.size()) {
                tmcs = timeInfos.get(selectIndex).getElements().get(0).getTMCs();
            }

            int pathIndex = timeInfos.get(selectIndex).getElements().get(0).getPathindex();
            if (pathIndex < 0 || pathIndex > driveRoutePlanResult.getPaths().size()) {
                return;
            }

            DrivePlanPath drivePlanPath = driveRoutePlanResult.getPaths().get(pathIndex);


            List<DrivePlanStep> drivePaths = drivePlanPath.getSteps();
            for (DrivePlanStep step : drivePaths) {
                List<LatLonPoint> latlonPoints = step.getPolyline();
                //List<TMC> tmclist = step.getTMCs();
                //tmcs.addAll(tmclist);
                for (LatLonPoint latlonpoint : latlonPoints) {
                    mPolylineOptions.add(convertToLatLng(latlonpoint));
                    mLatLngsOfPath.add(convertToLatLng(latlonpoint));
                }
            }
            if (startMarker != null) {
                startMarker.remove();
                startMarker = null;
            }
            if (endMarker != null) {
                endMarker.remove();
                endMarker = null;
            }
            addStartAndEndMarker();

            if (isColorfulline && tmcs.size()>0 ) {
                colorWayUpdate(tmcs);
                showcolorPolyline();
            }else {
                showPolyline();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }

    private void showcolorPolyline() {
        addPolyLine(mPolylineOptionscolor);

    }

    /**
     * 根据不同的路段拥堵情况展示不同的颜色
     *
     * @param tmcSection
     */
    private void colorWayUpdate(List<TMC> tmcSection) {
        if (mAMap == null) {
            return;
        }
        if (tmcSection == null || tmcSection.size() <= 0) {
            return;
        }
        TMC segmentTrafficStatus;
        mPolylineOptionscolor = null;
        mPolylineOptionscolor = new PolylineOptions();
        mPolylineOptionscolor.width(getRouteWidth());
        List<Integer> colorList = new ArrayList<Integer>();
        mPolylineOptionscolor.add(AMapUtil.convertToLatLng(tmcSection.get(0).getPolyline().get(0)));
        colorList.add(getDriveColor());
        for (int i = 0; i < tmcSection.size(); i++) {
            segmentTrafficStatus = tmcSection.get(i);
            int color = getcolor(segmentTrafficStatus.getStatus());
            List<LatLonPoint> mployline = segmentTrafficStatus.getPolyline();
            for (int j = 1; j < mployline.size(); j++) {
                mPolylineOptionscolor.add(AMapUtil.convertToLatLng(mployline.get(j)));
                colorList.add(color);
            }
        }
        colorList.add(getDriveColor());
        mPolylineOptionscolor.colorValues(colorList);
    }

    private int getcolor(String status) {

        if (status.equals("畅通")) {
            return Color.GREEN;
        } else if (status.equals("缓行")) {
            return Color.YELLOW;
        } else if (status.equals("拥堵")) {
            return Color.RED;
        } else if (status.equals("严重拥堵")) {
            return Color.parseColor("#990033");
        } else {
            return Color.parseColor("#537edc");
        }
    }

    public LatLng convertToLatLng(LatLonPoint point) {
        return new LatLng(point.getLatitude(),point.getLongitude());
    }
}
