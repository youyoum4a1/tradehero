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
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.security.FxFlagContainer;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import java.util.Comparator;
import java.util.Date;
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

        public DTO(@NonNull Resources resources, @NonNull CurrentUserId currentUserId, @NonNull PositionDTO positionDTO,
                @NonNull SecurityCompactDTO securityCompactDTO)
        {
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
                        || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED
                        || currentUserId.get() != positionDTO.userId)
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
            final Double gainPercent = positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod()
                    ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
                    : positionDTO.getROISinceInception();
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                positionPercentVisibility = GONE;
                positionPercent = "";
            }
            else
            {
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

    public static class AscendingLatestTradeDateComparator implements Comparator<DTO>
    {
        @Override public int compare(@NonNull DTO lhs, @NonNull DTO rhs)
        {
            if (lhs.positionDTO.id == rhs.positionDTO.id)
            {
                return 0;
            }
            Date lTrade = lhs.positionDTO.latestTradeUtc;
            Date rTrade = rhs.positionDTO.latestTradeUtc;
            return rTrade.compareTo(lTrade);
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
        @Bind(R.id.gain_indicator) @Nullable ImageView gainIndicator;
        @Bind(R.id.stock_logo) ImageView stockLogo;
        @Bind(R.id.flags_container) FxFlagContainer flagsContainer;
        @Bind(R.id.stock_symbol) @Nullable TextView stockSymbol;
        @Bind(R.id.company_name) @Nullable TextView companyName;
        @Bind(R.id.last_price_and_rise) @Nullable TextView lastPriceAndRise;
        @Bind(R.id.share_count_row) @Nullable View shareCountRow;
        @Bind(R.id.share_count_header) @Nullable TextView shareCountHeader;
        @Bind(R.id.share_count_text) @Nullable TextView shareCountText;
        @Bind(R.id.share_count) @Nullable TextView shareCount;
        @Bind(R.id.hint_forward) @Nullable View forwardCaret;
        @Bind(R.id.gain_loss_header) @Nullable TextView gainLossHeader;

        @Bind(R.id.gain_loss) @Nullable TextView gainLoss;
        @Bind(R.id.gain_loss_percent) @Nullable TextView gainLossPercent;
        @Bind(R.id.total_invested_value) @Nullable TextView totalInvested;
        @Bind(R.id.position_percentage) @Nullable TextView positionPercent;
        @Bind(R.id.position_unrealised_pl) @Nullable TextView positionUnrealisedPL;
        @Bind(R.id.last_amount_container) @Nullable View lastAmountContainer;
        @Bind(R.id.position_last_amount_header) @Nullable TextView positionLastAmountHeader;
        @Bind(R.id.position_last_amount) @Nullable TextView positionLastAmount;
        @Bind(R.id.btn_position_close) @Nullable TextView btnClose;
        @Bind(R.id.position_status) @Nullable TextView positionStatus;
        @NonNull private final PublishSubject<CloseUserAction> userActionSubject;

        private DTO dto;
        private final Picasso picasso;

        public ViewHolder(PositionPartialTopView view, Picasso picasso)
        {
            super(view);
            this.picasso = picasso;
            this.userActionSubject = PublishSubject.create();
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

        @SuppressWarnings("unused")
        @OnClick(R.id.btn_position_close) @Nullable
        protected void handleBtnCloseClicked(View view)
        {
            if (dto != null)
            {
                userActionSubject.onNext(new CloseUserAction(dto.positionDTO, dto.securityCompactDTO));
            }
        }

        public Observable<CloseUserAction> getUserActionObservable()
        {
            return userActionSubject.asObservable();
        }

        @Override public void onDisplay(Object o)
        {
            if (o instanceof DTO)
            {
                this.dto = (DTO) o;

                if (gainIndicator != null)
                {
                    gainIndicator.setImageResource(dto.gainIndicator);
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

                if (positionStatus != null)
                {
                    positionStatus.setText(dto.positionDTO.positionStatus.toString());
                }
            }
        }
    }
}
