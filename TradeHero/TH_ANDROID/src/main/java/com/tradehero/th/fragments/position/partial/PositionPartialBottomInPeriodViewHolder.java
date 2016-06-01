package com.ayondo.academy.fragments.position.partial;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import android.support.annotation.Nullable;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.position.PositionDTO;
import com.ayondo.academy.api.position.PositionInPeriodDTO;
import com.ayondo.academy.models.number.THSignedMoney;
import com.ayondo.academy.models.number.THSignedPercentage;
import com.ayondo.academy.utils.DateUtils;

public class PositionPartialBottomInPeriodViewHolder implements DTOView<PositionPartialBottomInPeriodViewHolder.DTO>
{
    @Bind(R.id.position_list_bottom_in_period_container) @Nullable protected View inPeriodPositionContainer;
    @Bind(R.id.position_list_in_period_title) @Nullable protected View inPeriodTitle;
    @Bind(R.id.position_list_overall_title) @Nullable protected View overallTitle;
    @Bind(R.id.in_period_pl_value_header) @Nullable protected TextView inPeriodPLHeader;
    @Bind(R.id.in_period_pl_value) @Nullable protected TextView inPeriodPL;
    @Bind(R.id.in_period_additional_invested) @Nullable protected TextView inPeriodAdditionalInvested;
    @Bind(R.id.in_period_start_value) @Nullable protected TextView inPeriodValueAtStart;
    @Bind(R.id.in_period_start_value_date) @Nullable protected TextView inPeriodStartValueDate;
    @Bind(R.id.in_period_roi_value) @Nullable protected TextView inPeriodRoiValue;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomInPeriodViewHolder(@NonNull View container)
    {
        super();
        ButterKnife.bind(this, container);
    }
    //</editor-fold>

    @Override public void display(DTO dto)
    {
        if (inPeriodPositionContainer != null)
        {
            inPeriodPositionContainer.setVisibility(dto.inPeriodVisibility);
        }
        if (inPeriodTitle != null)
        {
            inPeriodTitle.setVisibility(dto.inPeriodVisibility);
        }
        if (overallTitle != null)
        {
            overallTitle.setVisibility(dto.inPeriodVisibility);
        }
        if (inPeriodPLHeader != null)
        {
            inPeriodPLHeader.setText(dto.inPeriodPLHeader);
        }
        if (inPeriodPL != null)
        {
            inPeriodPL.setText(dto.inPeriodPL);
        }
        if (inPeriodAdditionalInvested != null)
        {
            inPeriodAdditionalInvested.setText(dto.inPeriodAdditionalInvested);
        }
        if (inPeriodValueAtStart != null)
        {
            inPeriodValueAtStart.setText(dto.inPeriodValueAtStart);
        }
        if (inPeriodStartValueDate != null)
        {
            inPeriodStartValueDate.setText(dto.inPeriodValueStartDate);
        }
        if (inPeriodRoiValue != null)
        {
            inPeriodRoiValue.setText(dto.inPeriodRoiValue);
        }
    }

    public static class DTO
    {
        @NonNull public final PositionDTO positionDTO;

        @ViewVisibilityValue public final int inPeriodVisibility;
        @NonNull public final CharSequence inPeriodPLHeader;
        @NonNull public final CharSequence inPeriodPL;
        @NonNull public final CharSequence inPeriodAdditionalInvested;
        @NonNull public final CharSequence inPeriodValueAtStart;
        @NonNull public final CharSequence inPeriodValueStartDate;
        @NonNull public final CharSequence inPeriodRoiValue;

        public DTO(@NonNull Resources resources, @NonNull PositionDTO positionDTO)
        {
            this.positionDTO = positionDTO;
            String na = resources.getString(R.string.na);

            inPeriodVisibility = positionDTO instanceof PositionInPeriodDTO ? View.VISIBLE : View.GONE;

            //<editor-fold desc="In Period PL">
            Double totalPLInPeriodRefCcy = positionDTO instanceof PositionInPeriodDTO
                    ? ((PositionInPeriodDTO) positionDTO).totalPLInPeriodRefCcy
                    : null;
            inPeriodPLHeader = totalPLInPeriodRefCcy == null
                    ? na
                    : resources.getString(
                            totalPLInPeriodRefCcy >= 0 ?
                                    R.string.position_in_period_profit :
                                    R.string.position_in_period_loss);
            inPeriodPL = totalPLInPeriodRefCcy == null
                    ? na
                    : THSignedMoney.builder(totalPLInPeriodRefCcy)
                            .withOutSign()
                            .currency(positionDTO.getNiceCurrency())
                            .build()
                            .createSpanned();
            //</editor-fold>

            //<editor-fold desc="In Period ROI Value">
            Double roiInPeriod = positionDTO instanceof PositionInPeriodDTO
                    ? ((PositionInPeriodDTO) positionDTO).getROIInPeriod()
                    : null;
            inPeriodRoiValue = roiInPeriod == null
                    ? na
                    : THSignedPercentage.builder(roiInPeriod * 100.0)
                            .signTypePlusMinusAlways()
                            .withDefaultColor()
                            .relevantDigitCount(3)
                            .build()
                            .createSpanned();
            //</editor-fold>

            //<editor-fold desc="In Period Additional Invested">
            Double sumPurchasesInPeriodRefCcy = positionDTO instanceof PositionInPeriodDTO
                    ? ((PositionInPeriodDTO) positionDTO).sumPurchasesInPeriodRefCcy
                    : null;
            inPeriodAdditionalInvested = sumPurchasesInPeriodRefCcy == null
                    ? na
                    : THSignedMoney.builder(sumPurchasesInPeriodRefCcy)
                            .withOutSign()
                            .currency(positionDTO.getNiceCurrency())
                            .build()
                            .createSpanned();
            //</editor-fold>

            //<editor-fold desc="In Period Value At Start">
            Double marketValueStartPeriodRefCcy = positionDTO instanceof PositionInPeriodDTO
                    ? ((PositionInPeriodDTO) positionDTO).marketValueStartPeriodRefCcy
                    : null;
            inPeriodValueAtStart = marketValueStartPeriodRefCcy == null || /* It appears iOS version does that */ marketValueStartPeriodRefCcy <= 0
                    ? na
                    : THSignedMoney.builder(marketValueStartPeriodRefCcy)
                            .withOutSign()
                            .currency(positionDTO.getNiceCurrency())
                            .build()
                            .createSpanned();
            //</editor-fold>

            inPeriodValueStartDate = resources.getString(
                    R.string.position_in_period_as_of,
                    DateUtils.getDisplayableDate(resources, positionDTO.latestTradeUtc));
        }
    }
}
