/*******************************************************************************
 * Copyright (c) 2012 Evelina Vrabie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package com.tradehero.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.tradehero.th.R;
import java.util.Arrays;

public class GaugeView extends View {

    private static final String TAG = "GaugeView";
    private boolean DEBUG = false;

    public static final int SIZE = 300;
    public static final float TOP = 0.0f;
    public static final float LEFT = 0.0f;
    public static final float RIGHT = 1.0f;
    public static final float BOTTOM = 1.0f;
    public static final float CENTER = 0.5f;
    public static final boolean SHOW_OUTER_SHADOW = true;
    public static final boolean SHOW_OUTER_BORDER = true;
    public static final boolean SHOW_OUTER_RIM = true;
    public static final boolean SHOW_INNER_RIM = true;
    public static final boolean SHOW_NEEDLE = true;
    public static final boolean SHOW_SCALE = false;
    public static final boolean SHOW_RANGES = true;
    public static final boolean SHOW_TEXT = false;

    public static final float OUTER_SHADOW_WIDTH = 0.03f;
    public static final float OUTER_BORDER_WIDTH = 0.04f;
    public static final float OUTER_RIM_WIDTH = 0.05f;
    public static final float INNER_RIM_WIDTH = 0.06f;
    public static final float INNER_RIM_BORDER_WIDTH = 0.005f;

    //public static final float NEEDLE_WIDTH = 0.035f;
    //public static final float NEEDLE_HEIGHT = 0.28f;

    public static final float NEEDLE_WIDTH = 0.038f;
    public static final float NEEDLE_HEIGHT = 0.3f;
    //public static final float NEEDLE_WIDTH = 0.035f;
    //public static final float NEEDLE_HEIGHT = 0.28f;

    public static final float SCALE_POSITION = 0.025f;
    public static final float SCALE_START_VALUE = 0.0f;
    public static final float SCALE_END_VALUE = 100.0f;
    //
    //public static final float SCALE_START_ANGLE = 30.0f;
    public static final float SCALE_START_ANGLE = 90.0f;
    public static final int SCALE_DIVISIONS = 10;
    public static final int SCALE_SUBDIVISIONS = 5;

    public static final int[] OUTER_SHADOW_COLORS = {Color.argb(40, 255, 254, 187), Color.argb(20, 255, 247, 219),
            Color.argb(5, 255, 255, 255)};
    public static final float[] OUTER_SHADOW_POS = {0.90f, 0.95f, 0.99f};

    public static final float[] RANGE_VALUES = {16.0f, 25.0f, 40.0f, 100.0f};
    public static final int[] RANGE_COLORS = {Color.rgb(231, 32, 43), Color.rgb(232, 111, 33), Color.rgb(232, 231, 33),
            Color.rgb(27, 202, 33)};

    public static final int TEXT_SHADOW_COLOR = Color.argb(100, 0, 0, 0);
    public static final int TEXT_VALUE_COLOR = Color.BLACK;
    public static final int TEXT_UNIT_COLOR = Color.BLACK;
    public static final float TEXT_VALUE_SIZE = 0.3f * 0.65f;
    public static final float TEXT_UNIT_SIZE = 0.1f * 0.65f;

    public static final float PADDING = 0.13f;

    public static final float PIVOT_X = 0.5f;
    public static final float PIVOT_Y = 1 - PADDING;

    // *--------------------------------------------------------------------- *//
    // Customizable properties
    // *--------------------------------------------------------------------- *//

    private boolean mShowOuterShadow;
    private boolean mShowOuterBorder;
    private boolean mShowOuterRim;
    private boolean mShowInnerRim;
    private boolean mShowScale;
    private boolean mShowRanges;
    private boolean mShowNeedle;
    private boolean mShowText;

    private float mOuterShadowWidth;
    private float mOuterBorderWidth;
    private float mOuterRimWidth;
    private float mInnerRimWidth;
    private float mInnerRimBorderWidth;
    private float mNeedleWidth;
    private float mNeedleHeight;

    private float mScalePosition;
    private float mScaleStartValue;
    private float mScaleHalfValue;
    private float mDrawScaleStartValue;
    private float mScaleEndValue;
    private float mScaleStartAngle;
    private float[] mRangeValues;

    private int[] mRangeColors;
    private int mDivisions;
    private int mSubdivisions;

    private RectF mOuterShadowRect;
    private RectF mOuterBorderRect;
    private RectF mOuterRimRect;
    private RectF mInnerRimRect;
    private RectF mInnerRimBorderRect;
    private RectF mFaceRect;
    private RectF mScaleRect;

    private Bitmap mBackground;
    private Paint[] mRangePaints;
    private Paint mNeedleRightPaint;
    private Paint mNeedleLeftPaint;
    private Paint mNeedleScrewPaint;
    private Paint mNeedleScrewBorderPaint;
    private Paint mTextValuePaint;
    private Paint mSubTextValuePaint;
    private Paint mTextUnitPaint;

    /** All these variables not necessary*/
    private Paint mBackgroundPaint;
    private Paint mOuterShadowPaint;
    private Paint mOuterBorderPaint;
    private Paint mOuterRimPaint;
    private Paint mInnerRimPaint;
    private Paint mInnerRimBorderLightPaint;
    private Paint mInnerRimBorderDarkPaint;
    private Paint mFacePaint;
    private Paint mFaceBorderPaint;
    private Paint mFaceShadowPaint;
    /***/

    private String mTextValue;
    private String mSubTextValue;
    private String mTopTextValue;
    private String mTextUnit;
    private int mTextValueColor;
    private int mTextUnitColor;
    private int mTextShadowColor;
    private float mTextValueSize;
    private float mTextUnitSize;

    private Path mNeedleRightPath;
    private Path mNeedleLeftPath;

    // *--------------------------------------------------------------------- *//

    private float mScaleRotation;
    /**每一个区间的值的大小*/
    private float mDivisionValue;
    /**每一个子区间的值的大小*/
    private float mSubdivisionValue;
    /**每一个子区间的角度*/
    private float mSubdivisionAngle;

    private float mTargetValue;
    private float mCurrentValue;

    private float mNeedleVelocity;
    private float mNeedleAcceleration;
    private long mNeedleLastMoved = -1;
    private boolean mNeedleInitialized;
    private static final int ANIMATION_DURATION = 4000;
    private long mAnimaDuration = ANIMATION_DURATION;

    private boolean mDrawWithAnimaion = true;

    private boolean mDrawCenterIndicator = false;

    public GaugeView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        readAttrs(context, attrs, defStyle);
        init();
    }

    public GaugeView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GaugeView(final Context context) {
        this(context, null, 0);
    }

    private void readAttrs(final Context context, final AttributeSet attrs, final int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GaugeView, defStyle, 0);
        mShowOuterShadow = a.getBoolean(R.styleable.GaugeView_showOuterShadow, SHOW_OUTER_SHADOW);
        mShowOuterBorder = a.getBoolean(R.styleable.GaugeView_showOuterBorder, SHOW_OUTER_BORDER);
        mShowOuterRim = a.getBoolean(R.styleable.GaugeView_showOuterRim, SHOW_OUTER_RIM);
        mShowInnerRim = a.getBoolean(R.styleable.GaugeView_showInnerRim, SHOW_INNER_RIM);
        mShowNeedle = a.getBoolean(R.styleable.GaugeView_showNeedle, SHOW_NEEDLE);
        mShowScale = a.getBoolean(R.styleable.GaugeView_showScale, SHOW_SCALE);
        mShowRanges = a.getBoolean(R.styleable.GaugeView_showRanges, SHOW_RANGES);
        mShowText = a.getBoolean(R.styleable.GaugeView_showText, SHOW_TEXT);

        mOuterShadowWidth = mShowOuterShadow ? a.getFloat(R.styleable.GaugeView_outerShadowWidth, OUTER_SHADOW_WIDTH) : 0.0f;
        mOuterBorderWidth = mShowOuterBorder ? a.getFloat(R.styleable.GaugeView_outerBorderWidth, OUTER_BORDER_WIDTH) : 0.0f;
        mOuterRimWidth = mShowOuterRim ? a.getFloat(R.styleable.GaugeView_outerRimWidth, OUTER_RIM_WIDTH) : 0.0f;
        mInnerRimWidth = mShowInnerRim ? a.getFloat(R.styleable.GaugeView_innerRimWidth, INNER_RIM_WIDTH) : 0.0f;
        mInnerRimBorderWidth = mShowInnerRim ? a.getFloat(R.styleable.GaugeView_innerRimBorderWidth, INNER_RIM_BORDER_WIDTH) : 0.0f;

        mNeedleWidth = a.getFloat(R.styleable.GaugeView_needleWidth, NEEDLE_WIDTH);
        mNeedleHeight = a.getFloat(R.styleable.GaugeView_needleHeight, NEEDLE_HEIGHT);

        mScalePosition = (mShowScale || mShowRanges) ? a.getFloat(R.styleable.GaugeView_scalePosition, SCALE_POSITION) : 0.0f;
        mScaleStartValue = a.getFloat(R.styleable.GaugeView_scaleStartValue, SCALE_START_VALUE);
        mScaleEndValue = a.getFloat(R.styleable.GaugeView_scaleEndValue, SCALE_END_VALUE);
        mScaleHalfValue = (mScaleEndValue - mScaleStartValue) / 2;
        mScaleStartAngle = a.getFloat(R.styleable.GaugeView_scaleStartAngle, SCALE_START_ANGLE);

        mDivisions = a.getInteger(R.styleable.GaugeView_divisions, SCALE_DIVISIONS);
        mSubdivisions = a.getInteger(R.styleable.GaugeView_subdivisions, SCALE_SUBDIVISIONS);

        if (mShowRanges) {
            mTextShadowColor = a.getColor(R.styleable.GaugeView_textShadowColor, TEXT_SHADOW_COLOR);
            final int rangesId = a.getResourceId(R.styleable.GaugeView_rangeValues, 0);
            final int colorsId = a.getResourceId(R.styleable.GaugeView_rangeColors, 0);
            readRanges(context.getResources(), rangesId, colorsId);
        }

        if (mShowText) {
            final int textValueId = a.getResourceId(R.styleable.GaugeView_textValue, 0);
            final String textValue = a.getString(R.styleable.GaugeView_textValue);
            mTextValue = (0 < textValueId) ? context.getString(textValueId) : (null != textValue) ? textValue : "";

            final int textUnitId = a.getResourceId(R.styleable.GaugeView_textUnit, 0);
            final String textUnit = a.getString(R.styleable.GaugeView_textUnit);
            mTextUnit = (0 < textUnitId) ? context.getString(textUnitId) : (null != textUnit) ? textUnit : "";
            mTextValueColor = a.getColor(R.styleable.GaugeView_textValueColor, TEXT_VALUE_COLOR);
            mTextUnitColor = a.getColor(R.styleable.GaugeView_textUnitColor, TEXT_UNIT_COLOR);
            mTextShadowColor = a.getColor(R.styleable.GaugeView_textShadowColor, TEXT_SHADOW_COLOR);

            mTextValueSize = a.getFloat(R.styleable.GaugeView_textValueSize, TEXT_VALUE_SIZE);
            mTextUnitSize = a.getFloat(R.styleable.GaugeView_textUnitSize, TEXT_UNIT_SIZE);
        }

        int animationDuration;
        final int animationDurationId = a.getResourceId(R.styleable.GaugeView_animationDuration, 0);
        if (animationDurationId > 0) {
            animationDuration = context.getResources().getInteger(animationDurationId);
        } else {
            animationDuration = a.getInteger(R.styleable.GaugeView_animationDuration,ANIMATION_DURATION);
        }
        mAnimaDuration = animationDuration;

        mDrawCenterIndicator = a.getBoolean(R.styleable.GaugeView_drawCenterIndicator,false);

        a.recycle();
    }

    private void readRanges(final Resources res, final int rangesId, final int colorsId) {
        if (rangesId > 0 && colorsId > 0) {
            final String[] ranges = res.getStringArray(rangesId);
            final String[] colors = res.getStringArray(colorsId);
            if (ranges.length != colors.length) {
                throw new IllegalArgumentException(
                        "The ranges and colors arrays must have the same length.");
            }

            final int length = ranges.length;
            mRangeValues = new float[length];
            mRangeColors = new int[length];
            for (int i = 0; i < length; i++) {
                mRangeValues[i] = Float.parseFloat(ranges[i]);
                mRangeColors[i] = Color.parseColor(colors[i]);
            }
        } else if (colorsId > 0) {
            //determine value ranges according to color ranges
            final String[] colors = res.getStringArray(colorsId);
            final int length = colors.length;
            mRangeValues = new float[length];
            mRangeColors = new int[length];
            float divisionValue = (mScaleEndValue - mScaleStartValue) / length;
            for (int i = 0; i < length; i++) {
                mRangeValues[i] = divisionValue * (i+1);
                mRangeColors[i] = Color.parseColor(colors[i]);
            }
            if (DEBUG)Log.d(TAG,"colorsId "+length);
        } else {
              mRangeValues = RANGE_VALUES;
              mRangeColors = RANGE_COLORS;
        }
        if (DEBUG) {
            Log.d(TAG,String.format("readRanges range values:%s, range color:%s",Arrays.toString(mRangeValues),Arrays.toString(mRangeColors)));
        }
    }

    @TargetApi(11)
    private void init() {
        // TODO Why isn't this working with HA layer?
        // The needle is not displayed although the onDraw() is being triggered by invalidate()
        // calls.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        initDrawingRects();
        initDrawingTools();

        // Compute the scale properties
        if (mShowRanges) {
            initScale();
        }
    }

    public void initDrawingRects() {
        // The drawing area is a rectangle of width 1 and height 1,
        // where (0,0) is the top left corner of the canvas.
        // Note that on Canvas X axis points to right, while the Y axis points downwards.

        mOuterShadowRect = new RectF(LEFT + PADDING * 0.3f, TOP + PADDING, RIGHT - PADDING * 0.3f, BOTTOM - PADDING);

        mOuterBorderRect = new RectF(mOuterShadowRect.left + mOuterShadowWidth, mOuterShadowRect.top + mOuterShadowWidth,
                mOuterShadowRect.right - mOuterShadowWidth, mOuterShadowRect.bottom - mOuterShadowWidth);

        mOuterRimRect = new RectF(mOuterBorderRect.left + mOuterBorderWidth, mOuterBorderRect.top + mOuterBorderWidth,
                mOuterBorderRect.right - mOuterBorderWidth, mOuterBorderRect.bottom - mOuterBorderWidth);

        mInnerRimRect = new RectF(mOuterRimRect.left + mOuterRimWidth, mOuterRimRect.top + mOuterRimWidth, mOuterRimRect.right
                - mOuterRimWidth, mOuterRimRect.bottom - mOuterRimWidth);

        mInnerRimBorderRect = new RectF(mInnerRimRect.left + mInnerRimBorderWidth, mInnerRimRect.top + mInnerRimBorderWidth,
                mInnerRimRect.right - mInnerRimBorderWidth, mInnerRimRect.bottom - mInnerRimBorderWidth);

        mFaceRect = new RectF(mInnerRimRect.left + mInnerRimWidth, mInnerRimRect.top + mInnerRimWidth,
                mInnerRimRect.right - mInnerRimWidth, mInnerRimRect.bottom - mInnerRimWidth);

        mScaleRect = new RectF(mFaceRect.left + mScalePosition, mFaceRect.top + mScalePosition, mFaceRect.right - mScalePosition,
                mFaceRect.bottom - mScalePosition);

        mScaleRect = new RectF(mOuterShadowRect);
        mScaleRect.top = mScaleRect.top + 0.02f;
    }

    private void initDrawingTools() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setFilterBitmap(true);

        if (mShowOuterShadow) {
            mOuterShadowPaint = getDefaultOuterShadowPaint();
        }
        if (mShowOuterBorder) {
            mOuterBorderPaint = getDefaultOuterBorderPaint();
        }
        if (mShowOuterRim) {
            mOuterRimPaint = getDefaultOuterRimPaint();
        }
        if (mShowInnerRim) {
            mInnerRimPaint = getDefaultInnerRimPaint();
            mInnerRimBorderLightPaint = getDefaultInnerRimBorderLightPaint();
            mInnerRimBorderDarkPaint = getDefaultInnerRimBorderDarkPaint();
        }
        if (mShowRanges) {
            setDefaultScaleRangePaints();
        }
        if (mShowNeedle) {
            setDefaultNeedlePaths();
            mNeedleLeftPaint = getDefaultNeedleLeftPaint();
            mNeedleRightPaint = getDefaultNeedleRightPaint();
            mNeedleScrewPaint = getDefaultNeedleScrewPaint();
            mNeedleScrewBorderPaint = getDefaultNeedleScrewBorderPaint();
        }
        if (mShowText) {
            mTextValuePaint = getDefaultTextValuePaint();
            mTextUnitPaint = getDefaultTextUnitPaint();

            float textSize = mTextValuePaint.getTextSize() * 0.65f;
            mSubTextValuePaint = new Paint(mTextValuePaint);
            mSubTextValuePaint.setTextSize(textSize);
        }

        mFacePaint = getDefaultFacePaint();
        mFaceBorderPaint = getDefaultFaceBorderPaint();
        mFaceShadowPaint = getDefaultFaceShadowPaint();
    }

    public Paint getDefaultOuterShadowPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(CENTER, CENTER, mOuterShadowRect.width() / 2.0f, OUTER_SHADOW_COLORS, OUTER_SHADOW_POS,
                TileMode.MIRROR));
        return paint;
    }

    private Paint getDefaultOuterBorderPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(245, 0, 0, 0));
        return paint;
    }

    public Paint getDefaultOuterRimPaint() {
        // Use a linear gradient to create the 3D effect
        final LinearGradient verticalGradient = new LinearGradient(mOuterRimRect.left, mOuterRimRect.top, mOuterRimRect.left,
                mOuterRimRect.bottom, Color.rgb(255, 255, 255), Color.rgb(84, 90, 100), TileMode.REPEAT);

        // Use a Bitmap shader for the metallic style
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.light_alu);
        final BitmapShader aluminiumTile = new BitmapShader(bitmap, TileMode.REPEAT, TileMode.REPEAT);
        final Matrix matrix = new Matrix();
        matrix.setScale(1.0f / bitmap.getWidth(), 1.0f / bitmap.getHeight());
        aluminiumTile.setLocalMatrix(matrix);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new ComposeShader(verticalGradient, aluminiumTile, PorterDuff.Mode.MULTIPLY));
        paint.setFilterBitmap(true);
        return paint;
    }

    private Paint getDefaultInnerRimPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(mInnerRimRect.left, mInnerRimRect.top, mInnerRimRect.left, mInnerRimRect.bottom, new int[]{
                Color.argb(255, 68, 73, 80), Color.argb(255, 91, 97, 105), Color.argb(255, 178, 180, 183), Color.argb(255, 188, 188, 190),
                Color.argb(255, 84, 90, 100), Color.argb(255, 137, 137, 137)}, new float[]{0, 0.1f, 0.2f, 0.4f, 0.8f, 1},
                TileMode.CLAMP));
        return paint;
    }

    private Paint getDefaultInnerRimBorderLightPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(100, 255, 255, 255));
        paint.setStrokeWidth(0.005f);
        return paint;
    }

    private Paint getDefaultInnerRimBorderDarkPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(100, 81, 84, 89));
        paint.setStrokeWidth(0.005f);
        return paint;
    }

    public Paint getDefaultFacePaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new RadialGradient(0.5f, 0.5f, mFaceRect.width() / 2, new int[]{Color.rgb(50, 132, 206), Color.rgb(36, 89, 162),
                Color.rgb(27, 59, 131)}, new float[]{0.5f, 0.96f, 0.99f}, TileMode.MIRROR));
        return paint;
    }

    public Paint getDefaultFaceBorderPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(100, 81, 84, 89));
        paint.setStrokeWidth(0.005f);
        return paint;
    }

    public Paint getDefaultFaceShadowPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new RadialGradient(0.5f, 0.5f, mFaceRect.width() / 2.0f, new int[]{Color.argb(60, 40, 96, 170),
                Color.argb(80, 15, 34, 98), Color.argb(120, 0, 0, 0), Color.argb(140, 0, 0, 0)},
                new float[]{0.60f, 0.85f, 0.96f, 0.99f}, TileMode.MIRROR));
        return paint;
    }

    public void setDefaultNeedlePaths() {
        final float x = PIVOT_X, y = PIVOT_Y;
        mNeedleLeftPath = new Path();
        mNeedleLeftPath.moveTo(x, y);
        mNeedleLeftPath.lineTo(x - mNeedleWidth, y);
        mNeedleLeftPath.lineTo(x, y - mNeedleHeight);
        mNeedleLeftPath.lineTo(x, y);
        mNeedleLeftPath.lineTo(x - mNeedleWidth, y);

        mNeedleRightPath = new Path();
        mNeedleRightPath.moveTo(x, y);
        mNeedleRightPath.lineTo(x + mNeedleWidth, y);
        mNeedleRightPath.lineTo(x, y - mNeedleHeight);
        mNeedleRightPath.lineTo(x, y);
        mNeedleRightPath.lineTo(x + mNeedleWidth, y);
    }

    public Paint getDefaultNeedleLeftPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(176, 10, 19));
        return paint;
    }

    public Paint getDefaultNeedleRightPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(252, 18, 30));
        paint.setShadowLayer(0.01f, 0.005f, -0.005f, Color.argb(127, 0, 0, 0));
        return paint;
    }

    public Paint getDefaultNeedleScrewPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new RadialGradient(0.5f, 0.5f, 0.07f, new int[]{Color.rgb(171, 171, 171), Color.WHITE}, new float[]{0.05f,
                0.9f}, TileMode.MIRROR));
        return paint;
    }

    public Paint getDefaultNeedleScrewBorderPaint() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(100, 81, 84, 89));
        paint.setStrokeWidth(0.005f);
        return paint;
    }

    public void setDefaultRanges() {
        mRangeValues = new float[]{16, 25, 40, 100};
        mRangeColors = new int[]{Color.rgb(231, 32, 43), Color.rgb(232, 111, 33), Color.rgb(232, 231, 33), Color.rgb(27, 202, 33)};
    }

    public void setDefaultScaleRangePaints() {
        final int length = mRangeValues.length;
        mRangePaints = new Paint[length];
        for (int i = 0; i < length; i++) {
            mRangePaints[i] = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            mRangePaints[i].setColor(mRangeColors[i]);
            mRangePaints[i].setStyle(Paint.Style.STROKE);
            mRangePaints[i].setStrokeWidth(0.016f);
            mRangePaints[i].setTextSize(0.05f);
            mRangePaints[i].setTypeface(Typeface.SANS_SERIF);
            mRangePaints[i].setTextAlign(Align.CENTER);
            mRangePaints[i].setShadowLayer(0.005f, 0.002f, 0.002f, mTextShadowColor);
        }
    }

    public Paint getDefaultTextValuePaint() {
        final Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mTextValueColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //paint.setStrokeWidth(0.01f);
        paint.setTextSize(mTextValueSize);
        paint.setTextAlign(Align.CENTER);
        //paint.setTypeface(Typeface.SANS_SERIF);
        //paint.setShadowLayer(0.01f, 0.002f, 0.002f, mTextShadowColor);
        return paint;
    }

    public Paint getDefaultTextUnitPaint() {
        final Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mTextUnitColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(0.005f);
        paint.setTextSize(mTextUnitSize);
        paint.setTextAlign(Align.CENTER);
        paint.setShadowLayer(0.01f, 0.002f, 0.002f, mTextShadowColor);
        return paint;
    }

    /**
     *
     * @param text
     */
    public void setContentText(String text) {
        mTextValue = text;
    }

    /**
     *
     * @param text
     */
    public void setTopText(String text) {
        mTopTextValue = text;
    }

    /**
     *
     * @param text
     */
    public void setSubText(String text) {
        mSubTextValue = text;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        final Bundle bundle = (Bundle) state;
        final Parcelable superState = bundle.getParcelable("superState");
        super.onRestoreInstanceState(superState);

        mNeedleInitialized = bundle.getBoolean("needleInitialized");
        mNeedleVelocity = bundle.getFloat("needleVelocity");
        mNeedleAcceleration = bundle.getFloat("needleAcceleration");
        mNeedleLastMoved = bundle.getLong("needleLastMoved");
        mCurrentValue = bundle.getFloat("currentValue");
        mTargetValue = bundle.getFloat("targetValue");
    }

    private void initScale() {
        mScaleRotation = (mScaleStartAngle + 180) % 360;
        mDivisionValue = (mScaleEndValue - mScaleStartValue) / mDivisions;
        mSubdivisionValue = mDivisionValue / mSubdivisions;
        mSubdivisionAngle = (360 - 2 * mScaleStartAngle) / (mDivisions * mSubdivisions);

        if (DEBUG) {
            Log.d(TAG,String.format("initScale mScaleRotation:%s, mDivisionValue:%s, mSubdivisionValue:%s, mSubdivisionAngle:%s",mScaleRotation, mDivisionValue, mSubdivisionValue, mSubdivisionAngle));
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();

        final Bundle state = new Bundle();
        state.putParcelable("superState", superState);
        state.putBoolean("needleInitialized", mNeedleInitialized);
        state.putFloat("needleVelocity", mNeedleVelocity);
        state.putFloat("needleAcceleration", mNeedleAcceleration);
        state.putLong("needleLastMoved", mNeedleLastMoved);
        state.putFloat("currentValue", mCurrentValue);
        state.putFloat("targetValue", mTargetValue);
        return state;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // Loggable.log.debug(String.format("widthMeasureSpec=%s, heightMeasureSpec=%s",
        // View.MeasureSpec.toString(widthMeasureSpec),
        // View.MeasureSpec.toString(heightMeasureSpec)));

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int chosenWidth = chooseDimension(widthMode, widthSize);
        final int chosenHeight = chooseDimension(heightMode, heightSize);
        setMeasuredDimension(chosenWidth, chosenHeight);
    }

    private int chooseDimension(final int mode, final int size) {
        switch (mode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.UNSPECIFIED:
            default:
                return getDefaultDimension();
        }
    }

    private int getDefaultDimension() {
        return SIZE;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        drawGauge();
    }

    private void drawGauge() {
        if (null != mBackground) {
            // Let go of the old background
            mBackground.recycle();
        }
        // Create a new background according to the new width and height
        mBackground = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(mBackground);
        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);
        if (DEBUG) {
            Log.d(TAG,"drawScale scale " +scale+" "+getHeight() + " "+getWidth());
        }
        //drawRim(canvas);
        //drawFace(canvas);

        if (mShowRanges) {
            drawScale(canvas);
        }
    }

    long drawTime = 0;
    long totalTime = 0;
    @Override
    protected void onDraw(final Canvas canvas) {
        if (DEBUG) {
            drawTime = System.currentTimeMillis();
            Log.d(TAG,"onDraw");
        }

        drawBackground(canvas);

        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);

        if (mShowNeedle) {
            drawNeedle(canvas);
        }

        if (mShowText) {
            drawText(canvas);
        }

        //computeCurrentValue();
        if (mDrawWithAnimaion)
        {
            computeCurrentValue2();
        }
        if (DEBUG) {
            totalTime = (System.currentTimeMillis() - drawTime) + totalTime;
            Log.d(TAG,"onDraw drawTime " + (System.currentTimeMillis() - drawTime) +" totalTime "+totalTime);
        }
    }

    private void drawBackground(final Canvas canvas) {
        if (null != mBackground) {
            canvas.drawBitmap(mBackground, 0, 0, mBackgroundPaint);
        }
    }

    private void drawRim(final Canvas canvas) {
        if (mShowOuterShadow) {
            canvas.drawOval(mOuterShadowRect, mOuterShadowPaint);
        }
        if (mShowOuterBorder) {
            canvas.drawOval(mOuterBorderRect, mOuterBorderPaint);
        }
        if (mShowOuterRim) {
            canvas.drawOval(mOuterRimRect, mOuterRimPaint);
        }
        if (mShowInnerRim) {
            canvas.drawOval(mInnerRimRect, mInnerRimPaint);
            canvas.drawOval(mInnerRimRect, mInnerRimBorderLightPaint);
            canvas.drawOval(mInnerRimBorderRect, mInnerRimBorderDarkPaint);
        }
    }

    private void drawFace(final Canvas canvas) {
        // Draw the face gradient
        canvas.drawOval(mFaceRect, mFacePaint);
        // Draw the face border
        canvas.drawOval(mFaceRect, mFaceBorderPaint);
        // Draw the inner face shadow
        canvas.drawOval(mFaceRect, mFaceShadowPaint);
    }

    private void drawText(final Canvas canvas) {
        final String textValue = !TextUtils.isEmpty(mTextValue) ? mTextValue : valueString(mCurrentValue);
        final float textValueWidth = mTextValuePaint.measureText(textValue);
        final float textUnitWidth = !TextUtils.isEmpty(mTextUnit) ? mTextUnitPaint.measureText(mTextUnit) : 0;

        final float startX = CENTER - textUnitWidth / 2;
        //final float startY = CENTER - 0.03f;
        final float startY = PIVOT_Y - 0.07f;
        //final float startY = CENTER + 0.1f;

        canvas.drawText(textValue, startX, startY, mTextValuePaint);
        //mTopTextValue = "S&P 500";
        //mSubTextValue = "Relative Performance";
        if (!TextUtils.isEmpty(mTopTextValue)) {
            final float w = 0;
            canvas.drawText(mTopTextValue, (CENTER - w / 2), PADDING - 0.01f , mSubTextValuePaint);
        }
        if (!TextUtils.isEmpty(mSubTextValue)) {
            final float w = 0;
            canvas.drawText(mSubTextValue, (CENTER - w / 2), (float)(1-0.01), mSubTextValuePaint);
        }

        if (!TextUtils.isEmpty(mTextUnit)) {
            canvas.drawText(mTextUnit, CENTER + textValueWidth / 2 + 0.03f, CENTER, mTextUnitPaint);
        }
    }

    private void drawScale(final Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        // On canvas, North is 0 degrees, East is 90 degrees, South is 180 etc.
        // We start the scale somewhere South-West so we need to first rotate the canvas.
        canvas.rotate(mScaleRotation, PIVOT_X, PIVOT_Y);

        final int totalTicks = mDivisions * mSubdivisions + 1;
        final int half = totalTicks / 2;
        for (int i = 0; i < totalTicks; i++) {
            final float y1 = mScaleRect.top;
            final float y2 = y1 + 0.080f;   // height of subdivision
            final float y3 = y1 + 0.15f;    // height of division

            final float value = getValueForTick(i);
            final Paint paint = getRangePaint(value);
            if (DEBUG) {
                Log.d(TAG,String.format("tick:%d",i,paint.getColor()));
            }
            float div = mScaleEndValue / (float) mDivisions;
            float mod = value % div;
            if ((Math.abs(mod - 0) < 0.001) || (Math.abs(mod - div) < 0.001)) {
                // Draw a division tick
                canvas.drawLine(PIVOT_X, y1, PIVOT_X, y3, paint);
                // Draw the text 0.15 away from the division tick
                //canvas.drawText(valueString(value), 0.5f, y3 + 0.045f, paint);
            } else {
                // Draw a subdivision tick
                canvas.drawLine(PIVOT_X, y1, PIVOT_X, y2, paint);
            }
            if (mDrawCenterIndicator) {
                if (half == i) {
                    Paint p = new Paint(paint);
                    p.setColor(Color.BLACK);
                    canvas.drawLine(PIVOT_X, (float)(y1-0.04), PIVOT_X, (float)(y3+0.07), p);
                }
            }

            canvas.rotate(mSubdivisionAngle, PIVOT_X, PIVOT_Y);
        }
        canvas.restore();
        if (DEBUG) {
            canvas.drawLine(0, mScaleRect.top, mScaleRect.right, mScaleRect.top, new Paint());
            canvas.drawLine(0,mScaleRect.bottom,mScaleRect.right,mScaleRect.bottom,new Paint());
            Log.d(TAG, String.format("ScaleRect %s, outerShadowRect %s", mScaleRect.toString(),
                    mOuterShadowRect.toString()));
        }
    }

    private String valueString(final float value) {
        return String.format("%d", (int) value);
    }

    private float getValueForTick(final int tick) {
        return tick * (mDivisionValue / mSubdivisions);
    }

    private Paint getRangePaint(final float value) {
        final int length = mRangeValues.length;
        for (int i = 0; i < length - 1; i++) {
            if (value < mRangeValues[i]) return mRangePaints[i];
        }
        if (value <= mRangeValues[length - 1]) return mRangePaints[length - 1];
        throw new IllegalArgumentException("Value " + value + " out of range!");
    }

    private void drawNeedle(final Canvas canvas) {
        if (mNeedleInitialized) {
            final float angle = getAngleForValue(mCurrentValue);
            // Logger.log.info(String.format("value=%f -> angle=%f", mCurrentValue, angle));

            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(angle, PIVOT_X, PIVOT_Y);
            float tY = -0.7f + PADDING * 2;
            canvas.translate(0,tY);
            setNeedleShadowPosition(angle);
            canvas.drawPath(mNeedleLeftPath, mNeedleLeftPaint);
            canvas.drawPath(mNeedleRightPath, mNeedleRightPaint);

            canvas.restore();

            // Draw the needle screw and its border
            //canvas.drawCircle(0.5f, 0.5f, 0.04f, mNeedleScrewPaint);
            //canvas.drawCircle(0.5f, 0.5f, 0.04f, mNeedleScrewBorderPaint);
        }
    }

    private void setNeedleShadowPosition(final float angle) {
        if (angle > 180 && angle < 360) {
            // Move shadow from right to left
            mNeedleRightPaint.setShadowLayer(0, 0, 0, Color.BLACK);
            mNeedleLeftPaint.setShadowLayer(0.01f, -0.005f, 0.005f, Color.argb(127, 0, 0, 0));
        } else {
            // Move shadow from left to right
            mNeedleLeftPaint.setShadowLayer(0, 0, 0, Color.BLACK);
            mNeedleRightPaint.setShadowLayer(0.01f, 0.005f, -0.005f, Color.argb(127, 0, 0, 0));
        }
    }

    private float getAngleForValue(final float value) {
        return (mScaleRotation + (value / mSubdivisionValue) * mSubdivisionAngle) % 360;
    }

    float allTime = 0;
    private boolean animationEnded = false;

    /**
     * repeat drawing
     */
    private void computeCurrentValue2() {
        if (mTargetValue <= mScaleStartValue) {
            return;
        }
        if (animationEnded) {
            animationEnded = false;
            return;
        }
        if (-1 != mNeedleLastMoved){
            final float time = (System.currentTimeMillis() - mNeedleLastMoved);
            mCurrentValue = (float)ani(time,mDrawScaleStartValue,(mTargetValue - mDrawScaleStartValue),mAnimaDuration);
            if (DEBUG) {
                Log.d(TAG,"mCurrentValue "+mCurrentValue +" mTargetValue " +mTargetValue+" time "+time);
            }

            if (Math.abs(mTargetValue - mCurrentValue)  < 0.01f ) {
                //mCurrentValue = mTargetValue;
                //mNeedleLastMoved = -1L;
                totalTime = 0;
                if (DEBUG)Log.e(TAG, "Red mCurrentValue "
                        + mCurrentValue
                        + " mTargetValue "
                        + mTargetValue+" "
                        + time +" --> "+Math.abs(((double)mTargetValue) - ((double)mCurrentValue)));
            } else {
                //mNeedleLastMoved = System.currentTimeMillis();

            }
            if (time >= mAnimaDuration) {
                mCurrentValue = mTargetValue;
                mNeedleLastMoved = -1L;
                animationEnded = true;
                invalidate();
                if (DEBUG)Log.e(TAG, "Red mCurrentValue "+time);
            } else {
                if (Math.abs(mCurrentValue - mTargetValue) < 0.1) {
                    postInvalidateDelayed(50);
                } else {
                    postInvalidateDelayed(30);
                }


           }
        } else {
            mNeedleLastMoved = System.currentTimeMillis();
            computeCurrentValue2();
        }
    }

    /**
     * repeat drawing
     */
    private void computeCurrentValue() {
        // Logger.log.warn(String.format("velocity=%f, acceleration=%f", mNeedleVelocity,
        // mNeedleAcceleration));

        if (!(Math.abs(mCurrentValue - mTargetValue) > 0.01f)) {
            return;
        }

        if (-1 != mNeedleLastMoved) {
            final float time = (System.currentTimeMillis() - mNeedleLastMoved) / 1000.0f;
            final float direction = Math.signum(mNeedleVelocity);
            if (Math.abs(mNeedleVelocity) < 90.0f) {
                mNeedleAcceleration = 5.0f * (mTargetValue - mCurrentValue);
            } else {
                mNeedleAcceleration = 0.0f;
            }

            mNeedleAcceleration = 5.0f * (mTargetValue - mCurrentValue);
            mCurrentValue += mNeedleVelocity * time;
            mNeedleVelocity += mNeedleAcceleration * time;

            if (DEBUG) {
                allTime += time;
                Log.d(TAG,String.format("mNeedleAcceleration %s, mNeedleVelocity %s, mCurrentValue %s, time %s, allTime %s",mNeedleAcceleration, mNeedleVelocity,mCurrentValue, time, allTime));
            }
            if ((mTargetValue - mCurrentValue) * direction < 0.01f * direction) {
                mCurrentValue = mTargetValue;
                mNeedleVelocity = 0.0f;
                mNeedleAcceleration = 0.0f;
                mNeedleLastMoved = -1L;
                allTime = 0;
            } else {
                mNeedleLastMoved = System.currentTimeMillis();
            }

            invalidate();

        } else {
            mNeedleLastMoved = System.currentTimeMillis();
            computeCurrentValue();
        }
    }

    /**
     * t: current time, b: begInnIng value, c: change In value, d: duration
     * @param currentTime
     * @param startValue
     * @param changeValue
     * @param duration
     * @return
     */
    private double ani(float currentTime, float startValue, float changeValue, float duration)
    {

        double s = 1.70158;
        double p = 0;
        double a = changeValue;
        if (currentTime == 0) return startValue;
        if ((currentTime /= duration) == 1) return startValue + changeValue;
        /**if (p == 0)*/p = duration * .3;
        if (a < Math.abs(changeValue))
        {
            a = changeValue;
            s = p / 4;
        }
        else
        {
            s = p / (2 * Math.PI) * Math.asin(changeValue / a);
        }
        double tmp = a * Math.pow(2, -10 * currentTime) * Math.sin((currentTime * duration - s) * (2 * Math.PI) / p);
        if (tmp > 0) {
            tmp = tmp * 0.6;
        }
        if (tmp + changeValue + startValue > mScaleEndValue) {
            tmp = tmp * 0.4;
        }
        //if (tmp + changeValue + startValue < mScaleHalfValue && (startValue + changeValue) > mScaleHalfValue)
        //{
        //    tmp = tmp * 1.6;
        //}
        if (DEBUG) {
            Log.d(TAG,"tmp "+tmp +" for "+currentTime);
        }
        return  tmp + changeValue + startValue;
    }

    public void setCurrentValue(float value) {
        if (value < mScaleStartValue) {
            value = mScaleStartValue;
        } else if (value > mScaleEndValue) {
            value = mScaleEndValue;
        }
        mCurrentValue = value;
        mNeedleInitialized = true;
        invalidate();
    }



  public void setTargetValue(final float value) {
        if (mShowScale || mShowRanges) {
            if (value < mScaleStartValue) {
                mTargetValue = mScaleStartValue;
            } else if (value > mScaleEndValue) {
                mTargetValue = mScaleEndValue;
            } else {
                mTargetValue = value;
            }
        } else {
            mTargetValue = value;
        }
        mNeedleInitialized = true;
        invalidate();
    }

    /**
     *
     * @param duration
     */
    public void setAnimationDuration(long duration) {
        if (duration <=0 ){
            throw new IllegalArgumentException("Duration must be positive!");
        }
        this.mAnimaDuration = duration;
    }

    /**
     * Stop animation and set mCurrentValue to mScaleStartValue.
     */
    public void clear() {
        mTargetValue = mScaleStartValue;
        mCurrentValue = mScaleStartValue;
        mDrawWithAnimaion = false;

        mNeedleLastMoved = -1;
        animationEnded = false;
    }

    /**
     * Set the flag that indicates whether draw with animation.
     * @param drawWithAnimation
     */
    public void setAnimiationFlag(boolean drawWithAnimation)
    {
        this.mDrawWithAnimaion = drawWithAnimation;
    }

    /**
     * The default value of mScaleStartValue is 0, and mScaleEndValue is 100
     * @param value
     */
    public void setDrawStartValue(float value)
    {
        if (value > mScaleEndValue) {
            value = mScaleEndValue;
        } else if (value < mScaleStartValue) {
            value = mScaleStartValue;
        }
        this.mDrawScaleStartValue = value;
    }

    /**
     * Just for test.
     * @param debug
     */
    public void setDebug(boolean debug)
    {
        DEBUG = debug;
    }


}

	