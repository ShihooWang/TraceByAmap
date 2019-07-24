package com.amap.map3d.demo.listmap;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * Created by shixin on 2018/4/24.
 */

public class MapEntity extends BaseEntity {
    public LatLng point;
    public List<LatLng> polylinePoints;

    public MapEntity(LatLng point) {
        super(Consts.VIEWTYPE_MAP);
        this.point = point;
    }

    public MapEntity(List<LatLng> points) {
        super(Consts.VIEWTYPE_MAP);
        this.polylinePoints = points;

    }

}
