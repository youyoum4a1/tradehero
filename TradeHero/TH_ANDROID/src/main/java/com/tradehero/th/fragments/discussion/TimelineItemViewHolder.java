package com.tradehero.th.fragments.discussion;

import android.view.View;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.RequestCreator;
import com.tradehero.common.graphics.ScaleKeepRatioTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import javax.inject.Inject;

public class TimelineItemViewHolder<TimelineItemDTOType extends TimelineItemDTO>
    extends AbstractDiscussionItemViewHolder<TimelineItemDTOType>
{
    @InjectView(R.id.timeline_vendor_picture) ImageView vendorImage;
    @InjectView(R.id.in_watchlist_indicator) ImageView watchlistIndicator;

    @Inject WatchlistPositionCache watchlistPositionCache;

    //<editor-fold desc="Constructors">
    public TimelineItemViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean isAutoTranslate()
    {
        return true;
    }

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
        if (discussionDTO == null)
        {
            vendorImage.setVisibility(View.GONE);
        }
        else
        {
            SecurityMediaDTO firstMediaWithLogo = discussionDTO.getFlavorSecurityForDisplay();
            if (firstMediaWithLogo != null && firstMediaWithLogo.url != null)
            {
                if (vendorImage != null)
                {
                    if (firstMediaWithLogo.securityId != 0)
                    {
                        vendorImage.setContentDescription(String.format("%s:%s", firstMediaWithLogo.exchange, firstMediaWithLogo.symbol));

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
        if (watchlistIndicator == null)
        {
            return;
        }

        if (discussionDTO != null && watchlistPositionCache.get(discussionDTO.createFlavorSecurityIdForDisplay()) != null)
        {
            watchlistIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            watchlistIndicator.setVisibility(View.INVISIBLE);
        }
    }

    //</editor-fold>

    @Optional @OnClick({R.id.discussion_user_picture, R.id.user_profile_name})
    protected void handleUserClicked(View view)
    {
        if (discussionDTO != null)
        {
            notifyUserClicked(discussionDTO.getSenderKey());
        }
    }

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
