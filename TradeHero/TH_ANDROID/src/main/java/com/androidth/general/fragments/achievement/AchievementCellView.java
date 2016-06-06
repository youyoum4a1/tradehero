package com.androidth.general.fragments.achievement;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.achievement.AchievementCategoryDTO;
import com.androidth.general.api.achievement.AchievementDefDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.utils.StringUtils;
import javax.inject.Inject;

public class AchievementCellView extends RelativeLayout implements DTOView<AchievementCategoryDTO>
{
    @Bind(R.id.achievement_badge) protected ImageView badge;

    @Bind(R.id.achievement_title) protected TextView title;
    @Bind(R.id.achievement_description) protected TextView description;

    @Bind(R.id.achievement_progress_indicator) protected AchievementProgressIndicator achievementProgressIndicator;

    @Inject Picasso picasso;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public AchievementCellView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AchievementCellView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AchievementCellView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
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
