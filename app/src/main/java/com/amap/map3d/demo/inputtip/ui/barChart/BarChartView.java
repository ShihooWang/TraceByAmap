package com.amap.map3d.demo.ui.barChart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import java.util.List;

import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.DisplayUtil;
import com.amap.map3d.demo.util.StringUtil;

public class BarChartView extends View {
    private int width;
    private int height;

    private int lineWidth;

    private int nameTextSize;
    private int clickedNameTextSize;

    private int barChartInterval;

    private int valueTextSize;
    private int clickedValueTextSize;
    private int barChartWidth;
    private int chartPaddingTop;
    private int paddingStart;
    private int paddingEnd;

    private int distanceFormNameToLine;

    private int valueTobarChartDistance;

    private int maxbarChartHeight;

    private Paint linePaint;

    private Paint namePaint;
    private Paint clickedNamePaint;
    private Paint.FontMetrics nameFontMetrics;
    private Paint.FontMetrics valueFontMetrics;

    private Paint valuePaint;
    private Paint clickedValuePaint;

    private Paint barChartPaint;

    private Rect barChartPaintRect;

    private int barChartTotalWidth;

    private SparseArray<int[]> barChartColors;

    private List<BarChartData> dataList;
    private float maxValue;

    private Scroller scroller;
    private int minimumVelocity;
    private int maximumVelocity;
    private VelocityTracker tracker;
    private int clickedPosition;
    private IndexListener indexListener;
    private boolean enableClicked;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        lineWidth = DisplayUtil.dp2px(2);
        int lineColor = Color.parseColor("#434343");
        int nameTextColor = Color.parseColor("#CC202332");
        int clickedNameTextColor = Color.parseColor("#CCFFFFFF");
        nameTextSize = DisplayUtil.dp2px(15);
        clickedNameTextSize = DisplayUtil.dp2px(20);
        barChartInterval = DisplayUtil.dp2px(30);

        int barChartValueTextColor = Color.parseColor("#CC202332");
        clickedValueTextSize = DisplayUtil.dp2px(20);
        valueTextSize = DisplayUtil.dp2px(12);
        clickedValueTextSize = DisplayUtil.dp2px(17);
        barChartWidth = DisplayUtil.dp2px(20);
        chartPaddingTop = DisplayUtil.dp2px(10);
        paddingStart = DisplayUtil.dp2px(15);
        paddingEnd = DisplayUtil.dp2px(15);
        distanceFormNameToLine = DisplayUtil.dp2px(15);
        valueTobarChartDistance = DisplayUtil.dp2px(10);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(lineColor);

        namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        namePaint.setTextSize(nameTextSize);
        namePaint.setColor(nameTextColor);
        nameFontMetrics = namePaint.getFontMetrics();

        clickedNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickedNamePaint.setColor(clickedNameTextColor);
        clickedNamePaint.setTextSize(clickedNameTextSize);

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setTextSize(valueTextSize);
        valuePaint.setColor(barChartValueTextColor);
        valueFontMetrics = valuePaint.getFontMetrics();

        clickedValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickedValuePaint.setColor(barChartValueTextColor);
        clickedValuePaint.setTextSize(clickedValueTextSize);

        barChartPaintRect = new Rect();
        barChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scroller = new Scroller(getContext(), new LinearInterpolator());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();

