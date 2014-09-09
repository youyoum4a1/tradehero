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
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;

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
        displayDescription(dto);
        displayIndicators(dto);

        AchievementDefDTO achievementDefDTO = null;
        if (dto.currentUserLevel > 0 && dto.achievementDefs.size() >= dto.currentUserLevel)
        {
            achievementDefDTO = dto.achievementDefs.get(dto.currentUserLevel - 1);
        }
        displayBadge(achievementDefDTO, dto.badge);

        displayTitle(achievementDefDTO, dto.displayName);
    }

    private void displayBadge(@Nullable AchievementDefDTO dto, @Nullable String defaultBadge)
    {
        picasso.cancelRequest(badge);
        if (dto != null && !StringUtils.isNullOrEmpty(dto.visual))
        {
            picasso.load(dto.visual)
                    .centerInside()
                    .fit()
                    .placeholder(R.drawable.achievement_unlocked_placeholder)
                    .into(badge);
        }
        else if (defaultBadge != null)
        {
            picasso.load(defaultBadge)
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
        if (dto.achievementDefs != null)
        {
            achievementProgressIndicator.setAchievementDef(dto.achievementDefs, dto.currentUserLevel);
            achievementProgressIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            achievementProgressIndicator.setVisibility(View.GONE);
        }
    }

    private void displayTitle(@Nullable AchievementDefDTO achievementDefDTO, String defaultName)
    {
        if (achievementDefDTO != null)
        {
            title.setText(achievementDefDTO.thName);
        }
        else
        {
            title.setText(defaultName);
        }
    }

    private void displayDescription(AchievementCategoryDTO dto)
    {
        description.setText(dto.description);
    }
}
