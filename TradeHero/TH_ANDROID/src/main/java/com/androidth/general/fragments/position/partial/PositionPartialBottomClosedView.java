package com.androidth.general.fragments.position.partial;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.adapters.ExpandableListItem;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedPercentage;

public class PositionPartialBottomClosedView extends AbstractPartialBottomView
{
    @Bind(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @Bind(R.id.realised_pl_value) protected TextView realisedPLValue;
    @Bind(R.id.roi_value) protected TextView roiValue;
    @Bind(R.id.total_invested_value) protected TextView totalInvestedValue;

    protected PositionPartialBottomInPeriodViewHolder inPeriodViewHolder;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomClosedView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        // in period
        inPeriodViewHolder = new PositionPartialBottomInPeriodViewHolder(this);
    }

    @Override public void display(@NonNull AbstractPartialBottomView.DTO dto)
    {
        super.display(dto);

        if (realisedPLValueHeader != null)
        {
            realisedPLValueHeader.setText(((DTO) dto).realisedPLValueHeader);
        }
        if (realisedPLValue != null)
        {
            realisedPLValue.setText(((DTO) dto).realisedPLValue);
        }
        if (roiValue != null)
        {
            roiValue.setText(((DTO) dto).roiValue);
        }
        if (totalInvestedValue != null)
        {
            totalInvestedValue.setText(((DTO) dto).totalInvestedValue);
        }
        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.display(((DTO) dto).positionPartialBottomInPeriodDTO);
        }
    }

    public static class DTO extends AbstractPartialBottomView.DTO
    {
        @NonNull public final CharSequence realisedPLValueHeader;
        @NonNull public final CharSequence realisedPLValue;
        @NonNull public final CharSequence roiValue;
        @NonNull public final CharSequence totalInvestedValue;

        @NonNull public final PositionPartialBottomInPeriodViewHolder.DTO positionPartialBottomInPeriodDTO;

        public DTO(@NonNull Resources resources, @NonNull ExpandableListItem<PositionDTO> expandablePositionDTO)
        {
            super(expandablePositionDTO);

            PositionDTO positionDTO = expandablePositionDTO.getModel();
            String na = resources.getString(R.string.na);

            //<editor-fold desc="Realised PL">
            Double realisedPLRefCcy = positionDTO.realizedPLRefCcy;
            realisedPLValueHeader = resources.getString(realisedPLRefCcy != null && realisedPLRefCcy < 0
                    ? R.string.position_realised_loss_header
                    : R.string.position_realised_profit_header);

            realisedPLValue = realisedPLRefCcy == null
                    ? na
                    : THSignedMoney.builder(realisedPLRefCcy)
                            .withOutSign()
                            .currency(positionDTO.getNiceCurrency())
                            .relevantDigitCount(3)
                            .build()
                            .createSpanned();
            //</editor-fold>

            //<editor-fold desc="ROI">
            Double roiSinceInception = positionDTO.getROISinceInception();
            roiValue = roiSinceInception == null
                    ? na
                    : THSignedPercentage.builder(roiSinceInception * 100.0)
                            .signTypePlusMinusAlways()
                            .withDefaultColor()
                            .relevantDigitCount(3)
                            .format("(%s)")
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

            positionPartialBottomInPeriodDTO = new PositionPartialBottomInPeriodViewHolder.DTO(resources, positionDTO);
        }
    }
}
