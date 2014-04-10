package com.tradehero.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 *
 */
public class CenterImageTextButton extends Button
{

    public CenterImageTextButton(Context context)
    {
        super(context);
    }

    public CenterImageTextButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CenterImageTextButton(Context context, AttributeSet attrs, int style)
    {
        super(context, attrs, style);
    }

    private Paint mPaint = new Paint();
    private String mText = null;
    private float mTextSize = 0;

    @Override
    protected void onDraw(Canvas canvas)
    {

        mText = getText().toString();
        mTextSize = getTextSize();

        //TextPaint paint = getPaint();
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(getCurrentTextColor());

        // get left top
        Drawable drawable = getCompoundDrawables()[0];
        Drawable curDrawable = null;
        if (drawable instanceof StateListDrawable)
        {
            curDrawable = ((StateListDrawable) drawable).getCurrent();
        }
        else
        {
            curDrawable = ((BitmapDrawable) drawable).getCurrent();
        }
        Bitmap image = ((BitmapDrawable) curDrawable).getBitmap();

        // call default drawing method without image/text
        setText("");
        setCompoundDrawables(null, null, null, null);
        super.onDraw(canvas);
        setText(mText);
        setCompoundDrawables(drawable, null, null, null);

        // get measurements of button and Image
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        // get measurements of text
        //float densityMultiplier = getContext().getResources().getDisplayMetrics().density;
        //float scaledPx = textSize * densityMultiplier;
        //paint.setTextSize(scaledPx);
        mPaint.setTextSize(mTextSize);

        float textWidth = mPaint.measureText(mText);

        int compoundDrawablePadding = getCompoundDrawablePadding();
        // draw Image and text
        float groupWidth = imgWidth + textWidth + compoundDrawablePadding;
        float groupHeight = Math.max(imgHeight, mTextSize);

        if (groupWidth >= width)
        {
            canvas.drawBitmap(image, (width - groupWidth) / 2, (height - imgHeight) / 2, null);
            canvas.drawText(mText, (width - groupWidth) / 2 + imgWidth + compoundDrawablePadding,
                    mTextSize - 5 + (height - mTextSize) / 2, mPaint);
        }
        else
        {
            canvas.drawBitmap(image, (width - groupWidth) / 2, (height - imgHeight) / 2, null);
            canvas.drawText(mText, (width - groupWidth) / 2 + imgWidth + compoundDrawablePadding,
                    mTextSize - 5 + (height - mTextSize) / 2, mPaint);
        }
    }
}