        barChartColors = new SparseArray<int[]>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        maxbarChartHeight = height - nameTextSize - lineWidth - distanceFormNameToLine - valueTobarChartDistance - valueTextSize - chartPaddingTop;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (direction > 0) {
            return barChartTotalWidth - getScrollX() - width + paddingStart + paddingEnd > 0;
        } else {
            return getScrollX() > 0;
        }
    }

    private int getMaxScrollX(int direction) {
        if (direction > 0) {
            return barChartTotalWidth - getScrollX() - width + paddingStart + paddingEnd > 0 ?
                    barChartTotalWidth - getScrollX() - width + paddingStart + paddingEnd : 0;
        } else if (direction < 0) {
            return getScrollX();
        }
        return 0;
    }

    private float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initTrackerIfNotExists();
        tracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                lastX = event.getX();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = (int) (event.getX() - lastX);
                lastX = event.getX();
                if (deltaX > 0 && canScrollHorizontally(-1)) {
                    scrollBy(-Math.min(getMaxScrollX(-1), deltaX), 0);
                } else if (deltaX < 0 && canScrollHorizontally(1)) {
                    scrollBy(Math.min(getMaxScrollX(1), -deltaX), 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                tracker.computeCurrentVelocity(1000, maximumVelocity);
                int velocityX = (int) tracker.getXVelocity();
                fling(velocityX);
                releaseTracker();

                if (!enableClicked) {
                    break;
                }

                clickedPosition = -1;
                for (int i = 0; i < dataList.size(); i++) {
                    BarChartData groupData = dataList.get(i);
                    Log.d("text ","i:" + i + "getPositionX:" + groupData.getPositionX() + " barChartbarChartWidth:" + barChartWidth);
                    if (event.getX() > groupData.getPositionX()  - scroller.getCurrX() && event.getX() < groupData.getPositionX() + barChartWidth*2 - scroller.getCurrX()) {
                        clickedPosition = i;
                        invalidate();

                        break;
                    }
                }

                if (indexListener !=null && clickedPosition != -1) {
                    indexListener.onClicked(clickedPosition);
                }
                Log.d("text","clickedPosition:" + clickedPosition + " scroller.getCurrX():" + scroller.getCurrX());

                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                releaseTracker();
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void initTrackerIfNotExists() {
        if (tracker == null) {
            tracker = VelocityTracker.obtain();
        }
    }

    private void releaseTracker() {
        if (tracker != null) {
            tracker.recycle();
            tracker = null;
        }
    }

    private void fling(int velocityX) {
        if (Math.abs(velocityX) > minimumVelocity) {
            if (Math.abs(velocityX) > maximumVelocity) {
                velocityX = maximumVelocity * velocityX / Math.abs(velocityX);
            }
            scroller.fling(getScrollX(), getScrollY(), -velocityX, 0, 0, barChartTotalWidth + paddingStart - width, 0, 0);
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
        }
    }

    public void setDataList( List<BarChartData> dataList) {
        this.dataList = dataList;
        barChartTotalWidth = 0;
        for (BarChartData groupData : dataList) {
            if (maxValue < groupData.getValue()) {
                maxValue = groupData.getValue();
            }

            barChartTotalWidth += barChartWidth ;
        }
        barChartTotalWidth += -barChartInterval;
        postInvalidate();
    }

    public void setBarChartColor(int[]... colors) {
        if (colors != null && colors.length > 0) {
            barChartColors.clear();
            for (int i = 0; i < colors.length; i++) {
                barChartColors.put(i, colors[i]);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            return;
        }
        int scrollX = getScrollX();
        int bottomLine = height - nameTextSize - distanceFormNameToLine - lineWidth / 2;
        //canvas.drawLine(coordinateAxisWidth / 2 + scrollX, 0, coordinateAxisWidth / 2 + scrollX, axisBottom, coordinateAxisPaint);
        canvas.drawLine(scrollX, bottomLine, width + scrollX, bottomLine, linePaint);
        int pos = 0;
        if (dataList != null && dataList.size() > 0) {
            int xOffset = paddingStart;
            for (BarChartData date : dataList) {
                int groupWidth = 0;
                barChartPaintRect.left = xOffset;
                barChartPaintRect.right = barChartPaintRect.left + barChartWidth;
                int barChartHeight = maxValue == 0 ? 0 : (int) (date.getValue() / maxValue * maxbarChartHeight);


                barChartPaintRect.top = height - barChartHeight - lineWidth - distanceFormNameToLine - nameTextSize;
                barChartPaintRect.bottom = barChartPaintRect.top + barChartHeight;
                int[] barChartShaderColor;
                if (pos == clickedPosition) {
                    barChartShaderColor = barChartColors.get(1);
                } else {
                    barChartShaderColor = barChartColors.get(0);
                }

                LinearGradient shader = null;
                if (barChartShaderColor != null && barChartShaderColor.length > 0) {
                    shader = getbarChartShader(barChartPaintRect.left, chartPaddingTop + valueTobarChartDistance + valueTextSize,
                            barChartPaintRect.right, barChartPaintRect.bottom, barChartShaderColor);
                }
                barChartPaint.setShader(shader);

                canvas.drawRect(barChartPaintRect, barChartPaint);
                String barChartHeightValue = StringUtil.NumericScaleByFloor(String.valueOf(date.getValue()), 0) + date.getUnit();

                if (pos == clickedPosition) {
                    float valueTextX = xOffset + (barChartWidth - clickedValuePaint.measureText(barChartHeightValue)) / 2;
                    float valueTextY = barChartPaintRect.top - distanceFormNameToLine + (valueFontMetrics.bottom) / 2;
                    canvas.drawText(barChartHeightValue, valueTextX, valueTextY, clickedValuePaint);
                }else {
                    float valueTextX = xOffset + (barChartWidth - valuePaint.measureText(barChartHeightValue)) / 2;
                    float valueTextY = barChartPaintRect.top - distanceFormNameToLine + (valueFontMetrics.bottom) / 2;
                    canvas.drawText(barChartHeightValue, valueTextX, valueTextY, valuePaint);
                }

                int deltaX =  barChartWidth ;
                groupWidth += deltaX;
                xOffset += deltaX + barChartInterval ;


                if (/*pos == clickedPosition*/false) {
                    float nameTextWidth = clickedNamePaint.measureText(date.getName());
                    float nameTextX = xOffset - groupWidth - barChartInterval + (groupWidth - nameTextWidth) / 2;
                    float nameTextY = (height - nameFontMetrics.bottom / 2);
                    date.setPositionX((int)nameTextX);
                    date.setWidth(width);
                    canvas.drawText(date.getName(), nameTextX, nameTextY, clickedNamePaint);
                } else {
                    float nameTextWidth = namePaint.measureText(date.getName());
                    float nameTextX = xOffset - groupWidth - barChartInterval + (groupWidth - nameTextWidth) / 2;
                    float nameTextY = (height - nameFontMetrics.bottom / 2);
                    date.setPositionX((int)nameTextX);
                    date.setWidth(width);
                    canvas.drawText(date.getName(), nameTextX, nameTextY, namePaint);
                }
                pos ++;
            }
        }
    }

    private LinearGradient getbarChartShader(float x0, float y0, float x1, float y1, int[] colors) {
        return new LinearGradient(x0, y0, x1, y1, colors, null, Shader.TileMode.CLAMP);
    }

    public interface IndexListener {
        void onClicked(int index);
    }

    public void setIndexListener(IndexListener indexListener) {
        this.indexListener = indexListener;
    }

    public void enableClicked(boolean enable) {
        enableClicked = enable;
    }

    public void setClickedPosition(int clickedPosition) {
        this.clickedPosition = clickedPosition;
    }
}
