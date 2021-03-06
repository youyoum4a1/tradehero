package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.RequestCreator;
import com.tradehero.common.graphics.ScaleKeepRatioTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import javax.inject.Inject;

public class TimelineItemViewHolder<TimelineItemDTOType extends TimelineItemDTO>
    extends AbstractDiscussionItemViewHolder<TimelineItemDTOType>
{
    @InjectView(R.id.timeline_vendor_picture) ImageView vendorImage;
    @InjectView(R.id.in_watchlist_indicator) ImageView watchlistIndicator;

    @Inject WatchlistPositionCacheRx watchlistPositionCache;

    //<editor-fold desc="Constructors">

    public TimelineItemViewHolder(Context context)
    {
        super(context);
    }

    // </editor-fold>

    @Override public void linkWith(TimelineItemDTOType discussionDTO, boolean andDisplay)
    {
        super.linkWith(discussionDTO, andDisplay);
        if (andDisplay)
        {
            displayMoreButton();
        }
    }

    //<editor-fold desc="Display Methods">
    protected void displayMoreButton()
    {
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setShowMore(
                    socialShareHelper.canTranslate(discussionDTO) || canShowStockMenu());
        }
    }

    public boolean canShowStockMenu()
    {
        return discussionDTO != null && discussionDTO.getFlavorSecurityForDisplay() != null;
    }

    @Override public void display()
    {
        super.display();
        displayUser();
        displayVendorLogo();
        displayWatchlistIndicator();
    }

    @Override public void onDetachedFromWindow()
    {
        picasso.cancelRequest(vendorImage);
        super.onDetachedFromWindow();
    }

    @Override protected String getUserDisplayName()
    {
        if (discussionDTO != null)
        {
            UserProfileCompactDTO userProfileCompactDTO = discussionDTO.getUser();
            if (userProfileCompactDTO != null)
            {
                return userProfileCompactDTO.displayName;
            }
        }
        return null;
    }

    @Override protected RequestCreator createUserPicassoRequest()
    {
        if (discussionDTO != null)
        {
            UserProfileCompactDTO userProfileCompactDTO = discussionDTO.getUser();
            if (userProfileCompactDTO != null && userProfileCompactDTO.picture != null)
            {
                return picasso.load(userProfileCompactDTO.picture);
            }
        }
        return super.createUserPicassoRequest();
    }

    protected void displayVendorLogo()
    {
        if (vendorImage != null)
        {
            if (discussionDTO != null)
            {
                SecurityMediaDTO firstMediaWithLogo = discussionDTO.getFlavorSecurityForDisplay();
                if (firstMediaWithLogo != null && firstMediaWithLogo.url != null)
                {
                    if (firstMediaWithLogo.securityId != 0)
                    {
                        vendorImage.setContentDescription(String.format("%s:%s", firstMediaWithLogo.exchange, firstMediaWithLogo.symbol));
                    }
                    picasso
                            .load(firstMediaWithLogo.url)
                            .transform(new WhiteToTransparentTransformation())
                            .transform(new ScaleKeepRatioTransformation(
                                    0,
                                    context.getResources().getDimensionPixelSize(R.dimen.timeline_vendor_logo_height),
                                    context.getResources().getDimensionPixelSize(R.dimen.timeline_vendor_logo_max_width),
                                    context.getResources().getDimensionPixelSize(R.dimen.timeline_vendor_logo_max_height)))
                            .into(vendorImage);
                    vendorImage.setVisibility(View.VISIBLE);
                }
                else
                {
                    vendorImage.setVisibility(View.GONE);
                }
            }
            else
            {
                vendorImage.setVisibility(View.GONE);
            }
        }
    }

    protected void displayWatchlistIndicator()
    {
        if (watchlistIndicator != null)
        {
            if (discussionDTO != null)
            {
                SecurityId securityIdForDisplay = discussionDTO.createFlavorSecurityIdForDisplay();
                if (securityIdForDisplay != null && watchlistPositionCache.getValue(securityIdForDisplay) != null)
                {
                    watchlistIndicator.setVisibility(View.VISIBLE);
                }
                else
                {
                    watchlistIndicator.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                watchlistIndicator.setVisibility(View.INVISIBLE);
            }
        }
    }

    //</editor-fold>

    @SuppressWarnings("UnusedDeclaration")
    @Optional @OnClick({R.id.discussion_user_picture, R.id.user_profile_name})
    protected void handleUserClicked(View view)
    {
        if (discussionDTO != null)
        {
            notifyUserClicked(discussionDTO.getSenderKey());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Optional @OnClick(R.id.timeline_vendor_picture)
    protected void handleSecurityClicked(View view)
    {
        notifySecurityClicked();
    }

    protected void notifySecurityClicked()
    {
        AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy instanceof OnMenuClickedListener)
        {
            ((OnMenuClickedListener) menuClickedListenerCopy).onSecurityClicked();
        }
    }

    public static interface OnMenuClickedListener extends AbstractDiscussionItemViewHolder.OnMenuClickedListener
    {
        void onSecurityClicked();
    }
}
