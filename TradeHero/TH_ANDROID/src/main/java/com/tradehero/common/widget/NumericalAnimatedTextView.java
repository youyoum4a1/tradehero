package com.tradehero.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.tradehero.thm.R;
import java.text.NumberFormat;
import timber.log.Timber;

public class NumericalAnimatedTextView extends TextSwitcher
        implements ViewSwitcher.ViewFactory
{
    private static final int ANIMATION_DURATION = 4000;
    private static final int ANIMATION_INTERVAL = 50;
    private long mAnimationDuration;
    private long mAnimationInterval;

    private float mStartValue;
    private float mEndValue;

    private NumberFormat numberFormat;
    private int textSize;
    private CountDownTimer mTimer;

    //private void initFormats() {
    //    mProgressNumberFormat = "%1d/%2d";
    //    mProgressPercentFormat = NumberFormat.getPercentInstance();
    //    mProgressPercentFormat.setMaximumFractionDigits(0);
    //}

    //<editor-fold desc="Constructors">
    public NumericalAnimatedTextView(Context context)
    {
        this(context, null);
    }

    public NumericalAnimatedTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        readAttrs(context, attrs);
    }
    //</editor-fold>

    private void readAttrs(final Context context, final AttributeSet attrs)
    {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimatedTextView);
        boolean showAnimation = a.getBoolean(R.styleable.AnimatedTextView_showAnimation, false);
        if (showAnimation)
        {
            int animationDuration;
            final int animationDurationId =
                    a.getResourceId(R.styleable.AnimatedTextView_animatedDuration, 0);
            if (animationDurationId > 0)
            {
                animationDuration = context.getResources().getInteger(animationDurationId);
            }
            else
            {
                animationDuration = a.getInteger(R.styleable.AnimatedTextView_animatedDuration,
                        ANIMATION_DURATION);
            }
            mAnimationDuration = animationDuration;
            if (mAnimationDuration <= 0)
            {
                throw new IllegalArgumentException("animationDuration must be positive");
            }

            int animationInterval;
            final int animationIntervalId =
                    a.getResourceId(R.styleable.AnimatedTextView_animatedInterval, 0);
            if (animationIntervalId > 0)
            {
                animationInterval = context.getResources().getInteger(animationIntervalId);
            }
            else
            {
                animationInterval = a.getInteger(R.styleable.AnimatedTextView_animatedInterval,
                        ANIMATION_INTERVAL);
            }

            mAnimationInterval = animationInterval;
            if (mAnimationDuration <= 0)
            {
                throw new IllegalArgumentException("animationInterval must be positive");
            }
        }
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                getContext().getResources().getDisplayMetrics());
        textSize = a.getDimensionPixelSize(R.styleable.AnimatedTextView_textSize, textSize);

        Timber.d("Get textSize %s", textSize);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(1);

        mTimer = createAnimationCountDownTimer(mAnimationDuration, mAnimationInterval);
        //mTimer.start();

        Animation in = AnimationUtils.loadAnimation(this.getContext(),
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this.getContext(),
                android.R.anim.fade_out);
        setInAnimation(in);
        setOutAnimation(out);

        setFactory(this);
    }

    public View makeView()
    {
        TextView t = new TextView(this.getContext());
        t.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        t.setTypeface(t.getTypeface(), Typeface.BOLD);
        t.getPaint().setTextSize(textSize);
        t.setIncludeFontPadding(false);
        //t.setPadding(0,0,0,-5);
        //Timber.d("makeView padding bottom %s",t.getPaddingBottom());
        //t.setTextSize(textSize);
        return t;
    }

    public void setStartValue(float startValue)
    {
        this.mStartValue = startValue;
    }

    public void setEndValue(float endValue)
    {
        this.mEndValue = endValue;
    }

    public void setFractionDigits(int fractionDigits)
    {
        if (fractionDigits >= 0)
        {
            numberFormat.setMaximumFractionDigits(fractionDigits);
            numberFormat.setMinimumFractionDigits(fractionDigits);
        }
    }

    public void showText()
    {
        setText(numberFormat.format(mEndValue));
    }

    public void startAnimation()
    {
        mTimer.start();
    }

    protected CountDownTimer createAnimationCountDownTimer(long millisInFuture, long countDownInterval)
    {
        return new AnimationCountDownTimer(millisInFuture, countDownInterval);
    }

    protected class AnimationCountDownTimer extends CountDownTimer
    {
        public AnimationCountDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override public void onTick(long millisUntilFinished)
        {
            float percent = (mAnimationDuration - millisUntilFinished) * 1f / mAnimationDuration;
            float value = (mEndValue - mStartValue) * percent;

            //String.format("%.2f",value);
            setText(numberFormat.format(value));
            Timber.d("onTick %s", value);
        }

        @Override public void onFinish()
        {
            setText(numberFormat.format(mEndValue));
            Timber.d("onFinish %s %s", mEndValue,
                    numberFormat.getMaximumFractionDigits());
        }
    }
}
