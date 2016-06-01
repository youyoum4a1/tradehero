package com.ayondo.academy.fragments.portfolio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.portfolio.DisplayablePortfolioDTO;
import com.ayondo.academy.api.portfolio.DisplayablePortfolioUtil;
import com.ayondo.academy.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTOUtil;
import com.ayondo.academy.api.portfolio.PortfolioDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.number.THSignedPercentage;
import com.ayondo.academy.utils.DateUtils;
import com.ayondo.academy.utils.GraphicUtil;
import javax.inject.Inject;
import rx.Subscription;

public class PortfolioListItemView extends RelativeLayout
        implements DTOView<DisplayablePortfolioDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;

    @Bind(R.id.portfolio_title) protected TextView title;
    @Bind(R.id.portfolio_description) protected TextView description;
    @Bind(R.id.roi_value) @Nullable protected TextView roiValue;
    @Bind(R.id.portfolio_image) @Nullable protected ImageView portfolioImage;

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
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        unsubscribe(userWatchlistSubscription);
        userWatchlistSubscription = null;

        if (portfolioImage != null)
        {
            picasso.cancelRequest(portfolioImage);
        }
        super.onDetachedFromWindow();
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
        displayTitle();
        displayDescription();
        displayRoiValue();
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
                String format = displayablePortfolioDTO.portfolioDTO.isDefault()
                        ? getContext().getString(R.string.roi_since_inception_format)
                        : getContext().getString(R.string.roi_since_format, "%1$s",
                                DateUtils.getDisplayableDate(getContext().getResources(), displayablePortfolioDTO.portfolioDTO.creationDate,
                                        R.string.data_format_d_mmm_yyyy));
                THSignedPercentage.builder(displayablePortfolioDTO.portfolioDTO.roiSinceInception * 100)
                        .withSign()
                        .signTypePlusMinusAlways()
                        .withDefaultColor()
                        .boldSign()
                        .boldValue()
                        .format(format)
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
