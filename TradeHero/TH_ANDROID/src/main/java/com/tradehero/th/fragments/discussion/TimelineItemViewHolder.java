package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import rx.functions.Action1;

public class TimelineItemViewHolder<TimelineItemDTOType extends TimelineItemDTO>
    extends AbstractDiscussionItemViewHolder<TimelineItemDTOType>
{
    @InjectView(R.id.timeline_vendor_picture) ImageView vendorImage;
    @InjectView(R.id.in_watchlist_indicator) ImageView watchlistIndicator;

    @Inject WatchlistPositionCacheRx watchlistPositionCache;

    //<editor-fold desc="Constructors">
    public TimelineItemViewHolder(@NonNull Context context)
    {
        super(context);
    }
    // </editor-fold>

    @Override public void linkWith(TimelineItemDTOType discussionDTO)
    {
        super.linkWith(discussionDTO);
        displayMoreButton();
    }

    //<editor-fold desc="Display Methods">
    protected void displayMoreButton()
    {
        if (discussionActionButtonsView != null && discussionDTO != null)
        {
            subscriptions.add(socialShareHelper.canTranslate(discussionDTO)
            .subscribe(new Action1<Boolean>()
            {
                @Override public void call(Boolean canTranslate)
                {
                    discussionActionButtonsView.setShowMore(canTranslate || canShowStockMenu());
                }
            }));
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

    @Override @Nullable protected String getUserDisplayName()
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

    @NonNull @Override protected RequestCreator createUserPicassoRequest()
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
                if (securityIdForDisplay != null && watchlistPositionCache.getCachedValue(securityIdForDisplay) != null)
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
    @Optional @OnClick(R.id.timeline_vendor_picture)
    protected void handleSecurityClicked(View view)
    {
        userActionBehavior.onNext(new SecurityUserAction());
    }

    public static class SecurityUserAction implements DiscussionActionButtonsView.UserAction
    {
    }
}
