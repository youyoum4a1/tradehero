package com.tradehero.th.fragments.achievement;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import java.util.ArrayList;
import java.util.List;

public class AchievementProgressIndicator extends LinearLayout
{
    List<ViewHolder> indicatorLists = new ArrayList<>();

    public AchievementProgressIndicator(Context context)
    {
        super(context);
    }

    public AchievementProgressIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AchievementProgressIndicator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        addIndicator(R.id.achievement_indicator_1, R.id.achievement_indicator_text_1);
        addIndicator(R.id.achievement_indicator_2, R.id.achievement_indicator_text_2);
        addIndicator(R.id.achievement_indicator_3, R.id.achievement_indicator_text_3);
        addIndicator(R.id.achievement_indicator_4, R.id.achievement_indicator_text_4);
        addIndicator(R.id.achievement_indicator_5, R.id.achievement_indicator_text_5);
    }

    private void addIndicator(int indicatorImageViewResId, int indicatorTextViewResId)
    {
        indicatorLists.add(new ViewHolder(this, indicatorImageViewResId, indicatorTextViewResId));
    }

    public void setAchievementDef(List<AchievementDefDTO> achievementDefs, int currentUserLevel)
    {
        for (int i = 0; i < achievementDefs.size() && i < indicatorLists.size(); i++)
        {
            ViewHolder viewHolder = indicatorLists.get(i);
            AchievementDefDTO achievementDefDTO = achievementDefs.get(i);

            viewHolder.display(achievementDefDTO);

            if (achievementDefDTO.achievementLevel <= currentUserLevel)
            {
                viewHolder.on();
            }
            else
            {
                viewHolder.off();
            }
        }

        if (achievementDefs.size() < indicatorLists.size())
        {
            for (int i = achievementDefs.size(); i < indicatorLists.size(); i++)
            {
                ViewHolder viewHolder = indicatorLists.get(i);
                viewHolder.hide();
            }
        }
    }

    protected class ViewHolder
    {
        TextView indicatorTextView;
        ImageView indicatorImageView;

        public ViewHolder(View parentView, int indicatorImageViewResId, int indicatorTextViewResId)
        {
            indicatorImageView = (ImageView) parentView.findViewById(indicatorImageViewResId);
            indicatorTextView = (TextView) parentView.findViewById(indicatorTextViewResId);
        }

        public void display(AchievementDefDTO achievementDefDTO)
        {
            indicatorTextView.setText(String.valueOf(achievementDefDTO.trigger));
        }

        public void on()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achievement_star_on);
            show();
        }

        public void off()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achievement_star_off);
            show();
        }

        private void show()
        {
            indicatorImageView.setVisibility(View.VISIBLE);
        }

        public void hide()
        {
            indicatorImageView.setVisibility(View.GONE);
        }
    }
}
