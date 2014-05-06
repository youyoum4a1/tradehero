package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.localytics.android.LocalyticsSession;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallbackWeakList;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import java.text.DecimalFormat;
import javax.inject.Inject;
import timber.log.Timber;

public class WatchlistItemView extends FrameLayout implements DTOView<SecurityId>
{
    public static final String WATCHLIST_ITEM_DELETED = "watchlistItemDeleted";
    private static final String INTENT_KEY_DELETED_SECURITY_ID = WatchlistItemView.class.getName() + ".deletedSecurityId";

    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    @Inject Lazy<Picasso> picasso;
    @Inject CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;

    @InjectView(R.id.stock_logo) protected ImageView stockLogo;
    @InjectView(R.id.stock_symbol) protected TextView stockSymbol;
    @InjectView(R.id.company_name) protected TextView companyName;
    @InjectView(R.id.number_of_shares) protected TextView numberOfShares;
    @InjectView(R.id.position_percentage) protected TextView gainLossLabel;
    @InjectView(R.id.position_last_amount) protected TextView positionLastAmount;
    @InjectView(R.id.position_watchlist_delete) protected Button deleteButton;
    @InjectView(R.id.position_watchlist_more) protected Button moreButton;

    private WatchlistPositionDTO watchlistPositionDTO;
    private SecurityId securityId;

    private MiddleCallbackWeakList<WatchlistPositionDTO> middleCallbackWatchlistDeletes;

    private PopupMenu morePopupMenu;

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
    public WatchlistItemView(Context context)
    {
        super(context);
    }

    public WatchlistItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WatchlistItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        middleCallbackWatchlistDeletes = new MiddleCallbackWeakList<>();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (deleteButton != null)
        {
            deleteButton.setOnClickListener(createWatchlistItemDeleteClickHandler());
        }

