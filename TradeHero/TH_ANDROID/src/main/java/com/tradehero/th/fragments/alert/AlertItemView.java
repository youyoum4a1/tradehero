package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import java.util.Date;
import javax.inject.Inject;


public class AlertItemView extends RelativeLayout
        implements DTOView<AlertId>
{
    @InjectView(R.id.logo) ImageView stockLogo;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.alert_description) TextView alertDescription;
    @InjectView(R.id.alert_status) TextView alertStatus;
    @InjectView(R.id.buy_stock) ImageView buyStock;
    @InjectView(R.id.sell_stock) ImageView sellStock;

    @Inject protected Lazy<AlertCompactCache> alertCompactCache;
    @Inject protected Lazy<Picasso> picasso;

    private AlertCompactDTO alertCompactDTO;

    //<editor-fold desc="Constructors">
    public AlertItemView(Context context)
    {
        super(context);
    }

    public AlertItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AlertItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        buyStock.setOnClickListener(buyAndSellStockClickListener);
        sellStock.setOnClickListener(buyAndSellStockClickListener);
    }

    @Override protected void onDetachedFromWindow()
    {
        buyStock.setOnClickListener(null);
        sellStock.setOnClickListener(null);

        super.onDetachedFromWindow();
    }

    @Override public void display(AlertId alertId)
    {
        if (alertId != null)
        {
            display(alertCompactCache.get().get(alertId));
        }
    }

    private void display(AlertCompactDTO alertCompactDTO)
    {
        this.alertCompactDTO = alertCompactDTO;
        if (alertCompactDTO != null)
        {
            displayStockSymbol();

            displayStockLogo();

            displayAlertDescription();

            displayAlertStatus();
            
            displayTrigger();

            updateActionButtonsVisibility();
        }
    }

    private void displayAlertDescription()
    {
        if (alertCompactDTO.priceMovement != null)
        {
            alertDescription.setText(getPriceMovementDescription(alertCompactDTO.priceMovement * 100));
        }
        else if (alertCompactDTO.upOrDown) // up
        {
            alertDescription.setText(getPriceRaiseDescription(alertCompactDTO.targetPrice));
        }
        else
        {
            alertDescription.setText(getPriceFallDescription(alertCompactDTO.targetPrice));
        }
    }

    private Spanned getPriceFallDescription(double targetPrice)
    {
        THSignedNumber thPriceRaise = new THSignedNumber(THSignedNumber.TYPE_MONEY, targetPrice, false);
        return Html.fromHtml(String.format(
                getContext().getString(R.string.stock_alert_when_price_falls),
                thPriceRaise.toString()
        ));
    }

    private Spanned getPriceRaiseDescription(double targetPrice)
    {
        THSignedNumber thPriceRaise = new THSignedNumber(THSignedNumber.TYPE_MONEY, targetPrice, false);
        return Html.fromHtml(String.format(
                getContext().getString(R.string.stock_alert_when_price_raises),
                thPriceRaise.toString()
        ));
    }

    private Spanned getPriceMovementDescription(double percentage)
    {
        THSignedNumber thPercentageChange = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, percentage);
        return Html.fromHtml(String.format(
                getContext().getString(R.string.stock_alert_when_price_move),
                thPercentageChange.toString()
        ));
    }

    private void updateActionButtonsVisibility()
    {

    }

    private void displayTrigger()
    {
        if (alertCompactDTO.activeUntilDate != null)
        {
            //alertDescription.setText(getFormattedTriggerDescription(alertCompactDTO.activeUntilDate));
        }
    }

    private Spanned getFormattedTriggerDescription(Date activeUntilDate)
    {
        return null;
    }

    private void displayAlertStatus()
    {
        if (alertCompactDTO.active)
        {
            alertStatus.setText(getFormattedActiveUntilString(alertCompactDTO.activeUntilDate));
            alertStatus.setTextColor(getResources().getColor(R.color.black));
        }
        else
        {
            alertStatus.setText(R.string.stock_alert_inactive);
            alertStatus.setTextColor(getResources().getColor(R.color.text_gray_normal));
        }
    }

    private Spanned getFormattedActiveUntilString(Date activeUntilDate)
    {
        return Html.fromHtml(String.format(getContext().getString(R.string.stock_alert_active_until_date), DateUtils.getFormattedDate(activeUntilDate)));
    }

    private void displayStockSymbol()
    {
        if (alertCompactDTO.security != null)
        {
            stockSymbol.setText(alertCompactDTO.security.getExchangeSymbol());
        }
    }

    private void displayStockLogo()
    {
        SecurityCompactDTO securityCompactDTO = alertCompactDTO.security;
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

    private OnClickListener buyAndSellStockClickListener = new OnClickListener()
    {
        @Override public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.buy_stock:
                case R.id.sell_stock:
                    handleBuyAndSellButtonClick();
                    break;
            }
        }
    };

    private void handleBuyAndSellButtonClick()
    {
        if (alertCompactDTO != null && alertCompactDTO.security != null &&
                alertCompactDTO.security.getSecurityId() != null)
        {
            Bundle args = new Bundle();
            args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE,
                    alertCompactDTO.security.getSecurityId().getArgs());
            getNavigator().pushFragment(BuySellFragment.class, args);
        }
    }

    private Navigator getNavigator()
    {
        return ((NavigatorActivity) getContext()).getNavigator();
    }
}
