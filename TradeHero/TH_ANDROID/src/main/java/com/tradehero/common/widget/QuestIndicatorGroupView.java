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
        super(context);
    }

    public QuestIndicatorGroupView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public QuestIndicatorGroupView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setCurrentCount(int progress)
    {
        int mCurrent = progress;
        int mCurrentN1 = progress - 1;
        int mCurrentN2 = progress - 2;
        int mCurrent1 = progress + 1;
        int mCurrent2 = progress + 2;

        questIndicatorView1.animateOn();
        questIndicatorView2.off();
        questIndicatorView3.off();
        questIndicatorView4.off();
        questIndicatorView5.off();

        questIndicatorView1.setText("Day 3", "TH$ 15k");
        questIndicatorView2.setText("Day 4", "TH$ 20k");
        questIndicatorView3.setText("Day 5", "TH$ 30k");
        questIndicatorView4.setText("Day 6", "TH$ 50k");
        questIndicatorView5.setText("Day 7", "TH$ 80k");

    }
}
