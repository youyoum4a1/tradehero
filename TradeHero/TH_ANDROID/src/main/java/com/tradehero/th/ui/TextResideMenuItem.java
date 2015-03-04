package com.tradehero.th.ui;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

public class TextResideMenuItem extends FrameLayout
{
    @InjectView(R.id.title) TextView titleView;

    //<editor-fold desc="Constructors">
    public TextResideMenuItem(Context context)
    {
        super(context);
    }

    public TextResideMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setTitle(@StringRes int title)
    {
        titleView.setText(title);
    }
}
