package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

public class AlertSecurityProfile extends RelativeLayout
    implements DTOView<AlertSecurityProfile.DTO>
{
    @Inject Picasso picasso;

    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.target_price) TextView targetPrice;
    @InjectView(R.id.target_price_label) TextView targetPriceLabel;
    @InjectView(R.id.current_price) TextView currentPrice;
    @InjectView(R.id.as_of_date) TextView asOfDate;
    @InjectView(R.id.active_until) TextView activeUntil;
    @InjectView(R.id.alert_toggle) Switch alertToggle;

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
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
        alertToggle.setVisibility(View.GONE);
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
        if (stockLogo != null)
        {
            RequestCreator request;
            if (dto.stockLogoUrl != null)
            {
                request = picasso.load(dto.stockLogoUrl)
                        .transform(new WhiteToTransparentTransformation());
            }
            else
            {
                request = picasso.load(dto.stockLogoRes);
            }
            request.into(stockLogo);
        }
    }

    public static class DTO
    {
        @NonNull public final AlertDTO alertDTO;

        @NonNull public final String targetPrice;
        @NonNull public final String targetPriceLabel;
        @NonNull public final String activeUntil;
        @NonNull public final String currentPrice;
        @NonNull public final String asOfDate;
        @NonNull public final String companyName;
        @NonNull public final String stockSymbol;
        @Nullable public final String stockLogoUrl;
        public final int stockLogoRes;

        public DTO(
                @NonNull Resources resources,
                @NonNull AlertDTO alertDTO)
        {
            this.alertDTO = alertDTO;
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
            currentPrice = THSignedMoney.builder(securityCompactDTO.lastPrice)
                    .withOutSign()
                    .currency(securityCompactDTO.currencyDisplay)
                    .build().toString();
            //</editor-fold>

            //<editor-fold desc="As Of Date">
            if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat(resources.getString(R.string.stock_alert_price_info_as_of_date_format));
                asOfDate = resources.getString(R.string.stock_alert_price_info_as_of_date, sdf.format(securityCompactDTO.lastPriceDateAndTimeUtc));
            }
            else
            {
                asOfDate = "";
            }
            //</editor-fold>

            companyName = securityCompactDTO.name;

            stockSymbol = securityCompactDTO.getExchangeSymbol();

            stockLogoUrl = securityCompactDTO.imageBlobUrl;
            stockLogoRes = securityCompactDTO.getExchangeLogoId();
        }
    }
}
