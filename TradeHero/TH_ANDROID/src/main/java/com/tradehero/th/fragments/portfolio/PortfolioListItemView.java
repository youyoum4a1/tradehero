package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.GraphicUtil;
import javax.inject.Inject;
import rx.Subscription;

public class PortfolioListItemView extends RelativeLayout
        implements DTOView<DisplayablePortfolioDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userImageTransformation;
    @Inject DashboardNavigator navigator;

    @InjectView(R.id.follower_profile_picture) @Optional protected ImageView userIcon;
    @InjectView(R.id.portfolio_title) protected TextView title;
    @InjectView(R.id.portfolio_description) protected TextView description;
    @InjectView(R.id.roi_value) @Optional protected TextView roiValue;
    @InjectView(R.id.portfolio_image) @Optional protected ImageView portfolioImage;

    private DisplayablePortfolioDTO displayablePortfolioDTO;
    @Nullable private Subscription userWatchlistSubscription;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PortfolioListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
        if (userIcon != null && picasso != null)
        {
            displayDefaultUserIcon();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        unsubscribe(userWatchlistSubscription);
        userWatchlistSubscription = null;

        if (this.userIcon != null)
        {
            this.userIcon.setOnClickListener(null);
        }
        if (portfolioImage != null)
        {
            picasso.cancelRequest(portfolioImage);
        }
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.follower_profile_picture) @Optional
    protected void handleUserIconClicked()
    {
        if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null)
        {
            Bundle bundle = new Bundle();
            UserBaseKey userToSee = new UserBaseKey(displayablePortfolioDTO.userBaseDTO.id);
            if (currentUserId.toUserBaseKey().equals(userToSee))
            {
                navigator.pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
                PushableTimelineFragment.putUserBaseKey(bundle, userToSee);
                navigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public void display(DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        this.displayablePortfolioDTO = displayablePortfolioDTO;

        displayUserIcon();
        displayTitle();
        displayDescription();
        displayRoiValue();
        displayImage();
    }

    private void displayImage()
    {
        if (portfolioImage != null)
        {
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
            {
                final PortfolioDTO portfolioDTO = displayablePortfolioDTO.portfolioDTO;
                int imageResId = PortfolioCompactDTOUtil.getIconResId(portfolioDTO);
                picasso.load(imageResId)
                        .into(new Target()
                        {
                            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                            {
                                portfolioImage.setImageBitmap(bitmap);
                                Integer colorResId = PortfolioCompactDTOUtil.getIconTintResId(portfolioDTO);
                                if (colorResId != null)
                                {
                                    GraphicUtil.applyColorFilter(portfolioImage,
                                            getResources().getColor(colorResId));
                                }
                            }

                            @Override public void onBitmapFailed(Drawable errorDrawable)
                            {
                            }

                            @Override public void onPrepareLoad(Drawable placeHolderDrawable)
                            {
                            }
                        });
            }
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayUserIcon();
        displayTitle();
        displayDescription();
        displayRoiValue();
    }

    public void displayUserIcon()
    {
        if (userIcon != null)
        {
            displayDefaultUserIcon();
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null)
            {
                picasso.load(displayablePortfolioDTO.userBaseDTO.picture)
                        .transform(userImageTransformation)
                        .placeholder(userIcon.getDrawable())
                        .into(userIcon);
            }
        }
    }

    public void displayDefaultUserIcon()
    {
        if (userIcon != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(userImageTransformation)
                    .into(userIcon);
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(DisplayablePortfolioUtil.getLongTitle(getResources(),
                    displayablePortfolioDTO));
            title.setTextColor(DisplayablePortfolioUtil.getLongTitleTextColor(getContext(),
                    displayablePortfolioDTO));
        }
    }

    public void displayDescription()
    {
        TextView descriptionCopy = this.description;
        if (descriptionCopy != null)
        {
            String descriptionText = getDescription();
            descriptionCopy.setText(descriptionText);
            description.setVisibility(TextUtils.isEmpty(descriptionText) ? GONE : VISIBLE);
        }
    }

    public String getDescription()
    {
        return DisplayablePortfolioUtil.getLongSubTitle(getResources(), currentUserId, displayablePortfolioDTO);
    }

    public void displayRoiValue()
    {
        if (roiValue != null)
        {
            if (displayablePortfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO.roiSinceInception != null)
            {
                THSignedPercentage.builder(displayablePortfolioDTO.portfolioDTO.roiSinceInception * 100)
                        .withSign()
                        .signTypePlusMinusAlways()
                        .withDefaultColor()
                        .boldSign()
                        .boldValue()
                        .format(getContext().getString(R.string.roi_since_format, "%1$s",
                                DateUtils.getDisplayableDate(getContext().getResources(), displayablePortfolioDTO.portfolioDTO.creationDate,
                                        R.string.data_format_d_mmm_yyyy)))
                        .build()
                        .into(roiValue);
                roiValue.setVisibility(VISIBLE);
            }
            else if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
            {
                roiValue.setVisibility(GONE);
            }
            else if (displayablePortfolioDTO != null
                    && displayablePortfolioDTO.portfolioDTO != null
                    && displayablePortfolioDTO.portfolioDTO.isWatchlist)
            {
                roiValue.setVisibility(GONE);
            }
            else
            {
                roiValue.setVisibility(VISIBLE);
                roiValue.setText(R.string.na);
            }
        }
    }
    //</editor-fold>
}
