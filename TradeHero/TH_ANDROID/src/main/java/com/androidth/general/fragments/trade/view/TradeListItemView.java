package com.androidth.general.fragments.trade.view;

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
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.R;
import com.androidth.general.adapters.ExpandableListItem;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.trade.TradeDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedNumber;
import java.text.DateFormat;
import java.util.TimeZone;
import org.ocpsoft.prettytime.PrettyTime;

public class TradeListItemView extends LinearLayout
        implements DTOView<TradeListItemView.DTO>
{
    @Bind(R.id.gain_indicator) protected ImageView gainIndicator;
    @Bind(R.id.trade_date_label) protected TextView dateTextView;
    @Bind(R.id.traded_quantity_verbose) protected TextView tradedQuantityVerbose;
    @Bind(R.id.pl_container) protected View plContainer;
    @Bind(R.id.pl_value_header) protected TextView plValueHeader;
    @Bind(R.id.pl_value) protected TextView plValue;

    @Nullable private DTO tradeItemViewDTO;

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
        ButterKnife.bind(this);
    }

    @Override public void display(@NonNull DTO dto)
    {
        this.tradeItemViewDTO = dto;

        if (gainIndicator != null)
        {
            gainIndicator.setVisibility(dto.gainIndicatorVisibility);
            gainIndicator.setImageResource(dto.gainIndicator);
        }

        if (tradedQuantityVerbose != null)
        {
            tradedQuantityVerbose.setText(dto.tradeBought);
        }

        if (dateTextView != null)
        {
            dateTextView.setText(dto.getTradeDateText());
        }

        if (plContainer != null)
        {
            plContainer.setVisibility(dto.pLVisibility);
        }

        if (plValueHeader != null)
        {
            plValueHeader.setText(dto.plHeader);
        }

        if (plValue != null)
        {
            plValue.setText(dto.plValue);
            plValue.setTextColor(dto.plValueColor);
        }
    }

    public void toggleTradeDateLook()
    {
        if (tradeItemViewDTO != null)
        {
            tradeItemViewDTO.togglePrettyDate();
            if (dateTextView != null)
            {
                dateTextView.setText(tradeItemViewDTO.getTradeDateText());
            }
        }
    }

    public static class DTO extends ExpandableListItem<TradeDTO>
    {
        private boolean isPrettyDate = true;
        @Nullable public final Double numberToDisplayRefCcy;
        @ViewVisibilityValue public final int gainIndicatorVisibility;
        @DrawableRes public final int gainIndicator;
        @NonNull public final CharSequence tradeBought;
        @NonNull public final CharSequence prettyDate;
        @NonNull public final CharSequence normalDate;
        @ViewVisibilityValue public final int pLVisibility;
        @NonNull public final CharSequence plHeader;
        @NonNull public final CharSequence plValue;
        public final int plValueColor;

        public DTO(
                @NonNull Resources resources,
                @NonNull SecurityCompactDTO securityCompactDTO,
                @NonNull PositionDTO positionDTO,
                boolean expanded,
                @NonNull TradeDTO tradeDTO,
                @NonNull PrettyTime prettyTime)
        {
            super(expanded, tradeDTO);

            numberToDisplayRefCcy = tradeDTO.realizedPLAfterTradeRefCcy;

            if (numberToDisplayRefCcy == 0)
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.default_image;
            }
            else if (numberToDisplayRefCcy > 0)
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.indicator_up;
            }
            else
            {
                gainIndicatorVisibility = VISIBLE;
                gainIndicator = R.drawable.indicator_down;
            }

            //<editor-fold desc="Action text">
            int textResId =
                    tradeDTO.quantity >= 0 ? R.string.trade_bought_quantity_verbose : R.string.trade_sold_quantity_verbose;
            THSignedNumber tradeQuantityL = THSignedNumber.builder((double) Math.abs(tradeDTO.quantity))
                    .withOutSign()
                    .build();
            THSignedNumber tradeValueL = THSignedMoney.builder(tradeDTO.unitPriceSecCcy)
                    .withOutSign()
                    .currency(securityCompactDTO.currencyDisplay)
                    .build();
            tradeBought = resources.getString(
                    textResId,
                    tradeQuantityL.toString(),
                    tradeValueL.toString());
            //</editor-fold>

            //<editor-fold desc="Date">
            if (tradeDTO.dateTime != null)
            {
                prettyDate = prettyTime.format(tradeDTO.dateTime);
                DateFormat sdf = DateFormat.getDateTimeInstance();
                sdf.setTimeZone(TimeZone.getDefault());
                normalDate = sdf.format(tradeDTO.dateTime);
            }
            else
            {
                normalDate = "";
                prettyDate = "";
            }
            //</editor-fold>

            pLVisibility = VISIBLE;

            plHeader = resources.getString(numberToDisplayRefCcy < 0
                    ? R.string.position_realised_loss_header
                    : R.string.position_realised_profit_header);

            plValue = THSignedMoney.builder(numberToDisplayRefCcy)
                    .withOutSign()
                    .currency(positionDTO.getNiceCurrency())
                    .build()
                    .createSpanned();
            plValueColor = resources.getColor(numberToDisplayRefCcy == 0
                    ? R.color.black
                    : numberToDisplayRefCcy > 0
                            ? R.color.number_up
                            : R.color.number_down);
        }

        @NonNull
        protected CharSequence getTradeDateText()
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
    }
}
