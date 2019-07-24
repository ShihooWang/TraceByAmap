package com.amap.map3d.demo.listmap;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shixin
 */
public class MapListViewActivity extends Activity {
    private ListView listView;
    private ListViewAdapter adapter;
    private LatLng A = new LatLng(39.962773, 116.391544);
    private LatLng B = new LatLng(39.922773, 116.401672);
    private LatLng C = new LatLng(39.913688, 116.40223);
    private LatLng D = new LatLng(39.913129, 116.392445);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_listview);
        listView = findViewById(R.id.listview);
        List<BaseEntity> list = getBaseEntities();
        adapter = new ListViewAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @NonNull
    private List<BaseEntity> getBaseEntities() {
        List<BaseEntity> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            if (i == 3) {
                list.add(new MapEntity(A));
            } else if (i == 5) {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(A);
                latLngs.add(B);
                latLngs.add(C);
                latLngs.add(D);
                list.add(new MapEntity(latLngs));
            } else if (i == 17) {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(A);
                latLngs.add(B);
                list.add(new MapEntity(latLngs));
            } else if (i == 9) {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(C);
                latLngs.add(D);
                list.add(new MapEntity(latLngs));
            } else {
                list.add(new TextEntity("我是列表:" + i));
            }
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.onDestroy();
    }


}
