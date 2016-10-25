package com.androidth.general.fragments.position.live1b;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.live1b.LivePositionDTO;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionInPeriodDTO;
import com.androidth.general.api.position.PositionStatus;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.security.key.FxPairSecurityId;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.common.graphics.WhiteToTransparentTransformation;
import com.androidth.general.fragments.security.FxFlagContainer;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.models.number.THSignedPercentage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.Comparator;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class LivePositionListRowView extends LinearLayout
{

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public LivePositionListRowView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LivePositionListRowView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LivePositionListRowView(Context context, AttributeSet attrs, int defStyle)
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

//    public static class AscendingLatestTradeDateComparator implements Comparator<DTO>
//    {
//        @Override public int compare(@NonNull DTO lhs, @NonNull DTO rhs)
//        {
//            if (lhs.positionDTO.id == rhs.positionDTO.id)
//            {
//                return 0;
//            }
//            Date lTrade = lhs.positionDTO.getLatestTradeUtc();
//            Date rTrade = rhs.positionDTO.getLatestTradeUtc();
//            if(lTrade!=null && rTrade!=null){
//                return rTrade.compareTo(lTrade);
//            }else{
//                return 0;
//            }
//        }
//    }

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
//        @Bind(R.id.flags_container) FxFlagContainer flagsContainer;
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
        @NonNull private final PublishSubject<CloseUserAction> userActionSubject;

        private LiveDTO liveDTO;
        private final Picasso picasso;

        public ViewHolder(LivePositionListRowView view, Picasso picasso)
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
//            if (dto != null)
//            {
//                userActionSubject.onNext(new CloseUserAction(dto.positionDTO, dto.securityCompactDTO));
//            }
        }

        public Observable<CloseUserAction> getUserActionObservable()
        {
            return userActionSubject.asObservable();
        }

        @Override public void onDisplay(Object o)
        {
            if(o instanceof LiveDTO){
                this.liveDTO = (LiveDTO)o;

                btnClose.setVisibility(INVISIBLE);
                if (companyName != null)
                {
                    companyName.setVisibility(VISIBLE);
                    companyName.setText(liveDTO.companyName);
                }

                if (lastPriceAndRise != null)
                {
                    lastPriceAndRise.setText(liveDTO.lastPriceAndRise);
                }

                if (shareCount != null)
                {
                    shareCountText.setText(liveDTO.shareCountText);
                }

                if(stockSymbol!=null){
                    stockSymbol.setText(liveDTO.stockSymbol);
                }

                if(stockLogo!=null){
                    stockLogo.setVisibility(liveDTO.stockLogoVisibility);
                    RequestCreator request;
                    if (liveDTO.stockLogoUrl != null)
                    {
                        request = picasso.load(liveDTO.stockLogoUrl);
                    }
                    else
                    {
                        request = picasso.load(liveDTO.stockLogoRes);
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
                                    stockLogo.setImageResource(liveDTO.stockLogoRes);
                                }
                            });
                }

            }
        }
    }

    public static class LiveDTO
    {
        public LivePositionDTO positionDTO;
//        @NonNull public final SecurityCompactDTO securityCompactDTO;
//
        @ViewVisibilityValue public final int stockLogoVisibility;
        @Nullable public final String stockLogoUrl;
        @DrawableRes public final int stockLogoRes;
//        @Nullable public final FxPairSecurityId fxPair;
        @NonNull public final String stockSymbol;
//        @ViewVisibilityValue public final int flagsContainerVisibility;
//        @ViewVisibilityValue public final int btnCloseVisibility;
//        @ViewVisibilityValue public final int companyNameVisibility;
        public final String companyName;
        public final CharSequence lastPriceAndRise;
//        @ViewVisibilityValue public final int shareCountRowVisibility;
//        @NonNull public final CharSequence shareCountHeader;
//        @ViewVisibilityValue public final int shareCountVisibility;
//        @NonNull public final CharSequence shareCount;
        public final CharSequence shareCountText;
//        @ViewVisibilityValue public final int lastAmountContainerVisibility;
//        @ViewVisibilityValue public final int positionPercentVisibility;
//        @NonNull public final CharSequence positionPercent;
//        @DrawableRes public final int gainIndicator;
//        @NonNull public final String gainLossHeader;
//        @NonNull public final CharSequence gainLoss;
//        @NonNull public final CharSequence gainLossPercent;
//        public final int gainLossColor;
//        public final CharSequence totalInvested;
//        @ViewVisibilityValue public final int unrealisedPLVisibility;
//        @NonNull public final CharSequence unrealisedPL;
//        @ViewVisibilityValue public final int lastAmountHeaderVisibility;
//        @NonNull public final CharSequence lastAmount;

        public LiveDTO(@NonNull LivePositionDTO positionDTO)
        {
//            this.positionDTO = positionDTO;
//            this.securityCompactDTO = securityCompactDTO;

//            String na = resources.getString(R.string.na);
            this.positionDTO = positionDTO;
            this.companyName = positionDTO.Product;
            this.stockSymbol = positionDTO.SecurityCompactDto.symbol_ay;
            lastPriceAndRise = Double.toString(positionDTO.EntryPrice);
            this.shareCountText = Double.toString(positionDTO.Qty);
//            //<editor-fold desc="Stock Logo">
            if (positionDTO.SecurityCompactDto.imageBlobUrl != null)
            {
                this.stockLogoVisibility = VISIBLE;
                this.stockLogoUrl = positionDTO.SecurityCompactDto.imageBlobUrl;
            }else{
                this.stockLogoVisibility = INVISIBLE;
                this.stockLogoUrl = positionDTO.SecurityCompactDto.imageBlobUrl;
            }
            stockLogoRes = R.drawable.default_image;
//            else if (securityCompactDTO instanceof FxSecurityCompactDTO) // TODO Improve and show flags?
//            {
//                stockLogoVisibility = GONE;
//                flagsContainerVisibility = VISIBLE;
//                stockLogoUrl = null;
//                stockLogoRes = R.drawable.default_image;
//            }
//            else
//            {
//                stockLogoVisibility = VISIBLE;
//                flagsContainerVisibility = GONE;
//                stockLogoUrl = null;
//                stockLogoRes = securityCompactDTO.getExchangeLogoId();
//            }
//            //</editor-fold>
//
//            //<editor-fold desc="Symbol and FxPair">
//            if (securityCompactDTO instanceof FxSecurityCompactDTO)
//            {
//                fxPair = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
//                stockSymbol = String.format("%s/%s", fxPair.left, fxPair.right);
//            }
//            else
//            {
//                fxPair = null;
//                stockSymbol = String.format("%s:%s", securityCompactDTO.exchange, securityCompactDTO.symbol);
//            }
//            //</editor-fold>
//
//            //<editor-fold desc="Company Name">
//            if (securityCompactDTO instanceof FxSecurityCompactDTO)
//            {
//                companyNameVisibility = GONE;
//                companyName = "";
//            }
//            else
//            {
//                companyNameVisibility = VISIBLE;
//                companyName = securityCompactDTO.name;
//            }
//            //</editor-fold>
//
//            //<editor-fold desc="Last Price and Rise">
//            final CharSequence lastPrice;
//            if(positionDTO!=null && positionDTO.EntryPrice!=null){
//                Log.v("Mark", "Mark price "+positionDTO.EntryPrice);
//                lastPrice = THSignedMoney.builder(positionDTO.EntryPrice)
//                        .relevantDigitCount(3)
//                        .currency(securityCompactDTO.currencyDisplay)
//                        .build()
//                        .createSpanned();
//
//            }else if (securityCompactDTO.lastPrice != null) {
//                Log.v("Mark", "Mark price null"+positionDTO);
//                Log.v("Mark", "Mark price null"+securityCompactDTO.lastPrice);
//                lastPrice = THSignedMoney.builder(securityCompactDTO.lastPrice)
//                        .relevantDigitCount(3)
//                        .currency(securityCompactDTO.currencyDisplay)
//                        .build()
//                        .createSpanned();
//            }
//            else
//            {
//                lastPrice = resources.getString(R.string.na);
//            }
//            final String formatter = lastPrice + " (%s)";
//            Double roiSinceInception = positionDTO.getROISinceInception();//use this instead of risePercent
//            if(roiSinceInception==null){
//                //if null, then go back to risePercent
//                roiSinceInception = securityCompactDTO.risePercent;
//            }
//
//            if (roiSinceInception != null)
//            {
//                lastPriceAndRise = THSignedPercentage.builder(roiSinceInception * 100)
//                        .relevantDigitCount(3)
//                        .signTypeArrow()
//                        .withSign()
//                        .withDefaultColor()
//                        .format(formatter)
//                        .build()
//                        .createSpanned();
//            }
//            else
//            {
//                lastPriceAndRise = lastPrice;
//            }
//            //</editor-fold>
//
//            //<editor-fold desc="Share Count">
//            if ((positionDTO.positionStatus == PositionStatus.CLOSED
//                    || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED))
//            {
//                shareCountVisibility = GONE;
//            }
//            else
//            {
//                shareCountVisibility = VISIBLE;
//            }
//            if (positionDTO.shares == null)
//            {
//                shareCountText = resources.getString(R.string.position_share_count_qty_prefix, resources.getString(R.string.na));
//            }
//            else
//            {
//                shareCountText = THSignedNumber.builder(Math.abs(positionDTO.shares))
//                        .format(resources.getString(R.string.position_share_count_qty_prefix))
//                        .build()
//                        .createSpanned();
//            }
//
//            if (securityCompactDTO instanceof FxSecurityCompactDTO)
//            {
//                shareCountHeader = resources.getString(R.string.position_share_count_header_fx);
//                if (positionDTO.positionStatus == PositionStatus.CLOSED
//                        || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED
//                        || currentUserId.get() != positionDTO.userId)
//                {
//                    btnCloseVisibility = GONE;
//                }
//                else
//                {
//                    btnCloseVisibility = VISIBLE;
//                }
//            }
//            else
//            {
//                shareCountHeader = resources.getString(R.string.position_share_count_header);
//                btnCloseVisibility = GONE;
//            }
//
//            shareCountRowVisibility = (positionDTO.isClosed() != null && positionDTO.isClosed())
//                    ? GONE
//                    : VISIBLE;
//            if (positionDTO.shares == null)
//            {
//                shareCount = resources.getString(R.string.na);
//            }
//            else
//            {
//                shareCount = THSignedNumber.builder(Math.abs(positionDTO.shares))
//                        .build()
//                        .createSpanned();
//            }
//            //</editor-fold>
//
//            lastAmountContainerVisibility = securityCompactDTO instanceof FxSecurityCompactDTO ? GONE : VISIBLE;
//
//            final Double realisedPLRefCcy = positionDTO.realizedPLRefCcy;
//            final Double unrealisedPLRefCcy = positionDTO.unrealizedPLRefCcy;
//
//            //<editor-fold desc="Percent and Gain">
//            final Double gainPercent = positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod()
//                    ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
//                    : positionDTO.getROISinceInception();
//            if (securityCompactDTO instanceof FxSecurityCompactDTO)
//            {
//                positionPercentVisibility = GONE;
//                positionPercent = "";
//            }
//            else
//            {
//                positionPercentVisibility = VISIBLE;
//                positionPercent = gainPercent == null
//                        ? na
//                        : THSignedPercentage.builder(gainPercent * 100.0)
//                        .signTypeArrow()
//                        .withSign()
//                        .withDefaultColor()
//                        .relevantDigitCount(3)
//                        .build()
//                        .createSpanned();
//            }
//
//            if (gainPercent == null || gainPercent == 0)
//            {
//                gainIndicator = R.drawable.default_image;
//                gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
//            }
//            else if (gainPercent > 0)
//            {
//                gainIndicator = R.drawable.indicator_up;
//                gainLossHeader = resources.getString(R.string.position_realised_profit_header) + ":";
//            }
//            else
//            {
//                gainIndicator = R.drawable.indicator_down;
//                gainLossHeader = resources.getString(R.string.position_realised_loss_header) + ":";
//            }
//
//            final double gain = (realisedPLRefCcy != null ? realisedPLRefCcy : 0)
//                    + (unrealisedPLRefCcy != null ? unrealisedPLRefCcy : 0);
//            gainLossColor = resources.getColor(gain == 0
//                    ? R.color.darker_grey
//                    : gain > 0
//                    ? R.color.number_up
//                    : R.color.number_down);
//            gainLoss = THSignedMoney.builder(gain)
//                    .currency(positionDTO.getNiceCurrency())
//                    .withOutSign()
//                    .relevantDigitCount(3)
//                    .build()
//                    .createSpanned();
//            gainLossPercent = gainPercent == null
//                    ? ""
//                    : THSignedPercentage.builder(gainPercent * 100)
//                    .signTypePlusMinusAlways()
//                    .relevantDigitCount(3)
//                    .format("(%s)")
//                    .build()
//                    .createSpanned();
//            //</editor-fold>
//
//            totalInvested = THSignedMoney.builder(positionDTO.sumInvestedAmountRefCcy)
//                    .currency(positionDTO.getNiceCurrency())
//                    .relevantDigitCount(3)
//                    .build()
//                    .createSpanned();
//
//            //<editor-fold desc="Unrealised">
//            if (securityCompactDTO instanceof FxSecurityCompactDTO)
//            {
//                unrealisedPLVisibility = VISIBLE;
//                if (unrealisedPLRefCcy != null)
//                {
//                    Double PLR;
//                    if (positionDTO.positionStatus == PositionStatus.CLOSED
//                            || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED)
//                    {
//                        PLR = realisedPLRefCcy;
//                    }
//                    else
//                    {
//                        PLR = unrealisedPLRefCcy;
//                    }
//                    unrealisedPL =
//                            THSignedMoney.builder(PLR)
//                                    .currency(positionDTO.getNiceCurrency())
//                                    .signTypePlusMinusAlways()
//                                    .withDefaultColor()
//                                    .build()
//                                    .createSpanned();
//                }
//                else
//                {
//                    unrealisedPL = resources.getString(R.string.na);
//                }
//            }
//            else
//            {
//                unrealisedPLVisibility = GONE;
//                unrealisedPL = resources.getString(R.string.na);
//            }
//            //</editor-fold>
//
//            //<editor-fold desc="Last Amount">
//            Boolean isOpen = positionDTO.isOpen();
//            Boolean isClosed = positionDTO.isClosed();
//            lastAmountHeaderVisibility = isOpen == null || isOpen
//                    ? GONE
//                    : VISIBLE;
//            String lastAmountFormat = resources.getString(R.string.position_last_amount_header);
//            if (isClosed != null && isClosed && realisedPLRefCcy != null)
//            {
//                lastAmount = THSignedMoney.builder(realisedPLRefCcy)
//                        .relevantDigitCount(3)
//                        .withSign()
//                        .signTypeMinusOnly()
//                        .currency(positionDTO.getNiceCurrency())
//                        .format(lastAmountFormat)
//                        .build()
//                        .createSpanned();
//            }
//            else if (isClosed != null && !isClosed)
//            {
//                lastAmount = THSignedMoney.builder(positionDTO.marketValueRefCcy)
//                        .relevantDigitCount(3)
//                        .withSign()
//                        .signTypeMinusOnly()
//                        .currency(positionDTO.getNiceCurrency())
//                        .format(lastAmountFormat)
//                        .build()
//                        .createSpanned();
//            }
//            else
//            {
//                lastAmount = na;
//            }
//            //</editor-fold>
        }

//        public void setLivePositionDTO(@NonNull LivePositionDTO positionDTO) {
//            this.positionDTO = positionDTO;
//        }
    }
}
