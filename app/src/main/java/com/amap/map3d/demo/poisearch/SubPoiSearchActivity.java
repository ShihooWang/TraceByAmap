package com.amap.map3d.demo.poisearch;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;
import com.amap.map3d.demo.util.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

/**
 * 介绍POI的父子关系功能
 */
public class SubPoiSearchActivity extends Activity implements OnPoiSearchListener, TextWatcher, InputtipsListener {
private String city = "北京";
private PoiListAdapter mpoiadapter;
private ListView mPoiSearchList;
private Button mSearchbtn;
private AutoCompleteTextView mKeywordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subpoisearch_activity);
        mPoiSearchList = (ListView)findViewById(R.id.listView);
        mKeywordText = (AutoCompleteTextView)findViewById(R.id.keyword);
        mKeywordText.addTextChangedListener(this);
        mSearchbtn = (Button)findViewById(R.id.search_btn);
        mSearchbtn.setOnClickListener(new OnClickListener() {          
            @Override
            public void onClick(View v) {
                String keyword = mKeywordText.getText().toString();
                poi_Search(keyword);      
            }
        });
        poi_Search("清华大学");
    }
    
    private void poi_Search(String str){
        PoiSearch.Query mPoiSearchQuery = new PoiSearch.Query(str, "", city);  
        mPoiSearchQuery.requireSubPois(true);   //true 搜索结果包含POI父子关系; false 
        mPoiSearchQuery.setPageSize(10);
        mPoiSearchQuery.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(SubPoiSearchActivity.this,mPoiSearchQuery);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();        
    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            List<PoiItem> poiItems = new ArrayList<PoiItem>();
            poiItems.add(item);
            mpoiadapter=new PoiListAdapter(this, poiItems);
            mPoiSearchList.setAdapter(mpoiadapter);
        } else {
        	ToastUtil.showerror(this, rCode);
		}
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null ) {
                List<PoiItem> poiItems = result.getPois();
                mpoiadapter=new PoiListAdapter(this, poiItems);
                mPoiSearchList.setAdapter(mpoiadapter);                    
            }                      
        } else {
        	ToastUtil.showerror(this, rCode);
		}       
    }

    @Override
    public void afterTextChanged(Editable s) {
        
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        
    }
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    	String newText = s.toString().trim();
    	if (!AMapUtil.IsEmptyOrNullString(newText)) {
    		InputtipsQuery inputquery = new InputtipsQuery(newText, city);
    		inputquery.setCityLimit(true);
    		Inputtips inputTips = new Inputtips(SubPoiSearchActivity.this, inputquery);
    		inputTips.setInputtipsListener(this);
    		inputTips.requestInputtipsAsyn();
    	}
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (tipList != null) {
                List<String> listString = new ArrayList<String>();
                int size = tipList.size();
                for (int i = 0; i < size; i++) {
                    listString.add(tipList.get(i).getName());
                }
                ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                        getApplicationContext(),
                        R.layout.route_inputs, listString);
                mKeywordText.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
            }
        } else {
        	ToastUtil.showerror(this, rCode);
		}     
    }
}
