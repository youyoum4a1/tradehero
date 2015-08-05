package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.squareup.picasso.Picasso;
import com.tradehero.common.rx.PopupMenuItemClickOperator;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.DiscussionActionButtonsView;
import com.tradehero.th.fragments.discussion.TimelineItemViewHolder;
import com.tradehero.th.models.discussion.OpenNewStockAlertUserAction;
import com.tradehero.th.models.discussion.OpenWatchlistUserAction;
import com.tradehero.th.models.discussion.SecurityUserAction;
import com.tradehero.th.models.discussion.UpdateStockAlertUserAction;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.utils.SecurityUtils;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class TimelineItemViewLinear extends AbstractDiscussionCompactItemViewLinear
{
    @Inject protected Picasso picasso;
    @NonNull private final PublishSubject<UserDiscussionAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public TimelineItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull @Override protected TimelineItemViewHolder createViewHolder()
    {
        return new TimelineItemViewHolder(picasso);
    }

    @NonNull @Override public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return super.getUserActionObservable().mergeWith(userActionSubject);
    }

    //<editor-fold desc="Popup dialog">
    @NonNull @Override protected Observable<UserDiscussionAction> handleUserAction(
            final UserDiscussionAction userAction)
    {
        final TimelineItemViewHolder.DTO viewHolderDTO = (TimelineItemViewHolder.DTO) viewDTO.viewHolderDTO;
        if (userAction instanceof DiscussionActionButtonsView.MoreUserAction)
        {
            final SecurityId securityId;
            if (userAction.discussionDTO instanceof TimelineItemDTO)
            {
                securityId = ((TimelineItemDTO) userAction.discussionDTO).createFlavorSecurityIdForDisplay();
            }
            else
            {
                securityId = null;
            }
            return createActionPopupMenu(userAction.discussionDTO, securityId)
                    .flatMap(new Func1<PopupMenu, Observable<? extends MenuItem>>()
                    {
                        @Override public Observable<? extends MenuItem> call(PopupMenu popupMenu)
                        {
                            return Observable.create(new PopupMenuItemClickOperator(popupMenu, true));
                        }
                    })
                    .flatMap(new Func1<MenuItem, Observable<? extends UserDiscussionAction>>()
                    {
                        @Override public Observable<? extends UserDiscussionAction> call(MenuItem menuItem)
                        {
                            switch (menuItem.getItemId())
                            {
                                case R.id.timeline_action_add_to_watchlist:
                                    if (securityId != null)
                                    {
                                        return Observable.just(new OpenWatchlistUserAction(userAction.discussionDTO, securityId));
                                    }
                                    return Observable.just(userAction);

                                case R.id.timeline_action_add_alert:
                                    if (viewHolderDTO.stockAlertId != null)
                                    {
                                        return Observable.just(new UpdateStockAlertUserAction(
                                                userAction.discussionDTO,
                                                viewHolderDTO.stockAlertId));
                                    }
                                    else if (securityId != null)
                                    {
                                        return Observable.just(new OpenNewStockAlertUserAction(
                                                userAction.discussionDTO,
                                                securityId));
                                    }
                                    return Observable.just(userAction);

                                case R.id.timeline_popup_menu_buy_sell:
                                    if (securityId != null)
                                    {
                                        return Observable.just(new SecurityUserAction(userAction.discussionDTO, securityId));
                                    }
                                    return Observable.just(userAction);
                            }
                            return Observable.empty();
                        }
                    });
        }
        return super.handleUserAction(userAction);
    }

    @NonNull private Observable<PopupMenu> createActionPopupMenu(
            @NonNull AbstractDiscussionCompactDTO discussionCompactDTO,
            @Nullable SecurityId securityId)
    {
        final PopupMenu popupMenu = new PopupMenu(getContext(), findViewById(R.id.discussion_action_button_more));
        final MenuInflater menuInflater = popupMenu.getMenuInflater();

        if (securityId != null)
        {
            menuInflater.inflate(R.menu.timeline_stock_popup_menu, popupMenu.getMenu());
            if (SecurityUtils.isFX(securityId))
            {
                MenuItem watchlistItem = popupMenu.getMenu().findItem(R.id.timeline_action_add_to_watchlist);
                if (watchlistItem != null)
                {
                    watchlistItem.setVisible(false);
                }
                MenuItem alertItem = popupMenu.getMenu().findItem(R.id.timeline_action_add_alert);
                if (alertItem != null)
                {
                    alertItem.setVisible(false);
                }
            }
        }


        return socialShareHelper.canTranslate(discussionCompactDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Boolean, PopupMenu>()
                {
                    @Override public PopupMenu call(Boolean canTranslate)
                    {
                        if (canTranslate)
                        {
                            menuInflater.inflate(R.menu.timeline_comment_share_popup_menu, popupMenu.getMenu());
                        }
                        return popupMenu;
                    }
                });
    }

    //</editor-fold>

    public static class Requisite extends AbstractDiscussionCompactItemViewLinear.Requisite
    {
        public final boolean onWatchlist;
        @Nullable public final AlertId stockAlertId;

        public Requisite(
                @NonNull Resources resources,
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

    public static class DTO extends AbstractDiscussionCompactItemViewLinear.DTO
    {
        public DTO(@NonNull Requisite requisite)
        {
            super(requisite);
        }

        @NonNull @Override protected AbstractDiscussionCompactItemViewHolder.DTO createViewHolderDTO(
                @NonNull AbstractDiscussionCompactItemViewLinear.Requisite requisite)
        {
            return new TimelineItemViewHolder.DTO(
                    new TimelineItemViewHolder.Requisite(
                            requisite.resources,
                            requisite.prettyTime,
                            (TimelineItemDTO) requisite.discussionDTO,
                            requisite.canTranslate,
                            requisite.isAutoTranslate,
                            ((Requisite) requisite).onWatchlist,
                            ((Requisite) requisite).stockAlertId));
        }
    }
}
