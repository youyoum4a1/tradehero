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
import com.tradehero.th.fragments.leaderboard.ExpandingLayout;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.position.PositionDTOUtils;
import java.text.DateFormat;
import java.util.TimeZone;
import org.ocpsoft.prettytime.PrettyTime;

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
    @InjectView(R.id.expanding_layout) public ExpandingLayout expandingLayout;

    private DTO tradeItemViewDTO;

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

    @Override public void display(TradeListItemView.DTO dto)
    {
        this.tradeItemViewDTO = dto;
        displayTopSection(dto);
        displayExpandableSection(dto);
    }

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
            dateTextView.setText(dto.getTradeDateText());
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

    public static class DTO extends ExpandableListItem<TradeDTO>
    {
        private Resources resources;
        private final PositionDTO positionDTO;
        private final SecurityCompactDTO securityCompactDTO;
        private final TradeDTO tradeDTO;
        private final boolean lastTrade;
        private PrettyTime prettyTime;
        private boolean isPrettyDate = true;
        @Nullable Double numberToDisplay;
        String tradeBought;
        String currencyDisplay;
        String prettyDate;
        String normalDate;
        String holdingQuantity;
        String averagePrice;
        @ViewVisibilityValue int unrealisedPLVisibility;
        String unrealisedHeader;
        CharSequence unrealisedValue;
        String realisedHeader;
        CharSequence realisedValue;
        String tradeValue;
        @ViewVisibilityValue int commentVisibility;
        String commentText;

        public DTO(Resources resources, PositionDTO positionDTO, SecurityCompactDTO securityCompactDTO, TradeDTO tradeDTO, boolean lastTrade,
                PrettyTime prettyTime)
        {
            super(tradeDTO);
            this.resources = resources;
            this.positionDTO = positionDTO;
            this.securityCompactDTO = securityCompactDTO;
            this.tradeDTO = tradeDTO;
            this.lastTrade = lastTrade;
            this.prettyTime = prettyTime;
            init();
        }

        private void init()
        {
            Boolean isClosed = positionDTO.isClosed();
            if (isLastTrade() && isClosed != null && !isClosed)
            {
                numberToDisplay = positionDTO.unrealizedPLRefCcy;
            }
            numberToDisplay = tradeDTO.realizedPLAfterTradeRefCcy;

            int textResId =
                    tradeDTO.quantity >= 0 ? R.string.trade_bought_quantity_verbose : R.string.trade_sold_quantity_verbose;
            THSignedNumber tradeQuantityL = THSignedNumber.builder((double) Math.abs(tradeDTO.quantity))
                    .withOutSign()
                    .build();
            THSignedNumber tradeValueL = THSignedMoney.builder(tradeDTO.unitPriceRefCcy)
                    .withOutSign()
                    .currency(getCurrencyDisplay())
                    .build();
            tradeBought = resources.getString(
                    textResId,
                    tradeQuantityL.toString(),
                    tradeValueL.toString());

            if (securityCompactDTO.currencyDisplay != null)
            {
                currencyDisplay = securityCompactDTO.currencyDisplay;
            }
            currencyDisplay = positionDTO.getNiceCurrency();

            if (tradeDTO.dateTime != null)
            {
                prettyDate = this.prettyTime.format(tradeDTO.dateTime);
                DateFormat sdf = DateFormat.getDateTimeInstance();
                sdf.setTimeZone(TimeZone.getDefault());
                normalDate = sdf.format(tradeDTO.dateTime);
            }
            else
            {
                normalDate = "";
                prettyDate = "";
            }

            THSignedNumber tradeQuantityAfterTrade = THSignedNumber
                    .builder((double) Math.abs(tradeDTO.quantityAfterTrade))
                    .withOutSign()
                    .build();
            holdingQuantity = resources.getString(
                    isLastTrade() ? R.string.trade_holding_quantity_verbose : R.string.trade_held_quantity_verbose,
                    tradeQuantityAfterTrade.toString());

            averagePrice = String.format("%s %,.2f", positionDTO.getNiceCurrency(), tradeDTO.averagePriceAfterTradeRefCcy);

            unrealisedPLVisibility = (isLastTrade() && isClosed != null && !isClosed) ? VISIBLE : GONE;

            if (positionDTO.unrealizedPLRefCcy != null && positionDTO.unrealizedPLRefCcy < 0)
            {
                unrealisedHeader = resources.getString(R.string.position_unrealised_loss_header);
            }
            else
            {
                unrealisedHeader = resources.getString(R.string.position_unrealised_profit_header);
            }

            if (isLastTrade() && isClosed != null && !isClosed)
            {
                unrealisedValue = PositionDTOUtils.getUnrealisedPLSpanned(resources, positionDTO);
            }
            else
            {
                unrealisedValue = resources.getString(R.string.na);
            }

            if (tradeDTO.realizedPLAfterTradeRefCcy < 0)
            {
                realisedHeader = resources.getString(R.string.position_realised_loss_header);
            }
            else
            {
                realisedHeader = resources.getString(R.string.position_realised_profit_header);
            }

            realisedValue = THSignedMoney
                    .builder(tradeDTO.realizedPLAfterTradeRefCcy)
                    .withOutSign()
                    .currency(positionDTO.getNiceCurrency())
                    .build()
                    .createSpanned();

            tradeValue = THSignedMoney.builder(tradeDTO.quantity * tradeDTO.unitPriceRefCcy)
                    .withOutSign()
                    .currency(getCurrencyDisplay())
                    .build().toString();

            commentVisibility = tradeDTO.commentText == null ? GONE : VISIBLE;
            commentText = tradeDTO.commentText;
        }

        public boolean isLastTrade()
        {
            return this.lastTrade;
        }

        @Nullable private Double getNumberToDisplay()
        {
            return numberToDisplay;
        }

        public String getTradeBoughtText()
        {
            return tradeBought;
        }

        @NonNull
        private String getCurrencyDisplay()
        {
            return currencyDisplay;
        }

        @NonNull
        protected String getTradeDateText()
        {
            if (isPrettyDate)
            {
                return prettyDate;
            }
            else
            {
                return normalDate;
            }
        }

        public void togglePrettyDate()
        {
            isPrettyDate = !isPrettyDate;
        }

        protected String getHoldingQuantityText()
        {
            return holdingQuantity;
        }

        public String getAveragePrice()
        {
            return averagePrice;
        }

        @ViewVisibilityValue public int getUnrealisedPLVisibility()
        {
            return unrealisedPLVisibility;
        }

        public String getUnrealisedPLValueHeaderText()
        {
            return unrealisedHeader;
        }

        public CharSequence getUnrealisedPLValueText()
        {
            return unrealisedValue;
        }

        public String getRealisedPLValueHeaderText()
        {
            return realisedHeader;
        }

        public CharSequence getRealisedPLValueText()
        {
            return realisedValue;
        }

        protected String getTradeValueText()
        {
            return tradeValue;
        }

        @ViewVisibilityValue public int getCommentSectionVisibility()
        {
            return commentVisibility;
        }

        public String getCommentText()
        {
            return commentText;
        }
    }
}
