package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.persistence.position.PositionCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import dagger.Lazy;
import java.text.DateFormat;
import java.util.TimeZone;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Subscription;

public class TradeListItemView extends LinearLayout
        implements DTOView<TradeListItemView.DTO>
{
    @InjectView(R.id.ic_position_profit_indicator_left) protected ColorIndicator profitIndicatorView;
    @InjectView(R.id.trade_date_label) protected TextView dateTextView;
    @InjectView(R.id.traded_quantity_verbose) protected TextView tradedQuantityVerbose;
    @InjectView(R.id.holding_quantity_verbose) protected TextView holdingQuantityVerbose;
    @InjectView(R.id.trade_avg_price) protected TextView averagePriceTextView;
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.unrealised_pl_container) protected View unrealisedPLContainer;
    @InjectView(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
    @InjectView(R.id.unrealised_pl_value) protected TextView unrealizedPLValue;
    @InjectView(R.id.trade_value_header) protected TextView tradeValueHeader;
    @InjectView(R.id.trade_value) protected TextView tradeValue;
    @InjectView(R.id.trade_list_comment_section) protected View commentSection;
    @InjectView(R.id.trade_list_comment) protected TextView commentText;

    private DTO tradeItemViewDTO;
    @Inject PrettyTime prettyTime;

    //<editor-fold desc="Constructors">
    public TradeListItemView(Context context)
    {
        super(context);
    }

    public TradeListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TradeListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        //ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        //ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(TradeListItemView.DTO dto)
    {
        this.tradeItemViewDTO = dto;
        displayTopSection(dto);
        displayExpandableSection(dto);
    }

    //public void linkWith(TradeListItemAdapter.ExpandableTradeItem item)
    //{
    //    this.tradeItem = item;
    //    if (this.tradeItem != null)
    //    {
    //        this.trade = tradeItem.getModel().tradeDTO;
    //        detachPositionSubscription();
    //        positionSubscription = positionCache.get().get(tradeItem.getModel().positionDTOKey)
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .flatMap(new Func1<Pair<PositionDTOKey, PositionDTO>, Observable<? extends Pair<SecurityIntegerId, SecurityId>>>()
    //                {
    //                    @Override public Observable<? extends Pair<SecurityIntegerId, SecurityId>> call(Pair<PositionDTOKey, PositionDTO> pair1)
    //                    {
    //                        position = pair1.second;
    //                        display();
    //                        return securityIdCache.get().get(pair1.second.getSecurityIntegerId());
    //                    }
    //                })
    //                .flatMap(new Func1<Pair<SecurityIntegerId, SecurityId>, Observable<? extends Pair<SecurityId, SecurityCompactDTO>>>()
    //                {
    //                    @Override public Observable<? extends Pair<SecurityId, SecurityCompactDTO>> call(
    //                            Pair<SecurityIntegerId, SecurityId> pair2)
    //                    {
    //                        return securityCache.get().get(pair2.second);
    //                    }
    //                })
    //                .subscribe(
    //                        new Action1<Pair<SecurityId, SecurityCompactDTO>>()
    //                        {
    //                            @Override public void call(Pair<SecurityId, SecurityCompactDTO> pair)
    //                            {
    //                                strDisplay = pair.second.currencyDisplay;
    //                                display();
    //                            }
    //                        },
    //                        new EmptyAction1<Throwable>());
    //    }
    //    else
    //    {
    //        this.position = null;
    //        this.trade = null;
    //    }
    //
    //    display();
    //}

    private void displayTopSection(DTO dto)
    {
        if (this.profitIndicatorView != null)
        {
            this.profitIndicatorView.linkWith(dto.getNumberToDisplay());
        }

        displayTradeBoughtText(dto);
        displayTradeDate(dto);
        displayHoldingQuantity(dto);
    }

    private void displayTradeBoughtText(DTO dto)
    {
        if (tradedQuantityVerbose != null)
        {
            tradedQuantityVerbose.setText(dto.getTradeBoughtText());
        }
    }

    private void displayHoldingQuantity(DTO dto)
    {
        if (this.holdingQuantityVerbose != null)
        {
            this.holdingQuantityVerbose.setText(dto.getHoldingQuantityText());
        }
    }

    private void displayTradeDate(DTO dto)
    {
        if (dateTextView != null)
        {
            dateTextView.setText(dto.getTradeDateText(prettyTime));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.trade_date_label)
    protected void toggleTradeDateLook(View view)
    {
        tradeItemViewDTO.togglePrettyDate();
        displayTradeDate(tradeItemViewDTO);
    }

    private void displayExpandableSection(DTO dto)
    {
        displayAveragePrice(dto);
        displayUnrealisedPLContainer(dto);
        displayUnrealisedPLValueHeader(dto);
        displayUnrealisedPLValue(dto);
        displayRealisedPLValueHeader(dto);
        displayRealisedPLValue(dto);
        displayTradeValue(dto);
        displayCommentSection(dto);
        displayCommentText(dto);
    }

    private void displayAveragePrice(DTO dto)
    {
        if (this.averagePriceTextView != null)
        {
            this.averagePriceTextView.setText(dto.getAveragePrice());
        }
    }

    private void displayUnrealisedPLContainer(DTO dto)
    {
        if (this.unrealisedPLContainer != null)
        {
            this.unrealisedPLContainer.setVisibility(dto.getUnrealisedPLVisibility());
        }
    }

    private void displayUnrealisedPLValueHeader(DTO dto)
    {
        if (unrealisedPLValueHeader != null)
        {
            unrealisedPLValueHeader.setText(dto.getUnrealisedPLValueHeaderText());
        }
    }

    private void displayUnrealisedPLValue(DTO dto)
    {
        if (this.unrealizedPLValue != null)
        {
            this.unrealizedPLValue.setText(dto.getUnrealisedPLValueText());
        }
    }

    private void displayRealisedPLValueHeader(DTO dto)
    {
        if (realisedPLValueHeader != null)
        {
            realisedPLValueHeader.setText(dto.getRealisedPLValueHeaderText());
        }
    }

    private void displayRealisedPLValue(DTO dto)
    {
        if (this.realisedPLValue != null)
        {
            this.realisedPLValue.setText(dto.getRealisedPLValueText());
        }
    }

    private void displayTradeValue(DTO dto)
    {
        if (tradeValue != null)
        {
            tradeValue.setText(dto.getTradeValueText());
        }
    }

    private void displayCommentSection(DTO dto)
    {
        if (this.commentSection != null)
        {
            this.commentSection.setVisibility(dto.getCommentSectionVisibility());
        }
    }

    private void displayCommentText(DTO dto)
    {
        if (this.commentText != null)
        {
            this.commentText.setText(dto.getCommentText());
        }
    }

    public static class DTO
    {
        private Resources resources;
        private final PositionDTO positionDTO;
        private final SecurityCompactDTO securityCompactDTO;
        private ExpandableTradeItem expandableTradeItem;
        private boolean prettyDate = true;

        public DTO(Resources resources, PositionDTO positionDTO, SecurityCompactDTO securityCompactDTO, ExpandableTradeItem expandableTradeItem)
        {
            this.resources = resources;
            this.positionDTO = positionDTO;
            this.securityCompactDTO = securityCompactDTO;
            this.expandableTradeItem = expandableTradeItem;
        }

        public ExpandableTradeItem getExpandableTradeItem()
        {
            return expandableTradeItem;
        }

        @Nullable private Double getNumberToDisplay()
        {
            Boolean isClosed = positionDTO.isClosed();
            if (expandableTradeItem.isLastTrade() && isClosed != null && !isClosed)
            {
                return positionDTO.unrealizedPLRefCcy;
            }
            return expandableTradeItem.getModel().realizedPLAfterTradeRefCcy;
        }

        public String getTradeBoughtText()
        {
            int textResId =
                    expandableTradeItem.getModel().quantity >= 0 ? R.string.trade_bought_quantity_verbose : R.string.trade_sold_quantity_verbose;
            THSignedNumber tradeQuantity = THSignedNumber.builder((double) Math.abs(expandableTradeItem.getModel().quantity))
                    .withOutSign()
                    .build();
            THSignedNumber tradeValue = THSignedMoney.builder(expandableTradeItem.getModel().unitPriceRefCcy)
                    .withOutSign()
                    .currency(getCurrencyDisplay())
                    .build();
            return resources.getString(
                    textResId,
                    tradeQuantity.toString(),
                    tradeValue.toString());
        }

        @NonNull
        private String getCurrencyDisplay()
        {
            if (securityCompactDTO.currencyDisplay != null)
            {
                return securityCompactDTO.currencyDisplay;
            }
            return positionDTO.getNiceCurrency();
        }

        @NonNull
        protected String getTradeDateText(PrettyTime prettyTime)
        {
            if (expandableTradeItem.getModel().dateTime != null)
            {
                if (prettyDate)
                {
                    return prettyTime.format(expandableTradeItem.getModel().dateTime);
                }
                else
                {
                    DateFormat sdf = DateFormat.getDateTimeInstance();
                    sdf.setTimeZone(TimeZone.getDefault());
                    return sdf.format(expandableTradeItem.getModel().dateTime);
                }
            }
            else
            {
                return "";
            }
        }

        public void togglePrettyDate()
        {
            prettyDate = !prettyDate;
        }

        protected String getHoldingQuantityText()
        {
            THSignedNumber tradeQuantityAfterTrade = THSignedNumber
                    .builder((double) Math.abs(expandableTradeItem.getModel().quantityAfterTrade))
                    .withOutSign()
                    .build();
            return resources.getString(
                    expandableTradeItem.isLastTrade() ? R.string.trade_holding_quantity_verbose : R.string.trade_held_quantity_verbose,
                    tradeQuantityAfterTrade.toString());
        }

        public String getAveragePrice()
        {
            return String.format("%s %,.2f", positionDTO.getNiceCurrency(), expandableTradeItem.getModel().averagePriceAfterTradeRefCcy);
        }

        @ViewVisibilityValue public int getUnrealisedPLVisibility()
        {
            Boolean isOpen = positionDTO.isOpen();
            return (expandableTradeItem.isLastTrade() && isOpen != null && isOpen) ? VISIBLE : GONE;
        }

        public String getUnrealisedPLValueHeaderText()
        {
            if (positionDTO.unrealizedPLRefCcy != null && positionDTO.unrealizedPLRefCcy < 0)
            {
                return resources.getString(R.string.position_unrealised_loss_header);
            }
            else
            {
                return resources.getString(R.string.position_unrealised_profit_header);
            }
        }

        public CharSequence getUnrealisedPLValueText()
        {
            Boolean isOpen = positionDTO.isOpen();
            if (expandableTradeItem.isLastTrade() && isOpen != null && isOpen)
            {
                return PositionDTOUtils.getUnrealisedPLSpanned(resources, positionDTO);
            }
            else
            {
                return resources.getString(R.string.na);
            }
        }

        public String getRealisedPLValueHeaderText()
        {
            if (expandableTradeItem.getModel().realizedPLAfterTradeRefCcy < 0)
            {
                return resources.getString(R.string.position_realised_loss_header);
            }
            else
            {
                return resources.getString(R.string.position_realised_profit_header);
            }
        }

        public CharSequence getRealisedPLValueText()
        {
            return THSignedMoney
                    .builder(expandableTradeItem.getModel().realizedPLAfterTradeRefCcy)
                    .withOutSign()
                    .currency(positionDTO.getNiceCurrency())
                    .build()
                    .createSpanned();
        }

        protected String getTradeValueText()
        {
            THSignedNumber tradeValue = THSignedMoney
                    .builder(expandableTradeItem.getModel().quantity * expandableTradeItem.getModel().unitPriceRefCcy)
                    .withOutSign()
                    .currency(getCurrencyDisplay())
                    .build();
            return tradeValue.toString();
        }

        @ViewVisibilityValue public int getCommentSectionVisibility()
        {
            return expandableTradeItem.getModel().commentText == null ? GONE : VISIBLE;
        }

        public String getCommentText()
        {
            return expandableTradeItem.getModel().commentText;
        }
    }

    public static class ExpandableTradeItem extends ExpandableListItem<TradeDTO>
    {
        private final boolean lastTrade;

        //<editor-fold desc="Constructors">
        public ExpandableTradeItem(final TradeDTO key)
        {
            this(key, false);
        }

        public ExpandableTradeItem(final TradeDTO key, final boolean lastTrade)
        {
            super(key);
            this.lastTrade = lastTrade;
        }
        //</editor-fold>

        public boolean isLastTrade()
        {
            return this.lastTrade;
        }
    }
}
