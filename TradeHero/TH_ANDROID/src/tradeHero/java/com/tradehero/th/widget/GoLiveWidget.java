package com.androidth.general.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.Button;
import com.tradehero.th.R;

public class GoLiveWidget extends Button
{
    public GoLiveWidget(Context context)
    {
        super(context);
        init();
    }

    public GoLiveWidget(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public GoLiveWidget(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public GoLiveWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.size_15));
        String imageTag = "[img]";
        String finalString = getResources().getString(R.string.go_live_span, imageTag);
        int start = finalString.indexOf(imageTag);
        int end = start + imageTag.length();
        setTransformationMethod(null);
        SpannableStringBuilder builder = new SpannableStringBuilder(finalString);
        ImageSpan imageSpan = new ImageSpan(getContext(), R.drawable.ic_logo_live, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(builder);
    }
}
