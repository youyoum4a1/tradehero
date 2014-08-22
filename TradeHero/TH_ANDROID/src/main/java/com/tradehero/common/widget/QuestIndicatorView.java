package com.tradehero.common.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

public class QuestIndicatorView extends RelativeLayout
{
    @InjectView(R.id.quest_logo_indicator) View logo;
    @InjectView(R.id.quest_top_indicator) TextView topIndicator;
    @InjectView(R.id.quest_bottom_indicator) TextView botIndicator;

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

    public void on()
    {
        logo.setVisibility(View.VISIBLE);
    }

    public void off()
    {
        logo.setVisibility(View.GONE);
    }

    public void animateOn()
    {
        Animator a = AnimatorInflater.loadAnimator(getContext(), R.animator.fade_in_out);
        a.setTarget(logo);
        a.start();
    }

    public void setText(String top, String bot)
    {
        topIndicator.setText(top);
        botIndicator.setText(bot);
    }
}
