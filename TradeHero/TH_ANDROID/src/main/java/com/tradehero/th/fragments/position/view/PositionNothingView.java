package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

public class PositionNothingView extends RelativeLayout
{
    @InjectView(R.id.position_nothing_description) protected TextView description;

    //<editor-fold desc="Constructors">
    public PositionNothingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public PositionNothingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionNothingView(Context context)
    {
        super(context);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        display();
    }

    public void display()
    {
        if (description != null)
        {
            description.setText(R.string.position_nothing_description);
        }
    }
}
