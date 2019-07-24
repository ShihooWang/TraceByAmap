package com.amap.map3d.demo.tools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.CoordinateConverter.CoordType;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

/**
 * 其他坐标系转换为高德坐标系 功能示例
 */
public class CoordConverActivity extends Activity implements OnItemSelectedListener {
	private EditText latView, lngView;
	private TextView resultview;
	private LatLng sourceLatLng = new LatLng(39.989646,116.480864);
	private LatLng desLatLng;
	private Spinner selectcoord;// 选择坐标系下拉列表
	private String[] itemCoords = { "BAIDU", "GPS", "MAPBAR", "MAPABC", "SOSOMAP", "ALIYUN", "GOOGLE" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert_activity);
		init();
		
	}

	/**
	 * 初始化View
	 */
	private void init() {
		selectcoord = (Spinner)findViewById(R.id.coord);
		latView = (EditText)findViewById(R.id.pointLat);
		lngView = (EditText)findViewById(R.id.pointLng);
		resultview = (TextView)findViewById(R.id.result);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, itemCoords);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectcoord.setAdapter(adapter);
		selectcoord.setPrompt("请选择坐标系：");
		selectcoord.setOnItemSelectedListener(this);
	}

	/**
	 * 根据类型 转换 坐标
     */
	private LatLng convert(LatLng sourceLatLng, CoordType coord ) {
		CoordinateConverter converter  = new CoordinateConverter(this); 
		// CoordType.GPS 待转换坐标类型
		converter.from(coord); 
		// sourceLatLng待转换坐标点
		converter.coord(sourceLatLng); 
		// 执行转换操作
		LatLng desLatLng = converter.convert();
		return desLatLng;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String latStr = latView.getText().toString().trim();
		String lngStr = lngView.getText().toString().trim();
		if ("".equals(latStr) || latStr == null) {
			latView.setText(String.valueOf(sourceLatLng.latitude));
			latStr = "39.989646";
		}
		if ("".equals(lngStr) || lngStr == null) {
			lngView.setText(String.valueOf(sourceLatLng.longitude));
			lngStr ="116.480864";
		}
		double lat = 0.0;
		double lon = 0.0;
		lat = Double.parseDouble(latStr);
		lon = Double.parseDouble(lngStr);
		sourceLatLng = new LatLng(lat, lon);
		String coordString = itemCoords[position];
		CoordType mcoordtype = CoordType.valueOf(coordString);	
		desLatLng = convert(sourceLatLng, mcoordtype);
		if (desLatLng != null) {
			resultview.setText(desLatLng.toString());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}
