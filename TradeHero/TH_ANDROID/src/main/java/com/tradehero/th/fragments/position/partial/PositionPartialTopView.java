package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.fragments.security.FxFlagContainer;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class PositionPartialTopView extends LinearLayout
{

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

    //@SuppressWarnings("unused")
    //@OnClick(R.id.btn_position_close) @Optional
    //protected void handleBtnCloseClicked(View view)
    //{
    //    if (viewDTO != null)
    //    {
    //        userActionSubject.onNext(new CloseUserAction(viewDTO.positionDTO, viewDTO.securityCompactDTO));
    //    }
    //}

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public static class DTO
    {
        @NonNull private final PublishSubject<CloseUserAction> userActionSubject;

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
        @NonNull public final CharSequence lastPriceAndRise;
        @ViewVisibilityValue public final int shareCountRowVisibility;
        @NonNull public final CharSequence shareCountHeader;
        @ViewVisibilityValue public final int shareCountVisibility;
        @NonNull public final CharSequence shareCount;
        @NonNull public final CharSequence shareCountText;
        @ViewVisibilityValue public final int lastAmountContainerVisibility;
        @ViewVisibilityValue public final int positionPercentVisibility;
        @NonNull public final CharSequence positionPercent;
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
            this.userActionSubject = PublishSubject.create();

            this.positionDTO = positionDTO;
            this.securityCompactDTO = securityCompactDTO;

            String na = resources.getString(R.string.na);

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

            //<editor-fold desc="Last Price and Rise">
            final CharSequence lastPrice;
            if (securityCompactDTO.lastPrice != null)
            {
                lastPrice = THSignedMoney.builder(securityCompactDTO.lastPrice)
                        .relevantDigitCount(3)
                        .currency(securityCompactDTO.currencyDisplay)
                        .build()
                        .createSpanned();
            }
            else
            {
                lastPrice = resources.getString(R.string.na);
            }
            final String formatter = lastPrice + " (%s)";
            if (securityCompactDTO.risePercent != null)
            {
                lastPriceAndRise = THSignedPercentage.builder(securityCompactDTO.risePercent * 100)
                        .relevantDigitCount(3)
                        .signTypeArrow()
                        .withSign()
                        .withDefaultColor()
                        .format(formatter)
                        .build()
                        .createSpanned();
            }
            else
            {
                lastPriceAndRise = lastPrice;
            }
            //</editor-fold>

            //<editor-fold desc="Share Count">
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
                shareCountText = resources.getString(R.string.position_share_count_qty_prefix, resources.getString(R.string.na));
            }
            else
            {
                shareCountText = THSignedNumber.builder(Math.abs(positionDTO.shares))
                        .format(resources.getString(R.string.position_share_count_qty_prefix))
                        .build()
                        .createSpanned();
            }

            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                shareCountHeader = resources.getString(R.string.position_share_count_header_fx);
                if (positionDTO.positionStatus == PositionStatus.CLOSED
                        || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED)
                {
                    btnCloseVisibility = GONE;
                }
                else
                {
                    btnCloseVisibility = VISIBLE;
                }
            }
            else
            {
                shareCountHeader = resources.getString(R.string.position_share_count_header);
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

            lastAmountContainerVisibility = securityCompactDTO instanceof FxSecurityCompactDTO ? GONE : VISIBLE;

            final Double realisedPLRefCcy = positionDTO.realizedPLRefCcy;
            final Double unrealisedPLRefCcy = positionDTO.unrealizedPLRefCcy;

            //<editor-fold desc="Percent and Gain">
            final Double gainPercent;
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                positionPercentVisibility = GONE;
                positionPercent = "";
                if (unrealisedPLRefCcy != null)
                {
                    if (positionDTO.positionStatus == PositionStatus.CLOSED
                            || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED)
                    {
                        gainPercent = positionDTO.realizedPLRefCcy;
                    }
                    else
                    {
                        gainPercent = unrealisedPLRefCcy;
                    }
                }
                else
                {
                    gainPercent = null;
                }
            }
            else
            {
                gainPercent = positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod()
                        ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
                        : positionDTO.getROISinceInception();
                positionPercentVisibility = VISIBLE;
                positionPercent = gainPercent == null
                        ? na
                        : THSignedPercentage.builder(gainPercent * 100.0)
                                .signTypeArrow()
                                .withSign()
                                .withDefaultColor()
                                .relevantDigitCount(3)
                                .build()
                                .createSpanned();
            }

            if (gainPercent == null || gainPercent == 0)
            {
                gainIndicator = R.drawable.default_image;
                gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
            }
            else if (gainPercent > 0)
            {
                gainIndicator = R.drawable.indicator_up;
                gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
            }
            else
            {
                gainIndicator = R.drawable.indicator_down;
                gainLossHeader = resources.getString(R.string.position_realised_loss_header) + ":";
            }

            final double gain = (realisedPLRefCcy != null ? realisedPLRefCcy : 0)
                    + (unrealisedPLRefCcy != null ? unrealisedPLRefCcy : 0);
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
                if (unrealisedPLRefCcy != null)
                {
                    Double PLR;
                    if (positionDTO.positionStatus == PositionStatus.CLOSED
                            || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED)
                    {
                        PLR = realisedPLRefCcy;
                    }
                    else
                    {
                        PLR = unrealisedPLRefCcy;
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
            Boolean isClosed = positionDTO.isClosed();
            lastAmountHeaderVisibility = isOpen == null || isOpen
                    ? GONE
                    : VISIBLE;
            String lastAmountFormat = resources.getString(R.string.position_last_amount_header);
            if (isClosed != null && isClosed && realisedPLRefCcy != null)
            {
                lastAmount = THSignedMoney.builder(realisedPLRefCcy)
                        .relevantDigitCount(3)
                        .withSign()
                        .signTypeMinusOnly()
                        .currency(positionDTO.getNiceCurrency())
                        .format(lastAmountFormat)
                        .build()
                        .createSpanned();
            }
            else if (isClosed != null && !isClosed)
            {
                lastAmount = THSignedMoney.builder(positionDTO.marketValueRefCcy)
                        .relevantDigitCount(3)
                        .withSign()
                        .signTypeMinusOnly()
                        .currency(positionDTO.getNiceCurrency())
                        .format(lastAmountFormat)
                        .build()
                        .createSpanned();
            }
            else
            {
                lastAmount = na;
            }
            //</editor-fold>
        }
    }

    public static class CloseUserAction
    {
        @NonNull public final PositionDTO positionDTO;
        @NonNull public final SecurityCompactDTO securityCompactDTO;

        public CloseUserAction(@NonNull PositionDTO positionDTO, @NonNull SecurityCompactDTO securityCompactDTO)
        {
            this.positionDTO = positionDTO;
            this.securityCompactDTO = securityCompactDTO;
        }
    }

    public static class ViewHolder extends TypedRecyclerAdapter.TypedViewHolder<Object>
    {
        @InjectView(R.id.gain_indicator) @Optional ImageView gainIndicator;
        @InjectView(R.id.stock_logo) ImageView stockLogo;
        @InjectView(R.id.flags_container) FxFlagContainer flagsContainer;
        @InjectView(R.id.stock_symbol) @Optional TextView stockSymbol;
        @InjectView(R.id.company_name) @Optional TextView companyName;
        @InjectView(R.id.last_price_and_rise) @Optional TextView lastPriceAndRise;
        @InjectView(R.id.share_count_row) @Optional View shareCountRow;
        @InjectView(R.id.share_count_header) @Optional TextView shareCountHeader;
        @InjectView(R.id.share_count_text) @Optional TextView shareCountText;
        @InjectView(R.id.share_count) @Optional TextView shareCount;
        @InjectView(R.id.hint_forward) @Optional View forwardCaret;

        @InjectView(R.id.gain_loss_header) @Optional TextView gainLossHeader;
        @InjectView(R.id.gain_loss) @Optional TextView gainLoss;
        @InjectView(R.id.gain_loss_percent) @Optional TextView gainLossPercent;
        @InjectView(R.id.total_invested_value) @Optional TextView totalInvested;
        @InjectView(R.id.position_percentage) @Optional TextView positionPercent;
        @InjectView(R.id.position_unrealised_pl) @Optional TextView positionUnrealisedPL;
        @InjectView(R.id.last_amount_container) @Optional View lastAmountContainer;
        @InjectView(R.id.position_last_amount_header) @Optional TextView positionLastAmountHeader;
        @InjectView(R.id.position_last_amount) @Optional TextView positionLastAmount;
        @InjectView(R.id.btn_position_close) @Optional TextView btnClose;

        public ViewHolder(PositionPartialTopView view)
        {
            super(view);
        }

        public void hideCaret()
        {
            if (forwardCaret != null)
            {
                forwardCaret.setVisibility(View.GONE);
            }
        }

        public void showCaret()
        {
            if (forwardCaret != null)
            {
                forwardCaret.setVisibility(View.VISIBLE);
            }
        }

        @Override public void display(Object o)
        {
            if (o instanceof DTO)
            {
                DTO dto = (DTO) o;

                if (gainIndicator != null)
                {
                    gainIndicator.setImageResource(dto.gainIndicator);
                }

                if (stockLogo != null)
                {
                    //TODO
                    //stockLogo.setVisibility(dto.stockLogoVisibility);
                    //RequestCreator request;
                    //if (dto.stockLogoUrl != null)
                    //{
                    //    request = picasso.load(dto.stockLogoUrl);
                    //}
                    //else
                    //{
                    //    request = picasso.load(dto.stockLogoRes);
                    //}
                    //request.placeholder(R.drawable.default_image)
                    //        .transform(new WhiteToTransparentTransformation())
                    //        .into(stockLogo, new Callback()
                    //        {
                    //            @Override public void onSuccess()
                    //            {
                    //            }
                    //
                    //            @Override public void onError()
                    //            {
                    //                stockLogo.setImageResource(dto.stockLogoRes);
                    //            }
                    //        });
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

                if (lastPriceAndRise != null)
                {
                    lastPriceAndRise.setText(dto.lastPriceAndRise);
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

                if (lastAmountContainer != null)
                {
                    lastAmountContainer.setVisibility(dto.lastAmountContainerVisibility);
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
        }
    }
}
