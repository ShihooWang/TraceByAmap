package com.amap.map3d.demo.indoor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.map3d.demo.R;

/**
 * 楼层控制控件
 */
public class IndoorFloorSwitchView extends ScrollView {
	public static final String TAG = IndoorFloorSwitchView.class
			.getSimpleName();

	private Context context;

	private LinearLayout views;

	private int itemHeight = 0;
	private List<String> items;

	private int scrollDirection = -1;
	private static final int SCROLL_DIRECTION_UP = 0;
	private static final int SCROLL_DIRECTION_DOWN = 1;

	private int viewWidth;

	private Bitmap selectBitmap = null;

	private int backGroundColor = Color.parseColor("#eeffffff");
	private int strokeColor = Color.parseColor("#44383838");
	private int strokeWidth = 4; // 边框宽度

	private int offset = 1; // 偏移量在最前面和最后面补全
	private int displayItemCount; // 每页显示的数
	int selectedIndex = 1;

	private int initialY;

	private Runnable scrollerTask;
	private int newCheck = 50;

	public IndoorFloorSwitchView(Context context) {
		super(context);
		init(context);
	}

	public IndoorFloorSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public IndoorFloorSwitchView(Context context, AttributeSet attrs,
                                 int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public int getOffset() {
		return offset;
	}

	/**
	 * 修改偏移量，即当第一个显示在中间是上面有几个空白<br>
	 * 也会影响整体显示，如设置1，上下各偏移1，总共显3个；设置2总共显示5个；
	 *
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	private void init(Context context) {
		this.context = context;

		this.setVerticalScrollBarEnabled(false);

        selectBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_indoor_select);

		views = new LinearLayout(context);
		views.setOrientation(LinearLayout.VERTICAL);
		this.addView(views);
		scrollerTask = new Runnable() {

			public void run() {

				int newY = getScrollY();
				if (initialY - newY == 0) { // stopped
					final int remainder = initialY % itemHeight;
					final int divided = initialY / itemHeight;
					if (remainder == 0) {
						selectedIndex = divided + offset;

						onSeletedCallBack();
					} else {
						if (remainder > itemHeight / 2) {
							IndoorFloorSwitchView.this.post(new Runnable() {
								@Override
								public void run() {
									IndoorFloorSwitchView.this.smoothScrollTo(
											0, initialY - remainder
													+ itemHeight);
									selectedIndex = divided + offset + 1;
									onSeletedCallBack();
								}
							});
						} else {
							IndoorFloorSwitchView.this.post(new Runnable() {
								@Override
								public void run() {
									IndoorFloorSwitchView.this.smoothScrollTo(
											0, initialY - remainder);
									selectedIndex = divided + offset;
									onSeletedCallBack();
								}
							});
						}

					}

				} else {
					initialY = getScrollY();
					IndoorFloorSwitchView.this.postDelayed(scrollerTask,
							newCheck);
				}
			}
		};

	}

	public void startScrollerTask() {

		initialY = getScrollY();
		this.postDelayed(scrollerTask, newCheck);
	}

	private void initData() {
		if(items==null||items.size()==0){
			return;
		}

		views.removeAllViews();
		displayItemCount = offset * 2 + 1;

		for (int i=items.size()-1;i>=0;i--) {
			views.addView(createView(items.get(i)));
		}

		refreshItemView(0);
	}

	private TextView createView(String item) {
		TextView tv = new TextView(context);
		tv.setLayoutParams(new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		tv.setSingleLine(true);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setText(item);
		tv.setGravity(Gravity.CENTER);
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);
		int padding_h = dip2px(context, 8);
		int padding_v = dip2px(context, 6);
		tv.setPadding(padding_h, padding_v, padding_h, padding_v);
		if (0 == itemHeight) {
			itemHeight = getViewMeasuredHeight(tv);
			views.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight
							* displayItemCount));
			this.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, itemHeight * displayItemCount));
		}
		return tv;
	}

	private void refreshItemView(int y) {
		int position = y / itemHeight + offset;
		int remainder = y % itemHeight;
		int divided = y / itemHeight;

		if (remainder == 0) {
			position = divided + offset;
		} else {
			if (remainder > itemHeight / 2) {
				position = divided + offset + 1;
			}

		}

		int childSize = views.getChildCount();
		for (int i = 0; i < childSize; i++) {
			TextView itemView = (TextView) views.getChildAt(i);
			if (null == itemView) {
				return;
			}
			if (position == i) {
				itemView.setTextColor(Color.parseColor("#0288ce"));
			} else {
				itemView.setTextColor(Color.parseColor("#bbbbbb"));
			}
		}
	}

	private List<String> getItems() {
		return items;
	}

	/**
	 * 设置显示的内*
	 *
	 * @param list
	 */
	public void setItems(List<String> list) {
		if (null == items) {
			items = new ArrayList<String>();
		}
		items.clear();
		items.addAll(list);

		// 前面和后面补
		for (int i = 0; i < offset; i++) {
			items.add(0, "");
			items.add("");
		}

		initData();

	}

	/**
	 * 设置显示的内*
	 *
	 * @param strs
	 */
	public void setItems(String[] strs) {
		if (null == items) {
			items = new ArrayList<String>();
		}

		items.clear();
		for (int i = 0; i < strs.length; i++)
			items.add(strs[i]);

		// 前面和后面补
		for (int i = 0; i < offset; i++) {
			items.add(0, "");
			items.add("");
		}

		initData();

	}

	public void setBackgroundColor(int color) {
		this.backGroundColor = color;
	}

	public void setStrokeColor(int color) {
		this.strokeColor = color;
	}

	public void setStrokeWidth(int width) {
		this.strokeWidth = width;
	}

	/**
	 * 设置选中状图片
	 *
	 * @param bitmap
	 */
	public void setIndoorSelectBitmap(Bitmap bitmap) {
		this.selectBitmap = bitmap;
	}

	public void destroy() {
		if (selectBitmap != null && !selectBitmap.isRecycled()) {
			selectBitmap.recycle();
			selectBitmap = null;
		}
	}

	@Override
	public void setBackgroundDrawable(Drawable background) {
		if (viewWidth == 0) {
			viewWidth = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getWidth();
		}

		background = new Drawable() {
			@Override
			public void draw(Canvas canvas) {

				try {
					drawBg(canvas);
					drawCenterLine(canvas);
					drawStroke(canvas);
				} catch (Throwable e) {
				}

			}

			private void drawBg(Canvas canvas) {
				canvas.drawColor(backGroundColor);
			}

			/**
			 * @param canvas
			 */
			private void drawCenterLine(Canvas canvas) {
				final Paint paint = new Paint();
				Rect src = new Rect();// 图片 >>原矩
				Rect dst = new Rect();// 屏幕 >>目标矩形

				src.left = 0;
				src.top = 0;
				src.right = 0 + selectBitmap.getWidth();
				src.bottom = 0 + selectBitmap.getHeight();

				dst.left = 0;
				dst.top = obtainSelectedAreaBorder()[0];
				dst.right = 0 + viewWidth;
				dst.bottom = obtainSelectedAreaBorder()[1];

				canvas.drawBitmap(selectBitmap, src, dst, paint);

			}

			/**
			 * @param canvas
			 */
			private void drawStroke(Canvas canvas) {
				final Paint mPaint = new Paint();
				Rect rect = canvas.getClipBounds();
				mPaint.setColor(strokeColor);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeWidth(strokeWidth);
				canvas.drawRect(rect, mPaint);
			}

			@Override
			public void setAlpha(int alpha) {

			}

			@Override
			public void setColorFilter(ColorFilter cf) {

			}

			@Override
			public int getOpacity() {
				return 0;
			}
		};

		super.setBackgroundDrawable(background);

	}

	/**
	 * 获取选中区域的边
	 */
	private int[] obtainSelectedAreaBorder() {
		int[] selectedAreaBorder = null;
		if (null == selectedAreaBorder) {
			selectedAreaBorder = new int[2];
			selectedAreaBorder[0] = itemHeight * offset;
			selectedAreaBorder[1] = itemHeight * (offset + 1);
		}
		return selectedAreaBorder;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		setBackgroundDrawable(null);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		refreshItemView(t);
		if (t > oldt) {
			scrollDirection = SCROLL_DIRECTION_DOWN;
		} else {
			scrollDirection = SCROLL_DIRECTION_UP;
		}
	}

	/**
	 * 选中回调
	 */
	private void onSeletedCallBack() {
		if (null != onIndoorFloorSwtichListener) {
			try {
				onIndoorFloorSwtichListener.onSelected(getSeletedIndex());
			} catch (Throwable e) {
			}
		}

	}

	public void setSeletion(String selectValue) {
		if(items==null||items.size()==0){
			return;
		}
		int position = items.indexOf(selectValue);
		final int p = items.size()-offset-1-position;
		selectedIndex = p + offset;
		this.post(new Runnable() {
			@Override
			public void run() {
				IndoorFloorSwitchView.this.smoothScrollTo(0, p * itemHeight);
			}
		});

	}

	public String getSeletedItem() {
		return items.get(selectedIndex);
	}

	public int getSeletedIndex() {
		if(items==null||items.size()==0){
			return 0;
		}
		int result = items.size()-1-selectedIndex - offset;
		return Math.min(items.size() - 2 * offset, Math.max(0, result));
	}

	@Override
	public void fling(int velocityY) {
		super.fling(velocityY / 3);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {

			startScrollerTask();
		}
		return super.onTouchEvent(ev);
	}

	private OnIndoorFloorSwitchListener onIndoorFloorSwtichListener;

	public OnIndoorFloorSwitchListener getOnIndoorFloorSwitchListener() {
		return onIndoorFloorSwtichListener;
	}

	public void setOnIndoorFloorSwitchListener(
			OnIndoorFloorSwitchListener onIndoorFloorSwtichListener) {
		this.onIndoorFloorSwtichListener = onIndoorFloorSwtichListener;
	}

	public static abstract interface OnIndoorFloorSwitchListener {
		public abstract void onSelected(int selectedIndex);
	}

	// utils
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
	 *
	 * @param view
	 * @return
	 */
	public static int getViewMeasuredHeight(View view) {
		calcViewMeasure(view);
		return view.getMeasuredHeight();
	}

	/**
	 * 获取控件的宽度，如果获取的宽度为0，则重新计算尺寸后再返回宽度
	 *
	 * @param view
	 * @return
	 */
	public static int getViewMeasuredWidth(View view) {
		calcViewMeasure(view);
		return view.getMeasuredWidth();
	}

	/**
	 * 测量控件的尺*
	 *
	 * @param view
	 */
	public static void calcViewMeasure(View view) {

		int width = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
		int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		view.measure(width, expandSpec);
	}

    public void setVisible(boolean isEnable) {
        if (isEnable) {
            if(!isVisible()) {
                setVisibility(VISIBLE);
            }
        } else {
            if(isVisible())
                setVisibility(GONE);
        }
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE ? true : false;
    }

}
