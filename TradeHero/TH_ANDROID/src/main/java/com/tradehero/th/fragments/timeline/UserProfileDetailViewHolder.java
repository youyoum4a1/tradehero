package com.tradehero.th.fragments.timeline;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForUserPhotoBackground;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.level.LevelDefListCache;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.widget.UserLevelProgressBar;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class UserProfileDetailViewHolder extends UserProfileCompactViewHolder
{
    @InjectView(R.id.profile_screen_user_detail_top) @Optional protected View profileTop;
    @InjectView(R.id.txt_profile_tradeprofit) @Optional protected TextView profitFromTrades;
    @InjectView(R.id.txt_total_wealth) @Optional protected TextView totalWealth;
    @InjectView(R.id.txt_additional_cash) @Optional protected TextView additionalCash;
    @InjectView(R.id.txt_cash_on_hand) @Optional protected TextView cashOnHand;
    @InjectView(R.id.user_profile_achievement_count) @Optional protected TextView achievementCount;
    @InjectView(R.id.user_level_progress_bar) @Optional protected UserLevelProgressBar userLevelProgressBar;

    @Inject @ForUserPhotoBackground protected Transformation peopleBackgroundTransformation;
    @Inject GraphicUtil graphicUtil;
    @Inject LevelDefListCache levelDefListCache;

    private Target topBackgroundTarget;
    private Target topDefaultBackgroundTarget;
    protected Runnable displayTopViewBackgroundRunnable;
    private DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList> levelDefDTOListListener;

    public UserProfileDetailViewHolder(View view)
    {
        super(view);
    }

    @Override public void initViews(View view)
    {
        super.initViews(view);
        topBackgroundTarget = new BackgroundTarget();
        topDefaultBackgroundTarget = new DefaultBackgroundTarget();
        levelDefDTOListListener = new LevelDefListCacheListener();
        LevelDefListId levelDefListId = new LevelDefListId();
        levelDefListCache.register(levelDefListId, levelDefDTOListListener);
        levelDefListCache.getOrFetchAsync(levelDefListId);
    }

    @Override public void detachViews()
    {
        topBackgroundTarget = null;
        topDefaultBackgroundTarget = null;
        if (profileTop != null)
        {
            profileTop.removeCallbacks(displayTopViewBackgroundRunnable);
        }
        levelDefListCache.unregister(levelDefDTOListListener);
        super.detachViews();
    }

    @Override public void display(final UserProfileDTO dto)
    {
        super.display(dto);
        displayTotalWealth();
        displayAdditionalCash();
        displayCashOnHand();
        displayAchievementCount();
        displayLevelProgress();
        loadBgPicture();
    }

    protected void loadBgPicture()
    {
        displayTopViewBackgroundRunnable = new Runnable()
        {
            @Override public void run()
            {
                if (userProfileDTO != null &&
                        profileTop != null &&
                        profileTop.getWidth() > 0 &&
                        profileTop.getHeight() > 0 &&
                        topBackgroundTarget != null)
                {
                    loadDefaultBG();
                    if (userProfileDTO.picture != null)
                    {
                        picasso.load(userProfileDTO.picture)
                                .transform(peopleBackgroundTransformation)
                                .resize(profileTop.getWidth(), profileTop.getHeight())
                                .centerCrop()
                                .into(topBackgroundTarget);
                    }
                }
            }
        };
        if (profileTop != null)
        {
            profileTop.post(displayTopViewBackgroundRunnable);
        }
    }

    public void loadDefaultBG()
    {
        if (profileTop != null && topDefaultBackgroundTarget != null
                && profileTop.getWidth() > 0 && profileTop.getHeight() > 0)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(peopleBackgroundTransformation)
                    .resize(profileTop.getWidth(), profileTop.getHeight())
                    .centerCrop()
                    .into(topDefaultBackgroundTarget);
        }
    }

    protected void displayTotalWealth()
    {
        if (totalWealth != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                THSignedNumber thTotalWealth = THSignedMoney.builder(userProfileDTO.portfolio.totalValue)
                        .currency(userProfileDTO.portfolio.getNiceCurrency())
                        .build();
                totalWealth.setText(thTotalWealth.toString());
            }
            else
            {
                totalWealth.setText(R.string.na);
            }
        }
    }

    protected void displayAdditionalCash()
    {
        if (additionalCash != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                THSignedNumber thAdditionalCash = THSignedMoney.builder(userProfileDTO.portfolio.getTotalExtraCash())
                        .currency(userProfileDTO.portfolio.getNiceCurrency())
                        .build();
                additionalCash.setText(thAdditionalCash.toString());
            }
            else
            {
                additionalCash.setText(R.string.na);
            }
        }
    }

    protected void displayCashOnHand()
    {
        if (cashOnHand != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                THSignedNumber thCashOnHand = THSignedMoney.builder(userProfileDTO.portfolio.cashBalance)
                        .currency(userProfileDTO.portfolio.getNiceCurrency())
                        .build();
                cashOnHand.setText(thCashOnHand.toString());
            }
            else
            {
                cashOnHand.setText(R.string.na);
            }
        }
    }

    protected void displayAchievementCount()
    {
        if (achievementCount != null)
        {
            if (userProfileDTO != null)
            {
                achievementCount.setText(String.valueOf(userProfileDTO.achievementCount));
            }
            else
            {
                achievementCount.setText(R.string.na);
            }
        }
    }

    protected void setLevelDef(LevelDefDTOList levelDefDTOList)
    {
        if(userLevelProgressBar != null)
        {
            userLevelProgressBar.setLevelDefDTOList(levelDefDTOList);
        }
        displayLevelProgress();
    }

    protected void displayLevelProgress()
    {
        if(userProfileDTO != null && userLevelProgressBar != null && userLevelProgressBar.getLevelDefDTOList() != null)
        {
            userLevelProgressBar.startsWith(userProfileDTO.currentXp);
        }
    }

    public void setVisibility(int visibility)
    {
        if (visibility == View.VISIBLE
                && displayTopViewBackgroundRunnable != null
                && profileTop != null)
        {
            profileTop.post(displayTopViewBackgroundRunnable);
        }
    }

    protected class LevelDefListCacheListener implements DTOCacheNew.HurriedListener<LevelDefListId,LevelDefDTOList>
    {
        @Override public void onPreCachedDTOReceived(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
        {
            setLevelDef(value);
        }

        @Override public void onDTOReceived(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
        {
            setLevelDef(value);
        }

        @Override public void onErrorThrown(@NotNull LevelDefListId key, @NotNull Throwable error)
        {
            if(userLevelProgressBar != null)
            {
                userLevelProgressBar.setVisibility(View.GONE);
            }
        }
    }

    protected class BackgroundTarget implements Target
    {
        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            if (profileTop != null)
            {
                graphicUtil.setBackground(profileTop, new BitmapDrawable(context.getResources(), bitmap));
            }
        }

        @Override public void onBitmapFailed(Drawable errorDrawable)
        {
        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable)
        {
        }
    }

    protected class DefaultBackgroundTarget
            extends BackgroundTarget
    {
    }

    @OnClick(R.id.user_profile_achievement_count_wrapper) @Optional
    @Override protected void notifyDefaultPortfolioClicked()
    {
        super.notifyDefaultPortfolioClicked();
    }
}
