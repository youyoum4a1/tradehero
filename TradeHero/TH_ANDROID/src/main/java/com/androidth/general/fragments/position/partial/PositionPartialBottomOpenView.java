package com.androidth.general.fragments.position.partial;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.Bind;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.adapters.ExpandableListItem;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionInPeriodDTO;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedPercentage;

public class PositionPartialBottomOpenView extends AbstractPartialBottomView
{
    @Bind(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
    @Bind(R.id.unrealised_pl_value) protected TextView unrealisedPLValue;
    @Bind(R.id.unrealised_pl_percent) @Nullable protected TextView unrealisedPLPercent;
    @Bind(R.id.realised_pl_value_header) @Nullable protected TextView realisedPLValueHeader;
    @Bind(R.id.realised_pl_value) @Nullable protected TextView realisedPLValue;
    @Bind(R.id.total_invested_value) protected TextView totalInvestedValue;
    @Bind(R.id.average_price_value) protected TextView averagePriceValue;

    protected PositionPartialBottomInPeriodViewHolder inPeriodViewHolder;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomOpenView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        inPeriodViewHolder = new PositionPartialBottomInPeriodViewHolder(this);
    }

    @Override public void display(@NonNull AbstractPartialBottomView.DTO dto)
    {
        super.display(dto);
        if (!(dto instanceof DTO))
        {
            return;
        }

        if (unrealisedPLValueHeader != null)
        {
            unrealisedPLValueHeader.setText(((DTO) dto).unrealisedPLValueHeader);
        }

        if (unrealisedPLValue != null)
        {
            unrealisedPLValue.setText(((DTO) dto).unrealisedPLValue);
        }

        if (unrealisedPLPercent != null)
        {
            unrealisedPLPercent.setText(((DTO) dto).unrealisedPLPercent);
        }

        if (realisedPLValueHeader != null)
        {
            realisedPLValueHeader.setText(((DTO) dto).realisedPLValueHeader);
        }

        if (realisedPLValue != null)
        {
            realisedPLValue.setText(((DTO) dto).realisedPLValue);
        }

        if (totalInvestedValue != null)
        {
            totalInvestedValue.setText(((DTO) dto).totalInvestedValue);
        }

        if (averagePriceValue != null)
        {
            averagePriceValue.setText(((DTO) dto).averagePriceValue);
        }

        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.display(((DTO) dto).positionPartialBottomInPeriodDTO);
        }
    }

    public static class DTO extends AbstractPartialBottomView.DTO
    {
        @NonNull public final CharSequence unrealisedPLValueHeader;
        @NonNull public final CharSequence unrealisedPLValue;
        @NonNull public final CharSequence unrealisedPLPercent;
        @NonNull public final CharSequence realisedPLValueHeader;
        @NonNull public final CharSequence realisedPLValue;
        @NonNull public final CharSequence totalInvestedValue;
        @NonNull public final CharSequence averagePriceValue;

        @NonNull public final PositionPartialBottomInPeriodViewHolder.DTO positionPartialBottomInPeriodDTO;

        public DTO(@NonNull Resources resources, @NonNull ExpandableListItem<PositionDTO> expandablePositionDTO)
        {
            super(expandablePositionDTO);

            PositionDTO positionDTO = expandablePositionDTO.getModel();
            String na = resources.getString(R.string.na);

            //<editor-fold desc="Unrealised PL">
            Double unrealisedPLRefCcy = positionDTO.unrealizedPLRefCcy;
            unrealisedPLValueHeader = resources.getString(unrealisedPLRefCcy != null && unrealisedPLRefCcy < 0
                    ? R.string.position_unrealised_loss_header
                    : R.string.position_unrealised_profit_header);
            unrealisedPLValue = unrealisedPLRefCcy == null
                    ? na
                    : THSignedMoney.builder(unrealisedPLRefCcy)
                            .withOutSign()
                            .currency(positionDTO.getNiceCurrency())
                            .withDefaultColor()
                            .build()
                            .createSpanned();
            Double gainPercent = positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod()
                    ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
                    : positionDTO.getROISinceInception();
            unrealisedPLPercent = gainPercent == null
                    ? na
                    : THSignedPercentage.builder(gainPercent * 100)
                            .signTypePlusMinusAlways()
                            .relevantDigitCount(3)
                            .format("(%s)")
                            .withDefaultColor()
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

            //<editor-fold desc="Average Price Value">
            Double averagePriceRefCcy = positionDTO.averagePriceRefCcy;
            averagePriceValue = averagePriceRefCcy == null
                    ? na
                    : THSignedMoney.builder(averagePriceRefCcy)
                            .withOutSign()
                            .currency(positionDTO.getNiceCurrency())
                            .build()
                            .toString();
            //</editor-fold>

            positionPartialBottomInPeriodDTO = new PositionPartialBottomInPeriodViewHolder.DTO(resources, positionDTO);
        }
    }
}
