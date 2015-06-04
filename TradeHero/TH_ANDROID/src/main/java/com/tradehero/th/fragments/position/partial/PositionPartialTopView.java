package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
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
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.security.FxFlagContainer;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.position.PositionDTOUtils;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Subscription;

public class PositionPartialTopView extends LinearLayout
        implements DTOView<PositionPartialTopView.DTO>
{
    @Inject protected Picasso picasso;
    @Inject protected Lazy<DashboardNavigator> navigator;

    @InjectView(R.id.gain_indicator) ImageView mGainIndicator;
    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.flags_container) FxFlagContainer flagsContainer;
    @InjectView(R.id.stock_symbol) @Optional TextView stockSymbol;
    @InjectView(R.id.company_name) @Optional TextView companyName;
    @InjectView(R.id.share_count_row) @Optional View shareCountRow;
    @InjectView(R.id.share_count_header) @Optional TextView shareCountHeader;
    @InjectView(R.id.share_count_text) @Optional TextView shareCountText;
    @InjectView(R.id.share_count) @Optional TextView shareCount;
    @InjectView(R.id.last_price_container) @Optional View lastPriceContainer;
    @InjectView(R.id.hint_forward) View forwardCaret;

    @InjectView(R.id.gain_loss_header) @Optional TextView gainLossHeader;
    @InjectView(R.id.gain_loss) @Optional TextView gainLoss;
    @InjectView(R.id.gain_loss_percent) @Optional TextView gainLossPercent;
    @InjectView(R.id.total_invested_value) @Optional TextView totalInvested;
    @InjectView(R.id.position_percentage) @Optional TextView positionPercent;
    @InjectView(R.id.position_unrealised_pl) @Optional TextView positionUnrealisedPL;
    @InjectView(R.id.position_last_amount_header) @Optional TextView positionLastAmountHeader;
    @InjectView(R.id.position_last_amount) @Optional TextView positionLastAmount;
    @InjectView(R.id.btn_position_close) TextView btnClose;

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
        if (!isInEditMode())
        {
            ButterKnife.inject(this);
        }
        if (stockLogo != null)
        {
            stockLogo.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            ButterKnife.inject(this);
        }
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

    public void hideCaret()
    {
        forwardCaret.setVisibility(View.GONE);
    }

    public void showCaret()
    {
        forwardCaret.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_position_close)
    protected void handleBtnCloseClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        Bundle args = new Bundle();
        FXMainFragment.putSecurityId(args, viewDTO.securityCompactDTO.getSecurityId());
        FXMainFragment.putApplicablePortfolioId(args, viewDTO.positionDTO.getOwnedPortfolioId());
        FXMainFragment.putCloseAttribute(args, viewDTO.positionDTO.shares);
        navigator.get().pushFragment(FXMainFragment.class, args);
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

        if (btnClose != null)
        {
            btnClose.setVisibility(dto.btnCloseVisibility);
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

        if (shareCountRow != null)
        {
            shareCountRow.setVisibility(dto.shareCountRowVisibility);
        }

        if (shareCountHeader != null)
        {
            shareCountHeader.setText(dto.shareCountHeader);
        }

        if (shareCountText != null)
        {
            shareCountText.setVisibility(dto.shareCountVisibility);
            shareCountText.setText(dto.shareCountText);
        }

        if (shareCount != null)
        {
            shareCount.setText(dto.shareCount);
        }

        if (gainLossHeader != null)
        {
            gainLossHeader.setText(dto.gainLossHeader);
        }

        if (gainLoss != null)
        {
            gainLoss.setText(dto.gainLoss);
            gainLoss.setTextColor(dto.gainLossColor);
        }

        if (gainLossPercent != null)
        {
            gainLossPercent.setText(dto.gainLossPercent);
            gainLossPercent.setTextColor(dto.gainLossColor);
        }

        if (totalInvested != null)
        {
            totalInvested.setText(dto.totalInvested);
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
        @ViewVisibilityValue public final int btnCloseVisibility;
        @ViewVisibilityValue public final int companyNameVisibility;
        @NonNull public final String companyName;
        @ViewVisibilityValue public final int shareCountRowVisibility;
        @NonNull public final CharSequence shareCountHeader;
        @ViewVisibilityValue public final int shareCountVisibility;
        @NonNull public final CharSequence shareCount;
        @NonNull public final CharSequence shareCountText;
        @ViewVisibilityValue public final int lastPriceContainerVisibility;
        @ViewVisibilityValue public final int positionPercentVisibility;
        @NonNull public final CharSequence positionPercent;
        @ViewVisibilityValue public final int gainIndicatorVisibility;
        @DrawableRes public final int gainIndicator;
        @NonNull public final String gainLossHeader;
        @NonNull public final CharSequence gainLoss;
        @NonNull public final CharSequence gainLossPercent;
        public final int gainLossColor;
        public final CharSequence totalInvested;
        @ViewVisibilityValue public final int unrealisedPLVisibility;
        @NonNull public final CharSequence unrealisedPL;
        @ViewVisibilityValue public final int lastAmountHeaderVisibility;
        @NonNull public final CharSequence lastAmount;

        public DTO(@NonNull Resources resources, @NonNull PositionDTO positionDTO, @NonNull SecurityCompactDTO securityCompactDTO)
        {
            this.positionDTO = positionDTO;
            this.securityCompactDTO = securityCompactDTO;

            //<editor-fold desc="Stock Logo">
            if (securityCompactDTO.imageBlobUrl != null)
            {
                stockLogoVisibility = VISIBLE;
                flagsContainerVisibility = GONE;
                stockLogoUrl = securityCompactDTO.imageBlobUrl;
                stockLogoRes = R.drawable.default_image;
            }
            else if (securityCompactDTO instanceof FxSecurityCompactDTO) // TODO Improve and show flags?
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
                shareCountHeader = resources.getString(R.string.position_share_count_header_fx);
                if ((positionDTO.positionStatus == PositionStatus.CLOSED
                        || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED))
                {
                    shareCountVisibility = GONE;
                    btnCloseVisibility = GONE;
                }
                else
                {
                    shareCountVisibility = VISIBLE;
                    btnCloseVisibility = VISIBLE;
                }
                if (positionDTO.shares == null)
                {
                    shareCountText = resources.getString(R.string.na);
                }
                else
                {
                    String unitFormat = resources.getQuantityString(R.plurals.position_unit_count, positionDTO.shares);
                    shareCountText = THSignedNumber.builder(Math.abs(positionDTO.shares))
                            .format(unitFormat)
                            .build()
                            .createSpanned();
                }
            }
            else
            {
                shareCountHeader = resources.getString(R.string.position_share_count_header);
                shareCountVisibility = GONE;
                shareCountText = "";
                btnCloseVisibility = GONE;
            }

            shareCountRowVisibility = (positionDTO.isClosed() != null && positionDTO.isClosed())
                    ? GONE
                    : VISIBLE;
            if (positionDTO.shares == null)
            {
                shareCount = resources.getString(R.string.na);
            }
            else
            {
                shareCount = THSignedNumber.builder(Math.abs(positionDTO.shares))
                        .build()
                        .createSpanned();
            }
            //</editor-fold>

            lastPriceContainerVisibility = securityCompactDTO instanceof FxSecurityCompactDTO ? GONE : VISIBLE;

            //<editor-fold desc="Percent and Gain">
            final Double gainPercent;
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                positionPercentVisibility = GONE;
                positionPercent = "";
                if (positionDTO.unrealizedPLRefCcy != null)
                {
                    if (positionDTO.positionStatus == PositionStatus.CLOSED
                            || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED)
                    {
                        gainPercent = positionDTO.realizedPLRefCcy;
                    }
                    else
                    {
                        gainPercent = positionDTO.unrealizedPLRefCcy;
                    }
                }
                else
                {
                    gainPercent = null;
                }
            }
            else if (positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod())
            {
                positionPercentVisibility = VISIBLE;
                positionPercent = PositionDTOUtils.getROISpanned(resources, ((PositionInPeriodDTO) positionDTO).getROIInPeriod());
                gainPercent = ((PositionInPeriodDTO) positionDTO).getROIInPeriod();
            }
            else
            {
                positionPercentVisibility = VISIBLE;
                positionPercent = PositionDTOUtils.getROISpanned(resources, positionDTO.getROISinceInception());
                gainPercent = positionDTO.getROISinceInception();
            }

            if (gainPercent == null || gainPercent == 0)
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.default_image;
                gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
            }
            else if (gainPercent > 0)
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.indicator_up;
                gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
            }
            else
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.indicator_down;
                gainLossHeader = resources.getString(R.string.position_realised_loss_header) + ":";
            }

            final double gain = (positionDTO.realizedPLRefCcy != null ? positionDTO.realizedPLRefCcy : 0)
                    + (positionDTO.unrealizedPLRefCcy != null ? positionDTO.unrealizedPLRefCcy : 0);
            gainLossColor = resources.getColor(gain == 0
                    ? R.color.black
                    : gain > 0
                            ? R.color.number_up
                            : R.color.number_down);
            gainLoss = THSignedMoney.builder(gain)
                    .currency(positionDTO.getNiceCurrency())
                    .withOutSign()
                    .relevantDigitCount(3)
                    .build()
                    .createSpanned();
            gainLossPercent = gainPercent == null
                    ? ""
                    : THSignedPercentage.builder(gainPercent * 100)
                            .signTypePlusMinusAlways()
                            .relevantDigitCount(3)
                            .format("(%s)")
                            .build()
                            .createSpanned();
            //</editor-fold>

            totalInvested = THSignedMoney.builder(positionDTO.sumInvestedAmountRefCcy)
                    .currency(positionDTO.getNiceCurrency())
                    .relevantDigitCount(3)
                    .build()
                    .createSpanned();

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
                    unrealisedPL = resources.getString(R.string.na);
                }
            }
            else
            {
                unrealisedPLVisibility = GONE;
                unrealisedPL = resources.getString(R.string.na);
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
                lastAmount = resources.getString(R.string.na);
            }
            else
            {
                lastAmount = number.createSpanned();
            }
            //</editor-fold>
        }
    }
}
