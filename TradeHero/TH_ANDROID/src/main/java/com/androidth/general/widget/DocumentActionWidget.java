package com.androidth.general.widget;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.androidth.general.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DocumentActionWidget extends RelativeLayout implements Target
{
    @Bind(R.id.document_action) Button btnAction;
    @Bind(R.id.document_clear) ImageButton btnClear;
    @Bind(R.id.document_preview) ImageView imgPreview;
    @Bind(R.id.document_progress) ProgressBar progressBar;

    @Nullable private View.OnClickListener clearOnClickListener;

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
                View.OnClickListener copy = clearOnClickListener;
                if (copy != null)
                {
                    copy.onClick(v);
                }
            }
        });
    }

    public void setActionOnClickListener(@Nullable View.OnClickListener onClickListener)
    {
        btnAction.setOnClickListener(onClickListener);
    }

    public void setClearOnClickListener(@Nullable View.OnClickListener onClickListener)
    {
        clearOnClickListener = onClickListener;
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

    public void setLoading(boolean isLoading)
    {
        if (isLoading)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }
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

    @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
    {
        setPreviewBitmap(bitmap);
    }

    @Override public void onBitmapFailed(Drawable errorDrawable)
    {
        setPreviewDrawable(errorDrawable);
    }

    @Override public void onPrepareLoad(Drawable placeHolderDrawable)
    {
        setPreviewDrawable(placeHolderDrawable);
    }
}
