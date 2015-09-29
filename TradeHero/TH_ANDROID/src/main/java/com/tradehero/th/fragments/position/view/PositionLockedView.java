package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;

public class PositionLockedView extends LinearLayout
{
    //<editor-fold desc="Constructors">
    public PositionLockedView(Context context)
    {
        super(context);
    }

    public PositionLockedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionLockedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public static class DTO
    {
        public final int id;
        @NonNull public final CharSequence positionPercent;
        @NonNull public final CharSequence unrealisedPLValueHeader;
        @NonNull public final CharSequence unrealisedPLValue;
        @NonNull public final CharSequence realisedPLValueHeader;
        @NonNull public final CharSequence realisedPLValue;
        @NonNull public final CharSequence totalInvestedValue;

        public DTO(@NonNull Resources resources, @NonNull PositionDTO positionDTO)
        {
            this.id = positionDTO.id;
            String na = resources.getString(R.string.na);

            //<editor-fold desc="ROI Since Inception">
            Double roi = positionDTO.getROISinceInception();
            positionPercent = roi == null
                    ? na
                    : THSignedPercentage.builder(roi * 100.0)
                            .signTypePlusMinusAlways()
                            .withDefaultColor()
                            .relevantDigitCount(3)
                            .build()
                            .createSpanned();
            //</editor-fold>

            //<editor-fold desc="Unrealised PL">
            Double unrealisedPLRefCcy = positionDTO.unrealizedPLRefCcy;
            unrealisedPLValueHeader = resources.getString(
                    unrealisedPLRefCcy != null && unrealisedPLRefCcy < 0
                            ? R.string.position_unrealised_loss_header
                            : R.string.position_unrealised_profit_header);
            unrealisedPLValue = unrealisedPLRefCcy == null
                    ? na
                    : THSignedMoney.builder(unrealisedPLRefCcy)
                            .withOutSign()
                            .withDefaultColor()
                            .currency(positionDTO.getNiceCurrency())
                            .build()
                            .createSpanned();
            //</editor-fold>

            //<editor-fold desc="Realised PL">
            Double realisedPLRefCcy = positionDTO.realizedPLRefCcy;
            realisedPLValueHeader = resources.getString(realisedPLRefCcy != null && realisedPLRefCcy < 0
                    ? R.string.position_realised_loss_header
                    : R.string.position_realised_profit_header);
            realisedPLValue = realisedPLRefCcy == null
                    ? na
                    : THSignedMoney.builder(realisedPLRefCcy)
                            .withOutSign()
                            .withDefaultColor()
                            .currency(positionDTO.getNiceCurrency())
                            .build()
                            .createSpanned();
            //</editor-fold>

            //<editor-fold desc="Sum Invested">
            Double sumInvestedRefCcy = positionDTO.sumInvestedAmountRefCcy;
            totalInvestedValue = sumInvestedRefCcy == null
                    ? na
                    : THSignedMoney.builder(sumInvestedRefCcy)
                            .withOutSign()
                            .currency(positionDTO.getNiceCurrency())
                            .build()
                            .createSpanned();
            //</editor-fold>
        }
    }

    public static class ViewHolder extends TypedRecyclerAdapter.TypedViewHolder<Object>
    {

        @Bind(R.id.position_percentage) protected TextView positionPercent;
        @Bind(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
        @Bind(R.id.unrealised_pl_value) protected TextView unrealisedPLValue;
        @Bind(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
        @Bind(R.id.realised_pl_value) protected TextView realisedPLValue;
        @Bind(R.id.total_invested_value) protected TextView totalInvestedValue;

        public ViewHolder(PositionLockedView itemView)
        {
            super(itemView);
        }

        @Override public void onDisplay(Object o)
        {
            if (o instanceof PositionLockedView.DTO)
            {
                PositionLockedView.DTO dto = (DTO) o;
                if (positionPercent != null)
                {
                    positionPercent.setText(dto.positionPercent);
                }
                if (unrealisedPLValueHeader != null)
                {
                    unrealisedPLValueHeader.setText(dto.unrealisedPLValueHeader);
                }
                if (unrealisedPLValue != null)
                {
                    unrealisedPLValue.setText(dto.unrealisedPLValue);
                }
                if (realisedPLValueHeader != null)
                {
                    realisedPLValueHeader.setText(dto.realisedPLValueHeader);
                }
                if (realisedPLValue != null)
                {
                    realisedPLValue.setText(dto.realisedPLValue);
                }
                if (totalInvestedValue != null)
                {
                    totalInvestedValue.setText(dto.totalInvestedValue);
                }
            }
        }
    }
}
