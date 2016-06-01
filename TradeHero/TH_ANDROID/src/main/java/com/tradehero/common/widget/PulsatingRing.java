package com.tradehero.common.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.ayondo.academy.R;
import java.util.ArrayList;

public class PulsatingRing extends View
{
    public static final int STROKE_WIDTH = 8;
    private static final int RING_NUM = 3;
    private static final int DEFAULT_DURATION = 300;
    private ArrayList<PulsatingRingHolder> pulsatingRingHolders = new ArrayList<>();

    private int mStrokeWidth;
    private int mRingColor;
    private int mRingNum;
    private int mDuration;
    private int mMinRad;
    private int mUsableRad;

    private Animator mValueAnimator;
    private float mRadAlphaThreshold;
    private float mRadAlphaRemainder;

    public PulsatingRing(Context context)
    {
        this(context, null);
    }

    public PulsatingRing(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PulsatingRing(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
        {
            setLayerType(LAYER_TYPE_HARDWARE, null);
            setDrawingCacheEnabled(true);
        }
        readAttributes(context, attrs, defStyleAttr);
        init();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mValueAnimator.end();
    }

    private void readAttributes(Context context, AttributeSet attrs, int defStyle)
    {
        if (attrs != null)
        {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PulsatingRing, defStyle, 0);
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.PulsatingRing_ringWidth, STROKE_WIDTH);
            mRingColor = a.getColor(R.styleable.PulsatingRing_ringColor, Color.BLACK);
            mRingNum = a.getInteger(R.styleable.PulsatingRing_ringNum, RING_NUM);
            mDuration = a.getInteger(R.styleable.PulsatingRing_ringDuration, DEFAULT_DURATION);
            mMinRad = a.getDimensionPixelSize(R.styleable.PulsatingRing_ringMinRadian, 0);
            a.recycle();
        }
        else
        {
            // Init with default values
            mStrokeWidth = STROKE_WIDTH;
            mRingColor = Color.BLACK;
            mRingNum = RING_NUM;
            mDuration = DEFAULT_DURATION;
        }
    }

    private void init()
    {
        for (int i = 0; i < mRingNum; i++)
        {
            pulsatingRingHolders.add(new PulsatingRingHolder(createPaint()));
        }
    }

    private Paint createPaint()
    {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(mRingColor);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(mStrokeWidth);
        return p;
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        int allowedWidth = w - getPaddingLeft() - getPaddingRight() - mStrokeWidth;
        int allowedHeight = h - getPaddingTop() - getPaddingBottom() - mStrokeWidth;
        int realMaxRad = (Math.min(allowedWidth, allowedHeight) / 2);

        mUsableRad = realMaxRad - mMinRad;
        mRadAlphaThreshold = mUsableRad * 0.3f;
        mRadAlphaRemainder = mUsableRad - mRadAlphaThreshold;

        for (PulsatingRingHolder r : pulsatingRingHolders)
        {
            r.circleX = w / 2;
            r.circleY = h / 2;
        }
    }

    @Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        for (PulsatingRingHolder r : pulsatingRingHolders)
        {
            canvas.drawCircle(r.circleX, r.circleY, r.circleR, r.paint);
        }
    }

    private void startAnimation()
    {
        mValueAnimator = getValueAnimator();
        mValueAnimator.start();
    }

    private Animator getValueAnimator()
    {
        final int factor = 5;
        final int maxVal = pulsatingRingHolders.size() * factor;
        ValueAnimator animator = ValueAnimator.ofFloat(0, maxVal);
        animator.setDuration(mDuration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (Float) animation.getAnimatedValue();
                for (int i = 0; i < pulsatingRingHolders.size(); i++)
                {
                    int cutValue = i * factor;
                    PulsatingRingHolder r = pulsatingRingHolders.get(i);
                    if (value >= cutValue)
                    {
                        r.circleR = mMinRad + (int) (((value - cutValue) / maxVal) * mUsableRad);
                    }
                    else
                    {
                        r.circleR = mMinRad + (int) (((value + (maxVal - cutValue)) / maxVal) * mUsableRad);
                    }
                    PulsatingRing.this.calculateAlpha(r.paint, r.circleR);
                }
                PulsatingRing.this.invalidate();
            }
        });

        return animator;
    }

    private void calculateAlpha(Paint p, int currentRad)
    {
        int maxAlpha = 255;
        if (currentRad >= mRadAlphaThreshold)
        {
            float fraction = (currentRad - mRadAlphaThreshold - mMinRad) / mRadAlphaRemainder;
            int alpha =  maxAlpha - (int) (fraction * 255f);
            p.setAlpha(alpha);
        }
        else
        {
            p.setAlpha(maxAlpha);
        }
    }

    public void setColorFilter(int color, PorterDuff.Mode mode)
    {
        for (PulsatingRingHolder r : pulsatingRingHolders)
        {
            r.paint.setColorFilter(new PorterDuffColorFilter(color, mode));
        }
    }
}
