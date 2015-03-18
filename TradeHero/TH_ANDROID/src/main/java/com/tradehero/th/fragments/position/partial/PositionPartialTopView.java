package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.fragments.security.FxFlagContainer;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.position.PositionDTOUtils;
import javax.inject.Inject;
import rx.Subscription;

public class PositionPartialTopView extends LinearLayout
    implements DTOView<PositionPartialTopView.DTO>
{
    @Inject protected Picasso picasso;

    @InjectView(R.id.gain_indicator) ImageView mGainIndicator;
    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.flags_container) FxFlagContainer flagsContainer;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.share_count) TextView shareCount;
    @InjectView(R.id.last_price_container) View lastPriceContainer;

    @InjectView(R.id.position_percentage) TextView positionPercent;
    @InjectView(R.id.position_unrealised_pl) TextView positionUnrealisedPL;
    @InjectView(R.id.position_last_amount_header) TextView positionLastAmountHeader;
    @InjectView(R.id.position_last_amount) TextView positionLastAmount;

    @Nullable protected DTO viewDTO;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PositionPartialTopView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionPartialTopView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionPartialTopView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
        if (stockLogo != null)
        {
            stockLogo.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        if (stockLogo != null)
        {
            picasso.cancelRequest(stockLogo);
        }
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull final DTO dto)
    {
        this.viewDTO = dto;

        if (mGainIndicator != null)
        {
            mGainIndicator.setVisibility(dto.gainIndicatorVisibility);
            mGainIndicator.setImageResource(dto.gainIndicator);
        }

        if (stockLogo != null)
        {
            stockLogo.setVisibility(dto.stockLogoVisibility);
            RequestCreator request;
            if (dto.stockLogoUrl != null)
            {
                request = picasso.load(dto.stockLogoUrl);
            }
            else
            {
                request = picasso.load(dto.stockLogoRes);
            }
            request.placeholder(R.drawable.default_image)
                .transform(new WhiteToTransparentTransformation())
                .into(stockLogo, new Callback()
                {
                    @Override public void onSuccess()
                    {
                    }

                    @Override public void onError()
                    {
                        stockLogo.setImageResource(dto.stockLogoRes);
                    }
                });
        }

        if (flagsContainer != null)
        {
            flagsContainer.setVisibility(dto.flagsContainerVisibility);
            flagsContainer.display(dto.fxPair);
        }

        if (stockSymbol != null)
        {
            stockSymbol.setText(dto.stockSymbol);
        }

        if (companyName != null)
        {
            companyName.setVisibility(dto.companyNameVisibility);
            companyName.setText(dto.companyName);
        }

        if (shareCount != null)
        {
            shareCount.setVisibility(dto.shareCountVisibility);
            shareCount.setText(dto.shareCount);
        }

        if (lastPriceContainer != null)
        {
            lastPriceContainer.setVisibility(dto.lastPriceContainerVisibility);
        }

        if (positionPercent != null)
        {
            positionPercent.setVisibility(dto.positionPercentVisibility);
            positionPercent.setText(dto.positionPercent);
        }

        if (positionUnrealisedPL != null)
        {
            positionUnrealisedPL.setVisibility(dto.unrealisedPLVisibility);
            positionUnrealisedPL.setText(dto.unrealisedPL);
        }

        if (positionLastAmountHeader != null)
        {
            positionLastAmountHeader.setVisibility(dto.lastAmountHeaderVisibility);
        }

        if (positionLastAmount != null)
        {
            positionLastAmount.setText(dto.lastAmount);
        }
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public static class DTO
    {
        @NonNull public final PositionDTO positionDTO;
        @NonNull public final SecurityCompactDTO securityCompactDTO;

        @ViewVisibilityValue public final int stockLogoVisibility;
        @Nullable public final String stockLogoUrl;
        @DrawableRes public final int stockLogoRes;
        @Nullable public final FxPairSecurityId fxPair;
        @NonNull public final String stockSymbol;
        @ViewVisibilityValue public final int flagsContainerVisibility;
        @ViewVisibilityValue public final int companyNameVisibility;
        @NonNull public final String companyName;
        @ViewVisibilityValue public final int shareCountVisibility;
        @NonNull public final Spanned shareCount;
        @ViewVisibilityValue public final int lastPriceContainerVisibility;
        @ViewVisibilityValue public final int positionPercentVisibility;
        @NonNull public final Spanned positionPercent;
        @ViewVisibilityValue public final int gainIndicatorVisibility;
        @DrawableRes public final int gainIndicator;
        @ViewVisibilityValue public final int unrealisedPLVisibility;
        @NonNull public final Spanned unrealisedPL;
        @ViewVisibilityValue public final int lastAmountHeaderVisibility;
        @NonNull public final Spanned lastAmount;

        public DTO(@NonNull Resources resources, @NonNull PositionDTO positionDTO, @NonNull SecurityCompactDTO securityCompactDTO)
        {
            this.positionDTO = positionDTO;
            this.securityCompactDTO = securityCompactDTO;

            //<editor-fold desc="Stock Logo">
            if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
            {
                stockLogoVisibility = VISIBLE;
                flagsContainerVisibility = GONE;
                stockLogoUrl = securityCompactDTO.imageBlobUrl;
                stockLogoRes = R.drawable.default_image;
            }
            else if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                stockLogoVisibility = GONE;
                flagsContainerVisibility = VISIBLE;
                stockLogoUrl = null;
                stockLogoRes = R.drawable.default_image;
            }
            else
            {
                stockLogoVisibility = VISIBLE;
                flagsContainerVisibility = GONE;
                stockLogoUrl = null;
                stockLogoRes = securityCompactDTO.getExchangeLogoId();
            }
            //</editor-fold>

            //<editor-fold desc="Symbol and FxPair">
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                fxPair = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
                stockSymbol = String.format("%s/%s", fxPair.left, fxPair.right);
            }
            else
            {
                fxPair = null;
                stockSymbol = String.format("%s:%s", securityCompactDTO.exchange, securityCompactDTO.symbol);
            }
            //</editor-fold>


            //<editor-fold desc="Company Name">
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                companyNameVisibility = GONE;
                companyName = "";
            }
            else
            {
                companyNameVisibility = VISIBLE;
                companyName = securityCompactDTO.name;
            }
            //</editor-fold>

            //<editor-fold desc="Share Count">
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                if ((positionDTO.positionStatus == PositionStatus.CLOSED
                        || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED))
                {
                    shareCountVisibility = GONE;
                }
                else
                {
                    shareCountVisibility = VISIBLE;
                }
                if (positionDTO.shares == null)
                {
                    shareCount = new SpannableString(resources.getString(R.string.na));
                }
                else
                {
                    String unitFormat = resources.getQuantityString(R.plurals.position_unit_count, positionDTO.shares);
                    shareCount = THSignedNumber.builder(Math.abs(positionDTO.shares))
                            .format(unitFormat)
                            .build()
                            .createSpanned();
                }
            }
            else
            {
                shareCountVisibility = GONE;
                shareCount = new SpannableString("");
            }
            //</editor-fold>

            lastPriceContainerVisibility = securityCompactDTO instanceof FxSecurityCompactDTO ? GONE : VISIBLE;

            //<editor-fold desc="Percent and Gain">
            final Double gain;
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                positionPercentVisibility = GONE;
                positionPercent = new SpannableString("");
                gain = null;
            }
            else if (positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod())
            {
                positionPercentVisibility = VISIBLE;
                positionPercent = PositionDTOUtils.getROISpanned(resources, ((PositionInPeriodDTO) positionDTO).getROIInPeriod());
                gain = ((PositionInPeriodDTO) positionDTO).getROIInPeriod();
            }
            else
            {
                positionPercentVisibility = VISIBLE;
                positionPercent = PositionDTOUtils.getROISpanned(resources, positionDTO.getROISinceInception());
                gain = positionDTO.getROISinceInception();
            }

            if (gain == null || gain == 0)
            {
                gainIndicatorVisibility = INVISIBLE;
                gainIndicator = R.drawable.default_image;
            }
            else if (gain > 0)
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.indicator_green;
            }
            else
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.indicator_red;
            }
            //</editor-fold>

            //<editor-fold desc="Unrealised">
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                unrealisedPLVisibility = VISIBLE;
                if (positionDTO.unrealizedPLRefCcy != null)
                {
                    Double PLR;
                    if (positionDTO.positionStatus == PositionStatus.CLOSED
                            || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED)
                    {
                        PLR = positionDTO.realizedPLRefCcy;
                    }
                    else
                    {
                        PLR = positionDTO.unrealizedPLRefCcy;
                    }
                    unrealisedPL =
                            THSignedMoney.builder(PLR)
                                    .currency(positionDTO.getNiceCurrency())
                                    .signTypePlusMinusAlways()
                                    .withDefaultColor()
                                    .build()
                                    .createSpanned();
                }
                else
                {
                    unrealisedPL = new SpannableString(resources.getString(R.string.na));
                }
            }
            else
            {
                unrealisedPLVisibility = GONE;
                unrealisedPL = new SpannableString(resources.getString(R.string.na));
            }
            //</editor-fold>

            //<editor-fold desc="Last Amount">
            Boolean isOpen = positionDTO.isOpen();
            if (isOpen == null || isOpen)
            {
                lastAmountHeaderVisibility = GONE;
            }
            else
            {
                lastAmountHeaderVisibility = VISIBLE;
            }
            THSignedNumber number = null;
            Boolean closed = positionDTO.isClosed();
            if (closed != null && closed && positionDTO.realizedPLRefCcy != null)
            {
                number = THSignedMoney.builder(positionDTO.realizedPLRefCcy)
                        .withSign()
                        .signTypeMinusOnly()
                        .currency(positionDTO.getNiceCurrency())
                        .build();
            }
            else if (closed != null && !closed)
            {
                number = THSignedMoney.builder(positionDTO.marketValueRefCcy)
                        .withSign()
                        .signTypeMinusOnly()
                        .currency(positionDTO.getNiceCurrency())
                        .build();
            }
            if (number == null)
            {
                lastAmount = new SpannableString(resources.getString(R.string.na));
            }
            else
            {
                lastAmount = number.createSpanned();
            }
            //</editor-fold>
        }
    }
}
