package com.amap.map3d.demo.listmap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shixin on 2018/4/24.
 */

public class RecycleViewActivity extends Activity {
    private LatLng A = new LatLng(39.962773, 116.391544);
    private LatLng B = new LatLng(39.922773, 116.401672);
    private LatLng C = new LatLng(39.913688, 116.40223);
    private LatLng D = new LatLng(39.913129, 116.392445);
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecycleViewAdapter mAdapter;
    private List<BaseEntity> list;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("SHIXIN", "刷新列表");
            list.clear();
            list.addAll(getNewsEntities());
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_recycleview);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list = getBaseEntities();
        mAdapter = new MyRecycleViewAdapter(list);
        //
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }


    @NonNull
    private List<BaseEntity> getNewsEntities() {
        List<BaseEntity> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (i == 3) {
                list.add(new MapEntity(A));
            } else if (i == 7) {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(A);
                latLngs.add(B);
                latLngs.add(C);
                latLngs.add(D);
                list.add(new MapEntity(latLngs));
            } else {
                list.add(new TextEntity("我是列表:" + i));
            }
        }
        return list;
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
        mAdapter.onDestroy();
    }
}
