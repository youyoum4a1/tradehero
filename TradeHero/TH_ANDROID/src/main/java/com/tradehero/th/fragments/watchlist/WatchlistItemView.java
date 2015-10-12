package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.trade.AbstractBuySellFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import java.text.DecimalFormat;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class WatchlistItemView extends FrameLayout implements DTOView<WatchlistPositionDTO>
{
    public static final String WATCHLIST_ITEM_DELETED = "watchlistItemDeleted";
    private static final String INTENT_KEY_DELETED_SECURITY_ID = WatchlistItemView.class.getName() + ".deletedSecurityId";

    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    @Inject Lazy<Picasso> picasso;
    @Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject CurrentUserId currentUserId;

    @Bind(R.id.stock_logo) protected ImageView stockLogo;
    @Bind(R.id.stock_symbol) protected TextView stockSymbol;
    @Bind(R.id.company_name) protected TextView companyName;
    @Bind(R.id.position_percentage) protected TextView gainLossLabel;
    @Bind(R.id.position_last_amount) protected TextView positionLastAmount;
    @Bind(R.id.position_watchlist_delete) protected Button deleteButton;
    @Bind(R.id.position_watchlist_more) protected Button moreButton;

    @Nullable private WatchlistPositionDTO watchlistPositionDTO;
    private SubscriptionList subscriptions;

    //<editor-fold desc="Argument Passing">
    public static void putDeletedSecurityId(@NonNull Intent intent, @NonNull SecurityId securityId)
    {
        intent.putExtra(INTENT_KEY_DELETED_SECURITY_ID, securityId.getArgs());
    }

    @Nullable public static SecurityId getDeletedSecurityId(@NonNull Intent intent)
    {
        SecurityId deleted = null;
        if (intent.hasExtra(INTENT_KEY_DELETED_SECURITY_ID))
        {
            deleted = new SecurityId(intent.getBundleExtra(INTENT_KEY_DELETED_SECURITY_ID));
        }
        return deleted;
    }
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public WatchlistItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            ButterKnife.bind(this);
        }
        subscriptions = new SubscriptionList();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            ButterKnife.bind(this);
        }
        subscriptions.add(ViewObservable.clicks(moreButton)
                .flatMap(new Func1<OnClickEvent, Observable<AbstractBuySellFragment.Requisite>>()
                {
                    @Override public Observable<AbstractBuySellFragment.Requisite> call(OnClickEvent onClickEvent)
                    {
                        return portfolioCompactListCache.getOne(currentUserId.toUserBaseKey())
                                .map(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, AbstractBuySellFragment.Requisite>()
                                {
                                    @Override
                                    public AbstractBuySellFragment.Requisite call(@NonNull Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                                    {
                                        return new AbstractBuySellFragment.Requisite(
                                                watchlistPositionDTO.securityDTO.getSecurityId(), // TODO better
                                                pair.second.getDefaultPortfolio().getOwnedPortfolioId(),
                                                0);
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<AbstractBuySellFragment.Requisite>()
                        {
                            @Override public void call(AbstractBuySellFragment.Requisite requisite)
                            {
                                // TODO use Rx
                                Bundle args = new Bundle();
                                BuySellStockFragment.putRequisite(args, requisite);
                                navigator.get().pushFragment(BuySellStockFragment.class, args);
                                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_More_Tap));
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to clicks")));
    }

    @Override protected void onDetachedFromWindow()
    {
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.position_watchlist_delete)
    public void onDeleteButtonClicked(View v)
    {
        setEnabledSwipeButtons(false);
        deleteSelf();
    }

    @Override public void display(@NonNull WatchlistPositionDTO watchlistPosition)
    {
        this.watchlistPositionDTO = watchlistPosition;
        displayStockLogo(watchlistPosition);
        displayExchangeSymbol(watchlistPosition);
        displayCompanyName(watchlistPosition);
        displayLastPrice(watchlistPosition);
    }

    protected void setEnabledSwipeButtons(boolean enabled)
    {
        setEnabled(deleteButton, enabled);
    }

    protected void setEnabled(View button, boolean enabled)
    {
        if (button != null)
        {
            button.setEnabled(enabled);
        }
    }

    public void displayPlPercentage(boolean showInPercentage)
    {
        if (gainLossLabel == null)
        {
            return;
        }
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;
        if (securityCompactDTO == null)
        {
            gainLossLabel.setText("0");
            return;
        }
        double roi = 0;
        if (securityCompactDTO.risePercent != null)
        {
            roi = securityCompactDTO.risePercent;
        }

        THSignedPercentage
                .builder(roi * 100)
                .relevantDigitCount(3)
                .signTypePlusMinusAlways()
                .build()
                .into(gainLossLabel);
        if (roi > 0)
        {
            gainLossLabel.setBackgroundResource(R.drawable.round_label_up);
            gainLossLabel.setTextColor(getResources().getColor(R.color.text_primary_inverse));
        }
        else if (roi < 0)
        {
            gainLossLabel.setBackgroundResource(R.drawable.round_label_down);
            gainLossLabel.setTextColor(getResources().getColor(R.color.text_primary_inverse));
        }
        else
        {
            gainLossLabel.setTextColor(getResources().getColor(R.color.text_primary));
            gainLossLabel.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }

    private void displayLastPrice(@NonNull WatchlistPositionDTO watchlistPositionDTO)
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (securityCompactDTO != null)
        {
            Double lastPrice = securityCompactDTO.lastPrice;
            if (lastPrice == null)
            {
                lastPrice = 0.0;
            }
            // last price
            positionLastAmount.setText(formatLastPrice(securityCompactDTO.currencyDisplay, lastPrice));
        }
    }

    private Spanned formatLastPrice(String currencyDisplay, Double lastPrice)
    {
        return Html.fromHtml(String.format(getContext().getString(R.string.watchlist_last_price_format),
                currencyDisplay,
                new DecimalFormat("#.##").format(lastPrice)));
    }

    private void displayCompanyName(@NonNull WatchlistPositionDTO watchlistPositionDTO)
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;
        if (companyName != null)
        {
            if (securityCompactDTO != null)
            {
                companyName.setText(securityCompactDTO.name);
            }
            else
            {
                companyName.setText("");
            }
        }
    }

    private void displayStockLogo(@NonNull WatchlistPositionDTO watchlistPositionDTO)
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (stockLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
            {
                picasso.get()
                        .load(securityCompactDTO.imageBlobUrl)
                        .transform(new WhiteToTransparentTransformation())
                        .into(stockLogo);
            }
            else if (securityCompactDTO != null)
            {
                picasso.get()
                        .load(securityCompactDTO.getExchangeLogoId())
                        .into(stockLogo);
            }
            else
            {
                stockLogo.setImageResource(R.drawable.default_image);
            }
        }
    }

    private void displayExchangeSymbol(@NonNull WatchlistPositionDTO watchlistPositionDTO)
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (stockSymbol != null)
        {
            if (securityCompactDTO != null)
            {
                stockSymbol.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                stockSymbol.setText("");
            }
        }
    }

    private void deleteSelf()
    {
        // not to show dialog but request deletion in background
        if (watchlistPositionDTO != null)
        {
            subscriptions.add(watchlistServiceWrapper.get().deleteWatchlistRx(
                    watchlistPositionDTO.getPositionCompactId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<WatchlistPositionDTO>()
                            {
                                @Override public void call(WatchlistPositionDTO watchlistPositionDTO1)
                                {
                                    WatchlistItemView.this.onWatchlistDeleteReceived(watchlistPositionDTO1);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    WatchlistItemView.this.onWatchlistDeleteError(error);
                                }
                            }));
        }
    }

    protected void onWatchlistDeleteReceived(WatchlistPositionDTO args)
    {
        Context contextCopy = getContext();
        if (contextCopy != null && watchlistPositionDTO != null)
        {
            Timber.d(contextCopy.getString(R.string.watchlist_item_deleted_successfully), watchlistPositionDTO.id);

            Intent itemDeletionIntent = new Intent(WatchlistItemView.WATCHLIST_ITEM_DELETED);
            putDeletedSecurityId(itemDeletionIntent, watchlistPositionDTO.securityDTO.getSecurityId());
            LocalBroadcastManager.getInstance(contextCopy).sendBroadcast(itemDeletionIntent);
        }
    }

    protected void onWatchlistDeleteError(Throwable e)
    {
        Context contextCopy = getContext();
        setEnabledSwipeButtons(true);
        if (contextCopy != null && watchlistPositionDTO != null)
        {
            Timber.e(contextCopy.getString(R.string.watchlist_item_deleted_failed), watchlistPositionDTO.id, e);
        }
    }
}
