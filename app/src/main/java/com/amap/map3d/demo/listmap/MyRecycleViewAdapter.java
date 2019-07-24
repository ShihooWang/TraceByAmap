package com.amap.map3d.demo.listmap;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.map3d.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shixin on 2018/4/24.
 */

class MyRecycleViewAdapter extends android.support.v7.widget.RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TextureMapView> mapViews = new ArrayList();
    private List<BaseEntity> mData;

    public MyRecycleViewAdapter(List<BaseEntity> list) {
        this.mData = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == Consts.VIEWTYPE_TEXT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_text_item, parent, false);
            MyTextHolder myTextHolder = new MyTextHolder(v);
            return myTextHolder;
        }
        if (viewType == Consts.VIEWTYPE_MAP) {
            Log.i("SHIXIN", "onCreateViewHolder : 创建地图");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_map_item, parent, false);
            MyMapHolder myMapHolder = new MyMapHolder(v);
            myMapHolder.mapView.onCreate(null);
            mapViews.add(myMapHolder.mapView);
            return myMapHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == Consts.VIEWTYPE_TEXT) {
            MyTextHolder textHolder = (MyTextHolder) holder;
            TextEntity textEntity = (TextEntity) mData.get(position);
            textHolder.textView.setText(textEntity.text);
        }
        if (viewType == Consts.VIEWTYPE_MAP) {
            MyMapHolder mapHolder = (MyMapHolder) holder;
            Log.i("SHIXIN", "update mapView");
            MapEntity mapEntity = (MapEntity) mData.get(position);
            addElementOnMap(mapHolder.mapView.getMap(), position, mapEntity);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    private void addElementOnMap(AMap map, int position, MapEntity mapEntity) {
        try {
            /**
             * 因为listview 中的mapview会复用，所以我们每次清空上一次的element
             */
            map.clear();
            if (position == 3) {
                addMarker(map, mapEntity.point);
            }
            if (position == 5) {
                addPolyline(map, mapEntity.polylinePoints);
            }
            if (position == 17) {
                addPolyline(map, mapEntity.polylinePoints);
            }
            if (position == 9) {
                addPolyline(map, mapEntity.polylinePoints);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPolyline(AMap map, List<LatLng> polylinePoints) {
        map.addPolyline((new PolylineOptions())
                .addAll(polylinePoints)
                .width(10));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(polylinePoints.get(0), 15));
    }

    public void addMarker(AMap aMap, LatLng D) {
        Marker marker2 = aMap.addMarker(new MarkerOptions().position(D).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
        Marker marker1 = aMap.addMarker(new MarkerOptions().position(new LatLng(D.latitude + 0.00015, D.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
        marker1.setVisible(true);
        marker2.setVisible(true);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(D, 18));
    }

    public void onDestroy() {
        for (TextureMapView mapView : mapViews) {
            Log.i("SHIXIN", "onDestroy : 销毁地图");
            mapView.onDestroy();
        }
    }

    private static class MyTextHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyTextHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_tv);
        }
    }

    private static class MyMapHolder extends RecyclerView.ViewHolder {

        TextureMapView mapView;

        public MyMapHolder(View itemView) {
            super(itemView);
            mapView = (TextureMapView) itemView.findViewById(R.id.item_map);
        }
    }
}
