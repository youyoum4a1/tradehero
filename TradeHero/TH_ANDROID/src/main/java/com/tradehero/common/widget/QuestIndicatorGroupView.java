package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

public class QuestIndicatorGroupView extends LinearLayout
{
    @InjectView(R.id.quest_indicator_1) QuestIndicatorView questIndicatorView1;
    @InjectView(R.id.quest_indicator_2) QuestIndicatorView questIndicatorView2;
    @InjectView(R.id.quest_indicator_3) QuestIndicatorView questIndicatorView3;
    @InjectView(R.id.quest_indicator_4) QuestIndicatorView questIndicatorView4;
    @InjectView(R.id.quest_indicator_5) QuestIndicatorView questIndicatorView5;

    public QuestIndicatorGroupView(Context context)
    {
        this(context, null, 0);
    }

    public QuestIndicatorGroupView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public QuestIndicatorGroupView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        ButterKnife.inject(this);
    }

    public void setCurrentQuestProgress(int progress)
    {

    }
}
