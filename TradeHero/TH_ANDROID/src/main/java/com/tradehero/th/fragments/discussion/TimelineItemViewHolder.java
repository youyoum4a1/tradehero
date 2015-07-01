package com.tradehero.th.fragments.discussion;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.OnClick;
import android.support.annotation.Nullable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.ScaleKeepRatioTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.models.discussion.SecurityUserAction;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.functions.Func1;

public class TimelineItemViewHolder
        extends AbstractDiscussionItemViewHolder
{
    @Bind(R.id.timeline_vendor_picture) ImageView vendorImage;
    @Bind(R.id.in_watchlist_indicator) ImageView watchlistIndicator;

    //<editor-fold desc="Constructors">
    public TimelineItemViewHolder()
    {
        super();
    }
    // </editor-fold>

    @NonNull @Override public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return super.getUserActionObservable()
                .map(new Func1<UserDiscussionAction, UserDiscussionAction>()
                {
                    @Override public UserDiscussionAction call(UserDiscussionAction userDiscussionAction)
                    {
                        if (userDiscussionAction instanceof DiscussionActionButtonsView.CommentUserAction)
                        {
                            return new TimelineCommentUserAction(userDiscussionAction.discussionDTO);
                        }
                        return userDiscussionAction;
                    }
                });
    }

    @Override public void display(@NonNull AbstractDiscussionCompactItemViewHolder.DTO parentViewDto)
    {
        super.display(parentViewDto);
        DTO dto = (DTO) parentViewDto;
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setShowMore(dto.canShowMore);
        }
        if (vendorImage != null)
        {
            vendorImage.setContentDescription(dto.vendorImageDescription);
            vendorImage.setVisibility(dto.vendorImageVisibility);
            ImageLoader.getInstance().displayImage(dto.vendorImageUrl, vendorImage);
        }
        if (watchlistIndicator != null)
        {
            watchlistIndicator.setVisibility(dto.onWatchlist ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @NonNull @Override protected String getUserAvatarURL()
    {
        if (viewDTO != null && ((DTO) viewDTO).pictureUrl != null)
        {
            return ((DTO) viewDTO).pictureUrl;
        }
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Nullable @OnClick(R.id.timeline_vendor_picture)
    protected void handleSecurityClicked(View view)
    {
        if (viewDTO != null)
        {
            SecurityId vendorSecurityId = ((DTO) viewDTO).vendorSecurityId;
            if (vendorSecurityId != null)
            {
                userActionSubject.onNext(new SecurityUserAction(viewDTO.discussionDTO, vendorSecurityId));
            }
        }
    }

    public static class Requisite
            extends AbstractDiscussionItemViewHolder.Requisite
    {
        public final boolean onWatchlist;
        @Nullable public final AlertId stockAlertId;

        public Requisite(@NonNull Resources resources,
                @NonNull PrettyTime prettyTime,
                @NonNull TimelineItemDTO discussionDTO,
                boolean canTranslate,
                boolean isAutoTranslate,
                boolean onWatchlist,
                @Nullable AlertId stockAlertId)
        {
            super(resources, prettyTime, discussionDTO, canTranslate, isAutoTranslate);
            this.onWatchlist = onWatchlist;
            this.stockAlertId = stockAlertId;
        }
    }

    public static class DTO extends AbstractDiscussionItemViewHolder.DTO
    {
        public final boolean canShowMore;
        @Nullable public final String pictureUrl;
        @NonNull public final String vendorImageDescription;
        @ViewVisibilityValue public final int vendorImageVisibility;
        @Nullable public final String vendorImageUrl;
        @NonNull public final AbstractSequentialTransformation vendorImageTransformation;
        protected boolean onWatchlist;
        @Nullable public final AlertId stockAlertId;
        @Nullable public final SecurityId vendorSecurityId;

        public DTO(Requisite requisite)
        {
            super(requisite);

            //<editor-fold desc="Picture URL">
            this.canShowMore = requisite.canTranslate || ((TimelineItemDTO) requisite.discussionDTO).getFlavorSecurityForDisplay() != null;
            UserProfileCompactDTO userProfileCompactDTO = ((TimelineItemDTO) requisite.discussionDTO).getUser();
            if (userProfileCompactDTO != null && userProfileCompactDTO.picture != null)
            {
                pictureUrl = userProfileCompactDTO.picture;
            }
            else
            {
                pictureUrl = null;
            }
            //</editor-fold>

            //<editor-fold desc="Vendor image">
            vendorImageTransformation = new AbstractSequentialTransformation()
            {
                @Override public String key()
                {
                    return "TimelineItemViewHolderTransformation";
                }
            };
            SecurityMediaDTO firstMediaWithLogo = ((TimelineItemDTO) requisite.discussionDTO).getFlavorSecurityForDisplay();
            if (firstMediaWithLogo != null)
            {
                vendorSecurityId = new SecurityId(firstMediaWithLogo.exchange, firstMediaWithLogo.symbol);
            }
            else
            {
                vendorSecurityId = null;
            }
            if (firstMediaWithLogo != null && firstMediaWithLogo.url != null)
            {
                if (firstMediaWithLogo.securityId != 0)
                {
                    vendorImageDescription = String.format("%s:%s", firstMediaWithLogo.exchange, firstMediaWithLogo.symbol);
                }
                else
                {
                    vendorImageDescription = requisite.resources.getString(R.string.na);
                }
                vendorImageUrl = firstMediaWithLogo.url;

                vendorImageTransformation.add(new WhiteToTransparentTransformation());
                vendorImageTransformation.add(new ScaleKeepRatioTransformation(
                        0,
                        requisite.resources.getDimensionPixelSize(R.dimen.timeline_vendor_logo_height),
                        requisite.resources.getDimensionPixelSize(R.dimen.timeline_vendor_logo_max_width),
                        requisite.resources.getDimensionPixelSize(R.dimen.timeline_vendor_logo_max_height)));
                vendorImageVisibility = View.VISIBLE;
            }
            else
            {
                vendorImageDescription = requisite.resources.getString(R.string.na);
                vendorImageUrl = null;
                vendorImageVisibility = View.GONE;
            }
            //</editor-fold>

            this.onWatchlist = requisite.onWatchlist;
            this.stockAlertId = requisite.stockAlertId;
        }

        @NonNull @Override protected String createUserDisplayName()
        {
            UserProfileCompactDTO userProfileCompactDTO = ((TimelineItemDTO) discussionDTO).getUser();
            if (userProfileCompactDTO != null)
            {
                return userProfileCompactDTO.displayName;
            }
            return super.createUserDisplayName();
        }

        public boolean isOnWatchlist()
        {
            return onWatchlist;
        }

        public void setOnWatchlist(boolean isOnWatchlist)
        {
            this.onWatchlist = isOnWatchlist;
        }
    }

    public static class TimelineCommentUserAction extends DiscussionActionButtonsView.CommentUserAction
    {
        public TimelineCommentUserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
        {
            super(discussionDTO);
        }
    }
}
