package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import com.tradehero.th.R;

public class QuestIndicatorView extends RelativeLayout
{
    public QuestIndicatorView(Context context)
    {
        this(context, null, 0);
    }

    public QuestIndicatorView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public QuestIndicatorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.quest_indicator, this, true);
        ButterKnife.inject(this);
    }
}
