package com.tradehero.th.fragments.live;

import android.content.Context;
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
    @Bind(R.id.stock_symbol) TextView stockSymbolTextView;
    @Bind(R.id.stock_name) TextView stockNameTextView;

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

    public void setImage(Context context, String url, int defaultStockLogoRes)
    {
        if (url != null)
        {
            Picasso.with(context)
                    .load(Uri.parse(url))
                    .into(stockImageView);
        }
        else
        {
            Picasso.with(context)
                    .load(defaultStockLogoRes)
                    .into(stockImageView);
        }
    }

    public void setStockSymbolText(String text)
    {
        stockSymbolTextView.setText(text);
    }

    public void setStockNameText(String text)
    {
        stockNameTextView.setText(text);
    }
}
