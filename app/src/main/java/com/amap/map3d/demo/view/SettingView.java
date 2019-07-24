package com.amap.map3d.demo.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amap.map3d.demo.R;

//import amap.com.travel.MainActivity;

import com.amap.map3d.demo.util.utils;

public class SettingView extends ConstraintLayout implements View.OnClickListener{

    private EditText queryFirstInterval;
    private EditText queryInterval;
    private EditText recommendDiff;
    private Button button;
    private ButtonVisibleListener buttonVisibleListener;
    //private MainActivity activity;

    public SettingView(Context context) {
        super(context);
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context,attrs);
        initView(context);
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.setting, this);

        queryFirstInterval  = findViewById(R.id.query_first_timestamp_content);
        queryInterval       = findViewById(R.id.query_interval_content);
        recommendDiff       = findViewById(R.id.query_diff_content);
        button              = findViewById(R.id.setting_ok);
        button.setOnClickListener(this);
    }

    public void setButtonVisible(ButtonVisibleListener buttonVisible) {
        this.buttonVisibleListener = buttonVisible;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_ok:
                try {
                    utils.commendDiff = Integer.valueOf(recommendDiff.getText().toString());
                    utils.queryFirstInterval = Integer.valueOf(queryFirstInterval.getText().toString());
                    utils.queryInterval = Integer.valueOf(queryInterval.getText().toString());
                }catch (Exception e){

                }

                this.setVisibility(GONE);
                if (buttonVisibleListener != null) {
                    buttonVisibleListener.onButtonVisible(View.VISIBLE);
                }
                break;

            default:
                break;
        }
    }

    public interface ButtonVisibleListener{
        void onButtonVisible(int visible);
    }
}
