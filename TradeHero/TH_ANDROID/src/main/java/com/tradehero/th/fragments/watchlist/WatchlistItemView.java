package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import dagger.Lazy;
import java.text.DecimalFormat;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class WatchlistItemView extends FrameLayout implements DTOView<WatchlistPositionDTO>
{
    public static final String WATCHLIST_ITEM_DELETED = "watchlistItemDeleted";
    private static final String INTENT_KEY_DELETED_SECURITY_ID = WatchlistItemView.class.getName() + ".deletedSecurityId";

    @Inject Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    @Inject Lazy<Picasso> picasso;
    @Inject Analytics analytics;
    @Inject DashboardNavigator navigator;

    @InjectView(R.id.gain_indicator) protected ImageView gainIndicator;
    @InjectView(R.id.stock_logo) protected ImageView stockLogo;
    @InjectView(R.id.stock_symbol) protected TextView stockSymbol;
    @InjectView(R.id.company_name) protected TextView companyName;
    @InjectView(R.id.position_percentage) protected TextView gainLossLabel;
    @InjectView(R.id.position_last_amount) protected TextView positionLastAmount;
    @InjectView(R.id.position_watchlist_delete) protected Button deleteButton;

    @Nullable private WatchlistPositionDTO watchlistPositionDTO;
    @NonNull private SubscriptionList subscriptions;

    public static void putDeletedSecurityId(Intent intent, SecurityId securityId)
    {
        intent.putExtra(INTENT_KEY_DELETED_SECURITY_ID, securityId.getArgs());
    }

    public static SecurityId getDeletedSecurityId(Intent intent)
    {
        SecurityId deleted = null;
        if (intent != null && intent.hasExtra(INTENT_KEY_DELETED_SECURITY_ID))
        {
            deleted = new SecurityId(intent.getBundleExtra(INTENT_KEY_DELETED_SECURITY_ID));
        }
        return deleted;
    }

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
        ButterKnife.inject(this);
        subscriptions = new SubscriptionList();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (deleteButton != null)
        {
            deleteButton.setOnClickListener(createWatchlistItemDeleteClickHandler());
        }
    }

    private OnClickListener createWatchlistItemDeleteClickHandler()
    {
        return new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                setEnabledSwipeButtons(false);
                deleteSelf();
            }
        };
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        if (deleteButton != null)
        {
            deleteButton.setOnClickListener(null);
        }

        subscriptions.unsubscribe();
    }

    @Override public void display(WatchlistPositionDTO watchlistPosition)
    {
        linkWith(watchlistPosition, true);
    }

    private void linkWith(WatchlistPositionDTO watchlistPosition, boolean andDisplay)
    {
        this.watchlistPositionDTO = watchlistPosition;

        if (watchlistPositionDTO == null)
        {
            return;
        }

        if (andDisplay)
        {
            displayStockLogo();
            displayExchangeSymbol();
            displayCompanyName();
            displayLastPrice();
        }
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
        if (gainLossLabel != null)
        {
            THSignedNumber value = getGainLoss(showInPercentage);
            if (value != null)
            {
                value.into(gainLossLabel);
            }
            else
            {
                gainLossLabel.setText("");
            }
        }
    }

    @Nullable protected THSignedNumber getGainLoss(boolean showInPercentage)
    {
        if (watchlistPositionDTO == null)
        {
            return null;
        }
        Double amountInvestedRefCcy = watchlistPositionDTO.getInvestedRefCcy();
        Double currentValueRefCcy = watchlistPositionDTO.getCurrentValueRefCcy();

        if (amountInvestedRefCcy == null || currentValueRefCcy == null)
        {
            return null;
        }
        if (showInPercentage)
        {
            if (currentValueRefCcy == 0)
            {
                return null;
            }
            return THSignedPercentage.builder(100 * ((currentValueRefCcy - amountInvestedRefCcy) / currentValueRefCcy))
                    .signTypePlusMinusAlways()
                    .build();
        }
        return THSignedMoney.builder(currentValueRefCcy - amountInvestedRefCcy)
                .currency(watchlistPositionDTO.getNiceCurrency())
                .withDefaultColor()
                .build();
    }

    private void displayLastPrice()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (securityCompactDTO != null)
        {
            Double lastPrice = securityCompactDTO.lastPrice;
            Double watchlistPrice = watchlistPositionDTO.watchlistPriceRefCcy;
            if (lastPrice == null)
            {
                lastPrice = 0.0;
            }
            // last price
            positionLastAmount.setText(formatLastPrice(securityCompactDTO.currencyDisplay, lastPrice));

            // pl percentage
            if (watchlistPrice != 0)
            {
                double pl = (lastPrice - watchlistPrice) * 100 / watchlistPrice;
                gainLossLabel.setText(String.format(getContext().getString(R.string.watchlist_pl_percentage_format),
                        new DecimalFormat("##.##").format(pl)
                ));

                gainLossLabel.setTextColor(Color.WHITE);
                if (pl > 0)
                {
                    gainLossLabel.setBackgroundResource(R.drawable.round_label_up);
                    gainIndicator.setVisibility(View.VISIBLE);
                    gainIndicator.setImageResource(R.drawable.indicator_green);
                }
                else if (pl < 0)
                {
                    gainLossLabel.setBackgroundResource(R.drawable.round_label_down);
                    gainIndicator.setVisibility(View.VISIBLE);
                    gainIndicator.setImageResource(R.drawable.indicator_red);
                }
                else
                {
                    gainLossLabel.setBackgroundColor(getResources().getColor(R.color.text_gray_normal));
                    gainIndicator.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                gainLossLabel.setText("");
            }
        }
        else
        {
            gainLossLabel.setText("");
        }
    }

    private Spanned formatLastPrice(String currencyDisplay, Double lastPrice)
    {
        return Html.fromHtml(String.format(getContext().getString(R.string.watchlist_last_price_format),
                currencyDisplay,
                new DecimalFormat("#.##").format(lastPrice)));
    }

    private void displayCompanyName()
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

    private void displayStockLogo()
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

    private void displayExchangeSymbol()
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
                            this::onWatchlistDeleteReceived,
                            this::onWatchlistDeleteError));
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

    //TODO: might be used later
    private void openSecurityProfile()
    {
        Bundle args = new Bundle();
        BuySellStockFragment.putSecurityId(args, watchlistPositionDTO.securityDTO.getSecurityId());
        navigator.pushFragment(BuySellStockFragment.class, args);
    }
}
