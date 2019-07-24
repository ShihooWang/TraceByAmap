package com.amap.map3d.demo.inputtip;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 输入提示功能实现
 */
public class InputtipsActivity extends Activity implements TextWatcher, InputtipsListener {

	private String city = "北京";
	private AutoCompleteTextView mKeywordText;
	private ListView minputlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inputtip);
		minputlist = (ListView)findViewById(R.id.inputlist);
		mKeywordText = (AutoCompleteTextView)findViewById(R.id.input_edittext);
        mKeywordText.addTextChangedListener(this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
        InputtipsQuery inputquery = new InputtipsQuery(newText, city);
        inputquery.setCityLimit(true);
        Inputtips inputTips = new Inputtips(InputtipsActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
        
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 输入提示结果的回调
	 * @param tipList
	 * @param rCode
     */
	@Override
	public void onGetInputtips(final List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            List<HashMap<String, String>> listString = new ArrayList<HashMap<String, String>>();
            if(tipList != null) {
            	int size = tipList.size();
				for (int i = 0; i < size; i++) {
					Tip tip = tipList.get(i);
					if(tip != null) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("name", tipList.get(i).getName());
						map.put("address", tipList.get(i).getDistrict());
						listString.add(map);
					}
				}
				SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), listString, R.layout.item_layout,
						new String[]{"name", "address"}, new int[]{R.id.poi_field_id, R.id.poi_value_id});

				minputlist.setAdapter(aAdapter);
				aAdapter.notifyDataSetChanged();
			}

        } else {
			ToastUtil.showerror(this.getApplicationContext(), rCode);
		}
		
	}

}
