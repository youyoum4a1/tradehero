package com.tradehero.th.widget;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.tradehero.th.R;

public class DocumentActionWidget extends RelativeLayout
{

    @Bind(R.id.document_action) Button btnAction;
    @Bind(R.id.document_clear) ImageButton btnClear;
    @Bind(R.id.document_preview) ImageView imgPreview;

    public DocumentActionWidget(Context context)
    {
        super(context);
        init(null);
    }

    public DocumentActionWidget(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public DocumentActionWidget(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public DocumentActionWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs)
    {
        setLayoutTransition(new LayoutTransition());
        LayoutInflater.from(getContext()).inflate(R.layout.document_action_layout_merged, this, true);
        ButterKnife.bind(this);

        if (attrs != null)
        {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DocumentActionWidget, 0, 0);
            Drawable d = a.getDrawable(R.styleable.DocumentActionWidget_android_drawableLeft);
            CharSequence t = a.getText(R.styleable.DocumentActionWidget_android_text);
            btnAction.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            btnAction.setText(t);
            a.recycle();
        }

        btnClear.setOnClickListener(new View.OnClickListener()
        {

            @Override public void onClick(View v)
            {
                imgPreview.setImageDrawable(null);
                hidePreview();
            }
        });
    }

    public void setActionOnClickListener(View.OnClickListener onClickListener)
    {
        btnAction.setOnClickListener(onClickListener);
    }

    public void setPreviewBitmap(Bitmap bmp)
    {
        imgPreview.setImageBitmap(bmp);
        hideAction();
    }

    public void setPreviewDrawable(Drawable drawable)
    {
        imgPreview.setImageDrawable(drawable);
        hideAction();
    }

    public void setPreviewDrawableResId(@DrawableRes int drawableResId)
    {
        imgPreview.setImageResource(drawableResId);
        hideAction();
    }

    private void hideAction()
    {
        btnAction.setVisibility(View.GONE);
        btnClear.setVisibility(View.VISIBLE);
        imgPreview.setVisibility(View.VISIBLE);
    }

    private void hidePreview()
    {
        btnAction.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.GONE);
        imgPreview.setVisibility(View.GONE);
    }
}
