package com.androidth.general.fragments.alert;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.androidth.general.common.graphics.WhiteToTransparentTransformation;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedPercentage;
import com.androidth.general.utils.DateUtils;
import com.androidth.general.utils.SecurityUtils;
import javax.inject.Inject;

public class AlertItemView extends RelativeLayout
        implements DTOView<AlertItemView.DTO>
{
    @Inject protected Picasso picasso;

    @Bind(R.id.logo) ImageView stockLogo;
    @Bind(R.id.stock_symbol) TextView stockSymbol;
    @Bind(R.id.alert_value) TextView alertValue;
    @Bind(R.id.company_name) TextView companyName;
    @Bind(R.id.alert_description) TextView alertDescription;
    @Bind(R.id.alert_status) TextView alertStatus;

    //<editor-fold desc="Constructors">
    public AlertItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.cancelRequest(stockLogo);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull DTO dto)
    {
        if (stockLogo != null)
        {
            RequestCreator request;
            if (dto.logoUrl != null)
            {
                request = picasso.load(dto.logoUrl);
            }
            else
            {
                request = picasso.load(dto.logoRes);
            }
            request.transform(new WhiteToTransparentTransformation())
                    .into(stockLogo);
        }

        if (stockSymbol != null)
        {
            stockSymbol.setText(dto.stockSymbol);
        }

        if (alertValue != null)
        {
            alertValue.setText(dto.alertValue);
        }

        if (companyName != null)
        {
            companyName.setText(dto.companyName);
        }

        if (alertDescription != null)
        {
            alertDescription.setText(dto.description);
        }

        if (alertStatus != null)
        {
            alertStatus.setText(dto.status);
        }
    }

    public static class DTO
    {
        @NonNull public final AlertCompactDTO alertCompactDTO;
        @Nullable public final String logoUrl;
        @DrawableRes public final int logoRes;
        @NonNull public final String stockSymbol;
        @NonNull public final String alertValue;
        @NonNull public final String companyName;
        @NonNull public final String description;
        @NonNull public final Spanned status;

        public DTO(@NonNull Resources resources, @NonNull AlertCompactDTO alertCompactDTO)
        {
            this.alertCompactDTO = alertCompactDTO;
            SecurityCompactDTO securityCompactDTO = alertCompactDTO.security;

            //<editor-fold desc="Logo">
            if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
            {
                logoUrl = securityCompactDTO.imageBlobUrl;
                logoRes = R.drawable.default_image;
            }
            else if (securityCompactDTO != null)
            {
                logoUrl = null;
                logoRes = securityCompactDTO.getExchangeLogoId();
            }
            else
            {
                logoUrl = null;
                logoRes = R.drawable.default_image;
            }
            //</editor-fold>

            //<editor-fold desc="Stock Symbol">
            if (securityCompactDTO != null)
            {
                stockSymbol = securityCompactDTO.getExchangeSymbol();
            }
            else
            {
                stockSymbol = resources.getString(R.string.na);
            }
            //</editor-fold>

            //<editor-fold desc="Alert Value">
            if (alertCompactDTO.priceMovement != null)
            {
                alertValue = THSignedPercentage.builder(alertCompactDTO.priceMovement * 100)
                        .signTypePlusMinusAlways()
                        .build().toString();
            }
            else
            {
                alertValue = THSignedMoney.builder(alertCompactDTO.targetPrice)
                        .currency(securityCompactDTO == null
                                ? SecurityUtils.getDefaultCurrency()
                                : securityCompactDTO.currencyDisplay)
                        .build().toString();
            }
            //</editor-fold>

            //<editor-fold desc="Company Name">
            if (securityCompactDTO != null)
            {
                companyName = securityCompactDTO.name;
            }
            else
            {
                companyName = "";
            }
            //</editor-fold>

            //<editor-fold desc="Description">
            if (alertCompactDTO.priceMovement != null)
            {
                description = resources.getString(R.string.stock_alert_price_movement);
            }
            else
            {
                description = resources.getString(R.string.stock_alert_target_price);
            }
            //</editor-fold>

            //<editor-fold desc="Status">
            if (!alertCompactDTO.active)
            {
                status = new SpannableString(resources.getString(R.string.stock_alert_inactive));

            }
            else if (alertCompactDTO.activeUntilDate == null)
            {
                status = new SpannableString(resources.getString(R.string.stock_alert_active_one));
            }
            else
            {
                status = Html.fromHtml(
                        resources.getString(R.string.stock_alert_active_until_date,
                                DateUtils.getFormattedDate(resources, alertCompactDTO.activeUntilDate)));
            }
            //</editor-fold>
        }
    }
}