        if (moreButton != null)
        {
            moreButton.setOnClickListener(createWatchlistItemMoreButtonClickHandler());
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

    private OnClickListener createWatchlistItemMoreButtonClickHandler()
    {
        return new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (morePopupMenu == null)
                {
                    morePopupMenu = createMoreOptionsPopupMenu();
                }
                morePopupMenu.show();

                localyticsSession.tagEvent(LocalyticsConstants.Watchlist_More_Tap);
            }
        };
    }

    private PopupMenu.OnMenuItemClickListener createMoreButtonPopupMenuClickHandler()
    {
        return new PopupMenu.OnMenuItemClickListener()
        {
            @Override public boolean onMenuItemClick(MenuItem item)
            {
                if (item == null)
                {
                    return false;
                }
                switch (item.getItemId())
                {
                    case R.id.watchlist_item_add_alert:
                        openAlertEditor();
                        break;
                    case R.id.watchlist_item_edit_in_watchlist:
                        openWatchlistEditor();
                        break;
                    case R.id.watchlist_item_new_discussion:
                        THToast.show(getContext().getString(R.string.watchlist_not_yet_implemented));
                        break;
                    case R.id.watchlist_item_view_graph:
                        openSecurityGraph();
                        break;
                    case R.id.watchlist_item_trade:
                        openSecurityProfile();
                        break;
                }
                return true;
            }
        };
    }

    private THCallback<WatchlistPositionDTO> createWatchlistDeletionCallback()
    {
        return new THCallback<WatchlistPositionDTO>()
        {
            // Make a copy here to sever links back to the origin class.
            final private Context contextCopy = WatchlistItemView.this.getContext();
            final private SecurityId securityIdCopy = securityId;
            final private WatchlistPositionDTO watchlistPositionDTOCopy = WatchlistItemView.this.watchlistPositionDTO;

            @Override protected void success(WatchlistPositionDTO watchlistPositionDTO, THResponse thResponse)
            {
                if (watchlistPositionDTO != null)
                {
                    Timber.d(contextCopy.getString(R.string.watchlist_item_deleted_successfully), watchlistPositionDTO.id);

                    Intent itemDeletionIntent = new Intent(WatchlistItemView.WATCHLIST_ITEM_DELETED);
                    putDeletedSecurityId(itemDeletionIntent, securityIdCopy);
                    LocalBroadcastManager.getInstance(contextCopy).sendBroadcast(itemDeletionIntent);
                }
            }

            @Override protected void failure(THException ex)
            {
                setEnabledSwipeButtons(true);
                if (watchlistPositionDTOCopy != null)
                {
                    Timber.e(getContext().getString(R.string.watchlist_item_deleted_failed), watchlistPositionDTOCopy.id, ex);
                }
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

        if (moreButton != null)
        {
            moreButton.setOnClickListener(null);
        }

        if (morePopupMenu != null)
        {
            morePopupMenu.setOnMenuItemClickListener(null);
        }

        middleCallbackWatchlistDeletes.detach();
    }

    @Override public void display(SecurityId securityId)
    {
        this.securityId = securityId;

        linkWith(securityId, true);
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        watchlistPositionDTO = watchlistPositionCache.get().get(securityId);

        if (watchlistPositionDTO == null)
        {
            return;
        }

        if (andDisplay)
        {
            displayStockLogo();

            displayExchangeSymbol();

            displayNumberOfShares();

            displayCompanyName();

            displayLastPrice();
        }
    }

    protected void setEnabledSwipeButtons(boolean enabled)
    {
        setEnabled(moreButton, enabled);
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
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (securityCompactDTO != null)
        {
            Double lastPrice = securityCompactDTO.lastPrice;
            Double watchlistPrice = watchlistPositionDTO.watchlistPrice;
            // pl percentage
            if (watchlistPrice != 0)
            {
                double gainLoss = (lastPrice - watchlistPrice);
                double pl = gainLoss * 100 / watchlistPrice;

                if (showInPercentage)
                {
                    gainLossLabel.setText(String.format(getContext().getString(R.string.watchlist_pl_percentage_format),
                            new DecimalFormat("##.##").format(pl)
                    ));
                }
                else
                {
                    gainLossLabel.setText(watchlistPositionDTO.securityDTO.currencyDisplay + " " +
                            new DecimalFormat("##.##").format(gainLoss));
                }

                gainLossLabel.setTextColor(getResources().getColor(ColorUtils.getColorResourceForNumber(pl)));
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

    private void displayLastPrice()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (securityCompactDTO != null)
        {
            Double lastPrice = securityCompactDTO.lastPrice;
            Double watchlistPrice = watchlistPositionDTO.watchlistPrice;
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

                if (pl > 0)
                {
                    gainLossLabel.setTextColor(getResources().getColor(R.color.number_up));
                }
                else if (pl < 0)
                {
                    gainLossLabel.setTextColor(getResources().getColor(R.color.number_down));
                }
                else
                {
                    gainLossLabel.setTextColor(getResources().getColor(R.color.text_gray_normal));
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

    private void displayNumberOfShares()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;
        if (numberOfShares != null)
        {
            if (securityCompactDTO != null)
            {
                Double watchListPrice = watchlistPositionDTO.watchlistPrice;
                numberOfShares.setText(formatNumberOfShares(watchlistPositionDTO.shares, securityCompactDTO.currencyDisplay, watchListPrice));
            }
            else
            {
                numberOfShares.setText("");
            }
        }
    }

    private Spanned formatNumberOfShares(Integer shares, String currencyDisplay, Double formattedPrice)
    {
        if (formattedPrice == null)
        {
            formattedPrice = 0.0;
        }
        if (shares == null)
        {
            shares = 0;
        }

        THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, formattedPrice, false, currencyDisplay);
        return Html.fromHtml(String.format(
                getContext().getString(R.string.watchlist_number_of_shares),
                shares, thSignedNumber.toString()
        ));
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
        middleCallbackWatchlistDeletes.add(watchlistServiceWrapper.get().deleteWatchlist(
                    watchlistPositionDTO,
                    createWatchlistDeletionCallback()));
    }

    private PopupMenu createMoreOptionsPopupMenu()
    {
        PopupMenu popupMenu = new PopupMenu(getContext(), moreButton);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.watchlist_more_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(createMoreButtonPopupMenuClickHandler());
        return popupMenu;
    }

    private void openAlertEditor()
    {
        Bundle args = new Bundle();
        //args.putBundle(AlertCreateFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
        args.putBundle(AlertCreateFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        getNavigator().pushFragment(AlertCreateFragment.class, args);
    }

    private void openSecurityProfile()
    {
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        getNavigator().pushFragment(BuySellFragment.class, args);
    }

    private void openSecurityGraph()
    {
        Bundle args = new Bundle();
        if (securityId != null)
        {
            args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        }
        getNavigator().pushFragment(StockInfoFragment.class, args);
    }

    private void openWatchlistEditor()
    {
        Bundle args = new Bundle();
        if (securityId != null)
        {
            args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            args.putString(WatchlistEditFragment.BUNDLE_KEY_TITLE, getContext().getString(R.string.watchlist_edit_title));
        }
        getNavigator().pushFragment(WatchlistEditFragment.class, args, Navigator.PUSH_UP_FROM_BOTTOM);
    }

    private Navigator getNavigator()
    {
        return ((NavigatorActivity) getContext()).getNavigator();
    }
}
