package com.androidth.general.fragments.alert;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.alert.AlertDTO;
import com.androidth.general.api.quote.QuoteDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.fragments.security.SecurityCircleProgressBar;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedPercentage;
import com.androidth.general.utils.SecurityUtils;
import java.text.SimpleDateFormat;
import rx.Observable;

public class AlertSecurityProfile extends RelativeLayout
        implements DTOView<AlertSecurityProfile.DTO>
{
    @Bind(R.id.security_circle) SecurityCircleProgressBar securityCircleProgressBar;
    @Bind(R.id.stock_symbol) TextView stockSymbol;
    @Bind(R.id.company_name) TextView companyName;
    @Bind(R.id.target_price) TextView targetPrice;
    @Bind(R.id.target_price_label) TextView targetPriceLabel;
    @Bind(R.id.current_price) TextView currentPrice;
    @Bind(R.id.as_of_date) TextView asOfDate;
    @Bind(R.id.active_until) TextView activeUntil;

    //<editor-fold desc="Constructors">
    public AlertSecurityProfile(Context context)
    {
        super(context);
    }

    public AlertSecurityProfile(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AlertSecurityProfile(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            ButterKnife.bind(this);
        }
    }

    @NonNull public Observable<Boolean> start(long durationMilliSeconds)
    {
        return securityCircleProgressBar.start(durationMilliSeconds);
    }

    @Override public void display(@NonNull DTO dto)
    {
        if (targetPrice != null)
        {
            targetPrice.setText(dto.targetPrice);
        }
        if (targetPriceLabel != null)
        {
            targetPriceLabel.setText(dto.targetPriceLabel);
        }
        if (activeUntil != null)
        {
            activeUntil.setText(dto.activeUntil);
        }
        if (currentPrice != null)
        {
            currentPrice.setText(dto.currentPrice);
        }
        if (asOfDate != null)
        {
            asOfDate.setText(dto.asOfDate);
        }
        if (companyName != null)
        {
            companyName.setText(dto.companyName);
        }
        if (stockSymbol != null)
        {
            stockSymbol.setText(dto.stockSymbol);
        }
        if (securityCircleProgressBar != null)
        {
            securityCircleProgressBar.display(dto.alertDTO.security);
        }
    }

    public static class DTO
    {
        @NonNull public final AlertDTO alertDTO;
        @NonNull public final QuoteDTO quoteDTO;

        @NonNull public final String targetPrice;
        @NonNull public final String targetPriceLabel;
        @NonNull public final String activeUntil;
        @NonNull public final String currentPrice;
        @NonNull public final String asOfDate;
        @NonNull public final String companyName;
        @NonNull public final String stockSymbol;

        public DTO(
                @NonNull Resources resources,
                @NonNull AlertDTO alertDTO,
                @NonNull QuoteDTO quoteDTO)
        {
            this.alertDTO = alertDTO;
            this.quoteDTO = quoteDTO;
            SecurityCompactDTO securityCompactDTO = alertDTO.security;
            if (securityCompactDTO == null)
            {
                throw new IllegalArgumentException("alertDTO.security should not be null");
            }

            //<editor-fold desc="Target Price">
            if (alertDTO.priceMovement == null)
            {
                targetPrice = THSignedMoney.builder(alertDTO.targetPrice)
                        .withOutSign()
                        .currency(securityCompactDTO.currencyDisplay)
                        .build().toString();
                targetPriceLabel = resources.getString(R.string.stock_alert_target_price);
            }
            else
            {
                targetPrice = THSignedPercentage.builder(alertDTO.priceMovement * 100)
                        .build().toString();
                targetPriceLabel = resources.getString(R.string.stock_alert_percentage_movement);
            }
            //</editor-fold>

            //<editor-fold desc="Active Until">
            if (!alertDTO.active)
            {
                activeUntil = "-";
            }
            else if (alertDTO.activeUntilDate != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat(resources.getString(R.string.stock_alert_price_info_as_of_date_format));
                activeUntil = sdf.format(alertDTO.activeUntilDate);
            }
            else
            {
                activeUntil = "";
            }
            //</editor-fold>

            //<editor-fold desc="Current Price">
            final double price;
            if (quoteDTO.ask != null && quoteDTO.bid != null)
            {
                price = (quoteDTO.ask + quoteDTO.bid) / 2;
            }
            else if (quoteDTO.ask != null)
            {
                price = quoteDTO.ask;
            }
            else if (quoteDTO.bid != null)
            {
                price = quoteDTO.bid;
            }
            else if (securityCompactDTO.lastPrice != null)
            {
                price = securityCompactDTO.lastPrice;
            }
            else
            {
                price = 0;
            }
            String currency;
            if (quoteDTO.currencyDisplay != null)
            {
                currency = quoteDTO.currencyDisplay;
            }
            else  if (securityCompactDTO.currencyDisplay != null)
            {
                currency = securityCompactDTO.currencyDisplay;
            }
            else
            {
                currency = SecurityUtils.getDefaultCurrency();
            }
            currentPrice = THSignedMoney.builder(price)
                    .withOutSign()
                    .currency(currency)
                    .build().toString();
            //</editor-fold>

            //<editor-fold desc="As Of Date">
            SimpleDateFormat sdf = new SimpleDateFormat(resources.getString(R.string.stock_alert_price_info_as_of_date_format));
            if (quoteDTO.asOfUtc != null)
            {
                asOfDate = resources.getString(R.string.stock_alert_price_info_as_of_date, sdf.format(quoteDTO.asOfUtc));
            }
            else if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
            {
                asOfDate = resources.getString(R.string.stock_alert_price_info_as_of_date, sdf.format(securityCompactDTO.lastPriceDateAndTimeUtc));
            }
            else
            {
                asOfDate = "";
            }
            //</editor-fold>

            companyName = securityCompactDTO.name;

            stockSymbol = securityCompactDTO.getExchangeSymbol();
        }
    }
}
