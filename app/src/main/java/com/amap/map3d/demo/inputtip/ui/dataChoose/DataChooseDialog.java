package com.amap.map3d.demo.ui.dataChoose;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.amap.map3d.demo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataChooseDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private DateChooseInterface dateChooseInterface;
    private Dialog mDialog;
    private Button mSureButton;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Date date;

    public DataChooseDialog(Context context, DateChooseInterface dateChooseInterface) {
        super(context);
        this.mContext = context;
        this.dateChooseInterface = dateChooseInterface;
        mDialog = new Dialog(context, R.style.dialog);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_btn://确定选择按钮监听
                Calendar calendar = Calendar.getInstance();
                calendar.set(date.year, date.month, date.day, date.hour, date.minute);
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                calendar.getTime();
                Log.d("qyd", "date:" + format.format(calendar.getTime()));
                dateChooseInterface.getDateTime(format.format(calendar.getTime()));

                dismissDialog();
                break;

            default:
                break;
        }
    }

    private void init() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_date_choose, null);
        mDialog.setContentView(view);

        datePicker = (DatePicker) view.findViewById(R.id.dpPicker);
        timePicker = (TimePicker) view.findViewById(R.id.tpPicker);

        mSureButton = (Button) view.findViewById(R.id.sure_btn);
        mSureButton.setOnClickListener(this);

        date = new Date();

        setDate();

        Calendar nowCalendar = Calendar.getInstance();

        datePicker.init(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), nowCalendar.get(Calendar.DATE), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                // 获取一个日历对象，并初始化为当前选中的时间
                date.year   = year;
                date.month  = monthOfYear;
                date.day    = dayOfMonth;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm");
                calendar.getTime();
            }
        });

        timePicker.setIs24HourView(true);
        timePicker
                .setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay,
                                              int minute) {
                        date.hour    = hourOfDay;
                        date.minute  = minute;
                    }
                });
    }

    private void setDate() {

        Calendar calendar = Calendar.getInstance();
        date.year   = calendar.get(Calendar.YEAR);
        date.month  = calendar.get(Calendar.MONTH);
        date.day    = calendar.get(Calendar.DATE);
        date.hour   = calendar.get(Calendar.HOUR_OF_DAY);
        date.minute = calendar.get(Calendar.MINUTE);
    }

    private void dismissDialog() {

        if (Looper.myLooper() != Looper.getMainLooper()) {

            return;
        }

        if (null == mDialog || !mDialog.isShowing() || null == mContext
                || ((Activity) mContext).isFinishing()) {

            return;
        }

        mDialog.dismiss();
        this.dismiss();
    }

    /**
     *  xx年xx月xx日xx时xx分转成yyyy-MM-dd HH:mm
     * @param yearStr
     * @param dateStr
     * @param hourStr
     * @param minuteStr
     * @return
     */
    private String strTimeToDateFormat(String yearStr, String dateStr, String hourStr, String minuteStr) {

        return yearStr.replace("年", "-") + dateStr.replace("月", "-").replace("日", " ")
                + hourStr + ":" + minuteStr;
    }

    /**
     * 显示日期选择dialog
     */
    public void showChooseDialog() {

        if (Looper.myLooper() != Looper.getMainLooper()) {

            return;
        }

        if (null == mContext || ((Activity) mContext).isFinishing()) {

            // 界面已被销毁
            return;
        }

        if (null != mDialog) {

            mDialog.show();
            return;
        }

        if (null == mDialog) {

            return;
        }

        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    /**
     * 回调选中的时间（默认时间格式"yyyy-MM-dd HH:mm:ss"）
     */
    public interface DateChooseInterface{
        void getDateTime(String time);
    }

    public class Date {
        public int year;
        public int month;
        public int day;
        public int hour;
        public int minute;
    }
}
