package com.tradehero.common.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import org.jetbrains.annotations.NotNull;

public class QuestIndicatorView extends RelativeLayout implements DTOView<QuestBonusDTO>
{
    @InjectView(R.id.quest_logo_indicator) ImageView logo;
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
        logo.setImageResource(R.drawable.ic_achievement_star_on);
    }

    public void off()
    {
        logo.setImageResource(R.drawable.ic_achievement_star_off);
        updateIndicatorTextColor(R.color.text_gray_normal);
    }

    public void animateOn()
    {
        updateIndicatorTextColor(R.color.achievement_dollars_earned_color);

        logo.setImageResource(R.drawable.ic_achivement_star_animate);
        AnimationDrawable animationDrawable = (AnimationDrawable) logo.getDrawable();
        animationDrawable.start();

        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.quest_indicator_zoom_in);
        startAnimation(a);
    }

    private void updateIndicatorTextColor(int colorResId)
    {
        int col = getResources().getColor(colorResId);
        topIndicator.setTextColor(col);
        botIndicator.setTextColor(col);
    }

    private void setText(String top, String bot)
    {
        topIndicator.setText(top);
        botIndicator.setText(bot);
    }

    @Override public void display(@NotNull QuestBonusDTO dto)
    {
        setText(dto.levelStr, dto.bonusStr);
    }

    public void display(@NotNull QuestBonusDTO dto, int currentLevel)
    {
        display(dto);
        if (dto.level < currentLevel)
        {
            on();
        }
        else if (dto.level == currentLevel)
        {
            animateOn();
        }
        else
        {
            off();
        }
    }
}
