package com.androidth.general.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class KChartBase extends View {

	/** default background color */
	public static final int DEFAULT_BACKGROUND = Color.WHITE;

	/** default x/y axis color */
	private static final int DEFAULT_AXIS_COLOR = Color.LTGRAY;

	/** 默认经纬线颜色 */
	private static final int DEFAULT_LONGI_LAITUDE_COLOR = Color.LTGRAY;

	/** 默认边框的颜色 */
	public static final int DEFAULT_BORDER_COLOR = Color.LTGRAY;

	/** default x/y axis font size **/
	public static final int DEFAULT_AXIS_TITLE_SIZE = 40;

	/** 默认上表纬线数 */
	public static final int DEFAULT_UPER_LATITUDE_NUM = 6;

	/** 默认经线数 */
	public static final int DEFAULT_LOGITUDE_NUM = 1;

	/** 默认虚线效果 */
	private static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(
            new float[] { 3, 3, 3, 3 }, 1);

	/** 上表的底部 */
	public static float UPER_CHART_BOTTOM;

    protected static final int DEFAULT_ONE_LINE_WIDTH = 1;
    protected static final int DEFAULT_TWO_LINE_WIDTH = 2;
    protected static final int DEFAULT_X_AXIS_TITLE_HEIGHT = DEFAULT_AXIS_TITLE_SIZE + DEFAULT_TWO_LINE_WIDTH;

	/** 上表高度 */
	private float mUperChartHeight;

	/** top Title's height */
	private float topTitleHeight;

	/** show top Titles */
	private boolean showTopTitles;

	private float longitudeSpacing;
	private float latitudeSpacing;

    /** Candle right margin */
    public final static int DEFAULT_CANDLE_LEFT_MARGIN = 50;
    private final static int DEFAULT_CANDLE_RIGHT_MARGIN = 200;

	public KChartBase(Context context) {
		super(context);
		init();
	}

	public KChartBase(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public KChartBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		showTopTitles = false;
		topTitleHeight = 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setBackgroundColor(DEFAULT_BACKGROUND);
		int viewHeight = getHeight();
		int viewWidth = getCandleWidth();
		if (showTopTitles) {
			topTitleHeight = DEFAULT_AXIS_TITLE_SIZE + DEFAULT_TWO_LINE_WIDTH;
		} else {
			topTitleHeight = 0;
		}

		longitudeSpacing = (viewWidth - DEFAULT_TWO_LINE_WIDTH - DEFAULT_CANDLE_LEFT_MARGIN) / (DEFAULT_LOGITUDE_NUM);

		latitudeSpacing = (viewHeight - DEFAULT_TWO_LINE_WIDTH - topTitleHeight - DEFAULT_X_AXIS_TITLE_HEIGHT)
				/ (DEFAULT_UPER_LATITUDE_NUM);
		mUperChartHeight = latitudeSpacing * (DEFAULT_UPER_LATITUDE_NUM);
		UPER_CHART_BOTTOM = DEFAULT_ONE_LINE_WIDTH + topTitleHeight + latitudeSpacing * (DEFAULT_UPER_LATITUDE_NUM);

		// 绘制边框
		drawBorders(canvas, viewHeight, viewWidth);

		// 绘制经线
		drawLongitudes(canvas, viewHeight, longitudeSpacing);

		// 绘制纬线
		drawLatitudes(canvas, viewHeight, viewWidth, latitudeSpacing);

		// 绘制X线及LowerChartTitles
		drawRegions(canvas, viewHeight, viewWidth);
	}

    public int getCandleWidth()
    {
        return getWidth() - DEFAULT_CANDLE_RIGHT_MARGIN;
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Rect rect = new Rect();
		getGlobalVisibleRect(rect);
		return false;
	}

	/**
	 * 绘制边框
	 * 
	 * @param canvas
	 */
	private void drawBorders(Canvas canvas, int viewHeight, int viewWidth) {
		Paint paint = new Paint();
		paint.setColor(DEFAULT_BORDER_COLOR);
		paint.setStrokeWidth(DEFAULT_TWO_LINE_WIDTH);
		canvas.drawLine(1 + DEFAULT_CANDLE_LEFT_MARGIN, 1, viewWidth - 1, 1, paint);
		canvas.drawLine(1 + DEFAULT_CANDLE_LEFT_MARGIN, 1, 1 + DEFAULT_CANDLE_LEFT_MARGIN, viewHeight - 1, paint);
		canvas.drawLine(viewWidth - 1, viewHeight - 1, viewWidth - 1, 1, paint);
		canvas.drawLine(viewWidth - 1, viewHeight - 1, 1 + DEFAULT_CANDLE_LEFT_MARGIN, viewHeight - 1, paint);
	}

	/**
	 * 绘制经线
	 * 
	 * @param canvas
	 * @param viewHeight
	 * @param longitudeSpacing
	 */
	private void drawLongitudes(Canvas canvas, int viewHeight, float longitudeSpacing) {
		Paint paint = new Paint();
		paint.setColor(DEFAULT_LONGI_LAITUDE_COLOR);
		paint.setPathEffect(DEFAULT_DASH_EFFECT);
		for (int i = 1; i <= DEFAULT_LOGITUDE_NUM; i++) {
			canvas.drawRect(DEFAULT_CANDLE_LEFT_MARGIN + DEFAULT_ONE_LINE_WIDTH + longitudeSpacing * i, topTitleHeight + DEFAULT_TWO_LINE_WIDTH, DEFAULT_CANDLE_LEFT_MARGIN+DEFAULT_ONE_LINE_WIDTH + longitudeSpacing * i + 2.5f,
                    UPER_CHART_BOTTOM, paint);
		}
	}

	/**
	 * 绘制纬线
	 * 
	 * @param canvas
	 * @param viewHeight
	 * @param viewWidth
	 */
	private void drawLatitudes(Canvas canvas, int viewHeight, int viewWidth, float latitudeSpacing) {
		Paint paint = new Paint();
		paint.setColor(DEFAULT_LONGI_LAITUDE_COLOR);
		paint.setPathEffect(DEFAULT_DASH_EFFECT);
		for (int i = 1; i <= DEFAULT_UPER_LATITUDE_NUM; i++) {
			canvas.drawRect(DEFAULT_CANDLE_LEFT_MARGIN + DEFAULT_ONE_LINE_WIDTH, topTitleHeight + DEFAULT_ONE_LINE_WIDTH + latitudeSpacing * i, viewWidth - 1,
                    topTitleHeight + DEFAULT_ONE_LINE_WIDTH + latitudeSpacing * i + 2.5f, paint);
		}
	}

	private void drawRegions(Canvas canvas, int viewHeight, int viewWidth) {
		Paint paint = new Paint();
		paint.setColor(DEFAULT_AXIS_COLOR);
		paint.setAlpha(150);
		if (showTopTitles) {
			canvas.drawLine(DEFAULT_CANDLE_LEFT_MARGIN + DEFAULT_ONE_LINE_WIDTH, DEFAULT_ONE_LINE_WIDTH + DEFAULT_AXIS_TITLE_SIZE + DEFAULT_TWO_LINE_WIDTH, viewWidth - DEFAULT_ONE_LINE_WIDTH,
                    DEFAULT_ONE_LINE_WIDTH + DEFAULT_AXIS_TITLE_SIZE + DEFAULT_TWO_LINE_WIDTH, paint);
		}
		canvas.drawLine(DEFAULT_CANDLE_LEFT_MARGIN + DEFAULT_ONE_LINE_WIDTH, UPER_CHART_BOTTOM, viewWidth - DEFAULT_ONE_LINE_WIDTH, UPER_CHART_BOTTOM, paint);
	}

	public float getUperChartHeight() {
		return mUperChartHeight;
	}

	public float getLatitudeSpacing() {
		return latitudeSpacing;
	}

}
