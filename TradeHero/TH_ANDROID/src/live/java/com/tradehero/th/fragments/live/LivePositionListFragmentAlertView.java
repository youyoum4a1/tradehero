package com.tradehero.th.fragments.live;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;

public class LivePositionListFragmentAlertView extends LinearLayout
{
    @Bind(R.id.stock_logo) ImageView stockImageView;
    @Bind(R.id.stock_name) TextView stockTextView;

    public LivePositionListFragmentAlertView(Context context)
    {
        super(context);
    }

    public LivePositionListFragmentAlertView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LivePositionListFragmentAlertView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setImage(Context context, String url)
    {
        Picasso.with(context)
                .load(Uri.parse(url))
                .into(stockImageView);
    }

    public void setText(String text)
    {
        stockTextView.setText(text);
    }
}
