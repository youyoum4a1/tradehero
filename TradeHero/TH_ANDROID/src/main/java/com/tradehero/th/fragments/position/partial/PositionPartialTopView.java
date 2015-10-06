package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.security.FxFlagContainer;
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

    public static class AscendingLatestTradeDateComparator implements Comparator<PositionDisplayDTO>
    {
        @Override public int compare(@NonNull PositionDisplayDTO lhs, @NonNull PositionDisplayDTO rhs)
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
        @NonNull private final PublishSubject<CloseUserAction> userActionSubject;

        private PositionDisplayDTO dto;
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
            //if (o instanceof PositionDisplayDTO)
            //{
            //    this.dto = (PositionDisplayDTO) o;
            //
            //    if (gainIndicator != null)
            //    {
            //        gainIndicator.setImageResource(dto.gainIndicator);
            //    }
            //
            //    if (stockLogo != null)
            //    {
            //        stockLogo.setVisibility(dto.stockLogoVisibility);
            //        RequestCreator request;
            //        if (dto.stockLogoUrl != null)
            //        {
            //            request = picasso.load(dto.stockLogoUrl);
            //        }
            //        else
            //        {
            //            request = picasso.load(dto.stockLogoRes);
            //        }
            //        request.placeholder(R.drawable.default_image)
            //                .transform(new WhiteToTransparentTransformation())
            //                .into(stockLogo, new Callback()
            //                {
            //                    @Override public void onSuccess()
            //                    {
            //                    }
            //
            //                    @Override public void onError()
            //                    {
            //                        stockLogo.setImageResource(dto.stockLogoRes);
            //                    }
            //                });
            //    }
            //
            //    if (flagsContainer != null)
            //    {
            //        flagsContainer.setVisibility(dto.flagsContainerVisibility);
            //        flagsContainer.display(dto.fxPair);
            //    }
            //
            //    if (btnClose != null)
            //    {
            //        btnClose.setVisibility(dto.btnCloseVisibility);
            //    }
            //
            //    if (stockSymbol != null)
            //    {
            //        stockSymbol.setText(dto.stockSymbol);
            //    }
            //
            //    if (companyName != null)
            //    {
            //        companyName.setVisibility(dto.companyNameVisibility);
            //        companyName.setText(dto.companyName);
            //    }
            //
            //    if (lastPriceAndRise != null)
            //    {
            //        lastPriceAndRise.setText(dto.lastPriceAndRise);
            //    }
            //
            //    if (shareCountRow != null)
            //    {
            //        shareCountRow.setVisibility(dto.shareCountRowVisibility);
            //    }
            //
            //    if (shareCountHeader != null)
            //    {
            //        shareCountHeader.setText(dto.shareCountHeader);
            //    }
            //
            //    if (shareCountText != null)
            //    {
            //        shareCountText.setVisibility(dto.shareCountVisibility);
            //        shareCountText.setText(dto.shareCountText);
            //    }
            //
            //    if (shareCount != null)
            //    {
            //        shareCount.setText(dto.shareCount);
            //    }
            //
            //    if (gainLossHeader != null)
            //    {
            //        gainLossHeader.setText(dto.gainLossHeader);
            //    }
            //
            //    if (gainLoss != null)
            //    {
            //        gainLoss.setText(dto.gainLoss);
            //        gainLoss.setTextColor(dto.gainLossColor);
            //    }
            //
            //    if (gainLossPercent != null)
            //    {
            //        gainLossPercent.setText(dto.gainLossPercent);
            //        gainLossPercent.setTextColor(dto.gainLossColor);
            //    }
            //
            //    if (totalInvested != null)
            //    {
            //        totalInvested.setText(dto.totalInvested);
            //    }
            //
            //    if (positionPercent != null)
            //    {
            //        positionPercent.setVisibility(dto.positionPercentVisibility);
            //        positionPercent.setText(dto.positionPercent);
            //    }
            //
            //    if (positionUnrealisedPL != null)
            //    {
            //        positionUnrealisedPL.setVisibility(dto.unrealisedPLVisibility);
            //        positionUnrealisedPL.setText(dto.unrealisedPL);
            //    }
            //
            //    if (lastAmountContainer != null)
            //    {
            //        lastAmountContainer.setVisibility(dto.lastAmountContainerVisibility);
            //    }
            //
            //    if (positionLastAmountHeader != null)
            //    {
            //        positionLastAmountHeader.setVisibility(dto.lastAmountHeaderVisibility);
            //    }
            //
            //    if (positionLastAmount != null)
            //    {
            //        positionLastAmount.setText(dto.lastValue);
            //    }
            //}
        }
    }
}
