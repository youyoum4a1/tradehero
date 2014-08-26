package com.tradehero.th.fragments.achievement;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.StringUtils;
import java.util.List;
import javax.inject.Inject;

public class AchievementCellView extends RelativeLayout implements DTOView<AchievementCategoryDTO>
{
    @InjectView(R.id.achievement_badge) protected ImageView badge;

    @InjectView(R.id.achievement_title) protected TextView title;
    @InjectView(R.id.achievement_description) protected TextView description;

    @InjectView(R.id.achievement_progress_indicator) protected AchievementProgressIndicator achievementProgressIndicator;

    @Inject Picasso picasso;

    public AchievementCellView(Context context)
    {
        super(context);
    }

    public AchievementCellView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AchievementCellView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        picasso.cancelRequest(badge);
        super.onDetachedFromWindow();
    }

    @Override public void display(AchievementCategoryDTO dto)
    {
        displayBadge(dto);
        displayTitle(dto);
        displayDescription(dto);
        displayIndicators(dto);
    }

    private void displayBadge(AchievementCategoryDTO dto)
    {
        picasso.cancelRequest(badge);
        if (!StringUtils.isNullOrEmpty(dto.badge))
        {
            picasso.load(dto.badge)
                    .centerInside()
                    .fit()
                    .placeholder(R.drawable.achievement_unlocked_placeholder)
                    .into(badge);
        }
        else
        {
            badge.setImageResource(R.drawable.achievement_unlocked_placeholder);
        }
    }

    public void displayIndicators(AchievementCategoryDTO dto)
    {
        if(dto.achievementDefs != null)
        {
            achievementProgressIndicator.setAchievementDef(dto.achievementDefs, dto.currentUserLevel);
            achievementProgressIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            achievementProgressIndicator.setVisibility(View.GONE);
        }
    }

    private void displayTitle(AchievementCategoryDTO dto)
    {
        title.setText(dto.displayName);
    }

    private void displayDescription(AchievementCategoryDTO dto)
    {
        description.setText(dto.description);
    }
}
