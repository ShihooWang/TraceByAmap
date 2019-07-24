package com.amap.map3d.demo.weather;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.ToastUtil;

import java.util.List;

/**
 * 天气查询 示例查询
 */
public class WeatherSearchActivity extends Activity implements OnWeatherSearchListener {
    private TextView forecasttv;
    private TextView reporttime1;
    private TextView reporttime2;
    private TextView weather;
    private TextView Temperature;
    private TextView wind;
    private TextView humidity;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private LocalWeatherForecast weatherforecast;
    private List<LocalDayWeatherForecast> forecastlist = null;
    private String cityname = "北京市";//天气搜索的城市，可以写名称或adcode；

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);
        init();
        searchliveweather();
        searchforcastsweather();
    }

    private void init() {
        TextView city = (TextView) findViewById(R.id.city);
        city.setText(cityname);
        forecasttv = (TextView) findViewById(R.id.forecast);
        reporttime1 = (TextView) findViewById(R.id.reporttime1);
        reporttime2 = (TextView) findViewById(R.id.reporttime2);
        weather = (TextView) findViewById(R.id.weather);
        Temperature = (TextView) findViewById(R.id.temp);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
    }

    /**
     * 预报天气查询
     */
    private void searchforcastsweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    /**
     * 实时天气查询
     */
    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    /**
     * 实时天气查询回调
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                reporttime1.setText(weatherlive.getReportTime() + "发布");
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "°");
                wind.setText(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
                humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
            } else {
                ToastUtil.show(WeatherSearchActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(WeatherSearchActivity.this, rCode);
        }
    }

    /**
     * 天气预报查询结果回调
     */
    @Override
    public void onWeatherForecastSearched(
            LocalWeatherForecastResult weatherForecastResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherForecastResult != null && weatherForecastResult.getForecastResult() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
                weatherforecast = weatherForecastResult.getForecastResult();
                forecastlist = weatherforecast.getWeatherForecast();
                fillforecast();

            } else {
                ToastUtil.show(WeatherSearchActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(WeatherSearchActivity.this, rCode);
        }
    }

    private void fillforecast() {
        reporttime2.setText(weatherforecast.getReportTime() + "发布");
        String forecast = "";
        for (int i = 0; i < forecastlist.size(); i++) {
            LocalDayWeatherForecast localdayweatherforecast = forecastlist.get(i);
            String week = null;
            switch (Integer.valueOf(localdayweatherforecast.getWeek())) {
                case 1:
                    week = "周一";
                    break;
                case 2:
                    week = "周二";
                    break;
                case 3:
                    week = "周三";
                    break;
                case 4:
                    week = "周四";
                    break;
                case 5:
                    week = "周五";
                    break;
                case 6:
                    week = "周六";
                    break;
                case 7:
                    week = "周日";
                    break;
                default:
                    break;
            }
            String temp = String.format("%-3s/%3s",
                    localdayweatherforecast.getDayTemp() + "°",
                    localdayweatherforecast.getNightTemp() + "°");
            String date = localdayweatherforecast.getDate();
            forecast += date + "  " + week + "                       " + temp + "\n\n";
        }
        forecasttv.setText(forecast);
    }
}
